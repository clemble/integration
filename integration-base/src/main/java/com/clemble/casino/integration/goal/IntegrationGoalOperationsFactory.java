package com.clemble.casino.integration.goal;

import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.goal.controller.GoalConfigurationController;
import com.clemble.casino.goal.controller.GoalConstructionController;
import com.clemble.casino.goal.controller.GoalActionController;
import com.clemble.casino.goal.controller.GoalVictoryController;
import com.clemble.casino.goal.suggestion.controller.GoalSuggestionController;

/**
 * Created by mavarazy on 9/15/14.
 */
public class IntegrationGoalOperationsFactory {

    final private GoalConfigurationController configurationService;
    final private GoalSuggestionController suggestionService;
    final private GoalConstructionController constructionService;
    final private GoalActionController actionService;
    final private GoalVictoryController victoryService;

    public IntegrationGoalOperationsFactory(
        GoalConfigurationController configurationService,
        GoalSuggestionController suggestionService,
        GoalConstructionController constructionService,
        GoalActionController actionService,
        GoalVictoryController victoryService) {
        this.configurationService = configurationService;
        this.suggestionService = suggestionService;
        this.constructionService = constructionService;
        this.actionService = actionService;
        this.victoryService = victoryService;
    }

    public GoalOperations construct(String player) {
        return new IntegrationGoalOperations(player,
            configurationService,
            suggestionService,
            constructionService,
            actionService,
            victoryService);
    }

}
