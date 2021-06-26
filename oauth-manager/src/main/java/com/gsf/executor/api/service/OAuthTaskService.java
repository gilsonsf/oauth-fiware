package com.gsf.executor.api.service;


import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.task.OAuthCSRFAttackTask;
import com.gsf.executor.api.task.OAuthHonestClientTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class OAuthTaskService {

    @Value("${oauthmanager.seleniumConfig}")
    private String seleniumConfig;

    @PostConstruct
    public void setup() {
        System.setProperty("webdriver.chrome.driver", seleniumConfig);
    }

//    @Bean
//    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GenericTask getOAuthHonestClientTask(UserTemplate user) {
        return new OAuthHonestClientTask(user);
    }

//    @Bean
//    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GenericTask getOAuthMixUpAttackTask(UserTemplate user) {
        return new OAuthMixUpAttackTask(user);
    }

//    @Bean
//    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GenericTask getOAuthCSRFAttackTask(UserTemplate user) {
        return new OAuthCSRFAttackTask(user);
    }

}
