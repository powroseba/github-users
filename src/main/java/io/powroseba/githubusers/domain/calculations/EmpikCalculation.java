package io.powroseba.githubusers.domain.calculations;

import io.powroseba.githubusers.domain.User;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class EmpikCalculation extends Calculation {

    EmpikCalculation(Supplier<User.Properties> user) {
        super(user);
    }

    @Override
    public Optional<String> description() {
        return Optional.of("Empik calculation");
    }

    @Override
    public Function<User.Properties, Number> equation() {
        return user -> {
            final Double followers = user.followersCount().doubleValue();
            if (followers.equals(0d)) {
                return 0d;
            }
            return 6 / followers * (2 + user.publicRepositoriesCount().doubleValue());
        };
    }
}
