package io.powroseba.githubusers.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

class AppRunnerSpec extends BaseIntegrationSpec {

    @Autowired
    private ApplicationContext context

    def 'should load context'() {
        expect:
        context != null
    }
}
