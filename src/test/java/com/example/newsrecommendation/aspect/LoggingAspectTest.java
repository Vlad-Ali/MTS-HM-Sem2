package com.example.newsrecommendation.aspect;

import com.example.newsrecommendation.NewsRecommendationApplication;
import com.example.newsrecommendation.controller.user.UsersController;
import com.example.newsrecommendation.databasesuite.DatabaseSuite;
import com.example.newsrecommendation.model.topic.Topic;
import com.example.newsrecommendation.model.user.request.UserRegisterRequest;
import com.example.newsrecommendation.model.website.Website;
import com.example.newsrecommendation.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {NewsRecommendationApplication.class})
@Transactional
@Testcontainers
public class LoggingAspectTest extends DatabaseSuite {
    @Autowired
    UsersController usersController;

    @Test
    public void testAspect(){
        int startValue = LoggingAspect.counter;
        usersController.register(new UserRegisterRequest("1","1","1"));
        assertEquals(LoggingAspect.counter, startValue + 2);
    }
}