package io.powroseba.githubusers.infrastructure.api;

import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.UserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
class UserEndpoint {

    private final UserProvider userProvider;

    UserEndpoint(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @GetMapping("/{login}")
    public ResponseEntity<?> getUser(@PathVariable("login") Login login) {
        return userProvider.get(login)
                .map(user -> ResponseEntity.ok().build())
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
