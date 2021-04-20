package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;

public class OAuthMixUpAttackTaskWebAttacker extends GenericTask {

    public OAuthMixUpAttackTaskWebAttacker(){}

    public OAuthMixUpAttackTaskWebAttacker(UserTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(UserTemplate user) {

        LOGGER.info("OAuthMixUpAttackTaskWebAttacker Init "+ user);

        seleniumConfig.initDriver();

        accessVulnerableClient(user);

        LOGGER.info("OAuthMixUpAttackTaskWebAttacker accessAuthorisationServer  "+ seleniumConfig.getCurrentUrl());
        accessAuthorisationServer(user, seleniumConfig.getCurrentUrl());

        //passo 10 (extrair code da url) e chama e inicia um fluxo novo com um state(new)
        String currentUrl = seleniumConfig.getCurrentUrl();
        String code = extractValue(currentUrl, "code");
        LOGGER.info("OAuthMixUpAttackTaskWebAttacker extract code from URL  "+ currentUrl);

        seleniumConfig.close();
        seleniumConfig.initDriver();

        LOGGER.info("OAuthMixUpAttackTaskWebAttacker End  "+ user);
    }



}
