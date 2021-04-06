package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.ClientTemplate;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ClientEvent extends ApplicationEvent {
    private List<ClientTemplate> message;

    public ClientEvent(Object source, List<ClientTemplate> message) {
        super(source);
        this.message = message;
    }


    public List<ClientTemplate> getMessage() {
        return message;
    }
}