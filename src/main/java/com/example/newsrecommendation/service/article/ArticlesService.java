package com.example.newsrecommendation.service.article;

import com.example.newsrecommendation.model.article.Article;
import com.example.newsrecommendation.repository.article.ArticlesRepository;
import com.example.newsrecommendation.repository.article.InMemoryArticlesRepository;
import com.example.newsrecommendation.model.user.UserId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticlesService {
    private final ArticlesRepository articlesRepository;

    public ArticlesService(InMemoryArticlesRepository articleRepository) {
        this.articlesRepository = articleRepository;
    }

    public List<Article> getUserArticles(UserId userId){
        return articlesRepository.getUserArticles(userId);
    }
}
