package com.gsf.executor.api.task;

import com.google.gson.Gson;
import com.gsf.executor.api.entity.Client;
import com.gsf.executor.api.entity.ClientTemplate;
import com.gsf.executor.api.entity.OAuth2Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class OAuthHonestClientTask extends GenericTask {

    public OAuthHonestClientTask(){}

    public OAuthHonestClientTask(Client client) {
        super(client);
    }

    @Override
    public void executeTask(Client client) {
        //EXECUTAR O FLOW X

        logger.info("OAuthHonestClientTask Init  "+client);

        seleniumConfig.initDriver();

        seleniumConfig.get(authorizationCodeTokenService.getAuthorizationEndpoint(client));

        logger.info("titulo >> " + seleniumConfig.getTitle());

        seleniumConfig.findElements();

        logger.info(seleniumConfig.getCurrentUrl());

        String code = authorizationCodeTokenService.extractCode(seleniumConfig.getCurrentUrl());
        OAuth2Token token = authorizationCodeTokenService.getToken(code, client);

        logger.info(token.toString());

        seleniumConfig.close();

        for (int i = 0; i < 3 ; i++) {
            authorizationCodeTokenService.getEntity(token.getAccessToken(), client);
        }

        logger.info("End  "+client);
    }

    public void getGson() {
        Gson gson = new Gson();
        try {
            ClientTemplate client = gson.fromJson(new FileReader("src/main/resources/client-template.json"),ClientTemplate.class);
            client.getClients();
            System.out.println(client);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new OAuthHonestClientTask().getGson();
    }

}
