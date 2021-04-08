package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.UserTemplate;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ClientEvent extends ApplicationEvent {
    private List<UserTemplate> message;

    public ClientEvent(Object source, List<UserTemplate> message) {
        super(source);
        this.message = message;
    }


    public List<UserTemplate> getMessage() {
        return message;
    }
}