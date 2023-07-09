package io.powroseba.githubusers.infrastructure.userprovider;

import io.netty.handler.logging.LogLevel;
import io.powroseba.githubusers.domain.Login;
import io.powroseba.githubusers.domain.User;
import io.powroseba.githubusers.domain.UserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
class UserProviderAdapter implements UserProvider {

    private final WebClient webClient;

    UserProviderAdapter(@Value("${providers.user.url}") String url) {
        final var clientConnector = new ReactorClientHttpConnector(
                HttpClient.create().wiretap(
                        this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL
                )
        );
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(clientConnector)
                .build();
    }

    @Override
    public Optional<User> get(Login login) {
        return webClient.get()
                .uri("/{login}", login.value())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), this::clientError)
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::serverError)
                .bodyToMono(UserDto.class)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorMap(WebClientResponseException.UnprocessableEntity.class, Exception::new)
                .blockOptional()
                .map(UserDto::toUser);
    }

    private Mono<Throwable> clientError(ClientResponse response) {
        return error(HttpStatus.NOT_FOUND, "User does not found", response);
    }

    private Mono<Throwable> serverError(ClientResponse response) {
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

    private record UserDto(
            Long id,
            String login,
            String name,
            String type,
            String avatar_url,
            ZonedDateTime created_at,
            Integer followers,
            Integer public_repos,
            Integer public_gists
    ) {

        private User toUser() {
            return new User(
                    id,
                    login,
                    name,
                    type,
                    avatar_url,
                    created_at,
                    followers,
                    public_repos,
                    public_gists
            );
        }
    }
}