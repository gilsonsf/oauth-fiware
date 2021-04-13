package com.gsf.executor.api.event;

import com.gsf.executor.api.entity.UserTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ClientEventObject {

    List<UserTemplate> client;
    Integer time;
}
