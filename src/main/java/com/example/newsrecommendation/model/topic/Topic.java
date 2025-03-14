package com.example.newsrecommendation.model.topic;

import com.example.newsrecommendation.model.user.UserId;

public record Topic(TopicId id, String description, UserId creatorId) {
    public static final Topic TOPIC_1 = new Topic(new TopicId(1L), "1", new UserId(1L));
    public static final Topic TOPIC_2 = new Topic(new TopicId(2L), "2", new UserId(2L));
    public static final Topic TOPIC_3 = new Topic(new TopicId(3L), "3", new UserId(3L));

    public Topic initializeWithId(TopicId newId){
        return new Topic(newId, description, creatorId);
    }

    public Topic withDescription(String newDescription){
        return new Topic(id, newDescription, creatorId);
    }

}
