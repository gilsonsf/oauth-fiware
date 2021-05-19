package com.gsf.executor.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserTemplate implements Cloneable {
    int id;
    String name;
    String login;
    String password;
    String siteUrl;
    String as;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
