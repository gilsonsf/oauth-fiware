package com.gsf.executor.api.event;

import org.springframework.context.ApplicationEvent;

public class ClientCaptureEvent extends ApplicationEvent {
    private ClientCaptureEventObject message;

    public ClientCaptureEvent(Object source, ClientCaptureEventObject message) {
        super(source);
        this.message = message;
    }


    public ClientCaptureEventObject getMessage() {
        return message;
    }
}