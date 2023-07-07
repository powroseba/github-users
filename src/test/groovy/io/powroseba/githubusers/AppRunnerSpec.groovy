package io.powroseba.githubusers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@SpringBootTest
class AppRunnerSpec extends Specification {

    @Autowired
    private ApplicationContext context

    def 'should load context'() {
        expect:
        context != null
    }
}
