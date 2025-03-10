package com.example.newsrecommendation.controller.topic;

import com.example.newsrecommendation.model.topic.Topic;
import com.example.newsrecommendation.model.topic.request.CustomTopicCreateRequest;
import com.example.newsrecommendation.model.topic.request.SubTopicsUpdateRequest;
import com.example.newsrecommendation.model.topic.response.TopicsResponse;
import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/default")
@Tag(name = "Topic Api", description = "Управление темами для пользователей")
public interface TopicOperations {

    @Operation(summary = "Найти тему по ID")
    @ApiResponse(responseCode = "200", description = "Тема найдена")
    @GetMapping("/{id}")
    ResponseEntity<Topic> get(@Parameter(description = "ID темы для получения") @PathVariable Long id);

    @Operation(summary = "Создать тему")
    @ApiResponse(responseCode = "200", description = "Тема создана")
    @PostMapping("/custom")
    ResponseEntity<Topic> create(@Parameter(description = "Данные для создания темы пользователем") @RequestBody CustomTopicCreateRequest customTopicCreateRequest);

    @Operation(summary = "Удалить созданную пользователем тему")
    @ApiResponse(responseCode = "200", description = "Тема удалена")
    @DeleteMapping("/custom/{topicId}")
    ResponseEntity<String> delete(@Parameter(description = "Реквизиты для входа пользователя") @RequestBody AuthenticationCredentials credentials,@Parameter(description = "ID созданной темы для удаления") @PathVariable Long topicId);

    @Operation(summary = "Получить все выбранные пользователем темы")
    @ApiResponse(responseCode = "200", description = "Темы получены")
    @GetMapping("/user")
    ResponseEntity<TopicsResponse> getUsersTopics(@Parameter(description = "Реквизиты для входа пользователя") @RequestBody AuthenticationCredentials credentials);

    @Operation(summary = "Установить выбранные темы для пользователя")
    @ApiResponse(responseCode = "200", description = "Выбранные темы обновлены")
    @PatchMapping
    ResponseEntity<String> patch(@Parameter(description = "Данные для обновления выбранных тем") @RequestBody SubTopicsUpdateRequest subTopicsUpdateRequest);
}
