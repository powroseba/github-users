package io.powroseba.githubusers.infrastructure.api;

import io.powroseba.githubusers.domain.UserProvider;
import io.powroseba.githubusers.domain.UserService;
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
    public ResponseEntity<Dto.UserDto> getUser(@PathVariable("login") Dto.Login loginDto) {
        return userService.get(loginDto.login())
                .map(Dto.UserDto::new)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    private ResponseEntity<Dto.Error> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new Dto.Error(exception));
    }

    @ExceptionHandler({UserProvider.Exception.class, IllegalStateException.class})
    private ResponseEntity<Dto.Error> handleUserProviderError(RuntimeException exception) {
        return ResponseEntity.unprocessableEntity().body(new Dto.Error(exception));
    }

}
