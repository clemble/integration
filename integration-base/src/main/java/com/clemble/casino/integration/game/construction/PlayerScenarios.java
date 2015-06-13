package com.clemble.casino.integration.game.construction;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.social.SocialAccessGrant;
import com.clemble.casino.social.SocialConnectionData;
import com.clemble.casino.registration.PlayerRegistrationRequest;

public interface PlayerScenarios extends ClembleCasinoRegistrationOperations {

    ClembleCasinoOperations createPlayer();

    ClembleCasinoOperations createPlayer(PlayerProfile playerProfile);

    ClembleCasinoOperations createPlayer(SocialAccessGrant socialConnectionData);

    ClembleCasinoOperations createPlayer(SocialConnectionData socialConnectionData);

    ClembleCasinoOperations createPlayer(PlayerRegistrationRequest playerRegistrationRequest);

}
