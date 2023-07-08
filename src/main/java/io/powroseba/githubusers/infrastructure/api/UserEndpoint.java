package io.powroseba.githubusers.infrastructure.api;

import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.UserProvider;
import io.powroseba.githubusers.domain.UserService;
import io.powroseba.githubusers.domain.UserWithCalculations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
class UserEndpoint {

    private final UserService userService;

    UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserWithCalculations> getUser(@PathVariable("login") Login login) {
        return userService.get(login)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new Error(exception));
    }

    @ExceptionHandler({UserProvider.Exception.class})
    public ResponseEntity<Error> handleUserProviderError(UserProvider.Exception exception) {
        return ResponseEntity.unprocessableEntity().body(new Error(exception));
    }

    private record Error(String message) {
        public Error(Throwable throwable) {
            this(throwable.getMessage());
        }
    }
}
