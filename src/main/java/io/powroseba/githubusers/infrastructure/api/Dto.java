package io.powroseba.githubusers.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.powroseba.githubusers.domain.User;
import io.powroseba.githubusers.domain.calculations.Calculation;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

interface Dto {

    record Login(String value) {
        public Login {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Login cannot be empty!");
            }
        }

        User.Login login() {
            return new User.Login(value);
        }
    }

    record UserDto(
            @JsonUnwrapped
            @JsonIgnoreProperties(value = {
                    "followersCount", "publicRepositoriesCount", "createdAt", "publicGistsCount", "login"
            })
            User.Properties user,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssZ")
            ZonedDateTime createdAt,
            String login,
            CalculationDto[] calculations
    ) {

        UserDto(User.UserWithCalculations userWithCalculations) {
            this(
                    userWithCalculations.user(),
                    userWithCalculations.user().createdAt(),
                    userWithCalculations.user().loginAsString(),
                    userWithCalculations.calculations()
                            .stream()
                            .map(CalculationDto::new)
                            .toArray(CalculationDto[]::new)
            );
        }
    }

    class CalculationDto {

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public final String description;
        private final Supplier<Number> value;

        private CalculationDto(Calculation calculation) {
            this.description = calculation.description().orElse(null);
            this.value = calculation.value();
        }

        @JsonProperty("value")
        public Number value() {
            return value.get();
        }
    }

    record Error(String message) {
        public Error(Throwable throwable) {
            this(throwable.getMessage());
        }
    }
}
