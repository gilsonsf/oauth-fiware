package com.gsf.executor.api.controller;

import com.gsf.executor.api.config.AuthorizationCodeTokenService;
import com.gsf.executor.api.entity.OAuth2Token;
import com.gsf.executor.api.entity.User;
import com.gsf.executor.api.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class ManagerController {
    @Autowired
    private ManagerService service;

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

//    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
//    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
//        for (MultipartFile file : files) {
//            service.saveUsers(file);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    @GetMapping(value = "/users", produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllUsers() {
       return  service.findAllUsers().thenApply(ResponseEntity::ok);
    }


    @GetMapping(value = "/getUsersByThread", produces = "application/json")
    public  ResponseEntity getUsers(){
        CompletableFuture<List<User>> users1=service.findAllUsers();
        CompletableFuture<List<User>> users2=service.findAllUsers();
        CompletableFuture<List<User>> users3=service.findAllUsers();
        CompletableFuture.allOf(users1,users2,users3).join();
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/start", produces = "application/json")
    public ResponseEntity startProcess() throws InterruptedException {

        int time = 2;
        service.startProcess(time);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public ResponseEntity callback(String code) {

        OAuth2Token token = authorizationCodeTokenService.getToken(code, null);

        System.out.println(token.getAccessToken());
        System.out.println(token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
