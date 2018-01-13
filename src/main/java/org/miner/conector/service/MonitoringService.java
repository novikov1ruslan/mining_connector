package org.miner.conector.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.miner.conector.MinerStatistics;
import org.miner.conector.Ping;
import org.miner.conector.TimeUtils;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {
    private static final String SENDER_ADDRESS = "novikov.ruslan@gmail.com";
    private static final String DESTINATION_ADDRESS = "leonidnovikov77@gmail.com";
    //    private static final String DESTINATION_ADDRESS = "ivy.games.studio@gmail.com";
    private static final int NO_CONNECTION_LIMIT_IN_MILLIS = 10 * 60 * 1000;

    private long emailSentTime;
    private long resetTime = time();

    private final Map<String, MinerStatistics> statistics = new HashMap<>();

    public String checkStatus() {
        log("checking status...");
        if (statistics.isEmpty()) {
            return "no pings received";
        }

        StringBuilder sb = new StringBuilder();
        for (String id : statistics.keySet()) {
            MinerStatistics minerStatistics = statistics.get(id);
            String message = createMessage(id, minerStatistics);
            sb.append(message).append("<br>");
            if (shouldNextConnectionAlertBeSend(minerStatistics) && isItTimeToSendAlert(minerStatistics.lastPingTime)) {
                sendAlert(id, message);
            }
        }

        return sb.toString();
    }

    private void sendAlert(String id, String message) {
        boolean emailSent = sendEmail(DESTINATION_ADDRESS, message);
        if (emailSent) {
            emailSentTime = time();
            MinerStatistics minerStatistics = statistics.get(id);
            statistics.put(id, new MinerStatistics(minerStatistics.count, minerStatistics.lastPingTime, time()));
        }
        sendEmail("ivy.games.studio@gmail.com", message);
    }

    public boolean shouldNextConnectionAlertBeSend(MinerStatistics minerStatistics) {
        long lastPingTime = minerStatistics.lastPingTime;
        return (flagIsOn() || lastPingTime > minerStatistics.lastConnectionLostTime);
    }

    private boolean isItTimeToSendAlert(long lastPingTime) {
        return time() - lastPingTime > NO_CONNECTION_LIMIT_IN_MILLIS;
    }

    private boolean flagIsOn() {
        return resetTime > emailSentTime;
    }

    private String createMessage(String id, MinerStatistics minerStatistics) {
        if (minerStatistics.count == 0) {
            return id + ": no pings received";
        }

        String timeString = TimeUtils.periodMessage(minerStatistics.lastPingTime);
        return id + ": last ping received " + timeString + " ago";
    }

    public void ping(Ping ping) {
        incrementPingCount(ping);
        log(new Date() + ": ping received for " + ping.id);
    }

    private void incrementPingCount(Ping ping) {
        MinerStatistics statisticsForId = getStatisticsForId(ping.id);
        statistics.put(ping.id, new MinerStatistics(statisticsForId.count + 1, time()));
    }

    public void reset() {
        resetTime = time();
        log("mail trigger reset");
    }

    public long getEmailSentTime() {
        return emailSentTime;
    }

    public long getResetTime() {
        return resetTime;
    }

    public Map<String, MinerStatistics> getStatistics() {
        return new HashMap<>(statistics);
    }

    public MinerStatistics getStatisticsForId(String id) {
        MinerStatistics minerStatistics = statistics.get(id);
        if (minerStatistics == null) {
            minerStatistics = new MinerStatistics();
        }
        return minerStatistics;
    }

    private static boolean sendEmail(String destination, String message) {
        try {
            log("sending email [" + message + "] to: " + destination + "...");
            sendEmailImpl(destination, message);
            return true;
        } catch (Exception e) {
            log("error: " + e);
        }
        return false;
    }

    private static void sendEmailImpl(String destination, String message) throws UnsupportedEncodingException, MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        Message msg = new MimeMessage(session);
        InternetAddress from = new InternetAddress(SENDER_ADDRESS, "Mining Monitoring Alert");
        InternetAddress to = new InternetAddress(destination, "Mr. User");
        msg.setFrom(from);
        msg.addRecipient(Message.RecipientType.TO, to);
        msg.setSubject("Connection lost");
        msg.setText(message);
        Transport.send(msg);
    }

    private static long time() {
        return System.currentTimeMillis();
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
