package com.gsf.executor.api.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ClientCaptureEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final ClientCaptureEventObject event) {
        System.out.println("Publishing client capture event. ");
        ClientCaptureEvent clientEvent = new ClientCaptureEvent(this, event);
        applicationEventPublisher.publishEvent(clientEvent);
    }
}
