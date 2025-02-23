package com.example.newsrecommendation.model.user.request;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;

public record UserChangePasswordRequest(String email, String password, String newPassword) {
    public AuthenticationCredentials getCredentials(){
        return new AuthenticationCredentials(email, password);
    }
}
