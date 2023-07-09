package io.powroseba.githubusers.domain.calculations

import io.powroseba.githubusers.domain.User
import io.powroseba.githubusers.domain.fixture.UserFixture
import spock.lang.Specification

class AllCodeSourcesCountCalculationSpec extends Specification {

    def 'should properly calculate empik equation'() {
        given:
        final Calculation allCodeSourcesCountCalculation = new AllCodeSourcesCountCalculation(
                { -> user(publicRepositoriesCount, publicGists) }
        )

        expect:
        allCodeSourcesCountCalculation.value().get() == publicRepositoriesCount + publicGists


        where:
        publicRepositoriesCount | publicGists
        0                       | 0
        3                       | 7
        10                      | 123
    }

    private User user(Long publicRepositoriesCount, Long publicGists) {
        return UserFixture.user([
                publicRepositoriesCount: publicRepositoriesCount,
                publicGistsCount: publicGists
        ])
    }


}
