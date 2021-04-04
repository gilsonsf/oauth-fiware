package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.Client;

public class OAuthMixUpAttackTask extends GenericTask {

    public OAuthMixUpAttackTask(){}

    public OAuthMixUpAttackTask(Client client) {
        super(client);
    }

    @Override
    public void executeTask(Client client) {
        //EXECUTAR O FLOW MIXUP_ATTACK
        logger.info("OAuthMixUpAttackTask Init >> tentativa de ataque ao: "+client);
    }

}
