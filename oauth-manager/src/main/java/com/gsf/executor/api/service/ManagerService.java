package com.gsf.executor.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.executor.api.entity.Client;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.entity.User;
import com.gsf.executor.api.event.CustomSpringEventPublisher;
import com.gsf.executor.api.repository.UserRepository;
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

    @Autowired
    private UserRepository repository;

    Object target;
    private Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private CustomSpringEventPublisher publisher;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;


    @Async
    public CompletableFuture<List<User>> findAllUsers(){
        logger.info("get list of user by "+Thread.currentThread().getName());
        List<User> users=repository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<GenericTask> createTask(Client client) {
        logger.info("create by "+Thread.currentThread().getName());

        GenericTask genericTask = null;
        if(client.getState().equalsIgnoreCase("abcde2")) {
            genericTask = new OAuthMixUpAttackTask(client);
        } else {
            genericTask = new OAuthHonestClientTask(client);
        }

        return CompletableFuture.completedFuture(genericTask);
    }

    public void startProcess(int minutes) {
        logger.info("Start process >> "+LocalDateTime.now());
        ThreadPoolTaskExecutor ex = ( ThreadPoolTaskExecutor)executor;

        LocalDateTime end = LocalDateTime.now().plusMinutes(minutes);
        List<Client> clients = getClients();

        while (LocalDateTime.now().isBefore(end)) {
            publisher.publishCustomEvent(clients);
            //Thread.sleep(5000);

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

    private List<Client> getClients() {

        Type listOfMyClassObject = new TypeToken<ArrayList<Client>>() {}.getType();
        List<Client> clients = null;
        try {
            clients = new Gson().fromJson(new FileReader("src/main/resources/clients.json"), listOfMyClassObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
