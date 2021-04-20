package com.gsf.client.api.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.entity.UserTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TemplateMemoryRepository {

    private static List<ClientTemplate> clients = null;
    private static List<UserTemplate> users = null;

    private static ClientTemplate loggedClientAS;

    static {
        Type listOfClientsTemplate = new TypeToken<ArrayList<ClientTemplate>>() {}.getType();

        try {
            clients = new Gson().fromJson(new FileReader("src/main/resources/client-template.json"), listOfClientsTemplate);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<ClientTemplate> getAll() {
        return clients;
    }

    public static ClientTemplate findByState(String state) {
        return clients.stream()
                .filter(c -> c.getState().equals(state))
                .findFirst().get();

    }

    public static ClientTemplate findByAS(String as) {
        return clients.stream()
                .filter(c -> c.getAuthorizationServerName().equals(as))
                .findFirst().get();

    }

    public static ClientTemplate findByClientId(String clientId) {
        return clients.stream()
                .filter(c -> c.getClientId().equals(clientId))
                .findFirst().get();

    }

    public static ClientTemplate findById(Integer id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst().get();
    }

    public static void update(ClientTemplate client) {
        clients.set(client.getId() -1, client);

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
    public static OAuth2Token createTokenFiwareLabTemporary(String authorizationCode) {
        OAuth2Token token = new OAuth2Token();
        token.setAccessToken(authorizationCode+"_fiwarelab-access-token");
        token.setExpiresIn("3600");
        token.setRefreshToken(authorizationCode+"_fiwarelab-refresh-token");
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
