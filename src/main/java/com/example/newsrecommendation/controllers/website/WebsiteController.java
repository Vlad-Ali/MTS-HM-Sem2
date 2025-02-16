package com.example.newsrecommendation.controllers.website;

import com.example.newsrecommendation.models.user.AuthenticationCredentials;
import com.example.newsrecommendation.models.user.UserId;
import com.example.newsrecommendation.models.user.UserService;
import com.example.newsrecommendation.models.user.exceptions.UserAuthenticationException;
import com.example.newsrecommendation.models.website.Website;
import com.example.newsrecommendation.models.website.WebsiteId;
import com.example.newsrecommendation.models.website.WebsiteService;
import com.example.newsrecommendation.models.website.exceptions.WebsiteAlreadyExistsException;
import com.example.newsrecommendation.models.website.exceptions.WebsiteNotFoundException;
import com.example.newsrecommendation.models.website.request.CustomWebsiteCreateRequest;
import com.example.newsrecommendation.models.website.request.SubWebsitesUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/websites")
public class WebsiteController implements WebsiteOperations{
    private final WebsiteService websiteService;
    private final UserService userService;
    private static final Logger LOG = LoggerFactory.getLogger(WebsiteController.class);

    public WebsiteController(WebsiteService websiteService, UserService userService) {
        this.websiteService = websiteService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Website> get(@PathVariable Long id) throws WebsiteNotFoundException {
        Website website = websiteService.findById(new WebsiteId(id));
        LOG.debug("Website found by id = {}",id);
        return ResponseEntity.ok(website);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String,List<Website>>> getUsersWebsites(@RequestBody AuthenticationCredentials credentials) throws UserAuthenticationException {
        Optional<UserId> userId = userService.authenticateOptional(credentials);
        if (userId.isEmpty()){
            LOG.debug("All websites found");
            return ResponseEntity.ok().body(Map.of("subscribed", List.of(),"other",websiteService.getAll()));
        }
        List<Website> subWebsites = websiteService.getSubscribedWebsitesByUserId(userId.get());
        List<Website> unSubWebsites = websiteService.getUnSubscribedWebsitesByUserId(userId.get());
        LOG.debug("Websites found by user with  = {}",userId.get().getValue());
        return ResponseEntity.ok(Map.of("subscribed", subWebsites,"other", unSubWebsites));
    }

    @PatchMapping
    public ResponseEntity<String> put(@RequestBody SubWebsitesUpdateRequest subWebsitesUpdateRequest) throws UserAuthenticationException {
        Optional<UserId> userId = userService.authenticate(subWebsitesUpdateRequest.getCredentials());
        List<WebsiteId> websiteIds = subWebsitesUpdateRequest.websiteIds().stream().map(WebsiteId::new).toList();
        assert userId.isPresent();
        websiteService.updateSubWebsites(websiteIds, userId.get());
        LOG.debug("Successfully updated subWebsites for user with id = {}", userId.get().getValue());
        return ResponseEntity.ok("SubWebsites updated");
    }

    @PostMapping("/custom")
    public ResponseEntity<Website> create(@RequestBody CustomWebsiteCreateRequest websiteCreateRequest) throws WebsiteAlreadyExistsException,UserAuthenticationException {
        Optional<UserId> userId = userService.authenticate(websiteCreateRequest.getCredentials());
        assert userId.isPresent();
        Website website = websiteService.create(new Website(new WebsiteId(null), websiteCreateRequest.url(), websiteCreateRequest.description(), userId.get()));
        LOG.debug("Successfully created website");
        return ResponseEntity.ok(website);
    }

    @DeleteMapping("/custom/{websiteId}")
    public ResponseEntity<String> delete(@RequestBody AuthenticationCredentials credentials, @PathVariable Long websiteId) throws WebsiteNotFoundException, UserAuthenticationException{
        Optional<UserId> userId = userService.authenticate(credentials);
        assert userId.isPresent();
        websiteService.delete(new WebsiteId(websiteId), userId.get());
        LOG.debug("Successfully deleted website with id = {} by user with id = {}",websiteId,userId.get().getValue());
        return ResponseEntity.ok("Website deleted");
    }

}

