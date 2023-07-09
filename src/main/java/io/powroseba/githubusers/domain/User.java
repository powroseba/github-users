package io.powroseba.githubusers.domain;

import java.time.ZonedDateTime;

public record User(
        Long id,
        String login,
        String name,
        String type,
        String avatarUrl,
        ZonedDateTime createdAt,
        Long followersCount,
        Long publicRepositoriesCount) {
}