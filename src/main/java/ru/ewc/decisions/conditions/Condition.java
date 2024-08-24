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
import ru.ewc.decisions.core.Coordinate;

/**
 * I am an assertion made for two {@link Coordinate}s. My main responsibility is to compute
 * the result of such an assertion and answer it to the computation engine.
 *
 * @since 0.1
 */
public interface Condition {
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

    /**
     * Checks if this Condition resolves to {@code false}, meaning it is not satisfied.
     *
     * @return True, if this Condition is already computed and resolves to {@code false}.
     */
    default boolean isNotSatisfied() {
        return this.isEvaluated() && !this.isSatisfied();
    }

    String asString();
}
