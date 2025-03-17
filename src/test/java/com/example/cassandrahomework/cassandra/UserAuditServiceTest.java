package com.example.cassandrahomework.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.example.cassandrahomework.model.user.UserAuditInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Testcontainers
public class UserAuditServiceTest {

    @Container
    private static final CassandraContainer<?> CASSANDRA = new CassandraContainer<>("cassandra:4.1").withExposedPorts(9042);

    @Autowired
    private UserAuditService userAuditService;

    @DynamicPropertySource
    static void cassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("cassandra.contact-points", CASSANDRA::getHost);
        registry.add("cassandra.port", () -> CASSANDRA.getMappedPort(9042));
        registry.add("cassandra.local-datacenter", () -> "datacenter1");
    }

    @BeforeAll
    static void setCassandra(){
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(CASSANDRA.getHost(), CASSANDRA.getMappedPort(9042)))
                .withLocalDatacenter("datacenter1")
                .build()) {

            session.execute("CREATE KEYSPACE IF NOT EXISTS my_keyspace WITH replication = "
                    + "{'class':'SimpleStrategy', 'replication_factor':1};");

            session.execute("CREATE TABLE IF NOT EXISTS my_keyspace.user_audit ("
                    + "user_id UUID,"
                    + "event_time TIMESTAMP,"
                    + "event_type TEXT,"
                    + "event_details TEXT,"
                    + "PRIMARY KEY ((user_id), event_time)"
                    + ") WITH CLUSTERING ORDER BY (event_time DESC);");
        }
    }

    @Test
    void shouldCreateRequest(){
        UUID userId = UUID.randomUUID();
        Instant eventTime = Instant.now();
        String eventType = "SELECT";
        String eventDetails = "Sub websites are got";

        assertTrue(userAuditService.createRequest(userId, eventTime, eventType, eventDetails));
    }

    @Test
    void shouldNotCreateRequest(){
        UUID userId = UUID.randomUUID();
        Instant eventTime = Instant.now();
        String eventType = "SELECT";
        String eventDetails = "Sub websites are got";
        userAuditService.createRequest(userId, eventTime, eventType, eventDetails);
        assertFalse(userAuditService.createRequest(null, eventTime, eventType, eventDetails));
    }

    @Test
    void shouldCreateRequestAndGetAuditInfo(){
        UUID userId = UUID.randomUUID();
        Instant eventTime = Instant.now();
        String eventType = "SELECT";
        String eventDetails = "Sub websites are got";

        userAuditService.createRequest(userId, eventTime, eventType, eventDetails);

        List<UserAuditInfo> auditInfoList = userAuditService.getInfoByUserId(userId);

        UserAuditInfo userAuditInfo = auditInfoList.get(0);
        Assertions.assertEquals(userId, userAuditInfo.userId());
        Assertions.assertEquals(eventTime.toEpochMilli(), userAuditInfo.eventTime().toEpochMilli());
        Assertions.assertEquals(eventType, userAuditInfo.eventType());
        Assertions.assertEquals(eventDetails, userAuditInfo.eventDetails());

    }


    @Configuration
    static class TestConfig {
        @Bean
        public CqlSession cqlSession() {
            return CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(CASSANDRA.getHost(), CASSANDRA.getMappedPort(9042)))
                    .withLocalDatacenter("datacenter1")
                    .build();
        }

        @Bean
        public UserAuditService userAuditService(CqlSession cqlSession) {
            return new UserAuditService(cqlSession);
        }

    }

}