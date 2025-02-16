package com.example.newsrecommendation.controllers.article;

import com.example.newsrecommendation.models.article.ArticleInfo;
import com.example.newsrecommendation.models.user.AuthenticationCredentials;
import com.example.newsrecommendation.models.user.exceptions.UserAuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequestMapping("/default")
@Tag(name = "Article API", description = "Управление статьями для пользователей")
public interface ArticleOperations {

    @Operation(summary = "Отправить рекомендуемые статьи пользователю по credentials")
    @ApiResponse(responseCode = "200", description = "Статьи получены")
    @GetMapping("/user")
    ResponseEntity<List<ArticleInfo>> getUserArticles(@Parameter(description = "Реквизиты пользователя для входа") @RequestBody AuthenticationCredentials credentials);
}
