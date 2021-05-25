package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.CaptureTemplate;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.enums.AttackTypes;
import com.gsf.executor.api.repository.CaptureTemplateMemoryRepository;
import com.gsf.executor.api.service.CaptureService;
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
public class ClientCaptureEventListener implements ApplicationListener<ClientCaptureEvent> {

    private Logger LOGGER = LoggerFactory.getLogger(ClientCaptureEventListener.class);
    @Autowired
    private ManagerService managerService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Autowired
    private CaptureService captureService;

    @Override
    public void onApplicationEvent(ClientCaptureEvent event) {

        LOGGER.info("Start capture process >> " + LocalDateTime.now());

        try {

            Thread.sleep(20000);

            ThreadPoolTaskExecutor ex = (ThreadPoolTaskExecutor) executor;
            LocalDateTime end = LocalDateTime.now().plusMinutes(event.getMessage().getTime()).plusSeconds(30);

            while (LocalDateTime.now().isBefore(end)) {

                CaptureTemplate captureTemplate = captureService.execute(new UserTemplate(), "async");

                CaptureTemplateMemoryRepository.addCaptureTemplateASYNC(captureTemplate);

                Thread.sleep(10000);

            }

            LOGGER.info("End capture process >> " + LocalDateTime.now());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
