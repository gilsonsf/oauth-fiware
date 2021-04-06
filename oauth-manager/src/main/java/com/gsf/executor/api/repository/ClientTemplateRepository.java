package com.gsf.executor.api.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.executor.api.entity.ClientTemplate;

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
            clients = new Gson().fromJson(new FileReader("src/main/resources/client-template-new.json"), listOfMyClassObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<ClientTemplate> getAll() {
        return clients;
    }

    public static ClientTemplate findById(Integer id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst().get();

    }

}
