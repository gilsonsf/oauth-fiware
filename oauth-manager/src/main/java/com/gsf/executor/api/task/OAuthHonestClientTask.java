package com.gsf.executor.api.task;

import com.gsf.executor.api.entity.ClientTemplate;
import org.openqa.selenium.By;

public class OAuthHonestClientTask extends GenericTask {

    public OAuthHonestClientTask(){}

    public OAuthHonestClientTask(ClientTemplate client) {
        super(client);
    }

    @Override
    public void executeTask(ClientTemplate client) {

        LOGGER.info("OAuthHonestClientTask Init  "+ client);

        executeFlow(client);

//        seleniumConfig.initDriver();
//
//        seleniumConfig.get(authorizationCodeTokenService.getAuthorizationEndpoint(client));
//
//        LOGGER.info("titulo >> " + seleniumConfig.getTitle());
//
//        seleniumConfig.findElements(client.getLogin(), client.getPassword());
//
//        LOGGER.info(seleniumConfig.getCurrentUrl());
//
//
//        seleniumConfig.close();


        LOGGER.info("OAuthHonestClientTask End  "+ client);
    }

    private void executeFlow(ClientTemplate client) {

        seleniumConfig.initDriver();

        seleniumConfig.get(client.getSiteUrl());

        LOGGER.info("Client page title >> " + seleniumConfig.getTitle());

        seleniumConfig.getDriver().findElement(By.name("login")).sendKeys(client.getLogin());
        seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();

        try { Thread.sleep(1000); } catch (Exception ign) {}

        seleniumConfig.getDriver().get(seleniumConfig.getCurrentUrl());

        LOGGER.info("Navigate to page title >> " + seleniumConfig.getDriver().getTitle());

        seleniumConfig.getDriver().findElement(By.id("id_email")).sendKeys(client.getLogin());
        seleniumConfig.getDriver().findElement(By.id("id_password")).sendKeys(client.getPassword());
        seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();

        LOGGER.info("Current URL >> " + seleniumConfig.getCurrentUrl());

        seleniumConfig.close();
    }

}
