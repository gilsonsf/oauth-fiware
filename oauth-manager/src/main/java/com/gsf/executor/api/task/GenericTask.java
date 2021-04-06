package com.gsf.executor.api.task;

import com.gsf.executor.api.config.SeleniumConfig;
import com.gsf.executor.api.entity.ClientTemplate;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@ToString
public abstract class GenericTask {

    protected Logger LOGGER = LoggerFactory.getLogger(GenericTask.class);

    protected SeleniumConfig seleniumConfig;


    public GenericTask(){}

    public GenericTask(ClientTemplate client) {
        super();
        this.seleniumConfig = new SeleniumConfig();
        executeTask(client);
    }

    public abstract void executeTask(ClientTemplate client);

}
