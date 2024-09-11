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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import ru.ewc.decisions.api.CheckFailure;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.commands.Assignment;
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.decisions.input.SourceLines;

/**
 * I am a single Rule (i.e. the column in the decision table). My main responsibility is to check
 * whether all my second-order {@link Condition}s are {@code true} and if so - compute my outcomes.
 *
 * @since 0.1
 */
@EqualsAndHashCode
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class Rule {
    /**
     * The rule's {@link Condition}s.
     */
    private final List<Condition> conditions;

    /**
     * The rule's assignments.
     */
    private final List<Assignment> assignments;

    /**
     * The rule's name.
     */
    private final String name;

    /**
     * The rule's fragments, i.e. the triplets containing the type, left and right parts.
     */
    private final List<RuleFragment> fragments;

    public Rule(final String name, final List<RuleFragment> fragments) {
        this.name = name;
        this.fragments = fragments;
        this.conditions = new ArrayList<>(5);
        this.assignments = new ArrayList<>(2);
    }

    public static Rule from(final SourceLines source, final int idx) {
        final List<RuleFragment> fragments = source.asFragments(idx);
        final RuleFragment header = fragments.stream()
            .filter(f -> "HDR".equals(f.type()))
            .findFirst()
            .orElse(new RuleFragment("HDR", source.fileName(), "rule_%02d".formatted(idx - 1)));
        final Rule result = new Rule("%s::%s".formatted(header.left(), header.right()), fragments);
        fragments.forEach(
            f -> {
                switch (f.type()) {
                    case "CND" -> result.conditions.add(Condition.from(f));
                    case "ASG" -> result.assignments.add(new Assignment(f.left(), f.right()));
                    default -> {
                    }
                }
            }
        );
        return result;
    }

    public static Rule elseRule(final String name) {
        return new Rule(
            "%s::else".formatted(name),
            List.of(new RuleFragment("OUT", "outcome", "undefined"))
        );
    }

    /**
     * Checks if this {@link Rule} is satisfied.
     *
     * @param context The {@link ComputationContext}'s instance.
     * @return True if the rule is satisfied.
     * @throws DecitaException If the rule's {@link Condition}s could not be resolved.
     */
    public boolean check(final ComputationContext context) throws DecitaException {
        final boolean result = this.conditions.stream()
            .allMatch(c -> c.evaluate(context));
        context.logComputation(
            OutputTracker.EventType.RL,
            "%s => %s".formatted(this.asString(), result)
        );
        return result;
    }

    /**
     * Performs a test based on this rule. This effectively means running all the assignments first,
     * then executing the specified command and checking all the conditions after that.
     *
     * @param context The {@link ComputationContext} to perform the test in. This is just the base
     *  for creating an empty-state copy for the test.
     * @return A list of {@link CheckFailure} objects, each of which describes a single failing
     *  assertion. Will be empty if the test passed successfully.
     */
    public List<CheckFailure> test(final ComputationContext context) {
        final ComputationContext copy = context.emptyStateCopy();
        this.assignments.forEach(a -> a.performIn(copy));
        if (this.outcome().containsKey("execute")) {
            copy.perform(this.outcome().get("execute"));
        }
        copy.reloadTables();
        return this.conditions.stream()
            .filter(c -> !c.evaluate(copy))
            .map(c -> new CheckFailure(c.asString(), c.result()))
            .toList();
    }

    /**
     * Returns this rule outcomes.
     *
     * @return The simple dictionary, containing all this rule's outcomes.
     */
    public Map<String, String> outcome() {
        return this.fragments.stream()
            .filter(f -> "OUT".equals(f.type()))
            .collect(Collectors.toMap(RuleFragment::left, RuleFragment::right));
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
