package com.gsf.authorizationserver.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TemplateUrls {
    String urlAuthorize;
    String redirectUri;
    String urlToken;
    String entities;
    String iss;
}
