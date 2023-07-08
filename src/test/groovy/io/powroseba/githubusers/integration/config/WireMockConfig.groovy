package io.powroseba.githubusers.integration.config

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@Configuration
class WireMockConfig {

    @Value("\${wiremock.port}")
    private Integer wiremockPort

    @Bean(destroyMethod = 'stop')
    WireMockServer wireMockServer() {
        def configuration = wireMockConfig().port(wiremockPort)
        WireMockServer wireMockServer = new WireMockServer(configuration)
        wireMockServer.start()
        return wireMockServer
    }
}
