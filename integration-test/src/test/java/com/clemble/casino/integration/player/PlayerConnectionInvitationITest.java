package com.clemble.casino.integration.player;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.player.PlayerConnection;
import com.clemble.casino.player.PlayerConnectionInvitation;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by mavarazy on 11/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class PlayerConnectionInvitationITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testInvitationAdded() {
        // Step 1. Creating player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();
        // Step 2. Requesting A to connect to B
        A.friendInvitationService().invite(B.getPlayer());
        // Step 3. Checking B received invitation
        List<PlayerConnectionInvitation> pending = B.friendInvitationService().myInvitations();
        Assert.assertFalse(pending.isEmpty());
        Assert.assertEquals(pending.iterator().next().getPlayer(), A.getPlayer());
    }

    @Test
    public void testInvitationAccept() {
        // Step 1. Creating player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();
        // Step 2. Requesting A to connect to B
        A.friendInvitationService().invite(B.getPlayer());
        // Step 3. Checking B received invitation
        List<PlayerConnectionInvitation> pending = B.friendInvitationService().myInvitations();
        Assert.assertFalse(pending.isEmpty());
        Assert.assertEquals(pending.iterator().next().getPlayer(), A.getPlayer());
        // Step 4. Accepting invitation
        B.friendInvitationService().reply(A.getPlayer(), true);
        // Step 4.1 Checking both players now connected
        PlayerConnection connectionA = new PlayerConnection(A.getPlayer(), A.profileOperations().myProfile().getFirstName());
        PlayerConnection connectionB = new PlayerConnection(B.getPlayer(), B.profileOperations().myProfile().getFirstName());
        Assert.assertEquals(B.connectionOperations().myConnections(), ImmutableSet.of(connectionA));
        Assert.assertEquals(A.connectionOperations().myConnections(), ImmutableSet.of(connectionB));
    }

    @Test
    public void testInvitationDecline() {
        // Step 1. Creating player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();
        // Step 2. Requesting A to connect to B
        A.friendInvitationService().invite(B.getPlayer());
        // Step 3. Checking B received invitation
        List<PlayerConnectionInvitation> pending = B.friendInvitationService().myInvitations();
        Assert.assertFalse(pending.isEmpty());
        Assert.assertEquals(pending.iterator().next().getPlayer(), A.getPlayer());
        // Step 4. Accepting invitation
        B.friendInvitationService().reply(A.getPlayer(), false);
        // Step 4.1 Checking both players now connected
        Assert.assertEquals(B.connectionOperations().myConnections().isEmpty(), true);
        Assert.assertEquals(A.connectionOperations().myConnections().isEmpty(), true);
    }

    @Test
    public void testInvitationCompensate() {
        // Step 1. Creating player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();
        // Step 2. Requesting A to connect to B
        A.friendInvitationService().invite(B.getPlayer());
        // Step 3. Requesting B to connect to A
        B.friendInvitationService().invite(A.getPlayer());
        // Step 4.1 Checking both players now connected
        PlayerConnection connectionA = new PlayerConnection(A.getPlayer(), A.profileOperations().myProfile().getFirstName());
        PlayerConnection connectionB = new PlayerConnection(B.getPlayer(), B.profileOperations().myProfile().getFirstName());
        Assert.assertEquals(B.connectionOperations().myConnections(), ImmutableSet.of(connectionA));
        Assert.assertEquals(A.connectionOperations().myConnections(), ImmutableSet.of(connectionB));
    }

}
