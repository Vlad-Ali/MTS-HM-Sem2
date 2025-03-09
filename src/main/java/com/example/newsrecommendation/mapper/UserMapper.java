package com.example.newsrecommendation.mapper;

import com.example.newsrecommendation.entity.UserEntity;
import com.example.newsrecommendation.model.user.User;
import com.example.newsrecommendation.model.user.UserId;

import java.util.UUID;

public class UserMapper {
    public static User toUser(UserEntity userEntity){
        UUID id = userEntity.getId();
        String email = userEntity.getEmail();
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();
        return new User(new UserId(id), email, username, password);
    }

    public static UserEntity toUserEntity(User user){
        return new UserEntity(user.email(), user.password(), user.username());
    }
}
