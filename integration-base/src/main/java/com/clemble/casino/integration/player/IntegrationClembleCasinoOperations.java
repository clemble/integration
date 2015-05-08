package com.clemble.casino.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.integration.IntegrationPlayerTagService;
import com.clemble.casino.integration.payment.IntegrationPlayerAccountService;
import com.clemble.casino.payment.service.PaymentTransactionOperations;
import com.clemble.casino.payment.service.PaymentTransactionService;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.player.service.*;
import com.clemble.casino.registration.service.PlayerPasswordService;
import com.clemble.casino.server.connection.controller.PlayerConnectionController;
import com.clemble.casino.server.connection.controller.PlayerFriendInvitationController;
import com.clemble.casino.server.email.controller.PlayerEmailServiceController;
import com.clemble.casino.server.notification.controller.PlayerNotificationController;
import com.clemble.casino.server.payment.controller.PaymentTransactionController;
import com.clemble.casino.server.payment.controller.PlayerAccountController;
import com.clemble.casino.server.post.controller.PlayerFeedController;
import com.clemble.casino.server.profile.controller.PlayerImageServiceController;
import com.clemble.casino.server.profile.controller.PlayerProfileController;
import com.clemble.casino.server.registration.controller.PlayerPasswordServiceController;
import com.clemble.casino.tag.service.PlayerTagService;
import com.clemble.server.tag.controller.PlayerTagController;
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
    final private PaymentTransactionService paymentTransactionOperations;
    final private GoalOperations goalOperations;
    final private PlayerPasswordService passwordResetService;
    final private PlayerEmailService emailService;
    final private PlayerTagService tagService;

    public IntegrationClembleCasinoOperations(
        final String host,
        final ObjectMapper objectMapper,
        final String player,
        final PlayerProfileController playerProfileService,
        final PlayerImageServiceController imageService,
        final PlayerConnectionController playerConnectionService,
        final PlayerFriendInvitationController invitationService,
        final PlayerAccountController accountServiceController,
        final PaymentTransactionController paymentTransactionService,
        final EventListenerOperationsFactory listenerOperationsFactory,
        final GoalOperations goalOperations,
        final PlayerNotificationController notificationServiceController,
        final PlayerFeedController feedServiceController,
        final PlayerPasswordServiceController passwordResetServiceController,
        final PlayerEmailServiceController emailService,
        final PlayerTagController tagServiceController
    ) {
        this.player = player;
        this.listenerOperations = listenerOperationsFactory.construct(player, host, objectMapper);

        this.profileOperations = new IntegrationPlayerProfileService(player, playerProfileService);
        this.notificationService = new IntegrationPlayerNotificationService(player, notificationServiceController);
        this.imageOperations = new IntegrationPlayerImageService(player, imageService);
        this.connectionOperations = new IntegrationPlayerConnectionService(player, playerConnectionService);
        this.friendInvitationService = new IntegrationPlayerFriendInvitationService(player, invitationService);
        this.paymentTransactionOperations = new IntegrationPaymentTransactionService(player, paymentTransactionService);
        this.accountService = new IntegrationPlayerAccountService(player, accountServiceController);

        this.feedService = new IntegrationPlayerFeedService(player, checkNotNull(feedServiceController));

        this.goalOperations = goalOperations;
        this.passwordResetService = passwordResetServiceController;
        this.emailService = new IntegrationPlayerEmailService(player, emailService);
        this.tagService = new IntegrationPlayerTagService(player, tagServiceController);
    }

    @Override
    public PlayerProfileService profileOperations() {
        return profileOperations;
    }

    @Override
    public PlayerImageService imageService() {
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
    public PaymentTransactionService paymentService() {
        return paymentTransactionOperations;
    }

    @Override
    public GoalOperations goalOperations() {
        return goalOperations;
    }

    @Override
    public PlayerPasswordService passwordResetService() {
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
