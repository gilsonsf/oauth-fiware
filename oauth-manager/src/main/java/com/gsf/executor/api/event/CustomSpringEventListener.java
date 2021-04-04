package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.Client;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class CustomSpringEventListener implements ApplicationListener<CustomSpringEvent> {

    @Autowired
    private ManagerService managerService;

    @Override
    public void onApplicationEvent(CustomSpringEvent event) {
        List<Client> clients = event.getMessage();

        List<CompletableFuture<Object>> futuresList = new ArrayList<>();

        clients.stream().forEach( client -> {
            CompletableFuture<GenericTask> run= managerService.createTask(client);
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
