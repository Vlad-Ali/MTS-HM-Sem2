package com.example.newsrecommendation.repository.website;

import com.example.newsrecommendation.databasesuite.DatabaseSuite;
import com.example.newsrecommendation.entity.UserEntity;
import com.example.newsrecommendation.entity.WebsiteEntity;
import com.example.newsrecommendation.mapper.UserMapper;
import com.example.newsrecommendation.mapper.WebsiteMapper;
import com.example.newsrecommendation.model.user.User;
import com.example.newsrecommendation.model.website.Website;
import com.example.newsrecommendation.repository.user.JpaUsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class JpaWebsitesRepositoryTest extends DatabaseSuite {
    @Autowired
    private JpaUsersRepository usersRepository;
    @Autowired
    private JpaWebsitesRepository websitesRepository;
    User testUser1 = new User(null, "1", "1","1");
    User testUser2 = new User(null, "2", "2","2");
    Website testWebsite1 = new Website(null,"1","1",null);

    @Transactional
    void subWebsiteForUser(UserEntity userEntity, WebsiteEntity websiteEntity){
        userEntity.subscribeToWebsite(websiteEntity);
        usersRepository.save(userEntity);
    }

    @Test
    void shouldFindWebsiteByUrl(){
        UserEntity userEntity = usersRepository.save(UserMapper.toUserEntity(testUser1));
        websitesRepository.save(WebsiteMapper.toWebsiteEntity(testWebsite1, userEntity));
        assertTrue(websitesRepository.findByUrl(testWebsite1.url()).isPresent());
        assertEquals(userEntity.getId(), websitesRepository.findByUrl(testWebsite1.url()).get().getCreatorId());
    }
    @Test
    void shouldNotFindWebsiteByUrl(){
        assertTrue(websitesRepository.findByUrl(testWebsite1.url()).isEmpty());
    }
    @Test
    void shouldFindSubWebsites(){
        UserEntity userEntity = usersRepository.save(UserMapper.toUserEntity(testUser1));
        WebsiteEntity websiteEntity = websitesRepository.save(WebsiteMapper.toWebsiteEntity(testWebsite1, userEntity));
        subWebsiteForUser(userEntity, websiteEntity);
        assertEquals(testWebsite1.url(), websitesRepository.findSubscribedWebsitesByUserId(userEntity.getId()).get(0).getUrl());
    }
    @Test
    void shouldFindUnSubWebsitesFromMigration(){
        UserEntity userEntity = usersRepository.save(UserMapper.toUserEntity(testUser1));
        assertEquals(5, websitesRepository.findUnSubscribedWebsitesByUserId(userEntity.getId()).size());
    }
}