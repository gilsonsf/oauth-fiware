package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;

public class OAuthHonestClientTask extends GenericTask {

    public OAuthHonestClientTask(){}

    public OAuthHonestClientTask(UserTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(UserTemplate user) {

        LOGGER.info("OAuthHonestClientTask Init  "+ user);

        seleniumConfig.initDriver();

        accessClient(user);

        String currentUrl = seleniumConfig.getCurrentUrl();
        LOGGER.info("Current URL 1>> " + currentUrl);
        accessAuthorisationServer(user, currentUrl);

        LOGGER.info("Current URL 2>> " + seleniumConfig.getCurrentUrl());

        seleniumConfig.close();

        LOGGER.info("OAuthHonestClientTask End  "+ user);
    }




}
