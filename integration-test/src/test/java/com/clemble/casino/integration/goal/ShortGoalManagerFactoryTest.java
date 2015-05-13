package com.clemble.casino.integration.goal;

import com.clemble.casino.bet.Bet;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalRoleConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.NoReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.share.ShareRule;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.utils.AsyncUtils;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.timeout.MoveTimeoutCalculatorByLimit;
import com.clemble.casino.lifecycle.configuration.rule.timeout.MoveTimeoutRule;
import com.clemble.casino.lifecycle.configuration.rule.timeout.TotalTimeoutCalculatorByLimit;
import com.clemble.casino.lifecycle.configuration.rule.timeout.TotalTimeoutRule;
import com.clemble.casino.lifecycle.construction.ConstructionState;
import com.clemble.casino.lifecycle.record.EventRecord;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.test.concurrent.AsyncCompletionUtils;
import com.clemble.test.concurrent.Check;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by mavarazy on 5/13/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class ShortGoalManagerFactoryTest {

    @Autowired
    public GoalManagerFactoryFacade managerFactory;

    @Autowired
    public GoalStateRepository recordRepository;

    final private GoalConfiguration configuration = new GoalConfiguration(
        "basic",
        "Basic",
        new Bet(Money.create(Currency.point, 500), Money.create(Currency.point, 50)),
        new BasicReminderRule(TimeUnit.HOURS.toMillis(4)),
        new BasicReminderRule(TimeUnit.HOURS.toMillis(2)),
        new MoveTimeoutRule(LooseBreachPunishment.getInstance(), new MoveTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(1))),
        new TotalTimeoutRule(LooseBreachPunishment.getInstance(), new TotalTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(3))),
        new GoalRoleConfiguration(3, LimitedBetRule.create(50, 100), 50, NoReminderRule.INSTANCE, NoReminderRule.INSTANCE),
        ShareRule.EMPTY
    );

    @Test
    @Ignore // TODO move this test to integration - Schedule is external service currently
    public void testSimpleTimeout() throws InterruptedException {
        // Step 1. Generating goal
        final String goalKey = RandomStringUtils.randomAlphabetic(10);
        String player = RandomStringUtils.randomAlphabetic(10);
        GoalConstruction initiation = new GoalConstruction(
            goalKey,
            player,
            "Create goal state",
            DateTimeZone.UTC,
            "",
            DateTime.now(DateTimeZone.UTC),
            configuration,
            ConstructionState.constructed
        );
        // Step 2. Starting initiation
        managerFactory.start(initiation);
        // Step 3. Checking there is a state for the game
        Thread.sleep(2000);
        AsyncUtils.verify(() -> {
            Set<EventRecord> events = recordRepository.findOne(goalKey).getEventRecords();
            for (EventRecord record : events) {
                if (record.getEvent() instanceof GoalEndedEvent) {
                    return true;
                }
            }
            return false;
        });
        AsyncUtils.verify(() -> managerFactory.get(goalKey) == null);
    }
}
