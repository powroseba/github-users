package io.powroseba.githubusers.domain;

import java.util.Optional;

public interface UserService {

    Optional<User.UserWithCalculations> get(User.Login login);
}
