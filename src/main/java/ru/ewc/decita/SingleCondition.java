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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * I represent a simple {@link Condition} using two {@link StateFragment}s and some comparison
 * operation. My main responsibility is to compute that comparison and check if it's true or not.
 *
 * @since 0.1
 */
public final class SingleCondition implements Condition {
    /**
     * Left-side {@link Coordinate}.
     */
    private final Coordinate left;

    /**
     * The comparison operation applied to both operands.
     */
    private final String comparison;

    /**
     * Right-side {@link Coordinate}.
     */
    private final Coordinate right;

    /**
     * Ctor.
     * @param left Left-side {@link Coordinate}.
     * @param comparison The comparison operation applied to both operands.
     * @param right Right-side {@link Coordinate}.
     */
    public SingleCondition(final Coordinate left, final String comparison, final Coordinate right) {
        this.left = left;
        this.comparison = comparison;
        this.right = right;
    }

    @Override
    public boolean evaluate(final ComputationContext context) throws DecitaException {
        return this.comparisonFor(this.rightFragment(context)).matches(this.leftFragment(context));
    }

    /**
     * Evaluates the left-side {@link StateFragment}.
     *
     * @param context The {@link ComputationContext} used to find the {@link StateFragment}.
     * @return The requested {@link StateFragment}.
     * @throws DecitaException If {@link StateFragment} cannot be found.
     */
    private StateFragment leftFragment(final ComputationContext context) throws DecitaException {
        return this.left.fragmentFrom(context);
    }

    /**
     * Evaluates the right-side {@link StateFragment}.
     *
     * @param context The {@link ComputationContext} used to find the {@link StateFragment}.
     * @return The requested {@link StateFragment}.
     * @throws DecitaException If {@link StateFragment} cannot be found.
     */
    private StateFragment rightFragment(final ComputationContext context) throws DecitaException {
        return this.right.fragmentFrom(context);
    }

    /**
     * Creates a {@link Matcher} that corresponds to the given operation and {@link StateFragment}.
     *
     * @param fragment The {@link StateFragment} to use with the {@link Matcher}.
     * @return The {@link Matcher} to use in this {@link Condition}.
     */
    private Matcher<?> comparisonFor(final StateFragment fragment) {
        Matcher<?> matcher = Matchers.not(Matchers.anything());
        if ("=".equalsIgnoreCase(this.comparison)) {
            matcher = Matchers.equalTo(fragment);
        }
        return matcher;
    }
}
