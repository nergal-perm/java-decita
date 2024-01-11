/*
 * MIT License
 *
 * Copyright (c) 2023-2024 Eugene Terekhov
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

package ru.ewc.decita;

import java.util.ArrayList;
import java.util.List;

/**
 * I am a single Rule (i.e. the column in the decision table). My main responsibility is to check
 * whether all my second-order {@link Condition}s are {@code true} and if so - compute my outcomes.
 *
 * @since 0.1
 */
public class Rule {
    /**
     * The rule's {@link Condition}s.
     */
    private final List<Condition> conditions = new ArrayList<>(5);

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
     * Adds a {@link Condition} to this rule.
     *
     * @param condition The {@link Condition} to add.
     * @return Itself, in order to implement fluent API.
     */
    public Rule with(final Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    /**
     * Checks if this {@link Rule} is satisfied.
     *
     * @param context The {@link ComputationContext}'s instance.
     * @throws DecitaException If the rule's {@link Condition}s could not be resolved.
     */
    public void check(final ComputationContext context) throws DecitaException {
        for (final Condition cond : this.conditions) {
            cond.evaluate(context);
        }
    }
}
