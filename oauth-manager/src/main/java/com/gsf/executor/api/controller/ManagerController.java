package com.gsf.executor.api.controller;

import com.gsf.executor.api.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManagerController {
    @Autowired
    private ManagerService service;

    @GetMapping(value = "/start", produces = "application/json")
    public ResponseEntity startProcess(Integer time) throws InterruptedException {

        service.startProcess(time);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
