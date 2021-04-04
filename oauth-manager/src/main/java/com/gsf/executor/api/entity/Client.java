package com.gsf.executor.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Client {
    String name;
    String clientId;
    String secret;
    String login;
    String password;
    String urlAuthorize;
    String redirectUri;
    String urlToken;
    String responseType;
    String state;
    String code;
}
