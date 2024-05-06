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

package ru.ewc.decita.conditions;

import lombok.EqualsAndHashCode;
import org.hamcrest.Matcher;
import ru.ewc.decita.api.ComputationContext;
import ru.ewc.decita.api.DecitaException;
import ru.ewc.decita.core.Coordinate;

/**
 * I represent a simple {@link Condition} using two {@link Coordinate}s and some comparison
 * operation. My main responsibility is to compute that comparison and check if it's true or not.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public abstract class BinaryCondition implements Condition {
    /**
     * Left-side {@link Coordinate}.
     */
    private final Coordinate left;

    /**
     * Right-side {@link Coordinate}.
     */
    private final Coordinate right;

    /**
     * Constructor.
     *
     * @param left Left-side {@link Coordinate}.
     * @param right Right-side {@link Coordinate}.
     */
    protected BinaryCondition(final Coordinate left, final Coordinate right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public final boolean evaluate(final ComputationContext context) throws DecitaException {
        this.right.locateIn(context);
        this.left.locateIn(context);
        return this.isSatisfied();
    }

    @Override
    public final boolean isEvaluated() {
        return this.right.isComputed() && this.left.isComputed();
    }

    @Override
    public final boolean isSatisfied() {
        return this.isEvaluated() && this.comparisonFor().matches(this.left);
    }

    /**
     * Creates a {@link Matcher} that corresponds to the given operation and {@link Coordinate}.
     *
     * @return The {@link Matcher} to use in this {@link Condition}.
     */
    protected abstract Matcher<Coordinate> comparisonFor();

    /**
     * Returns the right part of this {@link Condition}.
     *
     * @return The {@link Coordinate} that is its right part.
     */
    protected final Coordinate rightPart() {
        return this.right;
    }
}
