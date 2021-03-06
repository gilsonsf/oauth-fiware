package com.gsf.executor.api.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ClientEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final ClientEventObject event) {
        System.out.println("Publishing client event. ");
        ClientEvent clientEvent = new ClientEvent(this, event);
        applicationEventPublisher.publishEvent(clientEvent);
    }
}
