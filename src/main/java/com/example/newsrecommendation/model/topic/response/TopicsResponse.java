package com.example.newsrecommendation.model.topic.response;

import com.example.newsrecommendation.model.topic.Topic;

import java.util.List;

public record TopicsResponse(List<Topic> subscribed, List<Topic> other) {
}
