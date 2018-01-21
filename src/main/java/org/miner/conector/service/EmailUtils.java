package org.miner.conector.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailUtils {
    private static final String SENDER_ADDRESS = "novikov.ruslan@gmail.com";

    public static boolean sendEmail(String id, String destination, String message) {
        try {
            log("sending email [" + message + "] to: " + destination + "...");
            sendEmailImpl(id, destination, message);
            return true;
        } catch (Exception e) {
            log("error: " + e);
        }
        return false;
    }

    public static void sendEmailImpl(String id, String destination, String message)
            throws UnsupportedEncodingException, MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        Message msg = new MimeMessage(session);
        InternetAddress from = new InternetAddress(SENDER_ADDRESS, id);
        InternetAddress to = new InternetAddress(destination, "Mr. User");
        msg.setFrom(from);
        msg.addRecipient(Message.RecipientType.TO, to);
        msg.setSubject("Connection lost");
        msg.setText(message);
        Transport.send(msg);
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
