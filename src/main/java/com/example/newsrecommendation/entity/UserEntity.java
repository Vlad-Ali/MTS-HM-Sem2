package com.example.newsrecommendation.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class UserEntity {
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "email")
    @Setter
    @NotNull
    private String email;

    @Column(name = "password")
    @Setter
    @NotNull
    private String password;

    @Column(name = "username")
    @Setter
    @NotNull
    private String username;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WebsiteEntity> createdWebsites = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_websites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "website_id")
    )
    private Set<WebsiteEntity> subscribedWebsites = new HashSet<>();

    protected UserEntity() {}

    public UserEntity(String email, String password, String username) {
        this.id = null;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public void addWebsite(WebsiteEntity website) {
        createdWebsites.add(website);
        website.setCreator(this);
    }

    public void removeWebsite(WebsiteEntity website) {
        createdWebsites.remove(website);
        website.setCreator(null);
    }

    public void subscribeToWebsite(WebsiteEntity website) {
        subscribedWebsites.add(website);
        website.getSubscribers().add(this);
    }

    public void unsubscribeFromWebsite(WebsiteEntity website) {
        subscribedWebsites.remove(website);
        website.getSubscribers().remove(this);
    }

    public UUID getId() {
        return id;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public Set<WebsiteEntity> getCreatedWebsites() {
        return createdWebsites;
    }

    public Set<WebsiteEntity> getSubscribedWebsites() {
        return subscribedWebsites;
    }
}
