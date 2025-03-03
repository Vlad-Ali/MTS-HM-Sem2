package com.example.newsrecommendation.model.article;

import java.sql.Timestamp;

public record ArticleInfo(String title, String url, Timestamp createdAt, String topicDescription) {
}
