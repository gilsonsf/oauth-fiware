package com.gsf.executor.api;

import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import com.gsf.executor.api.service.ManagerService;
import com.gsf.executor.api.task.GenericTask;
import com.gsf.executor.api.task.OAuthHonestClientTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTask;
import com.gsf.executor.api.task.OAuthMixUpAttackTaskWebAttacker;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//@SpringBootTest
class OAuthManagerTests {

    @Autowired
    ManagerService service;

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) throws IOException {

//        System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:\\Development\\phantomjs-1.9.8-windows\\phantomjs.exe");
//        PhantomJSDriver driver = new PhantomJSDriver();
        String state = "state=CIeC1n";
        //String state = "";

        System.setProperty("webdriver.chrome.driver", "C:\\Development\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:9001");

        System.out.println("titulo 1 >> " + driver.getTitle());

        driver.findElement(By.name("login")).sendKeys("alice-the-admin@test.com");
        driver.findElement(By.xpath(".//button[@type='submit']")).click();

        //driver.get("http://localhost:3005/oauth2/authorize?client_id=tutorial-dckr-site-0000-xpresswebapp&redirect_uri=http://localhost:9001/callback&response_type=code&scope=read%20write&"+state);


        try {
            Thread.sleep(2000);
        } catch (Exception ign) {
        }

        System.out.println(driver.getCurrentUrl());
        driver.get(driver.getCurrentUrl());

        System.out.println("titulo 2 >> " + driver.getTitle());

        driver.findElement(By.id("id_email")).sendKeys("alice-the-admin@test.com");
        driver.findElement(By.id("id_password")).sendKeys("test");
        driver.findElement(By.xpath(".//button[@type='submit']")).click(); //FUNCIONA

        System.out.println(driver.getCurrentUrl());

//		driver.findElement(By.name("username")).sendKeys("client@gmail.com");
//		driver.findElement(By.name("password")).sendKeys("123");
//		driver.findElement(By.xpath(".//button[@type='submit']")).click();

        driver.close();


    }

    public static void TESTE(String[] args) throws IOException {

//        System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:\\Development\\phantomjs-1.9.8-windows\\phantomjs.exe");
//        PhantomJSDriver driver = new PhantomJSDriver();
        String state = "state=CIeC1n";
        //String state = "";

        System.setProperty("webdriver.chrome.driver", "C:\\Development\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:3005/oauth2/authorize?client_id=tutorial-dckr-site-0000-xpresswebapp&redirect_uri=http://localhost:9001/callback&response_type=code&scope=read%20write&" + state);


        System.out.println("titulo >> " + driver.getTitle());

        driver.findElement(By.id("id_email")).sendKeys("alice-the-admin@test.com");
        driver.findElement(By.id("id_password")).sendKeys("test");
        driver.findElement(By.xpath(".//button[@type='submit']")).click(); //FUNCIONA
        // driver.findElement(By.xpath(".//button")).click(); //FUNCIONA
        //driver.findElement(By.xpath("//button[contains(text(),'Sign In')]")).click(); //FUNCIONA

        System.out.println(driver.getCurrentUrl());

        driver.findElement(By.name("username")).sendKeys("client@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.xpath(".//button[@type='submit']")).click();

        //To capture page screenshot and save In D: drive.
//		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//		FileUtils.copyFile(scrFile, new File("C:\\dev\\error-details.jpeg"),true);

        //driver.close();
//		for (String type : driver.manage().logs().getAvailableLogTypes()) {
//			try {
//				List<LogEntry> entries = driver.manage().logs().get(type).getAll();
//				System.out.println(entries.size() + " " + type + " log entries found");
//
//				for (LogEntry entry : entries) {
//					System.out.println(entry.getLevel() + " " + entry.getMessage().replaceAll("'", ""));
//				}
//			}catch (Exception e) {
//				e.getStackTrace();
//			}
//		}

//        driver.navigate().to("http://localhost:9001/integracao/callback");
//
//        LogEntries logEntries1 = driver.manage().logs().get(LogType.BROWSER);
//        for (LogEntry entry : logEntries1) {
//            System.out.println(entry.getLevel() + " " + entry.getMessage().replaceAll("'", ""));
//        }


    }

    public void testLog(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Development\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:3005/oauth2/authorize?client_id=tutorial-dckr-site-0000-xpresswebapp&redirect_uri=http://localhost:9001/integracao/callback&response_type=code&scope=read%20write&state=CIeC1n");
        // inspect available log types
        Set<String> logtypes = driver.manage().logs().getAvailableLogTypes();
        System.out.println("suported log types: " + logtypes.toString()); // [logcat, bugreport, server, client]

        // print first and last 10 lines of logs
        LogEntries logs = driver.manage().logs().get("logcat");
        System.out.println("First and last ten lines of log: ");
        StreamSupport.stream(logs.spliterator(), false).limit(10).forEach(System.out::println);
        System.out.println("...");
        StreamSupport.stream(logs.spliterator(), false).skip(logs.getAll().size() - 10).forEach(System.out::println);

        // wait for more logs
        try {
            Thread.sleep(5000);
        } catch (Exception ign) {
        } // pause to allow visual verification

        // demonstrate that each time get logs, we only get new logs
        // which were generated since the last time we got logs
        LogEntries secondCallToLogs = driver.manage().logs().get("logcat");
        System.out.println("\nFirst ten lines of next log call: ");
        StreamSupport.stream(secondCallToLogs.spliterator(), false).limit(10).forEach(System.out::println);

        Assert.assertNotEquals(logs.iterator().next(), secondCallToLogs.iterator().next());
    }


    //@Test
    public void testScreenshot(String[] args) throws IOException {

        WebDriver driver;

        DesiredCapabilities capability = new DesiredCapabilities();
        capability.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:\\Development\\phantomjs-1.9.8-windows\\phantomjs.exe");
        driver = new PhantomJSDriver(capability);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        driver.get("http://only-testing-blog.blogspot.com/2014/04/calc.html");
        //Get current page title using javascript executor.
        JavascriptExecutor javascript = (JavascriptExecutor) driver;
        String pagetitle = (String) javascript.executeScript("return document.title");
        System.out.println("My Page Title Is  : " + pagetitle);
        driver.findElement(By.xpath("//input[@id='2']")).click();
        driver.findElement(By.xpath("//input[@id='plus']")).click();
        driver.findElement(By.xpath("//input[@id='3']")).click();
        driver.findElement(By.xpath("//input[@id='equals']")).click();
        String sum = driver.findElement(By.xpath("//input[@id='Resultbox']")).getAttribute("value");
        System.out.println("****** Sum Is : " + sum + " ******");

        //To capture page screenshot and save In D: drive.
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("C:\\dev\\Test.jpeg"), true);
    }

    @Test
    public void testTask() {
        UserTemplate userTemplate = UserTemplateMemoryRepository.findById(76);

        //userTemplate.setAs(userTemplate.getAs()+"_mixup-https");

        //new OAuthCSRFAttackTask(clientTemplate);
        //new OAuth307RedirectAttackTask(clientTemplate);
        new OAuthHonestClientTask(userTemplate);
        //new OAuthMixUpAttackTaskWebAttacker(userTemplate);

        //new OAuthMixUpAttackTask(userTemplate);

//		UserTemplateMemoryRepository.getAll().forEach( u -> {
//			if(u.getAs().equalsIgnoreCase("dummy")
//					|| u.getAs().equalsIgnoreCase("keyrock")) {
//				new OAuthHonestClientTask(u);
//			}
//		});

//        List<CompletableFuture<Object>> futuresList = new ArrayList<>();
//
//        UserTemplateMemoryRepository.getAll().forEach(u -> {
//            if (u.getAs().equalsIgnoreCase("dummy")
//                    || u.getAs().equalsIgnoreCase("keyrock")) {
//
//                CompletableFuture<GenericTask> run = service.createTask(u, AttackTypes.NONE);
//                run.join();
//               // futuresList.add(CompletableFuture.anyOf(run));
//            }
//        });
//
//        allOf(futuresList);

//        CompletableFuture<GenericTask> task = service.createTask(userTemplate, AttackTypes.NONE);
//        task.join();

        System.out.println("testing");


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
