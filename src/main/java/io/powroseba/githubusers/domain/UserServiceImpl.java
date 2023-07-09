package io.powroseba.githubusers.domain;

import io.powroseba.githubusers.domain.calculations.CalculationsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserProvider userProvider;
    private final CalculationsFactory calculationsFactory;

    public UserServiceImpl(UserProvider userProvider, CalculationsFactory calculationsFactory) {
        this.userProvider = userProvider;
        this.calculationsFactory = calculationsFactory;
    }

    public Optional<UserWithCalculations> get(Login login) {
        log.info("Fetching user with login {}", login.value());
        return userProvider.get(login)
                .map(this::addCalculations);
    }

    private UserWithCalculations addCalculations(User user) {
        return user.withCalculations(calculationsFactory.calculations(user));
    }
}
