package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.ClientTemplate;
import com.gsf.executor.api.service.ManagerService;
import com.gsf.executor.api.task.GenericTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class ClientEventListener implements ApplicationListener<ClientEvent> {

    @Autowired
    private ManagerService managerService;

    @Override
    public void onApplicationEvent(ClientEvent event) {
        List<ClientTemplate> clients = event.getMessage();

        List<CompletableFuture<Object>> futuresList = new ArrayList<>();

        int idClientToBeAttacked = ThreadLocalRandom.current().nextInt(1, clients.size() + 1);
        int idKindOfAttack = ThreadLocalRandom.current().nextInt(1, 4);

        clients.stream().forEach( client -> {
            CompletableFuture<GenericTask> run = managerService.createTask(client, idClientToBeAttacked, idKindOfAttack);
            futuresList.add(CompletableFuture.anyOf(run));
        });

        allOf(futuresList);
    }

    private <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));


        CompletableFuture<List<T>> listCompletableFuture = allFuturesResult.thenApply(v ->
                futuresList.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );

        return listCompletableFuture;
    }
}
