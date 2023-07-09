package io.powroseba.githubusers.domain.calculations;

import io.powroseba.githubusers.domain.User;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Calculation {

    protected final Supplier<User.Properties> user;

    protected Calculation(Supplier<User.Properties> user) {
        this.user = user;
    }

    public Optional<String> description() {
        return Optional.empty();
    }

    public Supplier<Number> value() {
        return () -> equation().apply(user.get());
    }

    protected abstract Function<User.Properties, Number> equation();
}
