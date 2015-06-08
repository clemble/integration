package com.clemble.casino.integration.schedule;

import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.utils.AsyncUtils;
import com.clemble.casino.schedule.listener.SystemAddJobScheduleEventListener;
import com.clemble.casino.server.event.SystemEvent;
import com.clemble.casino.server.event.bet.SystemBetCompletedEvent;
import com.clemble.casino.server.event.schedule.SystemAddJobScheduleEvent;
import com.clemble.casino.server.player.notification.SystemEventListener;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mavarazy on 11/8/14.
 */
// TODO move to schedule module
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class SimpleScheduleTest {

    @Configuration
    public static class TestEventListener implements SystemEventListener<SystemBetCompletedEvent> {

        final public BlockingQueue<SystemEvent> systemEvents = new ArrayBlockingQueue<SystemEvent>(10);

        @Override
        public void onEvent(SystemBetCompletedEvent event) {
            systemEvents.add(event);
        }

        @Override
        public String getChannel() {
            return SystemBetCompletedEvent.CHANNEL;
        }

        @Override
        public String getQueueName() {
            return SystemBetCompletedEvent.CHANNEL + " > test";
        }
    }

    @Autowired
    public SystemAddJobScheduleEventListener systemAddJobScheduleEventListener;

    @Test
    public void testSchedule() throws InterruptedException {
        // Step 0. Creating fake event listener
        final TestEventListener eventListener = new TestEventListener();
        // Step 1. Generating new add job event
        SystemAddJobScheduleEvent event = new SystemAddJobScheduleEvent(RandomStringUtils.random(5), RandomStringUtils.random(5), new SystemBetCompletedEvent("t:a", "a"), new DateTime(System.currentTimeMillis() + 300));
        // Step 2. Generating onEvent
        systemAddJobScheduleEventListener.onEvent(event);
        // Step 3. Checking event received
        AsyncUtils.verifyEquals(() -> eventListener.systemEvents.size(), () -> 1);
    }

}
