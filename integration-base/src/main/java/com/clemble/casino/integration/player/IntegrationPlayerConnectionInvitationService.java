package com.clemble.casino.integration.player;

import com.clemble.casino.player.PlayerConnectionInvitation;
import com.clemble.casino.player.service.PlayerConnectionInvitationService;
import com.clemble.casino.server.connection.controller.PlayerConnectionInvitationController;

import java.util.List;

/**
 * Created by mavarazy on 11/12/14.
 */
public class IntegrationPlayerConnectionInvitationService implements PlayerConnectionInvitationService {

    final private String me;
    final private PlayerConnectionInvitationController invitationService;

    public IntegrationPlayerConnectionInvitationService(String player, PlayerConnectionInvitationController invitationService) {
        this.me = player;
        this.invitationService = invitationService;
    }

    @Override
    public List<PlayerConnectionInvitation> myInvitations() {
        return invitationService.myInvitations(me);
    }

    @Override
    public PlayerConnectionInvitation invite(String player) {
        return invitationService.invite(me, player);
    }

    @Override
    public PlayerConnectionInvitation reply(String player, boolean accept) {
        return invitationService.reply(me, player, accept);
    }

}
