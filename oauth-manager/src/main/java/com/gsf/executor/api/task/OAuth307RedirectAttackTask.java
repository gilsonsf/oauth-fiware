package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;

public class OAuth307RedirectAttackTask extends GenericTask {

    public OAuth307RedirectAttackTask(){}

    public OAuth307RedirectAttackTask(UserTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(UserTemplate user) {
        LOGGER.info("OAuth307RedirectAttackTask Init "+ user);

        seleniumConfig.initDriver();

        accessClient(user);

        String currentUrl = createAttackRequest(seleniumConfig.getCurrentUrl());
        accessAuthorisationServer(user, currentUrl);

        getErrors();

        seleniumConfig.close();

        LOGGER.info("OAuth307RedirectAttackTask End  "+ user);
    }

}
