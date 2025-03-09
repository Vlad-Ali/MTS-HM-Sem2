package com.example.newsrecommendation.model.website;

import java.util.UUID;

public record WebsiteInfo(String url, String description, UUID userId) {
}
