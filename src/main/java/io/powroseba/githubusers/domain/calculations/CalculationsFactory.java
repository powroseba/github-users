package io.powroseba.githubusers.domain.calculations;

import io.powroseba.githubusers.domain.User;

import java.util.Set;

public interface CalculationsFactory {

    Set<Calculation> calculations(User user);
}
