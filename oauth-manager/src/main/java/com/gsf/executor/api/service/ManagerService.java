package com.gsf.executor.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.event.ClientEventPublisher;
import com.gsf.executor.api.repository.UserTemplateRepository;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.task.OAuth307RedirectAttackTask;
import com.gsf.executor.api.task.OAuthCSRFAttackTask;
import com.gsf.executor.api.task.OAuthHonestClientTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ManagerService {

    private Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ClientEventPublisher publisher;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Async
    public CompletableFuture<GenericTask> createTask(UserTemplate client, int idClientToBeAttacked, int idKindOfAttack) {
        GenericTask genericTask = createGenericTask(client, idClientToBeAttacked, idKindOfAttack);

        return CompletableFuture.completedFuture(genericTask);
    }

    private GenericTask createGenericTask(UserTemplate client, int idClientToBeAttacked, int idKindOfAttack) {

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
        logger.info("Start process >> "+LocalDateTime.now());
        ThreadPoolTaskExecutor ex = ( ThreadPoolTaskExecutor)executor;

        LocalDateTime end = LocalDateTime.now().plusMinutes(minutes);
        List<UserTemplate> clients = UserTemplateRepository.getAll();

        while (LocalDateTime.now().isBefore(end)) {
            publisher.publishCustomEvent(clients);

            while (ex.getActiveCount() > 0) {
                logger.info("in while >> " + ex.getActiveCount());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("fim  while >> " + ex.getActiveCount());
        }
        logger.info("End process >> "+LocalDateTime.now());
    }

    private List<UserTemplate> getClients() {

        Type listOfMyClassObject = new TypeToken<ArrayList<UserTemplate>>() {}.getType();
        List<UserTemplate> clients = null;
        try {
            clients = new Gson().fromJson(new FileReader("src/main/resources/client-template.json"), listOfMyClassObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
