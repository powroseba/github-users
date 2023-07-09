package io.powroseba.githubusers.domain;

import java.util.Optional;

public interface UserService {

    Optional<UserWithCalculations> get(Login login);
}
