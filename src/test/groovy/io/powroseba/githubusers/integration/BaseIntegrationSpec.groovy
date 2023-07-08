package io.powroseba.githubusers.integration

import com.github.tomakehurst.wiremock.WireMockServer
import io.powroseba.githubusers.AppRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class BaseIntegrationSpec extends Specification {

    @Autowired
    private WireMockServer wireMockServer

    def cleanup() {
        wireMockServer.resetAll()
    }
}
