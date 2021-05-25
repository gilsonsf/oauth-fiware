package com.gsf.client.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserTemplate {
    int id;
    String name;
    String login;
    String password;
    String siteUrl;
    String as;
}
