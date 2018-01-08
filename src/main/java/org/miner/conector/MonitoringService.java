package org.miner.conector;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class MonitoringService {
    private static final String SENDER_ADDRESS = "novikov.ruslan@gmail.com";
    private static final String DESTINATION_ADDRESS = "leonidnovikov77@gmail.com";
//    private static final String DESTINATION_ADDRESS = "ivy.games.studio@gmail.com";
    private static final int NO_CONNECTION_LIMIT_IN_MILLIS = 10 * 60 * 1000;

    private long lastPingTime;
    private long emailSentTime;
    private long resetTime = time();
    private long totalPings;

    public String checkStatus() {
        log("checking status...");
        String message = getLastPingMessage();

        if (shouldSendConnectionAlert() && System.currentTimeMillis() - lastPingTime > NO_CONNECTION_LIMIT_IN_MILLIS) {
            boolean emailSent = sendEmail(DESTINATION_ADDRESS, message);
            if (emailSent) {
                emailSentTime = time();
            }
            sendEmail("ivy.games.studio@gmail.com", message);
        }

        return message;
    }

    public boolean shouldSendConnectionAlert() {
        return resetTime > emailSentTime || lastPingTime > emailSentTime;
    }

    public String getLastPingMessage() {
        if (lastPingTime == 0) {
            return "no pings received";
        }

        String timeString = TimeUtils.periodMessage(lastPingTime);
        return "last ping received " + timeString + " ago";
    }

    public void ping() {
        totalPings++;
        lastPingTime = time();
        log("ping received at: " + new Date());
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

    public long totalPings() {
        return totalPings;
    }

    private static boolean sendEmail(String destination, String message) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            InternetAddress from = new InternetAddress(SENDER_ADDRESS, "Mining Monitoring Alert");
            InternetAddress to = new InternetAddress(destination, "Mr. User");
            msg.setFrom(from);
            msg.addRecipient(Message.RecipientType.TO, to);
            msg.setSubject("Connection lost");
            msg.setText(message);
            Transport.send(msg);
            log("send email");
            return true;
        } catch (Exception e) {
            log("error: " + e);
        }
        return false;
    }

    private static long time() {
        return System.currentTimeMillis();
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
