package com.example.newsrecommendation.e2e;

import com.example.newsrecommendation.NewsRecommendationApplication;
import com.example.newsrecommendation.databasesuite.DatabaseSuite;
import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.User;
import com.example.newsrecommendation.model.user.UserInfo;
import com.example.newsrecommendation.model.user.request.UserChangePasswordRequest;
import com.example.newsrecommendation.model.user.request.UserRegisterRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =
SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(classes = {NewsRecommendationApplication.class})
public class EndToEndTest extends DatabaseSuite {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(EndToEndTest.class);

    @Test
    public void endToEnd(){
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("1","1","1");
        ResponseEntity<User> testResponseUser1 = restTemplate.postForEntity("http://localhost:"+port+"/api/user/register", userRegisterRequest, User.class);
        User testUser1 = testResponseUser1.getBody();
        assertNotEquals(testUser1, null);
        assertEquals(testUser1.email(), userRegisterRequest.email());
        userRegisterRequest = new UserRegisterRequest("2","2","2");
        User testUser2 = restTemplate.postForEntity("http://localhost:"+port+"/api/user/register", userRegisterRequest, User.class).getBody();
        assertNotEquals(testUser2, null);
        assertEquals(testUser2.email(), userRegisterRequest.email());
        AuthenticationCredentials credentials = new AuthenticationCredentials(testUser1.email(), testUser1.password());
        ResponseEntity<UserInfo> testResponseUserInfo1 = restTemplate.exchange("http://localhost:"+port+"/api/user", HttpMethod.GET, new HttpEntity<>(credentials), UserInfo.class);
        assertEquals(credentials.email(), testResponseUserInfo1.getBody().email());
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("2","1","3");
        ResponseEntity<String> badResponseOnChangePassword = restTemplate.exchange("http://localhost:"+port+"/api/user/password",HttpMethod.PUT, new HttpEntity<>(userChangePasswordRequest), String.class);
        assertTrue(badResponseOnChangePassword.getStatusCode().is4xxClientError());
        userChangePasswordRequest = new UserChangePasswordRequest(testUser1.email(), testUser1.password(), "3");
        ResponseEntity<String> okResponseOnChangePassword = restTemplate.exchange("http://localhost:"+port+"/api/user/password",HttpMethod.PUT, new HttpEntity<>(userChangePasswordRequest), String.class);
        assertTrue(okResponseOnChangePassword.getStatusCode().is2xxSuccessful());
        /*Topic testTopic = Topic.TOPIC_1;
        ResponseEntity<Topic> response = restTemplate.exchange("http://localhost:"+port+"/api/topics/"+testTopic.id().getValue(),HttpMethod.GET,HttpEntity.EMPTY, Topic.class);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(testTopic, response.getBody());*/
    }
}
