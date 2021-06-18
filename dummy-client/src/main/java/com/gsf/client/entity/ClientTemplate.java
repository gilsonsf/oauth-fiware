package com.gsf.client.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClientTemplate implements Cloneable {
    int id;
    String clientId;
    String secret;
    String responseType;
    String state;
    OAuth2Token token;
    TemplateUrls urls;
    String authorizationServerName;

    @Override
    public ClientTemplate clone() throws CloneNotSupportedException {

        ClientTemplate clientTemplate = (ClientTemplate) super.clone();

        OAuth2Token token = new OAuth2Token();
        token.setAccessToken(clientTemplate.getToken().getAccessToken());
        token.setExpiresIn(clientTemplate.getToken().getExpiresIn());
        token.setRefreshToken(clientTemplate.getToken().getRefreshToken());
        token.setTokenType(clientTemplate.getToken().getTokenType());

        clientTemplate.token = token;

        TemplateUrls urltemplate = new TemplateUrls();

        urltemplate.setUrlAuthorize(clientTemplate.getUrls().getUrlAuthorize());
        urltemplate.setRedirectUri(clientTemplate.getUrls().getRedirectUri());
        urltemplate.setUrlToken(clientTemplate.getUrls().getUrlToken());
        urltemplate.setEntities(clientTemplate.getUrls().getEntities());
        urltemplate.setIss(clientTemplate.getUrls().getIss());


        clientTemplate.urls = urltemplate;

        return clientTemplate;
    }
}
