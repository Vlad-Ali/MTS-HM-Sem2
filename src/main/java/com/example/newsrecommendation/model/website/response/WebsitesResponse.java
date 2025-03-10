package com.example.newsrecommendation.model.website.response;

import com.example.newsrecommendation.model.website.Website;

import java.util.List;

public record WebsitesResponse(List<Website> subscribed, List<Website> other) {
}
