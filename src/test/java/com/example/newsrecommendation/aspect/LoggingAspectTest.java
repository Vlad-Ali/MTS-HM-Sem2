package com.example.newsrecommendation.aspect;

import com.example.newsrecommendation.model.topic.Topic;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoggingAspectTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    public void testAspect(){
        int startValue = LoggingAspect.counter;
        Topic testTopic = Topic.TOPIC_1;
        ResponseEntity<Topic> response = restTemplate.exchange("http://localhost:"+port+"/api/topics/"+testTopic.id().getValue(), HttpMethod.GET, HttpEntity.EMPTY, Topic.class);
        assertEquals(LoggingAspect.counter, startValue + 2);
    }
}