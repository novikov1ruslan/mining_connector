package org.miner.conector.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.miner.conector.Ping;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {
    private static final String BR = "<br>";

    private static final int NO_CONNECTION_LIMIT_IN_MILLIS = 10 * 60 * 1000;

    private final Map<String, User> users = new HashMap<>();
    private long resetTime = time();

    public MonitoringService() {
        try {
            initFcm();
        } catch (IOException e) {
            log(e.toString());
        }
    }

    public String checkStatus() {
        log("checking status...");
        if (users.isEmpty()) {
            return "no pings received";
        }

        StringBuilder sb = new StringBuilder();

        for (User user : users.values()) {
            checkForUser(user, sb);
        }

        return sb.toString();
    }

    private void checkForUser(User user, StringBuilder sb) {
        for (String id : user.getMinerIds()) {
            MinerStatistics minerStatistics = user.getStatisticsForMiner(id);
            String message = AlertMassageUtils.createMessage(id, minerStatistics);
            sb.append(message).append(BR).append(BR);
            if (shouldNextConnectionAlertBeSend(minerStatistics)
                    && isItTimeToSendAlert(minerStatistics.lastPingTime)) {
                sendFcm(user.email, message);
                boolean emailSent = EmailUtils.sendEmail(id, user.email, message);
                if (emailSent) {
                    MinerStatistics minerStatistics1 = user.getStatisticsForMiner(id);
                    user.updateStatistics(id, new MinerStatistics(minerStatistics1.count, minerStatistics1.lastPingTime, time()));
                }
            }
        }
    }

    private boolean sendFcm(String topic, String message) {
        try {
            sendToTopic(topic, message);
            return true;
        } catch (Exception e) {
            log(e.toString());
            return false;
        }
    }

    private boolean shouldNextConnectionAlertBeSend(MinerStatistics minerStatistics) {
        return !alertHasBeenSent(minerStatistics);
    }

    public boolean alertHasBeenSent(MinerStatistics minerStatistics) {
        return minerStatistics.alertNotificationTime > minerStatistics.lastPingTime;
    }

    private static boolean isItTimeToSendAlert(long lastPingTime) {
        return time() - lastPingTime > NO_CONNECTION_LIMIT_IN_MILLIS;
    }

    public User ping(Ping ping) {
        User user = saveUser(ping.getEmail());
        incrementPingCount(user, ping.getId());
        log(new Date() + ": ping received: " + ping);
        return user;
    }

    private User saveUser(String email) {
        User user = users.get(email);
        if (user == null) {
            user = new User(email);
            users.put(user.email, user);
        }
        return user;
    }

    private void incrementPingCount(User user, String id) {
        MinerStatistics statisticsForId = user.getStatisticsForMiner(id);
        user.updateStatistics(id, new MinerStatistics(statisticsForId.count + 1, time()));
    }

    public void reset() {
        resetTime = time();
        users.clear();
        log("server reset");
    }

    public long getResetTime() {
        return resetTime;
    }

    public Set<User> getStatistics() {
        return new HashSet<>(users.values());
    }

    private static void initFcm() throws IOException {
        log("initializing FCM...");
        FileInputStream serviceAccount = new FileInputStream("mining-connector-firebase-adminsdk.json");
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(googleCredentials)
                .setDatabaseUrl("https://mining-connector.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        log("FCM initialised");
    }

    private void sendToTopic(String topic, String text) throws ExecutionException, InterruptedException {
        Notification notification = new Notification("disconnect", text);
        Message message = Message.builder()
                .setNotification(notification)
                .setTopic(topic)
                .build();

        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log("Successfully sent message: " + response);
    }

    private static long time() {
        return System.currentTimeMillis();
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
