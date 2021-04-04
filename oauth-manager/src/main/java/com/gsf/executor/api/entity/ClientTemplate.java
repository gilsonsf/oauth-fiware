package com.gsf.executor.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ClientTemplate {

    TemplateUrls urls;
    List<Client> clients;
}
