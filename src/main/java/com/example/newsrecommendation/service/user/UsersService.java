package com.example.newsrecommendation.service.user;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.User;
import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.model.user.exception.CustomException;
import com.example.newsrecommendation.repository.user.InMemoryUsersRepository;
import com.example.newsrecommendation.repository.user.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UsersService.class);

    public UsersService(InMemoryUsersRepository userRepository) {
        this.usersRepository = userRepository;
    }

    public User findById(UserId userId){
        LOG.debug("Method findById called");
        return usersRepository.findById(userId);
    }

    //At least once
    @Retryable(retryFor = CustomException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
    public User register(User user){
        LOG.debug("Method register called");
        return usersRepository.create(user);
    }

    public void update(UserId userId, String email, String username){
        LOG.debug("Method update called");
        User user = usersRepository.findById(userId);
        usersRepository.update(user.withEmail(email).withUsername(username));
    }
    public void delete(final UserId userId){
        LOG.debug("Method delete called");
        usersRepository.delete(userId);
    }
    public Optional<UserId> authenticate(AuthenticationCredentials credentials){
        LOG.debug("Method authenticate called");
        return usersRepository.authenticate(credentials);
    }
    public Optional<UserId> authenticateOptional(AuthenticationCredentials credentials){
        LOG.debug("Method authenticateOptional called");
        return usersRepository.authenticateOptional(credentials);
    }

    public void changePassword(UserId userId, String newPassword){
        LOG.debug("Method changePassword called");
        User user = usersRepository.findById(userId);
        usersRepository.update(user.withPassword(newPassword));
    }
}
