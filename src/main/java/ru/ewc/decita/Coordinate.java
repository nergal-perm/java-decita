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
package ru.ewc.decita;

import lombok.EqualsAndHashCode;
import ru.ewc.decita.conditions.Condition;

/**
 * I am a simple data structure describing the position of the required value. My responsibility is
 * to provide everything that is needed to retrieve that value. The position of every value is
 * described by the {@link Locator}'s ID and the {@code String} value's ID, so it looks like a
 * 2D-coordinate.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class Coordinate implements Comparable<Coordinate> {
    /**
     * A constant value {@link Coordinate} that points to the "true" value.
     */
    public static final Coordinate TRUE = new Coordinate(Locator.CONSTANT_VALUES, "true");

    /**
     * A regular expression for a number.
     */
    public static final String NUMBER_REGEXP = "-?\\d+(\\.\\d+)?";

    /**
     * String identifier of the concrete {@link Locator} responsible for retrieving the value.
     */
    private String locator;

    /**
     * String identifier of the requested state property's value.
     */
    private String fragment;

    /**
     * Ctor.
     *
     * @param locator The {@link Locator} identifier.
     * @param fragment The value's identifier.
     */
    public Coordinate(final String locator, final String fragment) {
        this.locator = locator;
        this.fragment = fragment;
    }

    /**
     * Locates the required value in the provided {@link ComputationContext}. Always return the same
     * object with updated fields (not a new instance) because there could be many references to
     * this instance, i.e. the same {@link Coordinate} could be used in many {@link Condition}s.
     *
     * @param context Provided {@link ComputationContext}.
     * @return A constant value {@link Coordinate}.
     * @throws DecitaException If the specified {@link Locator} is missing.
     */
    public Coordinate locateIn(final ComputationContext context) throws DecitaException {
        if (!this.isComputed()) {
            final String located = context.valueFor(this.locator, this.fragment);
            this.locator = Locator.CONSTANT_VALUES;
            this.fragment = located;
        }
        return this;
    }

    /**
     * Tests if {@link Coordinate} is already computed, i.e. its value is constant.
     *
     * @return True, if {@link Coordinate} points to a constant value.
     */
    public boolean isComputed() {
        return Locator.CONSTANT_VALUES.equals(this.locator);
    }

    @Override
    public int compareTo(final Coordinate other) {
        final int result;
        if (this.isNumber() && other.isNumber()) {
            result = Double.compare(
                Double.parseDouble(this.fragment),
                Double.parseDouble(other.fragment)
            );
        } else {
            throw new IllegalArgumentException("Cannot compare strings");
        }
        return result;
    }

    /**
     * Tests if the fragment is a number.
     *
     * @return True, if the fragment is a number.
     */
    private boolean isNumber() {
        return this.fragment.matches(Coordinate.NUMBER_REGEXP);
    }
}
