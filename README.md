# OAuth FIWARE

System for detecting and analyzing three categories of OAuth 2.0 vulnerabilities (CSRF, AS Mix-Up e 307 Redirect), 
running over the HTTP/HTTPS protocol in FIWARE.

## Architecture HTTP Overview 

![](img/arquitetura-http.png)


### Requirements

Install <b>Docker</b>: https://docs.docker.com/engine/installation/

Install <b>docker-compose</b>: https://docs.docker.com/compose/install/

Install <b>Java 11</b>: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html

Download <b>ChromeDriver</b>: https://chromedriver.chromium.org/downloads

### Installation

Clone project: 
```
$ git clone https://github.com/gilsonsf/oauth-fiware.git
```

Run fiware-idms:
```
$ cd oauth-fiware/fiware-idm
$ sudo docker-compose up
```
Run oauth-manager:
```
$ cd oauth-fiware/oauth-manager/target
$ java -jar oauth-manager-0.0.1-SNAPSHOT.jar --spring.config.location=<PATH>
NOTE: The <PATH> is located at oauth-fiware\external-config\application.yml. Check that the paths are correct within the file.
```

### Access

Application http://localhost:9191/ 


## Architecture HTTPS Overview 

![](img/arquitetura-https.png)

### Requirements

Create a <b>FIWARE Lab</b> account: https://account.lab.fiware.org/

Generate certificate <b>PKCS12</b>: https://mkyong.com/spring-boot/spring-boot-ssl-https-examples/

Generate <b>jssecacerts</b>: https://github.com/escline/InstallCert

### Installation

Add  <b>jssecacerts</b> at /src/main/resources

Add user information to  <b>FIWARE Lab</b>: src/main/resources/user-template.json

Add clientId and secret information to  <b>FIWARE Lab</b>: src/main/resources/client-template.json

Run dummy-https:
```
$ cd oauth-fiware/dummy-https
$ sudo docker-compose up
```

Run oauth-manager:
```
$ cd oauth-fiware/oauth-manager/target
$ java -jar oauth-manager-0.0.1-SNAPSHOT.jar --spring.config.location=<PATH>
NOTE: The <PATH> is located at oauth-fiware\external-config\application.yml. Check that the paths are correct within the file.
```

### Access

Application http://localhost:9191/ 