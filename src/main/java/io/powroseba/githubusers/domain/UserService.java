package io.powroseba.githubusers.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserProvider userProvider;

    public UserService(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public Optional<UserWithCalculations> get(Login login) {
        log.info("Fetching user with login {}", login.value());
        return userProvider.get(login)
                .map(this::toUserWithCalculations);
    }

    private UserWithCalculations toUserWithCalculations(User user) {
        return new UserWithCalculations(user, calculation(user));
    }

    private Double calculation(User user) {
        if (user.followersCount().equals(0L)) {
            return 0d;
        }
        return 6 / user.followersCount().doubleValue() * (2 + user.publicRepositoriesCount().doubleValue());
    }
}