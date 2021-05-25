package com.gsf.executor.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Captured {
    int id;
    String title;
    String value;
    List<String> vulnerabilities = new ArrayList<>();
}
