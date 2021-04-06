package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.ClientTemplate;

public class OAuthCSRFAttackTask extends GenericTask {

    public OAuthCSRFAttackTask(){}

    public OAuthCSRFAttackTask(ClientTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(ClientTemplate client) {
        //EXECUTAR O FLOW OAuthCSRFAttackTask
        LOGGER.info("OAuthCSRFAttackTask Init >> tentativa de ataque ao: "+client);
    }

}
