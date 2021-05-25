package com.gsf.executor.api.event;

import org.springframework.context.ApplicationEvent;

public class ClientEvent extends ApplicationEvent {
    private ClientEventObject message;

    public ClientEvent(Object source, ClientEventObject message) {
        super(source);
        this.message = message;
    }


    public ClientEventObject getMessage() {
        return message;
    }
}