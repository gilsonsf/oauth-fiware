package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.ClientTemplate;

public class OAuth307RedirectAttackTask extends GenericTask {

    public OAuth307RedirectAttackTask(){}

    public OAuth307RedirectAttackTask(ClientTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(ClientTemplate client) {
        //EXECUTAR O FLOW OAuth307RedirectAttackTask
        LOGGER.info("OAuth307RedirectAttackTask Init >> tentativa de ataque ao: "+client);
    }

}
