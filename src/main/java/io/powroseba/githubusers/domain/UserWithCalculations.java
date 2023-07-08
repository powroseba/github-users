package io.powroseba.githubusers.domain;

import java.time.ZonedDateTime;

public record UserWithCalculations(
        Long id,
        String login,
        String name,
        String type,
        String avatarUrl,
        ZonedDateTime createdAt,
        Double calculations
) {

    public UserWithCalculations(User user, Double calculations) {
        this(
                user.id(),
                user.login(),
                user.name(),
                user.type(),
                user.avatarUrl(),
                user.createdAt(),
                calculations
        );
    }
}
