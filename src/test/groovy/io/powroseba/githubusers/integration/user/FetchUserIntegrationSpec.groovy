package io.powroseba.githubusers.integration.user

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import io.powroseba.githubusers.integration.BaseIntegrationSpec
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get

class FetchUserIntegrationSpec extends BaseIntegrationSpec {

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
                .expectBody().jsonPath('message').value(Matchers.equalTo("Login cannot be empty!"))
    }

    def 'should return not found when requested user does not exist'() {
        given:
        final String notExistingLogin = "notExistingLogin"

        and: 'user provider request return not found status'
        get("/users/${notExistingLogin}")
                .willReturn(aResponse().withStatus(404))
                .tap { mock(it)}


        when:
        def response = webClient
                .get()
                .uri("/users/$notExistingLogin")
                .exchange()

        then:
        response.expectStatus().isNotFound()
    }

    private void mock(MappingBuilder mappingBuilder) {
        wireMockServer.stubFor(mappingBuilder)
    }

    /**
     * should return user data with calculations (mock user provider)
     * should increase request counter for specific value
     */
}
