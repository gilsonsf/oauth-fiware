package com.gsf.executor.api.task;

import com.gsf.executor.api.config.AuthorizationCodeTokenService;
import com.gsf.executor.api.config.SeleniumConfig;
import com.gsf.executor.api.entity.Client;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@ToString
public abstract class GenericTask {

    protected Logger logger = LoggerFactory.getLogger(GenericTask.class);

    protected SeleniumConfig seleniumConfig;

    protected AuthorizationCodeTokenService authorizationCodeTokenService;

    public GenericTask(){}

    public GenericTask(Client client) {
        super();
        this.seleniumConfig = new SeleniumConfig();
        this.authorizationCodeTokenService = new AuthorizationCodeTokenService();
        executeTask(client);
    }

    public abstract void executeTask(Client client);

}
