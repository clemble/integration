package com.clemble.casino.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.integration.event.EventListenerOperationsFactory;
import com.clemble.casino.integration.goal.IntegrationGoalOperationsFactory;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.registration.*;
import com.clemble.casino.registration.service.PlayerSocialRegistrationService;
import com.clemble.casino.server.connection.controller.PlayerFriendInvitationController;
import com.clemble.casino.server.email.controller.PlayerEmailController;
import com.clemble.casino.server.post.controller.PlayerFeedController;
import com.clemble.casino.server.registration.controller.PlayerPasswordController;
import com.clemble.casino.server.registration.controller.PlayerRegistrationController;
import com.clemble.casino.social.SocialAccessGrant;
import com.clemble.casino.social.SocialConnectionData;
import com.clemble.casino.server.connection.controller.PlayerConnectionController;
import com.clemble.casino.server.payment.controller.PaymentTransactionController;
import com.clemble.casino.server.payment.controller.PlayerAccountController;
import com.clemble.casino.server.profile.controller.PlayerImageController;
import com.clemble.casino.server.profile.controller.PlayerProfileController;
import com.clemble.server.tag.controller.PlayerTagController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.clemble.casino.server.notification.controller.PlayerNotificationController;
import org.springframework.mock.web.MockHttpServletResponse;

public class IntegrationClembleCasinoRegistrationOperations implements ClembleCasinoRegistrationOperations {

    final private String host;
    final private ObjectMapper objectMapper;
    final private PlayerRegistrationController registrationController;
    final private PlayerSocialRegistrationService socialRegistrationController;
    final private PlayerProfileController profileOperations;
    final private PlayerImageController imageService;
    final private PlayerConnectionController connectionService;
    final private PlayerFriendInvitationController invitationService;
    final private PlayerAccountController paymentService;
    final private PaymentTransactionController paymentTransactionService;
    final private EventListenerOperationsFactory listenerOperations;
    final private IntegrationGoalOperationsFactory goalOperationsFactory;
    final private PlayerNotificationController notificationServiceController;
    final private PlayerFeedController feedServiceController;
    final private PlayerPasswordController passwordResetService;
    final private PlayerEmailController emailServiceController;
    final private PlayerTagController tagServiceController;

    public IntegrationClembleCasinoRegistrationOperations(
        String host,
        ObjectMapper objectMapper,
        EventListenerOperationsFactory listenerOperations,
        PlayerRegistrationController registrationController,
        PlayerSocialRegistrationService socialRegistrationController,
        PlayerProfileController profileOperations,
        PlayerImageController imageService,
        PlayerConnectionController connectionService,
        PlayerFriendInvitationController invitationService,
        PlayerAccountController accountOperations,
        PaymentTransactionController paymentTransactionService,
        IntegrationGoalOperationsFactory goalOperationsFactory,
        PlayerNotificationController notificationServiceController,
        PlayerFeedController feedServiceController,
        PlayerPasswordController passwordResetService,
        PlayerEmailController emailServiceController,
        PlayerTagController tagServiceController) {
        this.host = checkNotNull(host);
        this.objectMapper = checkNotNull(objectMapper);
        this.registrationController = checkNotNull(registrationController);
        this.socialRegistrationController = checkNotNull(socialRegistrationController);
        this.listenerOperations = checkNotNull(listenerOperations);
        this.profileOperations = checkNotNull(profileOperations);
        this.imageService = checkNotNull(imageService);
        this.connectionService = checkNotNull(connectionService);
        this.invitationService = checkNotNull(invitationService);
        this.paymentService = checkNotNull(accountOperations);
        this.paymentTransactionService = checkNotNull(paymentTransactionService);
        this.goalOperationsFactory = checkNotNull(goalOperationsFactory);
        this.notificationServiceController = checkNotNull(notificationServiceController);
        this.feedServiceController = feedServiceController;
        this.passwordResetService = passwordResetService;
        this.emailServiceController = emailServiceController;
        this.tagServiceController = tagServiceController;
    }

    @Override
    public ClembleCasinoOperations login(PlayerCredential playerCredentials) {
        String player = registrationController.login(playerCredentials);
        return create(player);
    }

    @Override
    public ClembleCasinoOperations register(PlayerCredential playerCredential, PlayerProfile playerProfile) {
        PlayerRegistrationRequest loginRequest = PlayerRegistrationRequest.create(playerCredential, playerProfile);
        String player = registrationController.register(loginRequest);
        return create(player);
    }

    @Override
    public ClembleCasinoOperations register(PlayerCredential playerCredential, SocialConnectionData socialConnectionData) {
        PlayerSocialRegistrationRequest loginRequest = new PlayerSocialRegistrationRequest(playerCredential, socialConnectionData);
        String player = socialRegistrationController.register(loginRequest);
        return create(player);
    }

    @Override
    public ClembleCasinoOperations register(PlayerCredential playerCredential, SocialAccessGrant accessGrant) {
        PlayerSocialGrantRegistrationRequest loginRequest = new PlayerSocialGrantRegistrationRequest(playerCredential, accessGrant);
        String token = socialRegistrationController.register(loginRequest);
        return create(token);
    }

    private ClembleCasinoOperations create(String player) {
        return new IntegrationClembleCasinoOperations(
            host,
            objectMapper,
            player,
            profileOperations,
            imageService,
            connectionService,
            invitationService,
            paymentService,
            paymentTransactionService,
            listenerOperations,
            goalOperationsFactory.construct(player),
            notificationServiceController,
            feedServiceController,
            passwordResetService,
            emailServiceController,
            tagServiceController
        );
    }

}
