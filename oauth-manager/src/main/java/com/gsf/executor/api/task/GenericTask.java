package com.gsf.executor.api.task;

import com.gsf.executor.api.config.SeleniumConfig;
import com.gsf.executor.api.entity.UserTemplate;
import lombok.Getter;
import lombok.ToString;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static java.util.Objects.nonNull;

@Getter
@ToString
@Service
public abstract class GenericTask {

    protected Logger LOGGER = LoggerFactory.getLogger(GenericTask.class);

    protected SeleniumConfig seleniumConfig;


    public GenericTask(){}

    public GenericTask(UserTemplate client) {
        super();
        this.seleniumConfig = new SeleniumConfig();
        executeTask(client);
    }

    public abstract void executeTask(UserTemplate user);

    protected void accessClient(UserTemplate user) {

        seleniumConfig.get(user.getSiteUrl());

        LOGGER.info("Client page title >> " + seleniumConfig.getTitle());

        if(seleniumConfig.getDriver().getTitle().equalsIgnoreCase("Privacy error")) {
            seleniumConfig.getDriver().findElement(By.id("details-button")).click();
            seleniumConfig.getDriver().findElement(By.id("proceed-link")).click();
        }

        addCode(user.getAs());

        seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();

    }

    protected void accessVulnerableClient(UserTemplate user) {

        seleniumConfig.get(user.getSiteUrl());

        LOGGER.info("Client page title >> " + seleniumConfig.getTitle());

        if(seleniumConfig.getDriver().getTitle().equalsIgnoreCase("Privacy error")) {
            seleniumConfig.getDriver().findElement(By.id("details-button")).click();
            seleniumConfig.getDriver().findElement(By.id("proceed-link")).click();
        }

        if(user.getAs().equalsIgnoreCase("vulnerable")) {
            LOGGER.info("OAuthMixUpAttackTask Add CodeInjection (AS vulnerable) "+ user);
            addCode(user.getAs());
            seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();
        } else {
            LOGGER.info("OAuthMixUpAttackTaskWebAttacker Alter (AS Fiware Lab) to (AS vulnerable) "+ user);
            addCode(user.getAs());
            seleniumConfig.getDriver().findElement(By.id("asVulnerable")).click();
        }

    }

    private void addCode(String authorizationName) {
        JavascriptExecutor jse = (JavascriptExecutor)seleniumConfig.getDriver();
        jse.executeScript("document.getElementsByName('authorizationName')[0].setAttribute('type', 'text');");
        seleniumConfig.getDriver().findElement(By.xpath("//input[@name='authorizationName']")).clear();
        seleniumConfig.getDriver().findElement(By.xpath("//input[@name='authorizationName']")).sendKeys(authorizationName);
        jse.executeScript("document.getElementsByName('authorizationName')[0].setAttribute('type', 'hidden');");
    }

    protected void accessAuthorisationServer(UserTemplate user, String currentUrl) {

        //seleniumConfig.get(currentUrl);
        LOGGER.info("Navigate to AS page title >> " + seleniumConfig.getDriver().getTitle());


        if(seleniumConfig.getDriver().getTitle().equalsIgnoreCase("Privacy error")) {
            seleniumConfig.getDriver().findElement(By.id("details-button")).click();
            seleniumConfig.getDriver().findElement(By.id("proceed-link")).click();
        }

        if(seleniumConfig.getDriver().getTitle().equalsIgnoreCase("FIWARE Lab Identity Manager - Login")) {
            seleniumConfig.getDriver().findElement(By.id("id_username")).sendKeys(user.getLogin());
        } else {
            seleniumConfig.getDriver().findElement(By.id("id_email")).sendKeys(user.getLogin());
        }

        seleniumConfig.getDriver().findElement(By.id("id_password")).sendKeys(user.getPassword());
        seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();

        try {
            WebElement authorize = seleniumConfig.getDriver().findElement(By.id("authorize_application_modal"));
            if(nonNull(authorize)) {
                seleniumConfig.getDriver().findElement(By.xpath(".//button[@type='submit']")).click();
            }
        } catch (Exception e) {
            System.out.println("Já autorizado");
        }
    }

    protected void getErrors() {
        //String error = seleniumConfig.getDriver().findElement(By.className("error-oauth")).getText();
        //
          //identify element and set value
//        jsExecutor.executeScript ("document.getElementsByName('authorizationName').value='vulnerable';");
//        String s = (String) jsExecutor.executeScript("return document.getElementsByName('authorizationName').value");

        LOGGER.info("Error >> " + "Missing parameter: `state`");
    }

    protected String createAttackRequest(String url) {

        String newUrl = url.split("\\?")[0];

        String[] params = url.split("\\?")[1].split("&");

        return newUrl + "?" + Arrays.asList(params).stream()
                .filter(p -> !p.startsWith("state"))
                .reduce((a, b) -> a + "&" + b).get();

    }
    protected String extractCode(String url) {
        return url.split("\\?")[1]
                .split("&")[0]
                .split("=")[1];
    }

    protected String extractState(String url) {
        return url.split("\\?")[1]
                .split("&")[3]
                .split("=")[1];
    }
}
