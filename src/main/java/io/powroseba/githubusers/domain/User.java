package io.powroseba.githubusers.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record User(
        Long id,
        String login,
        String name,
        String type,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("created_at") ZonedDateTime createdAt,
        @JsonProperty("followers") Long followersCount,
        @JsonProperty("public_repos") Long publicRepositoriesCount) {
}