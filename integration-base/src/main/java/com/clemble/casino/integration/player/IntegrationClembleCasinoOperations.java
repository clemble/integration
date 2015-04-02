package com.clemble.casino.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.integration.IntegrationPlayerTagServiceController;
import com.clemble.casino.integration.payment.IntegrationPlayerAccountService;
import com.clemble.casino.payment.service.PaymentTransactionOperations;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.player.service.*;
import com.clemble.casino.registration.service.PlayerPasswordResetService;
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
import com.clemble.casino.tag.service.PlayerTagService;
import com.clemble.server.tag.controller.PlayerTagServiceController;
import org.springframework.web.client.RestTemplate;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.event.EventListenerOperations;
import com.clemble.casino.integration.payment.IntegrationPaymentTransactionService;
import com.clemble.casino.integration.event.EventListenerOperationsFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IntegrationClembleCasinoOperations implements ClembleCasinoOperations {

    /**
     * Generated
     */
    private static final long serialVersionUID = -4160641502466429770L;

    final private String player;
    final private EventListenerOperations listenerOperations;

    final private PlayerProfileService profileOperations;
    final private PlayerImageService imageOperations;
    final private PlayerConnectionService connectionOperations;
    final private PlayerFriendInvitationService friendInvitationService;
    final private PlayerFeedService feedService;
    final private PlayerNotificationService notificationService;
    final private PlayerAccountService accountService;
    final private PaymentTransactionOperations paymentTransactionOperations;
    final private GoalOperations goalOperations;
    final private PlayerPasswordResetService passwordResetService;
    final private PlayerEmailService emailService;
    final private PlayerTagService tagService;

    public IntegrationClembleCasinoOperations(
        final String host,
        final ObjectMapper objectMapper,
        final String player,
        final PlayerProfileServiceController playerProfileService,
        final PlayerImageServiceController imageService,
        final PlayerConnectionServiceController playerConnectionService,
        final PlayerFriendInvitationServiceController invitationService,
        final PlayerAccountServiceController accountServiceController,
        final PaymentTransactionServiceController paymentTransactionService,
        final EventListenerOperationsFactory listenerOperationsFactory,
        final GoalOperations goalOperations,
        final PlayerNotificationServiceController notificationServiceController,
        final PlayerFeedServiceController feedServiceController,
        final PlayerPasswordResetServiceController passwordResetServiceController,
        final PlayerEmailServiceController emailService,
        final PlayerTagServiceController tagServiceController
    ) {
        this.player = player;
        this.listenerOperations = listenerOperationsFactory.construct(player, host, objectMapper);

        this.profileOperations = new IntegrationPlayerProfileService(player, playerProfileService);
        this.notificationService = new IntegrationPlayerNotificationService(player, notificationServiceController);
        this.imageOperations = new IntegrationPlayerImageService(player, imageService);
        this.connectionOperations = new IntegrationPlayerConnectionService(player, playerConnectionService);
        this.friendInvitationService = new IntegrationPlayerFriendInvitationService(player, invitationService);
        this.paymentTransactionOperations = new PaymentTransactionOperations(new IntegrationPaymentTransactionService(player, paymentTransactionService));
        this.accountService = new IntegrationPlayerAccountService(player, accountServiceController);

        this.feedService = new IntegrationPlayerFeedService(player, checkNotNull(feedServiceController));

        this.goalOperations = goalOperations;
        this.passwordResetService = passwordResetServiceController;
        this.emailService = new IntegrationPlayerEmailService(player, emailService);
        this.tagService = new IntegrationPlayerTagServiceController(player, tagServiceController);
    }

    @Override
    public PlayerProfileService profileOperations() {
        return profileOperations;
    }

    @Override
    public PlayerImageService imageOperations() {
        return imageOperations;
    }

    @Override
    public PlayerConnectionService connectionOperations() {
        return connectionOperations;
    }

    @Override
    public PlayerFriendInvitationService friendInvitationService() {
        return friendInvitationService;
    }

    @Override
    public PlayerNotificationService notificationService() {
        return notificationService;
    }

    @Override
    public PlayerFeedService feedService() {
        return feedService;
    }

    @Override
    public PlayerAccountService accountService() {
        return accountService;
    }

    @Override
    public PaymentTransactionOperations paymentOperations() {
        return paymentTransactionOperations;
    }

    @Override
    public GoalOperations goalOperations() {
        return goalOperations;
    }

    @Override
    public PlayerPasswordResetService passwordResetService() {
        return passwordResetService;
    }

    @Override
    public PlayerEmailService emailService() {
        return emailService;
    }

    @Override
    public PlayerTagService tagService() {
        return tagService;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public void close() {
        listenerOperations.close();
    }

    @Override
    public RestTemplate getRestTemplate() {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isAuthorized() {
        return true;
    }

    @Override
    public EventListenerOperations listenerOperations() {
        return listenerOperations;
    }

    @Override
    public void signOut() {
    }

}
