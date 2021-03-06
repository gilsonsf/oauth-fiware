package com.gsf.executor.api.config;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SeleniumConfig {

    private WebDriver driver;



    public SeleniumConfig() {
        //driver = new ChromeDriver();
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }


    public void close() {
        driver.close();
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    public void get(String url) {
        driver.get(url);
    }

    public void clickElement(WebElement element) {
        element.click();
    }

    public  void findElements(String email, String password) {
        driver.findElement(By.id("id_email")).sendKeys(email);
        driver.findElement(By.id("id_password")).sendKeys(password);
        driver.findElement(By.xpath(".//button[@type='submit']")).click();
    }

    public void initDriver() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public WebDriver getDriver() {
        return driver;
    }
}
