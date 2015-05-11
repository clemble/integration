package com.clemble.casino.integration.event;

import com.clemble.casino.server.event.SystemEvent;
import com.clemble.casino.server.player.notification.SystemEventListener;

import javax.validation.Valid;

/**
 * Created by mavarazy on 5/11/15.
 */
public class SystemEventAccumulator<T extends SystemEvent> extends EventAccumulator<T> implements SystemEventListener {

    final private String CHANNEL;

    public SystemEventAccumulator(String CHANNEL) {
        this.CHANNEL = CHANNEL;
    }

    @Override
    public void onEvent(@Valid SystemEvent event) {
        super.onEvent((T) event);
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }

    @Override
    public String getQueueName() {
        return CHANNEL + " > integration";
    }
}
