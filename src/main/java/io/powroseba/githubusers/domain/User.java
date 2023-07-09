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

    public UserWithCalculations withCalculations(Double calculations) {
        return new UserWithCalculations(this, calculations);
    }
}
