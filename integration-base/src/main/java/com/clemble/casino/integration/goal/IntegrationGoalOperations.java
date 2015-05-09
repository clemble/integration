package com.clemble.casino.integration.goal;

import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.goal.configuration.controller.GoalConfigurationController;
import com.clemble.casino.goal.controller.GoalActionController;
import com.clemble.casino.goal.controller.GoalVictoryController;
import com.clemble.casino.goal.lifecycle.configuration.service.GoalConfigurationService;
import com.clemble.casino.goal.construction.controller.GoalConstructionController;
import com.clemble.casino.goal.lifecycle.construction.service.GoalConstructionService;
import com.clemble.casino.goal.lifecycle.construction.service.GoalSuggestionService;
import com.clemble.casino.goal.lifecycle.management.service.GoalActionService;
import com.clemble.casino.goal.lifecycle.management.service.GoalVictoryService;
import com.clemble.casino.goal.suggestion.controller.GoalSuggestionController;

/**
 * Created by mavarazy on 9/15/14.
 */
public class IntegrationGoalOperations implements GoalOperations {

    final private GoalConfigurationService configurationService;
    final private GoalConstructionService constructionService;
    final private GoalSuggestionService suggestionService;
    final private GoalActionService actionService;
    final private GoalVictoryService victoryService;

    public IntegrationGoalOperations(String player,
        GoalConfigurationController configurationService,
        GoalSuggestionController suggestionService,
        GoalConstructionController constructionService,
        GoalActionController actionService,
        GoalVictoryController victoryService) {
        this.configurationService = new IntegrationGoalConfigurationService(player, configurationService);
        this.suggestionService = new IntegrationGoalSuggestionService(player, suggestionService);
        this.constructionService = new IntegrationGoalConstructionService(player, constructionService);
        this.actionService = new IntegrationGoalActionService(player, actionService);
        this.victoryService = new IntegrationGoalVictoryService(player, victoryService);
    }

    @Override
    public GoalConfigurationService configurationService() {
        return configurationService;
    }

    @Override
    public GoalConstructionService constructionService() {
        return constructionService;
    }

    @Override
    public GoalSuggestionService suggestionService() {
        return suggestionService;
    }

    @Override
    public GoalActionService actionService() {
        return actionService;
    }

    @Override
    public GoalVictoryService victoryService() {
        return victoryService;
    }

}
