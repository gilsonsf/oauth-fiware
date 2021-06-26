package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;

public class OAuthHonestClientTask extends GenericTask {

    public OAuthHonestClientTask(){}

    public OAuthHonestClientTask(UserTemplate user) {
        super(user);
    }

    @Override
    public void executeTask(UserTemplate user) {

        LOGGER.info("OAuthHonestClientTask Init  "+ user);

        seleniumConfig.initDriver();

        LOGGER.info("OAuthHonestClientTask accessClient  "+ user);
        accessClient(user);

        String currentUrl = seleniumConfig.getCurrentUrl();
        LOGGER.info("OAuthHonestClientTask accessAuthorisationServer  "+ currentUrl);
        accessAuthorisationServer(user, currentUrl);

        seleniumConfig.close();

        LOGGER.info("OAuthHonestClientTask End  "+ user);
    }




}
