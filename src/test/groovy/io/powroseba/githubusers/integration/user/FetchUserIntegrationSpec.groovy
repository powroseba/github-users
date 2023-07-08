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
import org.springframework.test.web.reactive.server.WebTestClient

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.get

class FetchUserIntegrationSpec extends BaseIntegrationSpec implements UserJsonFixture {

    @LocalServerPort
    private int port

    @Autowired
    private WireMockServer wireMockServer

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
        final String notExistingLogin = "notExistingLogin"

        and: 'user provider request return not found status'
        get("/users/${notExistingLogin}")
                .willReturn(aResponse().withStatus(404))
                .tap { mock(it) }


        when:
        def response = webClient
                .get()
                .uri("/users/$notExistingLogin")
                .exchange()

        then:
        response.expectStatus().isNotFound()
    }

    def 'should return unprocessable entity when provider request failed '() {
        given:
        final String unprocessableLogin = "unprocessableLogin"

        and:
        "user provider request failed with status ${status}"
        get("/users/${unprocessableLogin}")
                .willReturn(aResponse().withStatus(status.value()))
                .tap { mock(it) }

        when:
        def response = webClient
                .get()
                .uri("/users/$unprocessableLogin")
                .exchange()

        then:
        response.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)

        where:
        status << [
                HttpStatus.BAD_REQUEST,
                HttpStatus.INTERNAL_SERVER_ERROR,
        ]
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
        def response = webClient
                .get()
                .uri("/users/$login")
                .exchange()

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

    private void mock(MappingBuilder mappingBuilder) {
        wireMockServer.stubFor(mappingBuilder)
    }

    /**
     * should increase request counter for specific value
     */
}