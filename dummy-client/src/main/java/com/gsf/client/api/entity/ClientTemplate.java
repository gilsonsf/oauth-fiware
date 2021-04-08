package com.gsf.client.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClientTemplate {
    int id;
    String clientId;
    String secret;
    String responseType;
    String state;
    OAuth2Token token;
    TemplateUrls urls;
    String authorizationServerName;
}
