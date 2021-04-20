package com.gsf.client.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TemplateUrls implements Cloneable{
    String urlAuthorize;
    String redirectUri;
    String urlToken;
    String entities;
    String iss;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
