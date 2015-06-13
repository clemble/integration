package com.clemble.casino.integration.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.player.event.PlayerConnectionAddEvent;
import com.clemble.casino.social.SocialProvider;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.integration.event.EventAccumulator;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.social.SocialAccessGrant;
import com.clemble.casino.social.SocialConnectionData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.facebooktestjavaapi.testuser.FacebookTestUserAccount;
import com.jayway.facebooktestjavaapi.testuser.FacebookTestUserStore;
import com.jayway.facebooktestjavaapi.testuser.impl.HttpClientFacebookTestUserStore;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
@ClembleIntegrationTest
public class FacebookRegistrationITest {

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public PlayerScenarios playerScenarios;

    private FacebookTestUserStore facebookStore;

    @PostConstruct
    public void init(){
        try {
            facebookStore = new HttpClientFacebookTestUserStore("262763360540886", "beb651a120e8bf7252ba4e4be4f46437");
        } catch (Throwable throwable) {
            Assume.assumeTrue("Connection to google is missing", false);
        }
    }

    @Test
    public void testSocialDataRegistration() throws IOException {
        // Step 1. Generating Facebook test user
        FacebookTestUserStore facebookStore = new HttpClientFacebookTestUserStore("262763360540886", "beb651a120e8bf7252ba4e4be4f46437");
        FacebookTestUserAccount fbAccount = facebookStore.createTestUser(true, "email");
        JsonNode fb = objectMapper.readValue(fbAccount.getUserDetails(), JsonNode.class);
        assertNotNull(fbAccount);
        assertNotNull(fb);
        // Step 2. Converting to SocialConnectionData
        SocialConnectionData connectionData = new SocialConnectionData(SocialProvider.facebook, fb.get("id").asText(), fbAccount.accessToken(), "", "",
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        ClembleCasinoOperations casinoOperations = playerScenarios.createPlayer(connectionData);
        assertNotNull(casinoOperations);
        PlayerProfile profile = casinoOperations.profileOperations().myProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof PlayerProfile);
        PlayerProfile socialProfile = profile;
        assertTrue(socialProfile.getSocialConnections().contains(new ConnectionKey("facebook", fb.get("id").asText())));
    }

    @Test
    public void testSocialDataLogin() throws IOException {
        // Step 1. Generating Facebook test user
        FacebookTestUserStore facebookStore = new HttpClientFacebookTestUserStore("262763360540886", "beb651a120e8bf7252ba4e4be4f46437");
        FacebookTestUserAccount fbAccount = facebookStore.createTestUser(true, "email");
        JsonNode fb = objectMapper.readValue(fbAccount.getUserDetails(), JsonNode.class);
        assertNotNull(fbAccount);
        assertNotNull(fb);
        // Step 2. Converting to SocialConnectionData
        SocialConnectionData connectionData = new SocialConnectionData(SocialProvider.facebook, fb.get("id").asText(), fbAccount.accessToken(), "", "",
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        ClembleCasinoOperations A = playerScenarios.createPlayer(connectionData);
        assertNotNull(A);
        PlayerProfile profile = A.profileOperations().myProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof PlayerProfile);
        PlayerProfile socialProfile = profile;
        assertTrue(socialProfile.getSocialConnections().contains(new ConnectionKey("facebook", fb.get("id").asText())));
        // Step 3. Registering again with the same SocialAccessGrant
        ClembleCasinoOperations B = playerScenarios.createPlayer(connectionData);
        assertNotEquals(B, A);
        assertEquals(B.getPlayer(), A.getPlayer());
    }

    @Test
    public void testSocialGrantRegistration() throws IOException {
        // Step 1. Generating Facebook test user
        SocialAccessGrant accessGrant = randomSocialGrant();
        // Step 2. Converting to SocialConnectionData
        ClembleCasinoOperations casinoOperations = playerScenarios.createPlayer(accessGrant);
        assertNotNull(casinoOperations);
        PlayerProfile profile = casinoOperations.profileOperations().myProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof PlayerProfile);
        PlayerProfile socialProfile = profile;
        assertTrue(socialProfile.getSocialConnections().contains(new ConnectionKey("facebook", profile.getSocialConnection(SocialProvider.facebook).getProviderUserId())));
    }

    @Test
    public void testSocialGrantLogin() throws IOException {
        SocialAccessGrant accessGrant = randomSocialGrant();
        // Step 1. Converting to SocialConnectionData
        ClembleCasinoOperations A = playerScenarios.createPlayer(accessGrant);
        assertNotNull(A);
        PlayerProfile profile = A.profileOperations().myProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof PlayerProfile);
        PlayerProfile socialProfile = profile;
        assertTrue(socialProfile.getSocialConnections().contains(new ConnectionKey("facebook", profile.getSocialConnection(SocialProvider.facebook).getProviderUserId())));
        // Step 3. Registering again with the same SocialAccessGrant
        ClembleCasinoOperations B = playerScenarios.createPlayer(accessGrant);
        assertNotEquals(B, A);
        assertEquals(B.getPlayer(), A.getPlayer());
    }

    @Test
    public void testAutoDiscovery() throws IOException, InterruptedException {
        // Step 1. Generating Facebook test user
        FacebookTestUserAccount fbAccoA = facebookStore.createTestUser(true, "email");
        SocialAccessGrant grantA = new SocialAccessGrant(SocialProvider.facebook, fbAccoA.accessToken());
        ClembleCasinoOperations A = playerScenarios.createPlayer(grantA);
        // Step 2. Adding discovery event listener
        EventAccumulator<PlayerConnectionAddEvent> discoveryListener = new EventAccumulator<>();
        A.listenerOperations().subscribe(new EventTypeSelector(PlayerConnectionAddEvent.class), discoveryListener);
        // Step 3. Generating new user B, who is a friend of user A
        FacebookTestUserAccount fbAccoB = facebookStore.createTestUser(true, "email");
        fbAccoA.makeFriends(fbAccoB);
        fbAccoA.getFriends();
        SocialAccessGrant grantB = new SocialAccessGrant(SocialProvider.facebook, fbAccoB.accessToken());
        playerScenarios.createPlayer(grantB);
        // Step 3. Checking connection were mapped internally
        assertNotNull(discoveryListener.waitFor(new EventTypeSelector(PlayerConnectionAddEvent.class)));
    }

    private SocialAccessGrant randomSocialGrant() throws IOException{
        // Step 1. Generating Facebook test user
        FacebookTestUserAccount fbAccount = facebookStore.createTestUser(true, "email");
        assertNotNull(fbAccount);
        // Step 2. Converting to SocialConnectionData
        return new SocialAccessGrant(SocialProvider.facebook, fbAccount.accessToken());
    }

}
