package com.mk1476.MyGrave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mk1476.MyGrave.service.AuthService;


@RestController
@RequestMapping("/users")
public class UserController {

   

    @Autowired
    
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String spaceName, @RequestParam String password) {
        authService.registerUser(spaceName, password);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestParam String spaceName, @RequestParam String password) {
        if (authService.authenticateUser(spaceName, password)) {
            return ResponseEntity.ok("User authenticated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed.");
        }
    }
}
