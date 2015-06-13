package com.clemble.casino.integration.player;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import java.util.Set;

import com.clemble.casino.player.PlayerConnection;
import com.clemble.casino.player.service.PlayerConnectionService;
import com.clemble.casino.server.connection.controller.PlayerConnectionController;

public class IntegrationPlayerConnectionService implements PlayerConnectionService {

    /**
     * Generated 30/12/13
     */
    private static final long serialVersionUID = 4966541707719576636L;

    final private String player;
    final private PlayerConnectionController connectionService;

    public IntegrationPlayerConnectionService(String player, PlayerConnectionController connectionService) {
        this.player = checkNotNull(player);
        this.connectionService = checkNotNull(connectionService);
    }

    @Override
    public Set<PlayerConnection> myConnections() {
        return connectionService.myConnections(player);
    }

    @Override
    public Set<PlayerConnection> getConnections(String player) {
        return connectionService.getConnections(player);
    }

}
