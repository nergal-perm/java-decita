/*
 * MIT License
 *
 * Copyright (c) 2024 Eugene Terekhov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.ewc.decisions.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.commands.Assignment;
import ru.ewc.decisions.conditions.Condition;

/**
 * I am a single Rule (i.e. the column in the decision table). My main responsibility is to check
 * whether all my second-order {@link Condition}s are {@code true} and if so - compute my outcomes.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class Rule {
    /**
     * The rule's {@link Condition}s.
     */
    private final List<Condition> conditions;

    /**
     * The rule's outcomes.
     */
    private final Map<String, String> outcomes;

    /**
     * The rule's assignments.
     */
    private final List<Assignment> assignments;

    /**
     * The rule's name.
     */
    private final String name;

    public Rule(final String name) {
        this.name = name;
        this.outcomes = new HashMap<>();
        this.conditions = new ArrayList<>(5);
        this.assignments = new ArrayList<>(2);
    }

    /**
     * Adds a {@link Condition} to this rule. The condition is created from the given string
     * description of the base {@code Coordinate} and the value to compare it with. The value can
     * be a constant or another {@code Coordinate} description.
     *
     * @param target The String description of the left-hand side {@code Coordinate} to add.
     * @param value The String representation of the right-hand side {@code Coordinate} to add.
     * @return Itself, in order to implement fluent API.
     */
    public Rule withCondition(final String target, final String value) {
        this.conditions.add(Condition.from(Coordinate.from(target), value));
        return this;
    }

    /**
     * Adds an outcome to this rule.
     *
     * @param outcome The key of an outcome to add.
     * @param value The string representation of an outcome's value to add.
     * @return Itself, in order to implement fluent API.
     */
    public Rule withOutcome(final String outcome, final String value) {
        this.outcomes.put(outcome, value);
        return this;
    }

    /**
     * Adds an assignment to this rule.
     *
     * @param target The String description of the left-hand side {@code Coordinate} of the
     *  assignment.
     * @param value The String representation of the right-hand side {@code Coordinate} of the
     *  assignment.
     * @return Itself, in order to implement fluent API.
     */
    public Rule withAssignment(final String target, final String value) {
        this.assignments.add(new Assignment(target, value));
        return this;
    }

    /**
     * Checks whether this rule is computed, i.e. all of its {@link Condition}s were resolved
     * successfully.
     *
     * @return True if the rule is computed.
     */
    public boolean isComputed() {
        return this.conditions.stream().allMatch(Condition::isEvaluated);
    }

    /**
     * Checks whether this rule is not satisfied (i.e. any of its {@link Condition}s resolved to
     * {@code false}).
     *
     * @return True if the rule is dissatisfied.
     */
    public boolean isEliminated() {
        return this.conditions.stream().anyMatch(Condition::isNotSatisfied);
    }

    /**
     * Checks whether this rule is satisfied (i.e. all of its {@link Condition}s are computed and
     * resolved to {@code true}).
     *
     * @return True if the rule is satisfied.
     */
    public boolean isSatisfied() {
        return this.conditions.stream().allMatch(c -> c.isEvaluated() && !c.isNotSatisfied());
    }

    /**
     * Checks if this {@link Rule} is satisfied.
     *
     * @param context The {@link ComputationContext}'s instance.
     * @throws DecitaException If the rule's {@link Condition}s could not be resolved.
     */
    public void check(final ComputationContext context) throws DecitaException {
        while (!this.isSatisfied() && !this.isEliminated()) {
            this.conditions.stream()
                .filter(c -> !c.isEvaluated())
                .findFirst()
                .ifPresent(c -> c.evaluate(context));
        }
        context.logComputation(
            OutputTracker.EventType.RL,
            "%s => %s".formatted(this.asString(), this.isSatisfied())
        );
    }

    /**
     * Performs a test based on this rule. This effectively means running all the assignments first,
     * then executing the specified command and checking all the conditions after that.
     *
     * @param context The {@link ComputationContext} to perform the test in. This is just the base
     *  for creating an empty-state copy for the test.
     * @return A list of strings, containing the results of the test. Will be empty if the test
     *  passed.
     */
    public List<String> test(final ComputationContext context) {
        final ComputationContext copy = context.emptyStateCopy();
        this.assignments.forEach(a -> a.performIn(copy));
        if (this.outcomes.containsKey("execute")) {
            copy.perform(this.outcomes.get("execute"));
        }
        return this.conditions.stream()
            .filter(c -> !c.evaluate(copy))
            .map(c -> "Expected:%n\t%s%nbut was:%n\t%s".formatted(c.asString(), c.result()))
            .toList();
    }

    /**
     * Returns this rule outcomes.
     *
     * @return The simple dictionary, containing all this rule's outcomes.
     */
    public Map<String, String> outcome() {
        return this.outcomes;
    }

    /**
     * Performs all the assignments in this rule, effectively performing the state-changing command.
     *
     * @param context The {@link ComputationContext} to perform the assignments in.
     */
    public void perform(final ComputationContext context) {
        this.assignments.forEach(a -> a.performIn(context));
    }

    /**
     * Checks whether this rule describes a command.
     *
     * @return True if this rule describes a command.
     */
    public boolean describesCommand() {
        return !this.assignments.isEmpty();
    }

    public List<String> commandArgs() {
        return this.assignments.stream()
            .map(Assignment::commandArgs)
            .flatMap(List::stream)
            .toList();
    }

    public String asString() {
        return this.name;
    }
}
