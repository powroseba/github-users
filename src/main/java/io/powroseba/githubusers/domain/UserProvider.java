package io.powroseba.githubusers.domain;

import java.util.Optional;

public interface UserProvider {

    Optional<User.Properties> get(User.Login login);

    class Exception extends RuntimeException {

        public Exception(Throwable throwable) {
            super(throwable);
        }
    }
}
