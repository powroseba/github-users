package io.powroseba.githubusers.infrastructure.userprovider;

import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.User;
import io.powroseba.githubusers.domain.UserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Function;

@Component
class UserProviderAdapter implements UserProvider {

    private final WebClient webClient;

    UserProviderAdapter(@Value("${providers.user.url}") String url) {
        this.webClient = WebClient.create(url);
    }

    @Override
    public Optional<User> get(Login login) {
        return webClient.get()
                .uri("/{login}", login.value())
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), errorFunction())
                .bodyToMono(User.class)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .blockOptional();
    }

    private Function<ClientResponse, Mono<? extends Throwable>> errorFunction() {
        return response -> Mono.error(WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(),
                "user does not found",
                response.headers().asHttpHeaders(),
                null,
                Charset.defaultCharset()
        ));
    }
}