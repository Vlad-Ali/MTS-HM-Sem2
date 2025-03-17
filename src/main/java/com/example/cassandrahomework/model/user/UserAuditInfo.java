package com.example.cassandrahomework.model.user;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public record UserAuditInfo(UUID userId, Instant eventTime, String eventType, String eventDetails) {}
