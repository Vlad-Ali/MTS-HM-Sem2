package com.example.newsrecommendation.repository.user;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.User;
import com.example.newsrecommendation.model.user.UserId;

import java.util.Optional;

public interface UsersRepository {
    User findById(UserId userId);

    User create(User user);

    void update(User user);

    void delete(UserId userId);

    Optional<UserId> authenticate(AuthenticationCredentials credentials);


    Optional<UserId> authenticateOptional(AuthenticationCredentials credentials);
}
