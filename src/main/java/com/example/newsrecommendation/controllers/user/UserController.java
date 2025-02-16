package com.example.newsrecommendation.controllers.user;

import com.example.newsrecommendation.models.user.*;
import com.example.newsrecommendation.models.user.exceptions.EmailConflictException;
import com.example.newsrecommendation.models.user.exceptions.UserAuthenticationException;
import com.example.newsrecommendation.models.user.request.UserChangePasswordRequest;
import com.example.newsrecommendation.models.user.request.UserRegisterRequest;
import com.example.newsrecommendation.models.user.request.UserUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController implements UserOperations{
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegisterRequest userRegisterRequest) throws EmailConflictException {
        User user = userService.register(new User(new UserId(null), userRegisterRequest.email(), userRegisterRequest.password(), userRegisterRequest.username()));
        LOG.debug("User is registered");
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<UserInfo> get(@RequestBody AuthenticationCredentials credentials) throws UserAuthenticationException {
        Optional<UserId> userId = userService.authenticate(credentials);
        assert userId.isPresent();
        User user = userService.findById(userId.get());
        LOG.debug("User authenticated by email = {} and password = {}", credentials.email(), credentials.password());
        return ResponseEntity.ok(new UserInfo(user.email(),user.username()));
    }

    @PatchMapping
    public ResponseEntity<String> update(@RequestBody UserUpdateRequest userUpdateRequest) throws UserAuthenticationException {
        Optional<UserId> userId = userService.authenticate(userUpdateRequest.getCredentials());
        assert userId.isPresent();
        UserInfo newUserInfo = userUpdateRequest.getNewUserInfo();
        userService.update(userId.get(), newUserInfo.email(), newUserInfo.username());
        LOG.debug("Successfully updated user with id = {}", userId.get().getValue());
        return ResponseEntity.ok("User is updated");
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody UserChangePasswordRequest userChangePasswordRequest) throws UserAuthenticationException{
        Optional<UserId> userId = userService.authenticate(userChangePasswordRequest.getCredentials());
        assert userId.isPresent();
        String newPassword = userChangePasswordRequest.newPassword();
        userService.changePassword(userId.get(), newPassword);
        LOG.debug("Successfully updated password by user with id = {}", userId.get().getValue());
        return ResponseEntity.ok("Password is updated");
    }




}
