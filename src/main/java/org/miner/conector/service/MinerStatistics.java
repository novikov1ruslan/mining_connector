package org.miner.conector.service;

public class MinerStatistics {
    public final long count;
    public final long lastPingTime;
    public final long alertNotificationTime;

    public MinerStatistics() {
        this(0, 0, 0);
    }

    public MinerStatistics(long count, long lastPingTime) {
        this(count, lastPingTime, 0);
    }

    public MinerStatistics(long count, long lastPingTime, long alertNotificationTime) {
        this.count = count;
        this.lastPingTime = lastPingTime;
        this.alertNotificationTime = alertNotificationTime;
    }

    @Override
    public String toString() {
        return "(count=" + count + ", lastPingTime=" + lastPingTime + ')';
    }
}
