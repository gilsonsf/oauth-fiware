package com.gsf.executor.api.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsf.executor.api.entity.UserTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class UserTemplateMemoryRepository {

    private static List<UserTemplate> users = null;

    private static String fileUsersStatic;

    @Value("${oauthmanager.users}")
    public void setFileUsersStatic(String users){
        UserTemplateMemoryRepository.fileUsersStatic = users;
    }

    public static  List<UserTemplate> getUsers() {

        if (users == null) {
            Type listOfMyClassObject = new TypeToken<ArrayList<UserTemplate>>() {}.getType();

            try {
                users = new Gson().fromJson(new FileReader(fileUsersStatic), listOfMyClassObject);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    public static List<UserTemplate> getAll() {
        return getUsers();
    }

    public static UserTemplate findById(Integer id) {
        return getUsers().stream()
                .filter(c -> c.getId() == id)
                .findFirst().get();

    }

    public static UserTemplate findByNameAndAS(String name, String as) {
        return getUsers().stream()
                .filter(c -> (c.getName().equalsIgnoreCase(name) && c.getAs().equalsIgnoreCase(as)))
                .findFirst().get();

    }

    public static List<UserTemplate> findByAuthorizationServer(String as) {
        List<UserTemplate> collect = getUsers().stream()
                .filter(c -> c.getAs().equalsIgnoreCase(as))
                .collect(toList());
        return collect;
    }

    public static UserTemplate copyValues(UserTemplate user) {

        UserTemplate userCopied = new UserTemplate();

        userCopied.setId(user.getId());
        userCopied.setName(user.getName());
        userCopied.setLogin(user.getLogin());
        userCopied.setPassword(user.getPassword());
        userCopied.setSiteUrl(user.getSiteUrl());
        userCopied.setAs(user.getAs());

        return userCopied;
    }

}
