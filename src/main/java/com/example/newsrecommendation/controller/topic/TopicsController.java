package com.example.newsrecommendation.controller.topic;

import com.example.newsrecommendation.model.topic.Topic;
import com.example.newsrecommendation.model.topic.TopicId;
import com.example.newsrecommendation.service.topic.TopicsService;
import com.example.newsrecommendation.model.topic.exception.TopicAlreadyExistsException;
import com.example.newsrecommendation.model.topic.exception.TopicNotFoundException;
import com.example.newsrecommendation.model.topic.request.CustomTopicCreateRequest;
import com.example.newsrecommendation.model.topic.request.SubTopicsUpdateRequest;
import com.example.newsrecommendation.model.topic.response.TopicsResponse;
import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.service.user.UsersService;
import com.example.newsrecommendation.model.user.exception.UserAuthenticationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
@CircuitBreaker(name = "apiCircuitBreaker")
public class TopicsController implements TopicOperations{
    private static final Logger LOG = LoggerFactory.getLogger(TopicsController.class);
    private final TopicsService topicService;
    private final UsersService usersService;

    public TopicsController(TopicsService topicsService, UsersService usersService) {
        this.topicService = topicsService;
        this.usersService = usersService;
    }

    public ResponseEntity<Topic> get(@PathVariable Long id) throws TopicNotFoundException {
        Topic topic = topicService.findById(new TopicId(id));
        LOG.debug("Successfully found topic with id = "+id);
        return ResponseEntity.ok(topic);
    }

    public ResponseEntity<Topic> create(@RequestBody CustomTopicCreateRequest topicCreateRequest) throws UserAuthenticationException, TopicAlreadyExistsException {
        Optional<UserId> userId = usersService.authenticate(topicCreateRequest.getCredentials());
        Topic topic = topicService.create(new Topic(new TopicId(null), topicCreateRequest.description(), userId.get()));
        LOG.debug("Successfully created topic by user with id = "+userId.get().getValue());
        return ResponseEntity.ok(topic);
    }

    public ResponseEntity<String> delete(@RequestBody AuthenticationCredentials credentials, @PathVariable Long topicId) throws UserAuthenticationException, TopicNotFoundException{
        Optional<UserId> userId = usersService.authenticate(credentials);
        topicService.delete(new TopicId(topicId), userId.get());
        LOG.debug("Successfully deleted topic with id = {} by user with id = {}", topicId, userId.get().getValue());
        return ResponseEntity.ok("Topic deleted");
    }

    public ResponseEntity<TopicsResponse> getUsersTopics(@RequestBody AuthenticationCredentials credentials){
        Optional<UserId> userId = usersService.authenticateOptional(credentials);
        if (userId.isEmpty()){
            LOG.debug("All topics found");
            return ResponseEntity.ok(new TopicsResponse(List.of(), topicService.getAll()));
        }
        LOG.debug("User topics found");
        return ResponseEntity.ok(topicService.findSubAndUnSubTopics(userId.get()));
    }

    public ResponseEntity<String> patch(@RequestBody SubTopicsUpdateRequest topicsUpdateRequest) throws UserAuthenticationException{
        Optional<UserId> userId = usersService.authenticate(topicsUpdateRequest.gerCredentials());
        List<TopicId> topicIds = topicsUpdateRequest.topicIds().stream().map(TopicId::new).toList();
        topicService.updateSubTopics(topicIds, userId.get());
        LOG.debug("Successfully updated subTopics for user with id = {}",userId.get().getValue());
        return ResponseEntity.ok("SubTopics updated");
    }

}
