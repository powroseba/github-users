package io.powroseba.githubusers.integration


import io.powroseba.githubusers.AppRunner
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationSpec extends Specification {
}
