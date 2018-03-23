package org.miner.conector;

import java.util.Date;
import java.util.Set;

import org.miner.conector.service.MinerStatistics;
import org.miner.conector.service.MonitoringService;
import org.miner.conector.service.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectorController {
    private static final String BR = "<br>";
    private static final String LONG_LINE = "--------------------------------------------------";
    private static final String SHORT_LINE = "--------------------";

    @Autowired
    private MonitoringService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        Date date = new Date();
        return "<h3>" + "Welcome to Mining Connector (v4)" + "</h3>" +

                "<p>" + "Server time: " + TimeUtils.defaultFormatedTime(date) +
//                BR + "Israel time: " + TimeUtils.israeliFormatedTime(date) +
                "</p>" +
                "<p>" + "last time was reset " + TimeUtils.formattedPeriod(service.getResetTime()) + " ago" +
                "</p>" +
                "<p>" + statistics() + "</p>" +
                "<p>" +
                LONG_LINE +
                BR + "To force connection verification go to " + "<a href=/check>/check</a>" +
                BR + "To reset server go to " + "<a href=/reset>/reset</a>" +
                "</p>";
    }

    private StringBuilder statistics() {
        Set<User> users = service.getStatistics();
        StringBuilder sb = new StringBuilder();
        if (users.isEmpty()) {
            sb.append("<p style=\"color:red\">" + "no pings received" + "</p>");
        }
        for (User user : users) {
            sb.append(BR).append(LONG_LINE);
            sb.append(BR).append(statisticsForUser(user));
        }
        return sb;
    }

    private StringBuilder statisticsForUser(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.email);
        for (String id : user.getMinerIds()) {
            sb.append(BR).append(SHORT_LINE);

            MinerStatistics minerStatistics = user.getStatisticsForMiner(id);
            boolean alertSent = service.alertHasBeenSent(minerStatistics);
            if (alertSent) {
                sb.append("<div style=\"color:red\">");
            } else {
                sb.append("<div style=\"color:blue\">");
            }
            sb.append(statisticsForId(minerStatistics, id));
            sb.append("</div>");
        }
        return sb;
    }

    private StringBuilder statisticsForId(MinerStatistics minerStatistics, String id) {
        StringBuilder sb = new StringBuilder();
        boolean alertSent = service.alertHasBeenSent(minerStatistics);
        sb.append("[").append(id).append("]");
        sb.append(BR).append("(count=" + minerStatistics.count + ", last ping received " +
                TimeUtils.formattedPeriod(minerStatistics.lastPingTime) + " ago)");
        sb.append(BR).append(alertSent ? "alert has already been sent " + TimeUtils.formattedPeriod(minerStatistics.alertNotificationTime) + " ago"
                            : "next alert will be sent if no connection detected");
        return sb;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public String checkStatus() {
        return service.checkStatus();
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String reset() {
        service.reset();
        return "server data reset";
    }

    @RequestMapping(value = "/ping", method = RequestMethod.POST)
    public String ping(@RequestBody Ping ping) {
        User user = service.ping(ping);
        return "total pings " + user.getStatisticsForMiner(ping.getId()).count;
    }

}
