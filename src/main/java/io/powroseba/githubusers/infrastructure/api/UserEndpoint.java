package io.powroseba.githubusers.infrastructure.api;

import io.powroseba.githubusers.domain.UserLogin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserEndpoint {

    @GetMapping("/{login}")
    public ResponseEntity<?> getUser(@PathVariable("login") UserLogin login) {
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new Error(exception.getMessage()));
    }

    private record Error(String message) {}
}
