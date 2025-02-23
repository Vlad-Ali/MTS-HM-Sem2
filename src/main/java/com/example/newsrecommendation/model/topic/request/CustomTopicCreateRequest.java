package com.example.newsrecommendation.model.topic.request;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;

public record CustomTopicCreateRequest(String email, String password, String description) {
    public AuthenticationCredentials getCredentials(){
        return new AuthenticationCredentials(email, password);
    }
}
