package com.example.newsrecommendation.model.website.exception;

public class WebsiteNotFoundException extends RuntimeException {
    public WebsiteNotFoundException(String message) {
        super(message);
    }
}
