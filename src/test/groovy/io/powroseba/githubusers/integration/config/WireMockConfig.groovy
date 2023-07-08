package io.powroseba.githubusers.integration.config

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@Configuration
class WireMockConfig {

    @Bean(destroyMethod = 'stop')
    WireMockServer wireMockServer() {
        def configuration = wireMockConfig()
                .dynamicPort()
        WireMockServer wireMockServer = new WireMockServer(configuration)
        wireMockServer.start()
        return wireMockServer
    }
}
