package com.clemble.casino.integration.player;

import com.clemble.casino.player.service.PlayerEmailService;
import com.clemble.casino.server.email.controller.PlayerEmailServiceController;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

/**
 * Created by mavarazy on 2/2/15.
 */
public class IntegrationPlayerEmailService implements PlayerEmailService {

    final private String player;
    final private PlayerEmailServiceController emailServiceController;

    public IntegrationPlayerEmailService(String player, PlayerEmailServiceController emailServiceController) {
        this.player = player;
        this.emailServiceController = emailServiceController;
    }

    @Override
    public String myEmail() {
        return emailServiceController.myEmail(player);
    }

    @Override
    public boolean verify(String verificationCode) {
        try {
            emailServiceController.verify(verificationCode, new MockHttpServletResponse());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
