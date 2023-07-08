package io.powroseba.githubusers.domain;

import java.time.ZonedDateTime;

public record UserWithCalculations(
        String login,
        Long id,
        String name,
        String type,
        String avatarUrl,
        ZonedDateTime createdAt,
        Double calculations
) {

    public UserWithCalculations(User user, Double calculations) {
        this(
                user.login(),
                user.id(),
                user.name(),
                user.type(),
                user.avatarUrl(),
                user.createdAt(),
                calculations
        );
    }
}
