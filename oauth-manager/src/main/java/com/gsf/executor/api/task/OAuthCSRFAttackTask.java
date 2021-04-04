package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.Client;

public class OAuthCSRFAttackTask extends GenericTask {

    public OAuthCSRFAttackTask(){}

    public OAuthCSRFAttackTask(Client client) {
        super(client);
    }

    @Override
    public void executeTask(Client client) {
        //EXECUTAR O FLOW OAuthCSRFAttackTask
        logger.info("OAuthCSRFAttackTask Init >> tentativa de ataque ao: "+client);
    }

}
