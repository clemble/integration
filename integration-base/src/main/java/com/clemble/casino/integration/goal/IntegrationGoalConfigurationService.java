package com.clemble.casino.integration.goal;

import com.clemble.casino.goal.controller.GoalConfigurationController;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfigurationChoices;
import com.clemble.casino.goal.lifecycle.configuration.IntervalGoalConfigurationBuilder;
import com.clemble.casino.goal.lifecycle.configuration.service.GoalConfigurationService;
import com.clemble.casino.utils.CollectionUtils;

import java.util.List;

/**
 * Created by mavarazy on 9/15/14.
 */
public class IntegrationGoalConfigurationService implements GoalConfigurationService {

    final private String player;
    final private GoalConfigurationController configurationService;

    public IntegrationGoalConfigurationService(String player, GoalConfigurationController configurationService) {
        this.player = player;
        this.configurationService = configurationService;
    }

    @Override
    public GoalConfigurationChoices getChoices() {
        return configurationService.getChoices();
    }

    @Override
    public List<GoalConfiguration> getConfigurations() {
        return CollectionUtils.immutableList(configurationService.getConfigurations());
    }

    @Override
    public IntervalGoalConfigurationBuilder getIntervalBuilder() {
        return null;
    }

}
