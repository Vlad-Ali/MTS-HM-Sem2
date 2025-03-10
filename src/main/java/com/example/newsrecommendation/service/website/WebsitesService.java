package com.example.newsrecommendation.service.website;

import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.model.website.Website;
import com.example.newsrecommendation.model.website.WebsiteId;
import com.example.newsrecommendation.repository.website.InMemoryWebsitesRepository;
import com.example.newsrecommendation.repository.website.WebsiteRepository;
import com.example.newsrecommendation.model.website.response.WebsitesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebsitesService {
    private static final Logger LOG = LoggerFactory.getLogger(WebsitesService.class);
    private final WebsiteRepository websiteRepository;

    public WebsitesService(InMemoryWebsitesRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    public Website findById(WebsiteId websiteId) {
        LOG.debug("Method findById called");
        return websiteRepository.findById(websiteId);
    }

    public List<Website> getAll() {
        LOG.debug("Method getAll called");
        return websiteRepository.getAll();
    }

    public List<Website> getSubscribedWebsitesByUserId(UserId userId) {
        LOG.debug("Method getSubscribedWebsitesByUserId called");
        return websiteRepository.findSubscribedWebsitesByUserId(userId);
    }

    public List<Website> getUnSubscribedWebsitesByUserId(UserId userId) {
        LOG.debug("Method getUnSubscribedWebsitesByUserId called");
        return websiteRepository.findUnSubscribedWebsitesByUserId(userId);
    }

    public WebsitesResponse getSubAndUnSubWebsites(UserId userId){
        LOG.debug("Method getSubAndUnSubWebsites called");
        return new WebsitesResponse(websiteRepository.findSubscribedWebsitesByUserId(userId), websiteRepository.findUnSubscribedWebsitesByUserId(userId));
    }

    public Website create(Website website) {
        LOG.debug("Method create called");
        return websiteRepository.create(website);
    }

    public void update(WebsiteId websiteId, String url, String description) {
        LOG.debug("Method update called");
        Website website = websiteRepository.findById(websiteId);
        websiteRepository.update(website.withUrl(url).withDescription(description));
    }

    public void delete(WebsiteId websiteId, UserId userId) {
        LOG.debug("Method delete called");
        websiteRepository.delete(websiteId, userId);
    }

    public void updateSubWebsites(List<WebsiteId> websites, UserId userId){
        LOG.debug("Method updateSubWebsites called");
        websiteRepository.updateSubscribedWebsites(websites, userId);
    }
}
