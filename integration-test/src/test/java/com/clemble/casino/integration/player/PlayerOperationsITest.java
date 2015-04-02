package com.clemble.casino.integration.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.clemble.casino.integration.ClembleIntegrationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.player.PlayerGender;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.security.ClembleConsumerDetails;
import com.clemble.casino.registration.PlayerCredential;
import com.clemble.casino.registration.PlayerRegistrationRequest;
import com.clemble.test.random.ObjectGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class PlayerOperationsITest {

    @Autowired
    public PlayerScenarios playerOperations;

    @Test
    public void createRandomPlayer() {
        ClembleCasinoOperations player = playerOperations.createPlayer();

        assertNotNull(player);
    }

    @Test
    public void createPlayerUsingProfileTest() {
        String nickName = RandomStringUtils.randomAlphabetic(9);

        PlayerProfile profile = new PlayerProfile().
            setFirstName("Anton").
            setLastName("Oparin").
            setGender(PlayerGender.M).
            setNickName(nickName);

        ClembleCasinoOperations player = playerOperations.createPlayer(profile);

        assertNotNull(player);
        PlayerProfile playerProfile = player.profileOperations().myProfile();
        assertEquals(playerProfile.getFirstName(), "Anton");
        assertEquals(playerProfile.getLastName(), "Oparin");
        assertEquals(playerProfile.getGender(), PlayerGender.M);
        assertEquals(playerProfile.getNickName(), nickName);
    }

    @Test
    public void createPlayerUsingRegistrationRequest() {
        String nickName = RandomStringUtils.randomAlphabetic(9);

        PlayerProfile profile = new PlayerProfile().
                setFirstName("Anton").
                setLastName("Oparin").
                setGender(PlayerGender.M).
                setNickName(nickName);

        PlayerCredential playerCredential = new PlayerCredential(RandomStringUtils.randomAlphabetic(10) + "@gmail.com", "23443545");

        PlayerRegistrationRequest registrationRequest = PlayerRegistrationRequest.create(playerCredential, profile);

        ClembleCasinoOperations player = playerOperations.createPlayer(registrationRequest);

        assertNotNull(player);
        PlayerProfile playerProfile = player.profileOperations().myProfile();
        assertEquals(playerProfile.getFirstName(), "Anton");
        assertEquals(playerProfile.getLastName(), "Oparin");
        assertEquals(playerProfile.getGender(), PlayerGender.M);
        assertEquals(playerProfile.getNickName(), nickName);

        try {
            System.out.println(new ObjectMapper().writeValueAsString(registrationRequest));
        } catch (Throwable error) {
            throw new RuntimeException(error);
        }
    }

    /*
    @Test
    public void loginExistingUser() {
        PlayerProfile profile = new NativePlayerProfile().setCategory(PlayerCategory.Amateur).setFirstName("Anton").setLastName("Oparin").setGender(PlayerGender.M)
                .setNickName("mavarazy");

        ClembleCasinoOperations player = playerOperations.createPlayer(profile);

        PlayerCredential loginCredential = new PlayerCredential()
            .setEmail(player.getCredential().getEmail())
            .setPassword(player.getCredential().getPassword());
        ClembleConsumerDetails consumerDetails = new ClembleConsumerDetails(UUID.randomUUID().toString(), "IT", ObjectGenerator.generate(RSAKeySecret.class), null, new ClientDetails("IT"));;

        ClembleCasinoOperations loginPlayer = playerOperations.login(new PlayerLoginRequest(consumerDetails, loginCredential));

        assertEquals(loginPlayer.getPlayer(), player.getPlayer());

        assertEquals(loginPlayer.getCredential().getEmail(), player.getCredential().getEmail());
        assertEquals(loginPlayer.getCredential().getPassword(), player.getCredential().getPassword());

        // TODO put back assertEquals(loginPlayer.getIdentity().getSecret(), playerIdentity.getSecret());
        assertEquals(loginPlayer.getPlayer(), player.getPlayer());

    }
    */
}
