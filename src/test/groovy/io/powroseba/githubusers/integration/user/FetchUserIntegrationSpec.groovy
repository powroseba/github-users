package io.powroseba.githubusers.integration.user

import com.fasterxml.jackson.databind.JsonNode
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import io.powroseba.githubusers.integration.BaseIntegrationSpec
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.reactive.server.WebTestClient

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.get

class FetchUserIntegrationSpec extends BaseIntegrationSpec implements UserJsonFixture {

    @LocalServerPort
    private int port
    @Autowired
    private WireMockServer wireMockServer
    @Autowired
    private JdbcTemplate jdbcTemplate

    private WebTestClient webClient

    def setup() {
        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:${port}").build()
    }

    def 'should response bad request for empty login'() {
        expect:
        webClient
                .get()
                .uri("/users/  ")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath('message').value(Matchers.is("Login cannot be empty!"))
    }

    def 'should return not found when requested user does not exist'() {
        given:
        final String login = "notExistingLogin"

        and: 'user provider request return not found status'
        get("/users/${login}")
                .willReturn(aResponse().withStatus(404))
                .tap { mock(it) }


        when:
        def response = fetchUserForLogin(login)

        then:
        response.expectStatus().isNotFound()
    }

    def 'should return unprocessable entity when provider request failed'() {
        given:
        final String login = "unprocessableLogin"

        and:
        "user provider request failed with status ${status}"
        get("/users/${login}")
                .willReturn(aResponse().withStatus(status.value()))
                .tap { mock(it) }

        when:
        def response = fetchUserForLogin(login)

        then:
        response.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)

        where:
        status << [
                HttpStatus.BAD_REQUEST,
                HttpStatus.INTERNAL_SERVER_ERROR,
        ]
    }

    def 'should return unprocessable entity when provider return invalid data'() {
        given:
        final String login = "unprocessableLogin-invalidData"
        final JsonNode providedUserJson = providedUserJson(
                [login: login] + invalidParam
        )

        and: "user provider request with invalid user data"
        get("/users/${login}")
                .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withJsonBody(providedUserJson))
                .tap { mock(it) }

        when:
        def response = fetchUserForLogin(login)

        then:
        response.expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath('message').value(Matchers.is(expectedMessage))

        where:
        invalidParam         || expectedMessage
        [id: null]           || "id is required"
        [login: null]        || "login is required"
        [login: ""]          || "login is required"
        [login: "  "]        || "login is required"
        [name: null]         || "name is required"
        [name: ""]           || "name is required"
        [name: "  "]         || "name is required"
        [type: null]         || "type is required"
        [type: ""]           || "type is required"
        [type: " "]          || "type is required"
        [avatar_url: null]   || "avatarUrl is required"
        [avatar_url: ""]     || "avatarUrl is required"
        [avatar_url: "  "]   || "avatarUrl is required"
        [created_at: null]   || "createdAt is required"
        [followers: null]    || "followersCount is required"
        [public_repos: null] || "publicRepositoriesCount is required"
    }

    def 'should return user data with calculations'() {
        given:
        final String login = "login"
        final JsonNode providedUserJson = providedUserJson(
                login: login,
                followers: followersCount,
                public_repos: publicRepositoriesCount
        )

        and: "user provider request with user data"
        get("/users/${login}")
                .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withJsonBody(providedUserJson))
                .tap { mock(it) }

        when:
        def response = fetchUserForLogin(login)

        then:
        response.expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody()
                .json(userWithCalculationJsonFrom(providedUserJson, expectedCalculation))

        where:
        followersCount | publicRepositoriesCount || expectedCalculation
        0              | 1                       || 0
        2              | 3                       || (6 / followersCount.doubleValue() * (2 + publicRepositoriesCount).doubleValue())
        7              | 2                       || (6 / followersCount.doubleValue() * (2 + publicRepositoriesCount).doubleValue())
    }

    def 'should increase counter in database for every user data request'() {
        given:
        final String login = "loginWithCounter"
        final JsonNode providedUserJson = providedUserJson(
                login: login
        )

        expect:
        thereIsNoRequestCounterInDbForLogin(login)

        and: "user provider request with user data"
        get("/users/${login}")
                .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withJsonBody(providedUserJson))
                .tap { mock(it) }

        when:
        def response = fetchUserForLogin(login)

        then:
        response.expectStatus().isEqualTo(HttpStatus.OK)
        counterWasIncreasedTo(login, 1)

        when:
        def anotherResponse = fetchUserForLogin(login)

        then:
        anotherResponse.expectStatus().isEqualTo(HttpStatus.OK)
        counterWasIncreasedTo(login, 2)
    }

    private void thereIsNoRequestCounterInDbForLogin(String login) {
        assert jdbcTemplate.queryForList("SELECT * FROM LOGIN_REQUESTS WHERE LOGIN = ?", login).isEmpty()
    }

    private void counterWasIncreasedTo(String login, Integer expectedCounterValue) {
        var counterValue = jdbcTemplate.queryForObject(
                "SELECT REQUEST_COUNT FROM LOGIN_REQUESTS WHERE LOGIN = ?", Long.class, login
        )
        assert counterValue == expectedCounterValue
    }

    private WebTestClient.ResponseSpec fetchUserForLogin(String login) {
        webClient
                .get()
                .uri("/users/$login")
                .exchange()
    }

    private void mock(MappingBuilder mappingBuilder) {
        wireMockServer.stubFor(mappingBuilder)
    }
}