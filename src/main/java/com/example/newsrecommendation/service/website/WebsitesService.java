package com.example.newsrecommendation.service.website;

import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.model.website.Website;
import com.example.newsrecommendation.model.website.WebsiteId;
import com.example.newsrecommendation.model.website.exception.WebsiteAlreadyExistsException;
import com.example.newsrecommendation.model.website.exception.WebsiteNotFoundException;
import com.example.newsrecommendation.repository.website.InMemoryWebsitesRepository;
import com.example.newsrecommendation.repository.website.WebsiteRepository;
import com.example.newsrecommendation.model.website.response.WebsitesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebsitesService {
    private static final Logger LOG = LoggerFactory.getLogger(WebsitesService.class);
    private final WebsiteRepository websiteRepository;
    private final Set<Long> processedIds = ConcurrentHashMap.newKeySet();

    public WebsitesService(InMemoryWebsitesRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    //Exactly one
    @Cacheable(cacheNames = "Websites", key = "{#websiteId}")
    public Website findById(Long websiteId) {
        if (processedIds.contains(websiteId)){
            LOG.debug("Already processed");
            return null;
        }
        processedIds.add(websiteId);
        LOG.debug("Method findById called");
        return websiteRepository.findById(new WebsiteId(websiteId));
    }

    @Cacheable("responses")
    public List<Website> getAll() {
        LOG.debug("Method getAll called");
        return websiteRepository.getAll();
    }

    @Cacheable(cacheNames = "userWebsites", key = "{#userId.getValue()}")
    public List<Website> getSubscribedWebsitesByUserId(UserId userId) {
        LOG.debug("Method getSubscribedWebsitesByUserId called");
        return websiteRepository.findSubscribedWebsitesByUserId(userId);
    }

    @Cacheable(cacheNames = "userWebsites", key = "{#userId.getValue()}")
    public List<Website> getUnSubscribedWebsitesByUserId(UserId userId) {
        LOG.debug("Method getUnSubscribedWebsitesByUserId called");
        return websiteRepository.findUnSubscribedWebsitesByUserId(userId);
    }

    @Cacheable(cacheNames = "userWebsites", key = "{#userId.getValue()}")
    public WebsitesResponse getSubAndUnSubWebsites(UserId userId){
        LOG.debug("Method getSubAndUnSubWebsites called");
        return new WebsitesResponse(websiteRepository.findSubscribedWebsitesByUserId(userId), websiteRepository.findUnSubscribedWebsitesByUserId(userId));
    }

    @CachePut(cacheNames = "createWebsites", key = "#website.url()")
    public Website create(Website website) {
        LOG.debug("Method create called");
        return websiteRepository.create(website);
    }

    @CachePut(cacheNames = "updateWebsites", key = "{#WebsiteId.getValue(), #url, #description}")
    public void update(WebsiteId websiteId, String url, String description) {
        LOG.debug("Method update called");
        Website website = websiteRepository.findById(websiteId);
        websiteRepository.update(website.withUrl(url).withDescription(description));
        processedIds.remove(websiteId.getValue());
    }
    @CachePut(cacheNames = "UserWebsites", key = "{#websiteId.getValue(), #userId.getValue()}")
    public void delete(WebsiteId websiteId, UserId userId) {
        LOG.debug("Method delete called");
        websiteRepository.delete(websiteId, userId);
    }

    @CachePut(cacheNames ="UserWebsites", key = "{#websites, #userId.getValue()")
    public void updateSubWebsites(List<WebsiteId> websites, UserId userId){
        LOG.debug("Method updateSubWebsites called");
        websiteRepository.updateSubscribedWebsites(websites, userId);
    }
}
