package io.powroseba.githubusers.infrastructure.datasource;

import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.UserProvider;
import io.powroseba.githubusers.domain.UserService;
import io.powroseba.githubusers.domain.UserWithCalculations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class AttemptCounterUserServiceDecorator extends UserService {

    private static final String UPDATE_COUNTER = "UPDATE LOGIN_REQUESTS set request_count = request_count + 1 WHERE login = ?";
    private static final String INSERT_COUNTER = "INSERT INTO LOGIN_REQUESTS (login, request_count) VALUES (?, 1)";

    private final JdbcTemplate jdbcTemplate;

    public AttemptCounterUserServiceDecorator(UserProvider userProvider, JdbcTemplate jdbcTemplate) {
        super(userProvider);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public Optional<UserWithCalculations> get(Login login) {
        var result = super.get(login);
        increaseCounter(login);
        return result;
    }

    private void increaseCounter(Login login) {
        var result = jdbcTemplate.update(UPDATE_COUNTER, login.value());
        if (result == 0) {
            jdbcTemplate.update(INSERT_COUNTER, login.value());
        }
    }

}
