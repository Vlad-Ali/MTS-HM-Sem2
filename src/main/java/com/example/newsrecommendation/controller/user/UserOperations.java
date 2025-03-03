package com.example.newsrecommendation.controller.user;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.User;
import com.example.newsrecommendation.model.user.UserInfo;
import com.example.newsrecommendation.model.user.request.UserChangePasswordRequest;
import com.example.newsrecommendation.model.user.request.UserRegisterRequest;
import com.example.newsrecommendation.model.user.request.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/default")
@Tag(name = "User API", description = "Управление пользователями")
public interface UserOperations {

    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован")
    @PostMapping("/register")
    ResponseEntity<User> register(@Parameter(description = "Данные для регистрации пользователя") @RequestBody UserRegisterRequest userRegisterRequest);

    @Operation(summary = "Обновление имени и почты пользователя")
    @ApiResponse(responseCode = "200", description = "Данные пользователя обновлены")
    @PatchMapping
    ResponseEntity<String> update(@Parameter(description = "Данные для обновления пользователя") @RequestBody UserUpdateRequest userUpdateRequest);

    @Operation(summary = "Авторизация пользователя и получения его данных")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно авторизовался")
    @GetMapping
    ResponseEntity<UserInfo> get(@Parameter(description = "Реквизиты для входа пользователя") @RequestBody AuthenticationCredentials credentials);

    @Operation(summary = "Смена пароля пользователя")
    @ApiResponse(responseCode = "200", description = "Пароль изменен")
    @PutMapping("/password")
    ResponseEntity<String> changePassword(@Parameter(description = "Данные для смены пароля пользователя") @RequestBody UserChangePasswordRequest userChangePasswordRequest);
}
