package com.clemble.casino.integration.player;

import static org.junit.Assert.assertEquals;

import com.clemble.casino.error.ClembleErrorCode;
import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.test.util.ClembleCasinoExceptionMatcherFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.registration.PlayerCredential;
import com.clemble.test.random.ObjectGenerator;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class PlayerRegistrationServiceITest {

    @Autowired
    public Environment env;

    @Autowired
    public PlayerScenarios playerScenarios;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createAndLoginWithEmail(){
        // Step 1. Generating player credential & profile
        PlayerCredential credential = new PlayerCredential(
            RandomStringUtils.randomAlphabetic(10) + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile playerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null);
        // Step 2. Creating CasinoOperations with this credentials and Profile
        ClembleCasinoOperations A = playerScenarios.register(credential, playerProfile);

        // Step 3. Creating CasinoOperations by just login
        ClembleCasinoOperations emailA = playerScenarios.login(credential);
        // Step 4. Checking they are the same
        assertEquals(A.getPlayer(), emailA.getPlayer());
        assertEquals(A.profileOperations().myProfile(), emailA.profileOperations().myProfile());
//        assertEquals(A.presenceOperations().myPresence(), emailA.presenceOperations().myPresence());
        assertEquals(A.accountService().myAccount(), emailA.accountService().myAccount());
    }

    @Test
    public void createAndLoginWithNick(){
        // Step 1. Generating player credential & profile
        PlayerCredential credential = new PlayerCredential(
            RandomStringUtils.randomAlphabetic(10) + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile playerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null);
        // Step 2. Creating CasinoOperations with this credentials and Profile
        ClembleCasinoOperations A = playerScenarios.register(credential, playerProfile);

        // Step 3. Creating CasinoOperations by just login
        ClembleCasinoOperations nickA = playerScenarios.login(credential);
        // Step 4. Checking they are the same
        assertEquals(A.getPlayer(), nickA.getPlayer());
        assertEquals(A.profileOperations().myProfile(), nickA.profileOperations().myProfile());
//        assertEquals(A.presenceOperations().myPresence(), nickA.presenceOperations().myPresence());
        assertEquals(A.accountService().myAccount(), nickA.accountService().myAccount());
    }

    @Test
    @IfProfileValue(name = SpringConfiguration.INTEGRATION_TEST)
    public void createWithoutNick(){
        String nick = RandomStringUtils.randomAlphabetic(10);
        // Step 1. Generating player credential & profile
        PlayerCredential credential = new PlayerCredential(
            nick + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile playerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setNickName(null)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null);
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleErrorCode.NickMustNotBeNull));
        // Step 2. Creating CasinoOperations with this credentials and Profile
        ClembleCasinoOperations origA = playerScenarios.register(credential, playerProfile);
        // Step 3. Checking nick matches
        assertEquals(nick, origA.profileOperations().myProfile().getNickName());
    }

    @Test
    @IfProfileValue(name = SpringConfiguration.INTEGRATION_TEST)
    public void createWithTooShortNick(){
        Assume.assumeTrue(Arrays.asList(env.getActiveProfiles()).contains(SpringConfiguration.INTEGRATION_TEST));

        String nick = RandomStringUtils.randomAlphabetic(10);
        // Step 1. Generating player credential & profile
        PlayerCredential credential = new PlayerCredential(
            nick + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile playerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setNickName(null)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null)
                .setNickName(RandomStringUtils.randomAlphabetic(3));
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleErrorCode.NickTooShort));
        // Step 2. Creating CasinoOperations with this credentials and Profile
        ClembleCasinoOperations origA = playerScenarios.register(credential, playerProfile);
        // Step 3. Checking nick matches
        assertEquals(nick, origA.profileOperations().myProfile().getNickName());
    }

    @Test
    @IfProfileValue(name = SpringConfiguration.INTEGRATION_TEST)
    public void createWithTooLongNick(){
        String nick = RandomStringUtils.randomAlphabetic(10);
        // Step 1. Generating player credential & profile
        PlayerCredential credential = new PlayerCredential(
            nick + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile playerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setNickName(null)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null)
                .setNickName(RandomStringUtils.random(128));
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleErrorCode.NickTooLong));
        // Step 2. Creating CasinoOperations with this credentials and Profile
        ClembleCasinoOperations origA = playerScenarios.register(credential, playerProfile);
        // Step 3. Checking nick matches
        assertEquals(nick, origA.profileOperations().myProfile().getNickName());
    }

    @Test
    public void createDuplicateNick(){
        // Step 1. Generating player credential & profile
        PlayerCredential Acredential = new PlayerCredential(
            RandomStringUtils.randomAlphabetic(10) + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile AplayerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null);
        // Step 2. Creating CasinoOperations with this credentials and Profile
        ClembleCasinoOperations A = playerScenarios.register(Acredential, AplayerProfile);

        // Step 3. Generating player credential & profile
        PlayerCredential Bcredential = new PlayerCredential(
            RandomStringUtils.randomAlphabetic(10) + "@gmail.com",
            RandomStringUtils.random(10));
        PlayerProfile BplayerProfile = ObjectGenerator.generate(PlayerProfile.class)
                .setBirthDate(new DateTime(0))
                .setSocialConnections(null)
                .setNickName(AplayerProfile.getNickName());
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleErrorCode.NickOccupied));
        // Step 4. Creating CasinoOperations with this credentials and Profile
        playerScenarios.register(Bcredential, BplayerProfile);
    }

}
