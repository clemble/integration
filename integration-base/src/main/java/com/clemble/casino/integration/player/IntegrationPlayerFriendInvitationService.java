package com.clemble.casino.integration.player;

import com.clemble.casino.player.Invitation;
import com.clemble.casino.player.service.PlayerFriendInvitationService;
import com.clemble.casino.server.connection.controller.PlayerFriendInvitationController;

import java.util.List;

/**
 * Created by mavarazy on 11/12/14.
 */
public class IntegrationPlayerFriendInvitationService implements PlayerFriendInvitationService {

    final private String me;
    final private PlayerFriendInvitationController invitationService;

    public IntegrationPlayerFriendInvitationService(String player, PlayerFriendInvitationController invitationService) {
        this.me = player;
        this.invitationService = invitationService;
    }

    @Override
    public List<Invitation> myInvitations() {
        return invitationService.myInvitations(me);
    }

    @Override
    public Invitation invite(Invitation player) {
        return invitationService.invite(me, player);
    }

    @Override
    public Invitation reply(String player, boolean accept) {
        return invitationService.reply(me, player, accept);
    }

}
