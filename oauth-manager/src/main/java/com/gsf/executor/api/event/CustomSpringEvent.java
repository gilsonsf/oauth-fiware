package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.Client;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class CustomSpringEvent extends ApplicationEvent {
    private List<Client> message;

    public CustomSpringEvent(Object source, List<Client> message) {
        super(source);
        this.message = message;
    }


    public List<Client> getMessage() {
        return message;
    }
}