package com.gsf.client.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClientTemplate {
    int id;
    String name;
    String clientId;
    String secret;
    String login;
    String password;
    String responseType;
    String state;
    String code;
    OAuth2Token token;
    TemplateUrls urls;
}
