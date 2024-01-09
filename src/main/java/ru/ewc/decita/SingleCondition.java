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
 * I represent a simple {@link Condition} using two {@link Coordinate}s and some comparison
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
     * Constructor.
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
        this.right.locateIn(context);
        this.left.locateIn(context);
        return this.comparisonFor(this.right).matches(this.left);
    }

    /**
     * Creates a {@link Matcher} that corresponds to the given operation and {@link Coordinate}.
     *
     * @param coordinate The {@link Coordinate} to use with the {@link Matcher}.
     * @return The {@link Matcher} to use in this {@link Condition}.
     */
    private Matcher<?> comparisonFor(final Coordinate coordinate) {
        Matcher<?> matcher = Matchers.not(Matchers.anything());
        if ("=".equalsIgnoreCase(this.comparison)) {
            matcher = Matchers.equalTo(coordinate);
        }
        return matcher;
    }
}
