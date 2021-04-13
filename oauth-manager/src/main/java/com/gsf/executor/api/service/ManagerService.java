package com.gsf.executor.api.service;

import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.event.ClientEventObject;
import com.gsf.executor.api.event.ClientEventPublisher;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.task.OAuth307RedirectAttackTask;
import com.gsf.executor.api.task.OAuthCSRFAttackTask;
import com.gsf.executor.api.task.OAuthHonestClientTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTaskWebAttacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ManagerService {

    private Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ClientEventPublisher publisher;

    @Async
    public CompletableFuture<GenericTask> createTask(UserTemplate client, int idClientToBeAttacked, int idKindOfAttack) {
        GenericTask genericTask = createGenericTask(client, idClientToBeAttacked, idKindOfAttack);

        return CompletableFuture.completedFuture(genericTask);
    }

    private GenericTask createGenericTask(UserTemplate client, int idClientToBeAttacked, int idKindOfAttack) {

        if(client.getAs().equalsIgnoreCase("fiwarelab")) {
            return new OAuthMixUpAttackTaskWebAttacker(client);
        }

        GenericTask genericTask = null;
        if(client.getId() == idClientToBeAttacked) {

            switch(idKindOfAttack) {
                case 1:
                    genericTask = new OAuthMixUpAttackTask(client);
                    break;
                case 2:
                    genericTask = new OAuth307RedirectAttackTask(client);
                    break;
                case 3:
                    genericTask = new OAuthCSRFAttackTask(client);
                    break;
                default:
                    genericTask = new OAuthHonestClientTask(client);
            }

        } else {
            genericTask = new OAuthHonestClientTask(client);
        }

       return genericTask;

    }

    public void startProcess(int minutes) {

        List<UserTemplate> clients = UserTemplateMemoryRepository.findByAuthorizationServer("keyrock");
        clients.add(UserTemplateMemoryRepository.findById(5));

        ClientEventObject eventObject = new ClientEventObject(clients, minutes);
        publisher.publishCustomEvent(eventObject);
    }

}
