package com.clemble.casino.integration.goal;

import com.clemble.casino.android.ClembleCasinoTemplate;
import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.goal.event.action.GoalReachedAction;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.construction.GoalConstructionRequest;
import com.clemble.casino.goal.lifecycle.management.GoalPhase;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.utils.AsyncUtils;
import com.clemble.casino.lifecycle.management.event.action.surrender.GiveUpAction;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mavarazy on 5/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class GoalInspirationITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testInspirationsAddedOnWin() {
        createPlayerWithInspirations();
    }

    private ClembleCasinoOperations createPlayerWithInspirations() {
        // Step 1. Creating new player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Checking A does not have any inspirations
        AsyncUtils.verify(() -> A.accountService().myAccount().getMoney(Currency.inspiration).equals(Money.create(Currency.inspiration, 0)));
        // Step 3. Specify a goal
        GoalConfiguration configuration = A.goalOperations().configurationService().getConfigurations().get(0);
        GoalConstruction construction = A.goalOperations().constructionService().construct(new GoalConstructionRequest(configuration, "Test inspirations added", DateTimeZone.UTC));
        // Step 3.1. Checking goal already started
        AsyncUtils.verify(() -> A.goalOperations().actionService().getState(construction.getGoalKey()).getPhase() == GoalPhase.started);
        // Step 4. Finishing goal
        A.goalOperations().actionService().process(construction.getGoalKey(), new GoalReachedAction("I'm awesome"));
        // Step 5. Checking inspirations added
        AsyncUtils.verify(() -> A.accountService().myAccount().getMoney(Currency.inspiration).getAmount() != 0L);
        // Step 6. Sending created player
        return A;
    }

    @Test
    public void testInspirationsGetDeduced (){
        // Step 1. Check inspirations before inspire
        ClembleCasinoOperations A = createPlayerWithInspirations();
        ClembleCasinoOperations B = createPlayerWithInspirations();
        // Step 2. Specify a goal
        GoalConfiguration configuration = A.goalOperations().configurationService().getConfigurations().get(0);
        GoalConstruction construction = A.goalOperations().constructionService().construct(new GoalConstructionRequest(configuration, "Test inspirations added", DateTimeZone.UTC));
        // Step 2.1. Checking goal already started
        AsyncUtils.verify(() -> A.goalOperations().actionService().getState(construction.getGoalKey()).getPhase() == GoalPhase.started);
        long inspirationsBefore = B.accountService().myAccount().getMoney(Currency.inspiration).getAmount();
        B.goalOperations().actionService().inspire(construction.getGoalKey(), "Go for it man");
        AsyncUtils.verifyEquals(() -> B.accountService().myAccount().getMoney(Currency.inspiration).getAmount(), () -> inspirationsBefore - 1);
    }

    @Test
    public void testInspirationsKeepDeducedOnWin (){
        // Step 1. Check inspirations before inspire
        ClembleCasinoOperations A = createPlayerWithInspirations();
        ClembleCasinoOperations B = createPlayerWithInspirations();
        // Step 2. Specify a goal
        GoalConfiguration configuration = A.goalOperations().configurationService().getConfigurations().get(0);
        GoalConstruction construction = A.goalOperations().constructionService().construct(new GoalConstructionRequest(configuration, "Test inspirations added", DateTimeZone.UTC));
        // Step 2.1. Checking goal already started
        AsyncUtils.verify(() -> A.goalOperations().actionService().getState(construction.getGoalKey()).getPhase() == GoalPhase.started);
        long inspirationsBefore = B.accountService().myAccount().getMoney(Currency.inspiration).getAmount();
        B.goalOperations().actionService().inspire(construction.getGoalKey(), "Go for it man");
        AsyncUtils.verifyEquals(() -> B.accountService().myAccount().getMoney(Currency.inspiration).getAmount(), () -> inspirationsBefore - 1);
        long inspirationsAfter = B.accountService().myAccount().getMoney(Currency.inspiration).getAmount();
        A.goalOperations().actionService().process(construction.getGoalKey(), new GoalReachedAction("I'm awesome"));
        AsyncUtils.verify(() -> A.paymentService().getTransaction(construction.getGoalKey()) != null);
        AsyncUtils.verifyEquals(() -> B.accountService().myAccount().getMoney(Currency.inspiration).getAmount(), () -> inspirationsAfter);
    }

    @Test
    public void testInspirationsKeepDeducedOnLoose (){
        // Step 1. Check inspirations before inspire
        ClembleCasinoOperations A = createPlayerWithInspirations();
        ClembleCasinoOperations B = createPlayerWithInspirations();
        // Step 2. Specify a goal
        GoalConfiguration configuration = A.goalOperations().configurationService().getConfigurations().get(0);
        GoalConstruction construction = A.goalOperations().constructionService().construct(new GoalConstructionRequest(configuration, "Test inspirations added", DateTimeZone.UTC));
        // Step 2.1. Checking goal already started
        AsyncUtils.verify(() -> A.goalOperations().actionService().getState(construction.getGoalKey()).getPhase() == GoalPhase.started);
        long inspirationsBefore = B.accountService().myAccount().getMoney(Currency.inspiration).getAmount();
        B.goalOperations().actionService().inspire(construction.getGoalKey(), "Go for it man");
        AsyncUtils.verifyEquals(() -> B.accountService().myAccount().getMoney(Currency.inspiration).getAmount(), () -> inspirationsBefore - 1);
        long inspirationsAfter = B.accountService().myAccount().getMoney(Currency.inspiration).getAmount();
        A.goalOperations().actionService().process(construction.getGoalKey(), new GiveUpAction());
        AsyncUtils.verify(() -> A.paymentService().getTransaction(construction.getGoalKey()) != null);
        AsyncUtils.verifyEquals(() -> B.accountService().myAccount().getMoney(Currency.inspiration).getAmount(), () -> inspirationsAfter);
    }
}
