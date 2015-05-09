package com.clemble.casino.integration.spring;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import com.clemble.casino.goal.configuration.controller.GoalConfigurationController;
import com.clemble.casino.goal.construction.controller.GoalConstructionController;
import com.clemble.casino.goal.controller.GoalActionController;
import com.clemble.casino.goal.controller.GoalVictoryController;
import com.clemble.casino.goal.suggestion.controller.GoalSuggestionController;
import com.clemble.casino.integration.goal.IntegrationGoalOperationsFactory;
import com.clemble.casino.integration.player.IntegrationClembleCasinoRegistrationOperations;
import com.clemble.casino.integration.player.IntegrationClembleCasinoRegistrationOperationsWrapper;
import com.clemble.casino.registration.service.PlayerSocialRegistrationService;
import com.clemble.casino.server.connection.controller.PlayerConnectionController;
import com.clemble.casino.server.connection.controller.PlayerFriendInvitationController;
import com.clemble.casino.server.email.controller.PlayerEmailController;
import com.clemble.casino.server.notification.controller.PlayerNotificationController;
import com.clemble.casino.server.payment.controller.PaymentTransactionController;
import com.clemble.casino.server.payment.controller.PlayerAccountController;
import com.clemble.casino.server.post.controller.PlayerFeedController;
import com.clemble.casino.server.profile.controller.PlayerImageController;
import com.clemble.casino.server.profile.controller.PlayerProfileController;
import com.clemble.casino.server.registration.controller.PlayerPasswordController;
import com.clemble.casino.server.registration.controller.PlayerRegistrationController;
import com.clemble.server.tag.controller.PlayerTagController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth.common.signature.RSAKeySecret;
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.integration.event.EventListenerOperationsFactory;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.game.construction.SimplePlayerScenarios;
import com.clemble.test.random.AbstractValueGenerator;
import com.clemble.test.random.ObjectGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import(value = {BaseTestSpringConfiguration.Test.class})
public class BaseTestSpringConfiguration implements TestSpringConfiguration {

    @Autowired
    public PlayerScenarios playerOperations;

    @PostConstruct
    public void initialize() {
        ObjectGenerator.register(RSAKeySecret.class, new AbstractValueGenerator<RSAKeySecret>() {
            @Override
            public RSAKeySecret generate() {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(1024);
                KeyPair keyPair = generator.generateKeyPair();
                return new RSAKeySecret(keyPair.getPrivate(), keyPair.getPublic());
            } catch (NoSuchAlgorithmException algorithmException) {
                return null;
            }
            }
        });
    }

    @Bean
    public PlayerScenarios playerScenarios(ClembleCasinoRegistrationOperations registrationOperations) {
        return new SimplePlayerScenarios(registrationOperations);
    }

    @Configuration
    @Profile({TEST, DEFAULT})
    public static class Test {

        @Bean
        public EventListenerOperationsFactory playerListenerOperations() {
            return new EventListenerOperationsFactory.RabbitEventListenerServiceFactory();
        }

        @Bean
        public IntegrationGoalOperationsFactory goalOperationsFactory(
            GoalConfigurationController configurationService,
            GoalSuggestionController suggestionService,
            GoalConstructionController constructionService,
            GoalActionController actionServiceController,
            GoalVictoryController victoryServiceController) {
            return new IntegrationGoalOperationsFactory(configurationService,
                suggestionService,
                constructionService,
                actionServiceController,
                victoryServiceController);
        }

        @Bean
        public ClembleCasinoRegistrationOperations registrationOperations(
            @Value("${clemble.host}") String host,
            ObjectMapper objectMapper,
            EventListenerOperationsFactory listenerOperations,
            PlayerRegistrationController registrationController,
            PlayerSocialRegistrationService socialRegistrationController,
            PlayerProfileController profileOperations,
            PlayerImageController imageService,
            @Qualifier("playerConnectionController") PlayerConnectionController connectionService,
            PlayerFriendInvitationController invitationService,
            @Qualifier("playerAccountController") PlayerAccountController accountOperations,
            PaymentTransactionController paymentTransactionService,
            IntegrationGoalOperationsFactory goalOperationsFactory,
            PlayerNotificationController notificationServiceController,
            PlayerFeedController feedServiceController,
            PlayerPasswordController resetServiceController,
            PlayerEmailController emailServiceController,
            PlayerTagController tagServiceController) {
            ClembleCasinoRegistrationOperations registrationOperations = new IntegrationClembleCasinoRegistrationOperations(
                host,
                objectMapper,
                listenerOperations,
                registrationController,
                socialRegistrationController,
                profileOperations,
                imageService,
                connectionService,
                invitationService,
                accountOperations,
                paymentTransactionService,
                goalOperationsFactory,
                notificationServiceController,
                feedServiceController,
                resetServiceController,
                emailServiceController,
                tagServiceController);
            return new IntegrationClembleCasinoRegistrationOperationsWrapper(registrationOperations);
        }
    }
}
