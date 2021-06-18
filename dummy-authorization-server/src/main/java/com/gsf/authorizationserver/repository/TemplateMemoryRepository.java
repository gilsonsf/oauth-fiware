package com.gsf.authorizationserver.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.authorizationserver.entity.ClientTemplate;
import com.gsf.authorizationserver.entity.OAuth2Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class TemplateMemoryRepository {

    private static List<ClientTemplate> clients = null;

    private static ClientTemplate loggedClientAS;

    private static String fileClientsStatic;

    @Value("${oauthmanager.clients}")
    public void setFileUsersStatic(String clients){
        TemplateMemoryRepository.fileClientsStatic = clients;
    }

    public static  List<ClientTemplate> getClients() {

        if (clients == null) {
            Type listOfMyClassObject = new TypeToken<ArrayList<ClientTemplate>>() {}.getType();

            try {
                clients = new Gson().fromJson(new FileReader(fileClientsStatic), listOfMyClassObject);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        return clients;
    }


    public static List<ClientTemplate> getAll() {
        return getClients();
    }

    public static ClientTemplate findByState(String state) {
        return getClients().stream()
                .filter(c -> c.getState().equals(state))
                .findFirst().get();

    }

    public static ClientTemplate findByAS(String as) {
        return getClients().stream()
                .filter(c -> c.getAuthorizationServerName().equals(as))
                .findFirst().get();

    }

    public static ClientTemplate findByClientId(String clientId) {
        return getClients().stream()
                .filter(c -> c.getClientId().equals(clientId))
                .findFirst().get();

    }

    public static ClientTemplate findById(Integer id) {
        return getClients().stream()
                .filter(c -> c.getId() == id)
                .findFirst().get();
    }

    public static void update(ClientTemplate client) {
        getClients().set(client.getId() -1, client);

    }

    public static ClientTemplate copyValues(ClientTemplate client) {

        ClientTemplate clientCopied = new ClientTemplate();

        clientCopied.setId(client.getId());
        clientCopied.setClientId(client.getClientId());
        clientCopied.setSecret(client.getSecret());
        clientCopied.setResponseType(client.getResponseType());
        clientCopied.setState(client.getState());
        clientCopied.setToken(client.getToken());
        clientCopied.setUrls(client.getUrls());
        clientCopied.setAuthorizationServerName(client.getAuthorizationServerName());

        return clientCopied;
    }

    public static OAuth2Token createToken(String authorizationCode) {
        OAuth2Token token = new OAuth2Token();
        token.setAccessToken(authorizationCode+"_dummy-access-token");
        token.setExpiresIn("3600");
        token.setRefreshToken(authorizationCode+"_dummy-refresh-token");
        token.setTokenType("Bearer");

        return token;
    }

    public static void setClientLogged(String clientId) {

        if(loggedClientAS == null) {
            loggedClientAS = copyValues(findByClientId(clientId));
        } else if (!loggedClientAS.getClientId().equalsIgnoreCase(clientId)) {
            loggedClientAS = copyValues(findByClientId(clientId));
        }
    }

    public static ClientTemplate getLoggedClient() {
        return loggedClientAS;
    }
}
