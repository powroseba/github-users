package io.powroseba.githubusers.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserProvider userProvider;

    public UserService(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public Optional<UserWithCalculations> get(Login login) {
        log.info("Fetching user with login {}", login.value());
        return userProvider.get(login)
                .map(this::addCalculations);
    }

    private UserWithCalculations addCalculations(User user) {
        return user.withCalculations(calculation(user));
    }

    private Double calculation(User user) {
        if (user.followersCount().equals(0L)) {
            return 0d;
        }
        return 6 / user.followersCount().doubleValue() * (2 + user.publicRepositoriesCount().doubleValue());
    }
}
