package io.powroseba.githubusers.domain.calculations;

import io.powroseba.githubusers.domain.User;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class AllCodeSourcesCountCalculation extends Calculation {

    protected AllCodeSourcesCountCalculation(Supplier<User.Properties> user) {
        super(user);
    }

    @Override
    public Optional<String> description() {
        return Optional.of("All code sources count");
    }

    @Override
    protected Function<User.Properties, Number> equation() {
        return user -> user.publicRepositoriesCount() + user.publicGistsCount();
    }
}
