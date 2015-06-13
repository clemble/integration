package com.clemble.casino.integration.goal;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.goal.event.action.GoalStatusUpdateAction;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.construction.GoalConstructionRequest;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.utils.AsyncUtils;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mavarazy on 11/29/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class GoalDeadlineITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testDeadlineNotZero(){
        // Step 1. Creating player
        final ClembleCasinoOperations A = playerScenarios.createPlayer();
        final GoalOperations gA = A.goalOperations();
        // Step 2. Setting new goal
        final GoalConfiguration configuration = gA.configurationService().getConfigurations().get(0);
        final GoalConstruction construction = gA.constructionService().construct(new GoalConstructionRequest(configuration, "Test deadline", DateTimeZone.UTC));
        final String goalKey = construction.getGoalKey();
        // Step 2.1. Checking goal initiated
        // Step 3. Checking goal has deadline in timeout
        Assert.assertTrue(AsyncUtils.checkNotNull(() -> gA.actionService().getState(goalKey)));
        // Step 3.1. Extracting deadline
        long deadline = gA.actionService().getState(goalKey).getDeadline().getMillis();
        Assert.assertNotEquals(deadline, 0L);
    }

    @Test
    public void testDeadlineStaysTheSame() {
        // Step 1. Creating player
        final ClembleCasinoOperations A = playerScenarios.createPlayer();
        final GoalOperations gA = A.goalOperations();
        // Step 2. Setting new goal
        final GoalConfiguration configuration = gA.configurationService().getConfigurations().get(0);
        final GoalConstruction construction = gA.constructionService().construct(new GoalConstructionRequest(configuration, "Test deadline", DateTimeZone.UTC));
        final String goalKey = construction.getGoalKey();
        // Step 3. Checking goal has deadline in timeout
        Assert.assertTrue(AsyncUtils.checkNotNull(() -> gA.actionService().getState(goalKey)));
        // Step 3.1. Extracting deadline
        long clockDeadline = gA.actionService().getState(goalKey).getDeadline().getMillis();
        long goalDeadline = gA.actionService().getState(goalKey).getDeadline().getMillis();
        Assert.assertNotEquals(clockDeadline, 0L);
        // Step 3.2. Updating status
        gA.actionService().process(goalKey, new GoalStatusUpdateAction("New status"));
        long newClockDeadline = gA.actionService().getState(goalKey).getDeadline().getMillis();
        long newGoalDeadline = gA.actionService().getState(goalKey).getDeadline().getMillis();
        // Step 4. Check deadline remained same
        Assert.assertEquals(goalDeadline, newGoalDeadline);
        Assert.assertEquals(clockDeadline, newClockDeadline);
    }

}
