package io.powroseba.githubusers.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(
        String login,
        Long id,
        String name,
        String type,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("created_at") String createdAt,
        Long followersCount,
        @JsonProperty("public_repos") Long publicRepositoriesCount) {
}