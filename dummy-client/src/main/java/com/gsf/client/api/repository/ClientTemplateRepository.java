package com.gsf.client.api.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.client.api.entity.ClientTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClientTemplateRepository {

    private static List<ClientTemplate> clients = null;

    static {
        Type listOfMyClassObject = new TypeToken<ArrayList<ClientTemplate>>() {}.getType();

        try {
            clients = new Gson().fromJson(new FileReader("src/main/resources/client-template.json"), listOfMyClassObject);
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
}
