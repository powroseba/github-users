package io.powroseba.githubusers.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.User;
import io.powroseba.githubusers.domain.UserProvider;
import io.powroseba.githubusers.domain.UserService;
import io.powroseba.githubusers.domain.UserWithCalculations;
import io.powroseba.githubusers.domain.calculations.Calculation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

@RestController
@RequestMapping("/users")
class UserEndpoint {

    private final UserService userService;

    UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserDto> getUser(@PathVariable("login") Login login) {
        return userService.get(login)
                .map(UserDto::new)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new Error(exception));
    }

    @ExceptionHandler({UserProvider.Exception.class, IllegalStateException.class})
    public ResponseEntity<Error> handleUserProviderError(RuntimeException exception) {
        return ResponseEntity.unprocessableEntity().body(new Error(exception));
    }
    private record Error(String message) {
        public Error(Throwable throwable) {
            this(throwable.getMessage());
        }
    }

    private record UserDto(
            @JsonUnwrapped
            @JsonIgnoreProperties(value = {"followersCount", "publicRepositoriesCount", "createdAt", "publicGistsCount"})
            User user,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssZ")
            ZonedDateTime createdAt,
            CalculationDto[] calculations
    ) {

        private UserDto(UserWithCalculations userWithCalculations) {
            this(
                    userWithCalculations.user(),
                    userWithCalculations.user().createdAt(),
                    userWithCalculations.calculations()
                            .stream()
                            .map(CalculationDto::new)
                            .toArray(CalculationDto[]::new)
            );
        }
    }

    private static class CalculationDto {

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
}
