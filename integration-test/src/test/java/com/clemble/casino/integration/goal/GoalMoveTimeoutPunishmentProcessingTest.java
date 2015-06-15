package com.clemble.casino.integration.goal;

import com.clemble.casino.bet.Bet;
import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalRoleConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.NoReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.share.ShareRule;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.construction.GoalConstructionRequest;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.utils.AsyncUtils;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.breach.CountdownBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.breach.PenaltyBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.timeout.*;
import com.clemble.casino.lifecycle.management.outcome.Outcome;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * Created by mavarazy on 1/5/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class GoalMoveTimeoutPunishmentProcessingTest {

    @Test
    public void testName() throws Exception {

    }

    final private GoalConfiguration LOOSE_PUNISHMENT = new GoalConfiguration(
        "move:loose:punishment",
        "move:loose:punishment",
        new Bet(Money.create(Currency.point, 100), Money.create(Currency.point, 50)),
        new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
        NoReminderRule.INSTANCE,
        new MoveTimeoutRule(LooseBreachPunishment.getInstance(), new MoveTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(3))),
        new TotalTimeoutRule(LooseBreachPunishment.getInstance(), new TotalTimeoutCalculatorByLimit(TimeUnit.HOURS.toMillis(3))),
        new GoalRoleConfiguration(
            3,
            LimitedBetRule.create(50, 100),
            50,
            new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
            NoReminderRule.INSTANCE
        ),
        ShareRule.EMPTY
    );

    final private GoalConfiguration PENALTY_PUNISHMENT = new GoalConfiguration(
        "move:penalty:punishment",
        "move:penalty:punishment",
        new Bet(Money.create(Currency.point, 30), Money.create(Currency.point, 20)),
        new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
        NoReminderRule.INSTANCE,
        new MoveTimeoutRule(new PenaltyBreachPunishment(Money.create(Currency.point, 10)), new MoveTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(3))),
        new TotalTimeoutRule(new PenaltyBreachPunishment(Money.create(Currency.point, 10)), new TotalTimeoutCalculatorByLimit(TimeUnit.HOURS.toMillis(3))),
        new GoalRoleConfiguration(
            3,
            LimitedBetRule.create(50, 100),
            50,
            new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
            NoReminderRule.INSTANCE
        ),
        ShareRule.EMPTY
    );

    final private GoalConfiguration COUNTDOWN_PUNISHMENT = new GoalConfiguration(
        "move:countdown:punishment",
        "move:countdown:punishment",
        new Bet(Money.create(Currency.point, 100), Money.create(Currency.point, 50)),
        new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
        NoReminderRule.INSTANCE,
        new MoveTimeoutRule(new CountdownBreachPunishment(Money.create(Currency.point, 10), 100), new MoveTimeoutCalculatorByLimit(TimeUnit.SECONDS.toMillis(1))),
        new TotalTimeoutRule(new CountdownBreachPunishment(Money.create(Currency.point, 10), 100), new TotalTimeoutCalculatorByLimit(TimeUnit.HOURS.toMillis(3))),
        new GoalRoleConfiguration(
            3,
            LimitedBetRule.create(50, 100),
            50,
            new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
            NoReminderRule.INSTANCE
        ),
        ShareRule.EMPTY
    );


    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testLoose() {
        // Step 1. Creating player A
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating goal request
        GoalConstruction AC = A.goalOperations().constructionService().construct(new GoalConstructionRequest(LOOSE_PUNISHMENT, "Test loose timeout", DateTimeZone.UTC));
        // Step 3. Checking AC
        Outcome expected = Outcome.lost;
        boolean check = AsyncUtils.check(() -> expected.equals(A.goalOperations().actionService().getState(AC.getGoalKey()).getOutcome()));
        Assert.assertTrue(check);
    }

    @Test
    public void testPenalty() {
        // Step 1. Creating player A
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating goal request
        GoalConstruction AC = A.goalOperations().constructionService().construct(new GoalConstructionRequest(PENALTY_PUNISHMENT, "Test penalty timeout", DateTimeZone.UTC));
        // Step 3. Checking AC
        // Step 4. Checking penalty due to move timeout
        boolean checkPenalized = AsyncUtils.check(() -> A.goalOperations().actionService().getState(AC.getGoalKey()).getBank().getPenalty().getAmount() != 0);
        Assert.assertTrue(checkPenalized);
        // Step 5. Checking loosing after money expires
        Outcome expected = Outcome.lost;
        boolean checkLoose = AsyncUtils.check(() -> expected.equals(A.goalOperations().actionService().getState(AC.getGoalKey()).getOutcome()));
        Assert.assertTrue(checkLoose);
    }

}
