package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;

public class OAuthCSRFAttackTask extends GenericTask {

    public OAuthCSRFAttackTask(){}

    public OAuthCSRFAttackTask(UserTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(UserTemplate user) {
        LOGGER.info("OAuthCSRFAttackTask Init "+ user);

        seleniumConfig.initDriver();

        accessClient(user);

        String currentUrl = createAttackRequest(seleniumConfig.getCurrentUrl());
        accessAuthorisationServer(user, currentUrl);

        getErrors();

        seleniumConfig.close();

        LOGGER.info("OAuthCSRFAttackTask End  "+ user);
    }

}
