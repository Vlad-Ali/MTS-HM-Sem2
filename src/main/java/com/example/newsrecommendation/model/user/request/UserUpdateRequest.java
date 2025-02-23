package com.example.newsrecommendation.model.user.request;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.UserInfo;

public record UserUpdateRequest(String email, String password, String newEmail, String newUsername) {
    public AuthenticationCredentials getCredentials(){
    return new AuthenticationCredentials(email, password);
    }
    public UserInfo getNewUserInfo(){
        return new UserInfo(newEmail, newUsername);
    }
}
