package org.miner.conector.service;

import java.util.Date;

import org.miner.conector.TimeUtils;

public class AlertMassageUtils {
    private static final String BR = "<br>";

    public static String createMessage(String id, MinerStatistics minerStatistics) {
        if (minerStatistics.count == 0) {
            return id + ": no pings received";
        }

        String timeString = TimeUtils.formattedPeriod(minerStatistics.lastPingTime);
        Date date = new Date(minerStatistics.lastPingTime);
        return "No pings from [" + id + "] for " + timeString + BR +
                "Last ping received at:" + BR +
                "Server time: " + TimeUtils.defaultFormatedTime(date);
//                BR + "Israel time: " + TimeUtils.israeliFormatedTime(date);
    }
}
