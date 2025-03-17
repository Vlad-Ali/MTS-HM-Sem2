package com.example.cassandrahomework.service.website;

import com.example.cassandrahomework.cassandra.UserAuditService;
import com.example.cassandrahomework.entity.UserEntity;
import com.example.cassandrahomework.entity.WebsiteEntity;
import com.example.cassandrahomework.mapper.WebsiteMapper;
import com.example.cassandrahomework.model.user.UserId;
import com.example.cassandrahomework.model.website.Website;
import com.example.cassandrahomework.model.website.WebsiteId;
import com.example.cassandrahomework.model.website.exception.WebsiteAlreadyExistsException;
import com.example.cassandrahomework.model.website.exception.WebsiteNotFoundException;
import com.example.cassandrahomework.model.website.response.WebsitesResponse;
import com.example.cassandrahomework.repository.user.JpaUsersRepository;
import com.example.cassandrahomework.repository.website.JpaWebsitesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WebsitesService {
    private static final Logger LOG = LoggerFactory.getLogger(WebsitesService.class);
    private final JpaWebsitesRepository websitesRepository;
    private final JpaUsersRepository usersRepository;
    private final UserAuditService userAuditService;

    public WebsitesService(JpaWebsitesRepository websitesRepository, JpaUsersRepository usersRepository, UserAuditService userAuditService) {
        this.websitesRepository = websitesRepository;
        this.usersRepository = usersRepository;
        this.userAuditService = userAuditService;
    }

    @Cacheable(cacheNames = "Websites", key = "{#websiteId}")
    public Website findById(Long websiteId) {
        LOG.debug("Method findById called");
        Optional<WebsiteEntity> optionalWebsite = websitesRepository.findById(websiteId);
        if (optionalWebsite.isEmpty()){
            throw new WebsiteNotFoundException("Website is not found with id = " + websiteId);
        }
        WebsiteEntity websiteEntity = optionalWebsite.get();
        return WebsiteMapper.toWebsite(websiteEntity);
    }

    public List<Website> getSubscribedWebsitesByUserId(UserId userId) {
        LOG.debug("Method getSubscribedWebsitesByUserId called");
        ArrayList<WebsiteEntity> websiteEntityArrayList = new ArrayList<>(websitesRepository.findSubscribedWebsitesByUserId(userId.getValue()));
        ArrayList<Website> websites = new ArrayList<>();
        for(WebsiteEntity entity : websiteEntityArrayList){
            websites.add(WebsiteMapper.toWebsite(entity));
        }
        return websites.stream().toList();
    }

    public List<Website> getUnSubscribedWebsitesByUserId(UserId userId) {
        LOG.debug("Method getUnSubscribedWebsitesByUserId called");
        ArrayList<WebsiteEntity> websiteEntityArrayList = new ArrayList<>(websitesRepository.findUnSubscribedWebsitesByUserId(userId.getValue()));
        ArrayList<Website> websites = new ArrayList<>();
        for(WebsiteEntity entity : websiteEntityArrayList){
            websites.add(WebsiteMapper.toWebsite(entity));
        }
        return websites.stream().toList();
    }

    public WebsitesResponse getSubAndUnSubWebsites(UserId userId){
        LOG.debug("Method getSubAndUnSubWebsites called");
        userAuditService.createRequest(userId.getValue(), Instant.now(), WebsitesAction.SELECT.name(), "User got his subWebsites");
        return new WebsitesResponse(getSubscribedWebsitesByUserId(userId), getUnSubscribedWebsitesByUserId(userId));
    }

    @Transactional
    public Website create(Website website) {
        LOG.debug("Method create called");
        Optional<WebsiteEntity> optionalWebsite = websitesRepository.findByUrl(website.url());
        if (optionalWebsite.isPresent()){
            throw new WebsiteAlreadyExistsException("Website already exists with url = " + website.url());
        }
        Optional<UserEntity> optionalUser = usersRepository.findById(website.creatorId().getValue());
        UserEntity userEntity = optionalUser.get();
        WebsiteEntity websiteEntity = WebsiteMapper.toWebsiteEntity(website, userEntity);
        userEntity.addWebsite(websiteEntity);
        UserEntity savedUser = usersRepository.save(userEntity);
        WebsiteEntity savedWebsite = savedUser.getCreatedWebsites().stream()
                .filter(web -> web.getUrl().equals(website.url()))
                .findFirst().get();
        userAuditService.createRequest(userEntity.getId(), Instant.now(), WebsitesAction.INSERT.name(), "User created his website with url = "+website.url());
        return WebsiteMapper.toWebsite(savedWebsite);
    }

    @Transactional
    public void delete(WebsiteId websiteId, UserId userId) {
        LOG.debug("Method delete called");
        Optional<UserEntity> optionalUser = usersRepository.findById(userId.getValue());
        UserEntity userEntity = optionalUser.get();
        Optional<WebsiteEntity> optionalWebsite = websitesRepository.findById(websiteId.getValue());
        if(optionalWebsite.isEmpty()){
            throw new WebsiteNotFoundException("Website is not found with id = " + websiteId.getValue());
        }
        WebsiteEntity websiteEntity = optionalWebsite.get();
        if (websiteEntity.getCreatorId()==null || !websiteEntity.getCreatorId().equals(userEntity.getId())){
            throw new WebsiteNotFoundException("Website is not found with id = " + websiteId.getValue());
        }
        userEntity.removeWebsite(websiteEntity);
        usersRepository.save(userEntity);
        websitesRepository.deleteById(websiteId.getValue());
        userAuditService.createRequest(userId.getValue(), Instant.now(), WebsitesAction.DELETE.name(), "User deleted his website with id = "+websiteId.getValue());
    }

    @Transactional
    public void updateSubWebsites(List<WebsiteId> websites, UserId userId){
        LOG.debug("Method updateSubWebsites called");
        ArrayList<WebsiteEntity> websiteEntityArrayList = new ArrayList<>();
        for (WebsiteId websiteId : websites){
            Optional<WebsiteEntity> optionalWebsite = websitesRepository.findById(websiteId.getValue());
            if (optionalWebsite.isEmpty()){
                throw new WebsiteNotFoundException("Website is not found with id = " + websiteId.getValue());
            }
            websiteEntityArrayList.add(optionalWebsite.get());
        }
        UserEntity userEntity = usersRepository.findById(userId.getValue()).get();
        userEntity.getSubscribedWebsites().clear();
        for (WebsiteEntity websiteEntity : websiteEntityArrayList){
            userEntity.subscribeToWebsite(websiteEntity);
        }
        usersRepository.save(userEntity);
        userAuditService.createRequest(userId.getValue(), Instant.now(), WebsitesAction.UPDATE.name(), "User updated his sub websites");
    }
}
