package com.clemble.casino.integration.spring;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import com.clemble.casino.goal.configuration.controller.GoalConfigurationServiceController;
import com.clemble.casino.goal.construction.controller.GoalConstructionServiceController;
import com.clemble.casino.goal.construction.controller.GoalInitiationServiceController;
import com.clemble.casino.goal.controller.GoalActionServiceController;
import com.clemble.casino.goal.controller.GoalRecordServiceController;
import com.clemble.casino.goal.controller.GoalVictoryServiceController;
import com.clemble.casino.goal.suggestion.controller.GoalSuggestionServiceController;
import com.clemble.casino.integration.goal.IntegrationGoalOperationsFactory;
import com.clemble.casino.integration.player.IntegrationClembleCasinoRegistrationOperations;
import com.clemble.casino.integration.player.IntegrationClembleCasinoRegistrationOperationsWrapper;
import com.clemble.casino.registration.service.PlayerSocialRegistrationService;
import com.clemble.casino.server.connection.controller.PlayerConnectionServiceController;
import com.clemble.casino.server.connection.controller.PlayerFriendInvitationServiceController;
import com.clemble.casino.server.email.controller.PlayerEmailServiceController;
import com.clemble.casino.server.notification.controller.PlayerNotificationServiceController;
import com.clemble.casino.server.payment.controller.PaymentTransactionServiceController;
import com.clemble.casino.server.payment.controller.PlayerAccountServiceController;
import com.clemble.casino.server.post.controller.PlayerFeedServiceController;
import com.clemble.casino.server.profile.controller.PlayerImageServiceController;
import com.clemble.casino.server.profile.controller.PlayerProfileServiceController;
import com.clemble.casino.server.registration.controller.PlayerPasswordResetServiceController;
import com.clemble.casino.server.registration.controller.PlayerRegistrationController;
import com.clemble.server.tag.controller.PlayerTagServiceController;
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
            GoalConfigurationServiceController configurationService,
            GoalInitiationServiceController initiationService,
            GoalSuggestionServiceController suggestionService,
            GoalConstructionServiceController constructionService,
            GoalActionServiceController actionServiceController,
            GoalRecordServiceController recordServiceController,
            GoalVictoryServiceController victoryServiceController) {
            return new IntegrationGoalOperationsFactory(configurationService,
                initiationService,
                suggestionService,
                constructionService,
                actionServiceController,
                recordServiceController,
                victoryServiceController);
        }

        @Bean
        public ClembleCasinoRegistrationOperations registrationOperations(
            @Value("${clemble.host}") String host,
            ObjectMapper objectMapper,
            EventListenerOperationsFactory listenerOperations,
            PlayerRegistrationController registrationController,
            PlayerSocialRegistrationService socialRegistrationController,
            PlayerProfileServiceController profileOperations,
            PlayerImageServiceController imageService,
            @Qualifier("playerConnectionController") PlayerConnectionServiceController connectionService,
            PlayerFriendInvitationServiceController invitationService,
            @Qualifier("playerAccountController") PlayerAccountServiceController accountOperations,
            PaymentTransactionServiceController paymentTransactionService,
            IntegrationGoalOperationsFactory goalOperationsFactory,
            PlayerNotificationServiceController notificationServiceController,
            PlayerFeedServiceController feedServiceController,
            PlayerPasswordResetServiceController resetServiceController,
            PlayerEmailServiceController emailServiceController,
            PlayerTagServiceController tagServiceController) {
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
