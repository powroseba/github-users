package io.powroseba.githubusers.domain;

import io.powroseba.githubusers.domain.calculations.Calculation;

import java.time.ZonedDateTime;
import java.util.Set;

public interface User {
    record Properties(
            Long id,
            Login login,
            String name,
            String type,
            String avatarUrl,
            ZonedDateTime createdAt,
            Integer followersCount,
            Integer publicRepositoriesCount,
            Integer publicGistsCount
    ) {

        public Properties {
            requireNotNull(id, "id");
            requireNotNull(login, "login");
            requireNotBlank(name, "name");
            requireNotBlank(type, "type");
            requireNotBlank(avatarUrl, "avatarUrl");
            requireNotNull(createdAt, "createdAt");
            requireNotNull(followersCount, "followersCount");
            requireNotNull(publicRepositoriesCount, "publicRepositoriesCount");
            requireNotNull(publicGistsCount, "publicGistsCount");
        }

        public UserWithCalculations withCalculations(Set<Calculation> calculations) {
            return new UserWithCalculations(this, calculations);
        }

        public String loginAsString() {
            return login.value();
        }

        private <T> T requireNotNull(T value, String propertyName) {
            if (value == null) {
                throw new IllegalStateException(propertyName + " is required");
            }
            return value;
        }

        private String requireNotBlank(String value, String propertyName) {
            if (requireNotNull(value, propertyName).isBlank()) {
                throw new IllegalStateException(propertyName + " is required");
            }
            return value;
        }

    }

    record Login(String value) {

        public Login {
            if (value == null || value.isBlank()) {
                throw new IllegalStateException("login is required");
            }
        }
    }

    record UserWithCalculations(Properties user, Set<Calculation> calculations) {}
}
