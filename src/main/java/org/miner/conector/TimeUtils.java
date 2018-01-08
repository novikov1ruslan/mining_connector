package org.miner.conector;

public class TimeUtils {
    public static long getPeriodInSeconds(long since) {
        long time = System.currentTimeMillis();
        return (time - since) / 1000;
    }


    public static String periodMessage(long since) {
        long seconds = getPeriodInSeconds(since);
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return hours + "h:" + (minutes % 60) + "m:" + (seconds % 60) + "s";
    }
}
