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

import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.api.RuleFragments;
import ru.ewc.decisions.commands.Assignment;
import ru.ewc.decisions.conditions.Condition;

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
     * The rule's name.
     */
    private final String name;

    /**
     * The rule's fragments.
     */
    private final DecisionRuleFragments fragments;

    public Rule(final String name, final DecisionRuleFragments fragments) {
        this.name = name;
        this.fragments = fragments;
    }

    public Rule(final RuleFragments fragments) {
        this(fragments.header(), new DecisionRuleFragments(fragments));
    }

    public Rule(final String name, final RuleFragments fragments) {
        this(name, new DecisionRuleFragments(fragments));
    }

    public static Rule elseRule(final String name) {
        return new Rule(
            "%s::else".formatted(name),
            new DecisionRuleFragments(List.of(new RuleFragment("OUT", "outcome", "undefined")))
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
        final boolean result = this.fragments.conditions().stream()
            .allMatch(c -> c.evaluate(context));
        context.logComputation(
            OutputTracker.EventType.RL,
            "%s => %s".formatted(this.asString(), result)
        );
        return result;
    }

    /**
     * Returns this rule outcomes.
     *
     * @param context The {@link ComputationContext} to get the outcomes in.
     * @return The simple dictionary, containing all this rule's outcomes.
     */
    public Map<String, String> outcome(final ComputationContext context) {
        return this.fragments.outcomes(context);
    }

    /**
     * Performs all the assignments in this rule, effectively performing the state-changing command.
     *
     * @param context The {@link ComputationContext} to perform the assignments in.
     */
    public void perform(final ComputationContext context) {
        this.fragments.assignments().forEach(a -> a.performIn(context));
    }

    /**
     * Checks whether this rule describes a command.
     *
     * @return True if this rule describes a command.
     */
    public boolean describesCommand() {
        return !this.fragments.assignments().isEmpty();
    }

    public List<String> commandArgs() {
        return this.fragments.assignments().stream()
            .map(Assignment::commandArgs)
            .flatMap(List::stream)
            .toList();
    }

    public String asString() {
        return this.name;
    }
}
