package com.clemble.casino.integration.goal;

import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.goal.configuration.controller.GoalConfigurationController;
import com.clemble.casino.goal.construction.controller.GoalConstructionController;
import com.clemble.casino.goal.construction.controller.GoalInitiationController;
import com.clemble.casino.goal.controller.GoalActionController;
import com.clemble.casino.goal.controller.GoalRecordController;
import com.clemble.casino.goal.controller.GoalVictoryController;
import com.clemble.casino.goal.suggestion.controller.GoalSuggestionController;

/**
 * Created by mavarazy on 9/15/14.
 */
public class IntegrationGoalOperationsFactory {

    final private GoalConfigurationController configurationService;
    final private GoalInitiationController initiationService;
    final private GoalSuggestionController suggestionService;
    final private GoalConstructionController constructionService;
    final private GoalActionController actionService;
    final private GoalRecordController recordService;
    final private GoalVictoryController victoryService;

    public IntegrationGoalOperationsFactory(
        GoalConfigurationController configurationService,
        GoalInitiationController initiationService,
        GoalSuggestionController suggestionService,
        GoalConstructionController constructionService,
        GoalActionController actionService,
        GoalRecordController recordService,
        GoalVictoryController victoryService) {
        this.configurationService = configurationService;
        this.initiationService = initiationService;
        this.suggestionService = suggestionService;
        this.constructionService = constructionService;
        this.actionService = actionService;
        this.recordService = recordService;
        this.victoryService = victoryService;
    }

    public GoalOperations construct(String player) {
        return new IntegrationGoalOperations(player,
            configurationService,
            initiationService,
            suggestionService,
            constructionService,
            actionService,
            recordService,
            victoryService);
    }

}
