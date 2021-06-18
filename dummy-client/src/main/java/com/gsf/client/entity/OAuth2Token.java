package com.gsf.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class OAuth2Token implements Cloneable {
    @Getter
    @Setter
    @JsonProperty("access_token")
    private String accessToken;

    @Getter
    @Setter
    @JsonProperty("token_type")
    private String tokenType;

    @Getter
    @Setter
    @JsonProperty("expires_in")
    private String expiresIn;

    @Getter
    @Setter
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
