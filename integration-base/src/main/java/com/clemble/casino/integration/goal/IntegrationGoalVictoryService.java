package com.clemble.casino.integration.goal;

import com.clemble.casino.goal.controller.GoalVictoryController;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.service.GoalVictoryService;

import java.util.List;

/**
 * Created by mavarazy on 3/14/15.
 */
public class IntegrationGoalVictoryService implements GoalVictoryService {

    final private String player;
    final private GoalVictoryController victoryService;

    public IntegrationGoalVictoryService(String player, GoalVictoryController victoryService) {
        this.player = player;
        this.victoryService = victoryService;
    }

    @Override
    public List<GoalState> listMy() {
        return victoryService.listMy(player);
    }

    @Override
    public Integer countMy() {
        return victoryService.countMy(player);
    }

    @Override
    public List<GoalState> list(String player) {
        return victoryService.list(player);
    }

    @Override
    public Integer count(String player) {
        return victoryService.count(player);
    }

}
