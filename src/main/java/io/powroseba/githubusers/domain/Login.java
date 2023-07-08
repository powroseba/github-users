package io.powroseba.githubusers.domain;

public record Login(String value) {

    public Login {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Login cannot be empty!");
        }
    }
}
