package org.miner.conector.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {
    public final String email;
    private final Map<String, MinerStatistics> statistics = new HashMap<>();

    public User(String email) {
        this.email = email;
    }

    public Set<String> getMinerIds() {
        return statistics.keySet();
    }

    public MinerStatistics getStatisticsForMiner(String id) {
        MinerStatistics minerStatistics = statistics.get(id);
        if (minerStatistics == null) {
            minerStatistics = new MinerStatistics();
        }
        statistics.put(id, minerStatistics);
        return minerStatistics;
    }

    public void updateStatistics(String id, MinerStatistics minerStatistics) {
        statistics.put(id, minerStatistics);
    }
}
