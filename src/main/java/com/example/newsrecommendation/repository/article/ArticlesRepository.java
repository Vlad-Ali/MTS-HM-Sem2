package com.example.newsrecommendation.repository.article;

import com.example.newsrecommendation.model.article.Article;
import com.example.newsrecommendation.model.user.UserId;

import java.util.List;

public interface ArticlesRepository {
    List<Article> getUserArticles(UserId userId);
}
