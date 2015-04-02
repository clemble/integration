package com.clemble.casino.integration.goal;

import com.clemble.casino.goal.configuration.controller.GoalConfigurationServiceController;
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
    final private GoalConfigurationServiceController configurationService;

    public IntegrationGoalConfigurationService(String player, GoalConfigurationServiceController configurationService) {
        this.player = player;
        this.configurationService = configurationService;
    }

    @Override
    public GoalConfigurationChoices getChoises() {
        return configurationService.getChoises();
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
