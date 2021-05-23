package com.gsf.executor.api.event;

import com.gsf.executor.api.enums.AttackTypes;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.service.ManagerService;
import com.gsf.executor.api.task.GenericTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class ClientEventListener implements ApplicationListener<ClientEvent> {

    private Logger LOGGER = LoggerFactory.getLogger(ClientEventListener.class);
    @Autowired
    private ManagerService managerService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Override
    public void onApplicationEvent(ClientEvent event) {

        LOGGER.info("Start process >> "+ LocalDateTime.now());
        ThreadPoolTaskExecutor ex = ( ThreadPoolTaskExecutor)executor;

        LocalDateTime end = LocalDateTime.now().plusMinutes(event.getMessage().getTime());
        List<UserTemplate> clients = event.getMessage().getClient();

        while (LocalDateTime.now().isBefore(end)) {
            runClients(clients);

            while (ex.getActiveCount() > 0) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info("End process >> "+LocalDateTime.now());
    }

    private void runClients(List<UserTemplate> clients) {

        List<CompletableFuture<Object>> futuresList = new ArrayList<>();

        clients.stream().forEach( client -> {
            CompletableFuture<GenericTask> run = managerService.createTask(client, AttackTypes.NONE);
            futuresList.add(CompletableFuture.anyOf(run));
        });

        allOf(futuresList);
    }

    private <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));


        CompletableFuture<List<T>> listCompletableFuture = allFuturesResult.thenApply(v ->
                futuresList.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.<T>toList())
        );

        return listCompletableFuture;
    }
}
