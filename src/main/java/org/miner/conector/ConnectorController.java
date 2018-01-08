/**
 * Copyright 2017 Google Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.miner.conector;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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
        boolean alarmShouldBeSent = service.shouldSendConnectionAlert();
        return "<h3>" + "Welcome to Mining Connector (v1)" + "</h3>" +
                "<p>" + service.getLastPingMessage() + "</p>" +
                "<p>" + (emailSentTime == 0 ? "no mail alerts sent" : "last mail sent " + TimeUtils.periodMessage(emailSentTime) + " ago") + "</p>" +
                "<p>" + "last time was reset " + TimeUtils.periodMessage(resetTime) + " ago" + "</p>" +
                "<p>" + (alarmShouldBeSent ? "next alert will be sent if no connection detected" : "alert already been sent") + "</p>" +
                "<p>" +
                "To force connection verification go to " + "<a href=/check>/check</a>" +
                "<br>" +
                "To reset email alert go to " + "<a href=/reset>/reset</a>" +
                "</p>";
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
    public String ping() {
        service.ping();
        return new Date() + ": total pings " + service.totalPings();
    }

}
