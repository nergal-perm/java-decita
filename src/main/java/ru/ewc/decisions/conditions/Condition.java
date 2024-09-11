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

package ru.ewc.decisions.conditions;

import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.core.Coordinate;

/**
 * I am an assertion made for two {@link Coordinate}s. My main responsibility is to compute
 * the result of such an assertion and answer it to the computation engine.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public interface Condition {
    /**
     * Creates a {@link Condition} based on a string representation.
     *
     * @param base The base {@link Coordinate} for the condition.
     * @param argument String representation of a condition.
     * @return A concrete {@link Condition} based on given string representation.
     */
    static Condition from(final Coordinate base, final String argument) {
        final Condition result;
        final char operation = argument.charAt(0);
        if (operation == '~') {
            result = new EqualsCondition(base, base);
        } else if (operation == '!') {
            result = new NotCondition(Condition.from(base, argument.substring(1)));
        } else if (operation == '>') {
            result = new GreaterThanCondition(base, Coordinate.from(argument.substring(1)));
        } else if (operation == '<') {
            result = new LessThanCondition(base, Coordinate.from(argument.substring(1)));
        } else {
            result = new EqualsCondition(base, Coordinate.from(argument));
        }
        return result;
    }

    /**
     * Creates a {@link Condition} based on a {@link RuleFragment}.
     *
     * @param fragment A {@link RuleFragment} to create a Condition from.
     * @return A concrete {@link Condition} based on the given {@link RuleFragment}.
     */
    static Condition from(final RuleFragment fragment) {
        return Condition.from(Coordinate.from(fragment.left()), fragment.right());
    }

    /**
     * Evaluates all the parts of the {@link Condition} and provides the result of that evaluation.
     *
     * @param context The {@link ComputationContext} to evaluate {@link Condition} in.
     * @return Whether the {@link Condition} stands true.
     * @throws DecitaException If the evaluation cannot be performed.
     */
    boolean evaluate(ComputationContext context) throws DecitaException;

    /**
     * Checks if all the parts of the Condition are resolved and point to the constant values.
     *
     * @return True, if both parts of the Condition are evaluated.
     */
    boolean isEvaluated();

    /**
     * Checks if this {@link Condition} resolves to {@code true}.
     *
     * @return True, if it does.
     */
    boolean isSatisfied();

    String asString();

    String result();
}
