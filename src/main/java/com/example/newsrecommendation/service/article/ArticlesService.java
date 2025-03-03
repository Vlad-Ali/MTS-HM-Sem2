package com.example.newsrecommendation.service.article;

import com.example.newsrecommendation.model.article.Article;
import com.example.newsrecommendation.repository.article.ArticlesRepository;
import com.example.newsrecommendation.repository.article.InMemoryArticlesRepository;
import com.example.newsrecommendation.model.user.UserId;
import org.checkerframework.checker.units.qual.C;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ArticlesService {
    private final ArticlesRepository articlesRepository;

    public ArticlesService(InMemoryArticlesRepository articleRepository) {
        this.articlesRepository = articleRepository;
    }

    @Async
    public CompletableFuture<List<Article>> getUserArticles(UserId userId){
        CompletableFuture<List<Article>> future = new CompletableFuture<>();
        future.complete(articlesRepository.getUserArticles(userId));
        return future;
    }
}
