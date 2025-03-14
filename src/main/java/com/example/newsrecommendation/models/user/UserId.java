package com.example.newsrecommendation.models.user;

import java.util.Objects;

public class UserId {
    private final Long value;

    public UserId(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o instanceof UserId userId){
            return value.equals(userId.value);
        }
        return false;
    }
    @Override
    public int hashCode(){return Objects.hash(value);}
}
