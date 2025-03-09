package com.example.newsrecommendation.repository.user;

import com.example.newsrecommendation.databasesuite.DatabaseSuite;
import com.example.newsrecommendation.mapper.UserMapper;
import com.example.newsrecommendation.model.user.User;
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
class JpaUsersRepositoryTest extends DatabaseSuite {
    @Autowired
    private  JpaUsersRepository usersRepository;
    User testUser1 = new User(null, "1", "1","1");
    User testUser2 = new User(null, "2", "2","2");
    @Test
    void shouldFindUserByEmail(){
        usersRepository.save(UserMapper.toUserEntity(testUser1));
        assertTrue(usersRepository.findByEmail(testUser1.email()).isPresent());
        assertEquals(testUser1.email(), UserMapper.toUser(usersRepository.findByEmail(testUser1.email()).get()).email());
    }
    @Test
    void shouldNotFindUserByEmail(){
        assertTrue(usersRepository.findByEmail(testUser1.email()).isEmpty());
    }
    @Test
    void shouldFindUserByEmailAndPassword(){
        usersRepository.save(UserMapper.toUserEntity(testUser1));
        assertTrue(usersRepository.findByEmailAndPassword(testUser1.email(), testUser1.password()).isPresent());
        assertEquals(testUser1.username(),usersRepository.findByEmailAndPassword(testUser1.email(), testUser1.password()).get().getUsername());
    }

    @Test
    void shouldNotFindUserByEmailAndPassword(){
        assertTrue(usersRepository.findByEmailAndPassword(testUser1.email(), testUser1.password()).isEmpty());
    }

    @Test
    void findUserByEmailAndNotId(){
        UUID id = usersRepository.save(UserMapper.toUserEntity(testUser1)).getId();
        usersRepository.save(UserMapper.toUserEntity(testUser2));
        assertTrue(usersRepository.findByEmailAndNotId(id, testUser2.email()).isPresent());

    }
}