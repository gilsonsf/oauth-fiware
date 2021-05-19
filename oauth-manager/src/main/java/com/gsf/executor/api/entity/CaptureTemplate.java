package com.gsf.executor.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class CaptureTemplate {
    int id;
    long initFile;
    long endFile;
    String type;
    LocalDateTime captureDate;
    UserTemplate user;
    List<Captured> captureList = new ArrayList<>();
}
