package com.example.newsrecommendation.controller.website;

import com.example.newsrecommendation.model.user.AuthenticationCredentials;
import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.service.user.UsersService;
import com.example.newsrecommendation.model.user.exception.UserAuthenticationException;
import com.example.newsrecommendation.model.website.Website;
import com.example.newsrecommendation.model.website.WebsiteId;
import com.example.newsrecommendation.service.website.WebsitesService;
import com.example.newsrecommendation.model.website.exception.WebsiteAlreadyExistsException;
import com.example.newsrecommendation.model.website.exception.WebsiteNotFoundException;
import com.example.newsrecommendation.model.website.request.CustomWebsiteCreateRequest;
import com.example.newsrecommendation.model.website.request.SubWebsitesUpdateRequest;
import com.example.newsrecommendation.model.website.response.WebsitesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/websites")
public class WebsitesController implements WebsiteOperations{
    private final WebsitesService websitesService;
    private final UsersService usersService;
    private static final Logger LOG = LoggerFactory.getLogger(WebsitesController.class);

    public WebsitesController(WebsitesService websitesService, UsersService usersService) {
        this.websitesService = websitesService;
        this.usersService = usersService;
    }

    public ResponseEntity<Website> get(@PathVariable Long id) throws WebsiteNotFoundException {
        Website website = websitesService.findById(new WebsiteId(id));
        LOG.debug("Website found by id = {}",id);
        return ResponseEntity.ok(website);
    }

    public ResponseEntity<WebsitesResponse> getUsersWebsites(@RequestBody AuthenticationCredentials credentials) throws UserAuthenticationException {
        Optional<UserId> userId = usersService.authenticateOptional(credentials);
        if (userId.isEmpty()){
            LOG.debug("All websites found");
            return ResponseEntity.ok(new WebsitesResponse(List.of(), websitesService.getAll()));
        }
        LOG.debug("Websites found by user with  = {}",userId.get().getValue());
        return ResponseEntity.ok(websitesService.getSubAndUnSubWebsites(userId.get()));
    }

    public ResponseEntity<String> patch(@RequestBody SubWebsitesUpdateRequest subWebsitesUpdateRequest) throws UserAuthenticationException {
        Optional<UserId> userId = usersService.authenticate(subWebsitesUpdateRequest.getCredentials());
        List<WebsiteId> websiteIds = subWebsitesUpdateRequest.websiteIds().stream().map(WebsiteId::new).toList();
        websitesService.updateSubWebsites(websiteIds, userId.get());
        LOG.debug("Successfully updated subWebsites for user with id = {}", userId.get().getValue());
        return ResponseEntity.ok("SubWebsites updated");
    }

    public ResponseEntity<Website> create(@RequestBody CustomWebsiteCreateRequest websiteCreateRequest) throws WebsiteAlreadyExistsException,UserAuthenticationException {
        Optional<UserId> userId = usersService.authenticate(websiteCreateRequest.getCredentials());
        Website website = websitesService.create(new Website(new WebsiteId(null), websiteCreateRequest.url(), websiteCreateRequest.description(), userId.get()));
        LOG.debug("Successfully created website");
        return ResponseEntity.ok(website);
    }

    public ResponseEntity<String> delete(@RequestBody AuthenticationCredentials credentials, @PathVariable Long websiteId) throws WebsiteNotFoundException, UserAuthenticationException{
        Optional<UserId> userId = usersService.authenticate(credentials);
        websitesService.delete(new WebsiteId(websiteId), userId.get());
        LOG.debug("Successfully deleted website with id = {} by user with id = {}",websiteId,userId.get().getValue());
        return ResponseEntity.ok("Website deleted");
    }

}

