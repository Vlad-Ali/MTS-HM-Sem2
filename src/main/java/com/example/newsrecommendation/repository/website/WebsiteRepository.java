package com.example.newsrecommendation.repository.website;

import com.example.newsrecommendation.model.user.UserId;
import com.example.newsrecommendation.model.website.Website;
import com.example.newsrecommendation.model.website.WebsiteId;

import java.util.List;

public interface WebsiteRepository {
    Website findById(WebsiteId websiteId);

    Website create(Website website);

    List<Website> getAll();

    List<Website> findSubscribedWebsitesByUserId(UserId creatorId);

    List<Website> findUnSubscribedWebsitesByUserId(UserId creatorId);

    void update(Website website);

    void delete(WebsiteId id, UserId userId);

    void updateSubscribedWebsites(List<WebsiteId> websites, UserId userId);
}
