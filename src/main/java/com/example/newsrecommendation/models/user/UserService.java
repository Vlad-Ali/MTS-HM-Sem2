package com.example.newsrecommendation.models.user;

import com.example.newsrecommendation.models.user.exceptions.EmailConflictException;
import com.example.newsrecommendation.models.user.exceptions.UserNotFoundException;
import com.example.newsrecommendation.models.user.repository.InMemoryUserRepository;
import com.example.newsrecommendation.models.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public UserService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UserId userId){
        LOG.debug("Method findById called");
        return userRepository.findById(userId);
    }

    public User register(User user){
        LOG.debug("Method register called");
        return userRepository.create(user);
    }

    public void update(UserId userId, String email, String username){
        LOG.debug("Method update called");
        User user = userRepository.findById(userId);
        userRepository.update(user.withEmail(email).withUsername(username));
    }
    public void delete(final UserId userId){
        LOG.debug("Method delete called");
        userRepository.delete(userId);
    }
    public Optional<UserId> authenticate(AuthenticationCredentials credentials){
        LOG.debug("Method authenticate called");
        return userRepository.authenticate(credentials);
    }
    public Optional<UserId> authenticateOptional(AuthenticationCredentials credentials){
        LOG.debug("Method authenticateOptional called");
        return userRepository.authenticateOptional(credentials);
    }

    public void changePassword(UserId userId, String newPassword){
        LOG.debug("Method changePassword called");
        User user = userRepository.findById(userId);
        userRepository.update(user.withPassword(newPassword));
    }
}
