package io.powroseba.githubusers.domain.calculations

import io.powroseba.githubusers.domain.User
import spock.lang.Specification

import java.time.ZonedDateTime

class EmpikCalculationSpec extends Specification {

    def 'should properly calculate empik equation'() {
        given:
        final Calculation empikCalculation = new EmpikCalculation(
                { -> user(followers, publicRepositoriesCount) }
        )

        expect:
        final def result = empikCalculation.value().get()
        if (followers == 0) {
            result == 0
        } else {
            result == 6 / followers * (2 + publicRepositoriesCount)
        }

        where:
        followers   | publicRepositoriesCount
        3d          | 2d
        7d          | 3d
        0d          | 10d
    }

    private User user(Double followers, Double publicRepositoriesCount) {
        return new User(
                1, "_", "_", "_", "_", ZonedDateTime.now(),
                followers.longValue(), publicRepositoriesCount.longValue()
        )
    }
}
