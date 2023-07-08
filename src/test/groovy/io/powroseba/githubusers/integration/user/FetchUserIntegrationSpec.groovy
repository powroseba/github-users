package io.powroseba.githubusers.integration.user

import io.powroseba.githubusers.infrastructure.api.UserEndpoint
import io.powroseba.githubusers.integration.BaseIntegrationSpec
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

class FetchUserIntegrationSpec extends BaseIntegrationSpec {

    @LocalServerPort
    private int port

    @Autowired
    private UserEndpoint userEndpoint
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
}
