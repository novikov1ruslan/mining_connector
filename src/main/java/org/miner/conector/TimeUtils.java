package org.miner.conector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss");
    private static final TimeZone israelTimeZone = TimeZone.getTimeZone("Asia/Israel");
    private static final TimeZone defaultTimeZone = TimeZone.getDefault();

    private static String getFormattedTime(Date date, TimeZone timeZone) {
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public static String defaultFormatedTime(Date date) {
        return getFormattedTime(date, defaultTimeZone);
    }

    public static String israeliFormatedTime(Date date) {
        return getFormattedTime(date, israelTimeZone);
    }

    public static long getPeriodInSeconds(long since) {
        long time = System.currentTimeMillis();
        return (time - since) / 1000;
    }

    public static String formattedPeriod(long since) {
        long seconds = getPeriodInSeconds(since);
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return hours + "h:" + (minutes % 60) + "m:" + (seconds % 60) + "s";
    }
}
