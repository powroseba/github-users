package io.powroseba.githubusers.infrastructure.datasource;

import io.powroseba.githubusers.domain.User;
import io.powroseba.githubusers.domain.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Service
class AttemptCounterUserServiceDecorator implements UserService {

    private static final String UPDATE_COUNTER = "UPDATE LOGIN_REQUESTS set request_count = request_count + 1 WHERE login = ?";
    private static final String INSERT_COUNTER = "INSERT INTO LOGIN_REQUESTS (login, request_count) VALUES (?, 1)";

    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    public AttemptCounterUserServiceDecorator(UserService userService, JdbcTemplate jdbcTemplate) {
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public Optional<User.UserWithCalculations> get(User.Login login) {
        var result = userService.get(login);
        increaseCounter(login);
        return result;
    }

    private void increaseCounter(User.Login login) {
        var result = jdbcTemplate.update(UPDATE_COUNTER, login.value());
        if (result == 0) {
            jdbcTemplate.update(INSERT_COUNTER, login.value());
        }
    }

}
