package io.powroseba.githubusers.domain;

import java.util.Optional;

public interface UserProvider {

    Optional<User> get(Login login);
}
