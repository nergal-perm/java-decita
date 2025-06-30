/*
 * MIT License
 *
 * Copyright (c) 2024-2025 Eugene Terekhov
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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import ru.ewc.decisions.core.Coordinate;

/**
 * I am a specific {@link Condition} that checks if my right part exactly equals my left one.
 * @since 0.2
 */
public class EqualsCondition extends BinaryCondition {
    /**
     * Ctor.
     *
     * @param left The {@link Coordinate} that is its left part.
     * @param right The {@link Coordinate} that is its right part.
     */
    public EqualsCondition(final Coordinate left, final Coordinate right) {
        super(left, right);
    }

    @Override
    protected final String comparisonAsString() {
        return "=";
    }

    @Override
    protected final Matcher<Coordinate> comparisonFor() {
        return Matchers.equalTo(this.rightPart());
    }
}
