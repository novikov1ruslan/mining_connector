package org.miner.conector;

import java.util.Date;
import java.util.Map;

import org.miner.conector.service.MinerStatistics;
import org.miner.conector.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectorController {
    @Autowired
    private MonitoringService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        long emailSentTime = service.getEmailSentTime();
        long resetTime = service.getResetTime();
        Date date = new Date();
        return "<h3>" + "Welcome to Mining Connector (v2.1)" + "</h3>" +
                "<p>" + "Server time: " + TimeUtils.defaultFormatedTime(date) + "<br>" +
                "Israel time: " + TimeUtils.israeliFormatedTime(date) +
                "</p>" +
                statistics() +
                "<p>" + (emailSentTime == 0 ? "no mail alerts sent" : "last mail sent " + TimeUtils.periodMessage(emailSentTime) + " ago") + "</p>" +
                "<p>" + "last time was reset " + TimeUtils.periodMessage(resetTime) + " ago" + "</p>" +
                "<p>" +
                "To force connection verification go to " + "<a href=/check>/check</a>" +
                "<br>" +
                "To reset email alert go to " + "<a href=/reset>/reset</a>" +
                "</p>";
    }

    private String statistics() {
        Map<String, MinerStatistics> statistics = service.getStatistics();
        StringBuilder sb = new StringBuilder();
        if (statistics.isEmpty()) {
            sb.append("<p>" + "no pings received" + "</p>");
        }
        for (String id : statistics.keySet()) {
            MinerStatistics minerStatistics = statistics.get(id);
            boolean alertShouldBeSent = service.shouldNextConnectionAlertBeSend(minerStatistics);
            sb.append("<p>");
            sb.append("[").append(id).append("]").append("<br>");
            sb.append("(count=" + minerStatistics.count + ", last ping=" +
                    TimeUtils.periodMessage(minerStatistics.lastPingTime) + " ago)").append("<br>");
            sb.append(alertShouldBeSent ? "next alert will be sent if no connection detected" : "alert has already been sent");
            sb.append("</p>");
        }
        return sb.toString();
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public String checkStatus() {
        return service.checkStatus();
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String reset() {
        service.reset();
        return "email will be sent next time";
    }

    @RequestMapping(value = "/ping", method = RequestMethod.POST)
    public String ping(@RequestBody Ping ping) {
        service.ping(ping);
        return new Date() + ": total pings " + service.getStatisticsForId(ping.id).count;
    }

}
