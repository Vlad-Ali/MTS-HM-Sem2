package com.example.newsrecommendation.controller;

import com.example.newsrecommendation.model.user.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomController {
    @GetMapping("sync")
    public ResponseEntity<String> getSyncResponse(){
        if (Math.random() > 0.5){
            throw new CustomException("random");
        }
        return ResponseEntity.ok("Correct");
    }
}
