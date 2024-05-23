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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import ru.ewc.decisions.core.Coordinate;

/**
 * I am a simple condition that checks if the left-side value is greater than the right-side value.
 *
 * @since 0.3
 */
public class GreaterThanCondition extends BinaryCondition {
    /**
     * Constructor.
     *
     * @param left Left-side {@link Coordinate}.
     * @param right Right-side {@link Coordinate}.
     */
    public GreaterThanCondition(final Coordinate left, final Coordinate right) {
        super(left, right);
    }

    @Override
    protected final Matcher<Coordinate> comparisonFor() {
        return Matchers.greaterThan(this.rightPart());
    }
}
