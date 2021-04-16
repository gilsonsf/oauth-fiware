package com.gsf.client.api.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Base64;

@AllArgsConstructor
public class BasicAuthentication {
    @Getter
    private String login;

    @Getter
    private String senha;

    public String getCredenciaisBase64() {
        String credenciais = login + ":" + senha;
        String credenciaisCodificadasComBase64 = new String(
                Base64.getEncoder().encode(credenciais.getBytes()));

        return credenciaisCodificadasComBase64;
    }

}
