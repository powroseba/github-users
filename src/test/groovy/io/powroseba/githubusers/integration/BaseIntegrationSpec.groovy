package io.powroseba.githubusers.integration

import com.github.tomakehurst.wiremock.WireMockServer
import io.powroseba.githubusers.AppRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationSpec extends Specification {

    @Autowired
    private WireMockServer wireMockServer

    def cleanup() {
        wireMockServer.resetAll()
    }
}
