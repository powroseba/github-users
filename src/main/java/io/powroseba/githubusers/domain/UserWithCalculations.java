package io.powroseba.githubusers.domain;

import io.powroseba.githubusers.domain.calculations.Calculation;

import java.util.Set;

public record UserWithCalculations(User user, Set<Calculation> calculations) {}
