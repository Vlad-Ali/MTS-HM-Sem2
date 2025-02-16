package com.example.newsrecommendation.controllers.article;

import com.example.newsrecommendation.models.article.Article;
import com.example.newsrecommendation.models.article.ArticleInfo;
import com.example.newsrecommendation.models.article.ArticleService;
import com.example.newsrecommendation.models.topic.TopicService;
import com.example.newsrecommendation.models.user.AuthenticationCredentials;
import com.example.newsrecommendation.models.user.UserId;
import com.example.newsrecommendation.models.user.UserService;
import com.example.newsrecommendation.models.user.exceptions.UserAuthenticationException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/articles")
public class ArticleController implements ArticleOperations{
    private final ArticleService articleService;
    private final TopicService topicService;
    private final UserService userService;
    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);
    public ArticleController(ArticleService articleService, TopicService topicService, UserService userService){
        this.articleService = articleService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @Override
    @GetMapping("/user")
    public ResponseEntity<List<ArticleInfo>> getUserArticles(@Parameter(description = "Реквизиты пользователя для входа") @RequestBody AuthenticationCredentials credentials) throws UserAuthenticationException {
        Optional<UserId> userId = userService.authenticate(credentials);
        assert userId.isPresent();
        List<Article> articles = articleService.getUserArticles(userId.get());
        List<ArticleInfo> articleInfos = new ArrayList<>();
        for(Article article : articles){
            String topicName = topicService.findById(article.topicId()).description();
            articleInfos.add(new ArticleInfo(article.title(),article.url(),article.createdAt(), topicName));
        }
        LOG.debug("Successfully got articles for user with id = {}", userId.get().getValue());
        return ResponseEntity.ok(articleInfos);
    }

}
