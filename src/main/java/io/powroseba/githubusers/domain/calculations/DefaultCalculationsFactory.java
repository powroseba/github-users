package io.powroseba.githubusers.domain.calculations;

import io.powroseba.githubusers.domain.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
class DefaultCalculationsFactory implements CalculationsFactory {

    @Override
    public Set<Calculation> calculations(User user) {
        return Set.of(
                new EmpikCalculation(() -> user),
                new AllCodeSourcesCountCalculation(() -> user)
        );
    }
}
