package com.gsf.client.api.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.UserTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TemplateMemoryRepository {

    private static List<ClientTemplate> clients = null;
    private static List<UserTemplate> users = null;

    static {
        Type listOfClientsTemplate = new TypeToken<ArrayList<ClientTemplate>>() {}.getType();
        Type listOfUsersTemplate = new TypeToken<ArrayList<ClientTemplate>>() {}.getType();

        try {
            clients = new Gson().fromJson(new FileReader("src/main/resources/client-template.json"), listOfClientsTemplate);
            users = new Gson().fromJson(new FileReader("src/main/resources/user-template.json"), listOfUsersTemplate);
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

    public static ClientTemplate findById(Integer id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst().get();
    }

    public static void update(ClientTemplate client) {
        clients.set(client.getId() -1, client);

    }

    public static UserTemplate findUserByLogin(String login) {
        return users.stream()
                .filter(c -> c.getLogin().equals(login))
                .findFirst().get();
    }
}
