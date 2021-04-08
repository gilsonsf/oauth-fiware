package com.gsf.executor.api.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.executor.api.entity.UserTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserTemplateRepository {

    private static List<UserTemplate> clients = null;

    static {
        Type listOfMyClassObject = new TypeToken<ArrayList<UserTemplate>>() {}.getType();

        try {
            clients = new Gson().fromJson(new FileReader("src/main/resources/user-template.json"), listOfMyClassObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<UserTemplate> getAll() {
        return clients;
    }

    public static UserTemplate findById(Integer id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst().get();

    }

}
