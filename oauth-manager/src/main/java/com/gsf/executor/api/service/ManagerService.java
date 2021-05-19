package com.gsf.executor.api.service;

import com.gsf.executor.api.AttackTypes;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.event.ClientEventObject;
import com.gsf.executor.api.event.ClientEventPublisher;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.task.OAuth307RedirectAttackTask;
import com.gsf.executor.api.task.OAuthCSRFAttackTask;
import com.gsf.executor.api.task.OAuthHonestClientTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.gsf.executor.api.AttackTypes.*;

@Service
public class ManagerService {

    private Logger LOGGER = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ClientEventPublisher publisher;

    @Async
    public CompletableFuture<GenericTask> createTask(UserTemplate user, AttackTypes attackType) {
        LOGGER.info("attackType " + attackType);
        GenericTask genericTask = createGenericTask(user, attackType);

        return CompletableFuture.completedFuture(genericTask);
    }

    public void createTaskSync(UserTemplate user, AttackTypes attackType) {
        LOGGER.info("attackType " + attackType);
        createGenericTask(user, attackType);

    }

    private GenericTask createGenericTask(UserTemplate client, AttackTypes attackType) {

        if (attackType == NONE) {
            return new OAuthHonestClientTask(client);
        }

        GenericTask task = null;

        switch (attackType) {
            case MIXUP:
                task = new OAuthMixUpAttackTask(client);
                break;
            case REDIRECT_307:
                task = new OAuth307RedirectAttackTask(client);
                break;
            case CSRF:
                task = new OAuthCSRFAttackTask(client);
                break;
            default:
                task = new OAuthHonestClientTask(client);
        }

        return task;

    }

    public void startProcess(int minutes) {

        List<UserTemplate> clients = UserTemplateMemoryRepository.findByAuthorizationServer("keyrock");
        clients.add(UserTemplateMemoryRepository.findById(5));

        ClientEventObject eventObject = new ClientEventObject(clients, minutes);
        publisher.publishCustomEvent(eventObject);
    }

    public void testAspect() {
        System.out.println("Teste Loggin inside");
    }

}
