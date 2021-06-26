package com.gsf.executor.api.service;

import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.enums.AttackTypes;
import com.gsf.executor.api.event.ClientCaptureEventObject;
import com.gsf.executor.api.event.ClientCaptureEventPublisher;
import com.gsf.executor.api.event.ClientEventObject;
import com.gsf.executor.api.event.ClientEventPublisher;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import com.gsf.executor.api.task.GenericTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.gsf.executor.api.enums.AttackTypes.MIXUP;
import static com.gsf.executor.api.enums.AttackTypes.NONE;

@Service
public class ManagerService {

    private Logger LOGGER = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ClientEventPublisher publisher;

    @Autowired
    private ClientCaptureEventPublisher clientCaptureEventPublisher;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Autowired
    private OAuthTaskService oAuthTaskService;

    @Async
    public CompletableFuture<GenericTask> createTask(UserTemplate user, AttackTypes attackType) {
        LOGGER.info("attackType " + attackType);
        GenericTask genericTask = createGenericTask(user, attackType);

        return CompletableFuture.completedFuture(genericTask);
    }

    public void createTaskSync(UserTemplate user, AttackTypes attackType) {
        LOGGER.info("attackType " + attackType);

        UserTemplate userTemplate = UserTemplateMemoryRepository.copyValues(user);

        if (attackType == MIXUP) {
            userTemplate.setAs(user.getAs()+"_mixup");
        }

        createGenericTask(userTemplate, attackType);

    }

    private GenericTask createGenericTask(UserTemplate user, AttackTypes attackType) {

        if (attackType == NONE) {
            return oAuthTaskService.getOAuthHonestClientTask(user);
        }

        GenericTask task = null;

        switch (attackType) {
            case MIXUP:
                task = oAuthTaskService.getOAuthMixUpAttackTask(user);
                break;
            case CSRF:
                task = oAuthTaskService.getOAuthCSRFAttackTask(user);
                break;
            default:
                task = oAuthTaskService.getOAuthHonestClientTask(user);
        }

        return task;

    }

    public void startProcess(int minutes) {

        List<UserTemplate> clients = new ArrayList<>();

        UserTemplateMemoryRepository.getAll().forEach( u -> {
			if(u.getAs().equalsIgnoreCase("dummy")
					|| u.getAs().equalsIgnoreCase("keyrock")) {
				clients.add(u);
			}
		});

        ClientEventObject eventObject = new ClientEventObject(clients, minutes);
        publisher.publishCustomEvent(eventObject);
        clientCaptureEventPublisher.publishCustomEvent(new ClientCaptureEventObject(minutes));
    }

    public boolean hasExecution() {

        ThreadPoolTaskExecutor ex = (ThreadPoolTaskExecutor)executor;

        return ex.getActiveCount() > 0;

    }

}
