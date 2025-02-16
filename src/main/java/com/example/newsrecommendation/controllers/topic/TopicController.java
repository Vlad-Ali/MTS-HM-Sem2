package com.example.newsrecommendation.controllers.topic;

import com.example.newsrecommendation.models.topic.Topic;
import com.example.newsrecommendation.models.topic.TopicId;
import com.example.newsrecommendation.models.topic.TopicService;
import com.example.newsrecommendation.models.topic.exceptions.TopicAlreadyExistsException;
import com.example.newsrecommendation.models.topic.exceptions.TopicNotFoundException;
import com.example.newsrecommendation.models.topic.request.CustomTopicCreateRequest;
import com.example.newsrecommendation.models.topic.request.SubTopicsUpdateRequest;
import com.example.newsrecommendation.models.user.AuthenticationCredentials;
import com.example.newsrecommendation.models.user.UserId;
import com.example.newsrecommendation.models.user.UserService;
import com.example.newsrecommendation.models.user.exceptions.UserAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
public class TopicController implements TopicOperations{
    private static final Logger LOG = LoggerFactory.getLogger(TopicController.class);
    private final TopicService topicService;
    private final UserService userService;

    public TopicController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> get(@PathVariable Long id) throws TopicNotFoundException {
        Topic topic = topicService.findById(new TopicId(id));
        LOG.debug("Successfully found topic with id = "+id);
        return ResponseEntity.ok(topic);
    }

    @PostMapping("/custom")
    public ResponseEntity<Topic> create(@RequestBody CustomTopicCreateRequest topicCreateRequest) throws UserAuthenticationException, TopicAlreadyExistsException {
        Optional<UserId> userId = userService.authenticate(topicCreateRequest.getCredentials());
        assert userId.isPresent();
        Topic topic = topicService.create(new Topic(new TopicId(null), topicCreateRequest.description(), userId.get()));
        LOG.debug("Successfully created topic by user with id = "+userId.get().getValue());
        return ResponseEntity.ok(topic);
    }

    @DeleteMapping("/custom/{topicId}")
    public ResponseEntity<String> delete(@RequestBody AuthenticationCredentials credentials, @PathVariable Long topicId) throws UserAuthenticationException, TopicNotFoundException{
        Optional<UserId> userId = userService.authenticate(credentials);
        assert userId.isPresent();
        topicService.delete(new TopicId(topicId), userId.get());
        LOG.debug("Successfully deleted topic with id = {} by user with id = {}", topicId, userId.get().getValue());
        return ResponseEntity.ok("Topic deleted");
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String,List<Topic>>> getUsersTopics(@RequestBody AuthenticationCredentials credentials){
        Optional<UserId> userId = userService.authenticateOptional(credentials);
        if (userId.isEmpty()){
            LOG.debug("All topics found");
            return ResponseEntity.ok(Map.of("subscribed",List.of(),"other",topicService.getAll()));
        }
        List<Topic> subTopics = topicService.findSubscribedTopicsByUserId(userId.get());
        List<Topic> unSubTopics = topicService.findUnSubscribedTopicsByUserId(userId.get());
        LOG.debug("User topics found");
        return ResponseEntity.ok(Map.of("subscribed",subTopics,"other",unSubTopics));
    }

    @PatchMapping
    public ResponseEntity<String> patch(@RequestBody SubTopicsUpdateRequest topicsUpdateRequest) throws UserAuthenticationException{
        Optional<UserId> userId = userService.authenticate(topicsUpdateRequest.gerCredentials());
        assert userId.isPresent();
        List<TopicId> topicIds = topicsUpdateRequest.topicIds().stream().map(TopicId::new).toList();
        topicService.updateSubTopics(topicIds, userId.get());
        LOG.debug("Successfully updated subTopics for user with id = {}",userId.get().getValue());
        return ResponseEntity.ok("SubTopics updated");
    }

}
