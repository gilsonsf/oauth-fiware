package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.ClientTemplate;

public class OAuthMixUpAttackTask extends GenericTask {

    public OAuthMixUpAttackTask(){}

    public OAuthMixUpAttackTask(ClientTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(ClientTemplate client) {
        //EXECUTAR O FLOW MIXUP_ATTACK
        LOGGER.info("OAuthMixUpAttackTask Init >> tentativa de ataque ao: "+client);
    }

}
