package com.clemble.casino.integration.goal;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.goal.event.action.GoalReachedAction;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.construction.GoalConstructionRequest;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.utils.AsyncUtils;
import com.clemble.casino.lifecycle.record.EventRecord;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

/**
 * Created by mavarazy on 3/14/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class GoalVictoryITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testSimpleVictory() {
        // Step 1. Creating player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating GoalRequest
        GoalConstructionRequest goalRequest = new GoalConstructionRequest(A.goalOperations().configurationService().getConfigurations().get(0), "Simple victory notification test", DateTimeZone.UTC);
        final GoalConstruction construction = A.goalOperations().constructionService().construct(goalRequest);
        final String goalKey = construction.getGoalKey();
        // Step 4. Checking goal started
        AsyncUtils.verify(() -> A.goalOperations().actionService().getState(goalKey) != null);
        // Step 5. Performing simple action
        A.goalOperations().actionService().process(goalKey, new GoalReachedAction("Win bitch"));
        // Step 6. Waiting for goal to completes
        AsyncUtils.verify(() -> {
                GoalState state = A.goalOperations().actionService().getState(goalKey);
                Collection<EventRecord> events = state.getEventRecords();
                for (EventRecord event : events) {
                    if (event.getEvent() instanceof GoalEndedEvent)
                        return true;
                }
                return false;
            }
        );
        AsyncUtils.verify(() -> A.goalOperations().victoryService().listMy().size() == 1);
    }

}
