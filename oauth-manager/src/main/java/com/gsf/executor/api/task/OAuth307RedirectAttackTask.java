package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.Client;

public class OAuth307RedirectAttackTask extends GenericTask {

    public OAuth307RedirectAttackTask(){}

    public OAuth307RedirectAttackTask(Client client) {
        super(client);
    }

    @Override
    public void executeTask(Client client) {
        //EXECUTAR O FLOW OAuth307RedirectAttackTask
        logger.info("OAuth307RedirectAttackTask Init >> tentativa de ataque ao: "+client);
    }

}
