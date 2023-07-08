package io.powroseba.githubusers.domain;

public record UserLogin(String login) {

    public UserLogin {
        if (login.isBlank()) {
            throw new IllegalArgumentException("Login cannot be empty!");
        }
    }
}
