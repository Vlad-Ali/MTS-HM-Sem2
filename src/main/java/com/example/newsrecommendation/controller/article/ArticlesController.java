package com.example.newsrecommendation.controller.article;

import com.example.newsrecommendation.model.article.Article;
import com.example.newsrecommendation.model.article.ArticleInfo;
import com.example.newsrecommendation.service.article.ArticlesService;
import com.example.newsrecommendation.service.topic.TopicsService;
import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.service.user.UsersService;
import com.example.newsrecommendation.model.user.exception.UserAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/articles")
public class ArticlesController implements ArticleOperations{
    private final ArticlesService articlesService;
    private final TopicsService topicsService;
    private final UsersService usersService;
    private static final Logger LOG = LoggerFactory.getLogger(ArticlesController.class);
    public ArticlesController(ArticlesService articlesService, TopicsService topicsService, UsersService usersService){
        this.articlesService = articlesService;
        this.topicsService = topicsService;
        this.usersService = usersService;
    }

    public ResponseEntity<List<ArticleInfo>> getUserArticles(@RequestBody AuthenticationCredentials credentials) throws UserAuthenticationException {
        Optional<UserId> userId = usersService.authenticate(credentials);
        List<Article> articles;
        try {
            articles = articlesService.getUserArticles(userId.get()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        List<ArticleInfo> articleInfos = new ArrayList<>();
        for(Article article : articles){
            String topicName = topicsService.findById(article.topicId()).description();
            articleInfos.add(new ArticleInfo(article.title(),article.url(),article.createdAt(), topicName));
        }
        LOG.debug("Successfully got articles for user with id = {}", userId.get().getValue());
        return ResponseEntity.ok(articleInfos);
    }

}
