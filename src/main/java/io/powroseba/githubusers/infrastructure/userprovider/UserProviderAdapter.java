package io.powroseba.githubusers.infrastructure.userprovider;

import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.User;
import io.powroseba.githubusers.domain.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Optional;

@Component
class UserProviderAdapter implements UserProvider {

    private static final Logger log = LoggerFactory.getLogger(UserProviderAdapter.class);
    private final WebClient webClient;

    UserProviderAdapter(@Value("${providers.user.url}") String url) {
        this.webClient = WebClient.create(url);
    }

    @Override
    public Optional<User> get(Login login) {
        return Optional.ofNullable(webClient.get()
                .uri("/{login}", login.value())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), this::clientError)
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::serverError)
                .bodyToMono(User.class)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorMap(WebClientResponseException.UnprocessableEntity.class, Exception::new)
                .block());
    }

    private Mono<Throwable> clientError(ClientResponse response) {
        return error(HttpStatus.NOT_FOUND, "User does not found", response);
    }

    private Mono<Throwable> serverError(ClientResponse response) {
        log.error("Provider request failed with response {}", response);
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "Request failed", response);
    }

    private Mono<Throwable> error(HttpStatus unprocessableEntity, String message, ClientResponse response) {
        return Mono.error(WebClientResponseException.create(
                unprocessableEntity.value(),
                message,
                response.headers().asHttpHeaders(),
                null,
                Charset.defaultCharset()
        ));
    }
}