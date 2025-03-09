package com.example.newsrecommendation.model.topic.request;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;

import java.util.List;

public record SubTopicsUpdateRequest(String email, String password, List<Long> topicIds) {
    public AuthenticationCredentials gerCredentials(){
        return new AuthenticationCredentials(email, password);
    }
}
