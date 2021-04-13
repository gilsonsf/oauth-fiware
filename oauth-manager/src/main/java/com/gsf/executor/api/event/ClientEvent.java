package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.UserTemplate;
import org.springframework.context.ApplicationEvent;

import java.util.List;

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