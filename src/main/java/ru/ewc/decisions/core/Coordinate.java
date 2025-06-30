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

package ru.ewc.decisions.core;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.conditions.Condition;

/**
 * I am a simple data structure describing the position of the required value. My responsibility is
 * to provide everything that is needed to retrieve that value. The position of every value is
 * described by the {@link Locator}'s ID and the {@code String} value's ID, so it looks like a
 * 2D-coordinate.
 *
 * @since 0.1
 */
@EqualsAndHashCode
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class Coordinate implements Comparable<Coordinate> {
    /**
     * A constant value {@link Coordinate} that points to the "true" value.
     */
    public static final Coordinate TRUE = Coordinate.from("true");

    /**
     * A constant value {@link Coordinate} that points to the "false" value.
     */
    public static final Coordinate FALSE = Coordinate.from("false");

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
    private Coordinate(final String locator, final String fragment) {
        this.locator = locator;
        this.fragment = fragment;
    }

    /**
     * Creates a {@link Coordinate} based on a string representation. The provided string can
     * reference to a constant value or to some {@link Locator}'s value. In the latter case, the
     * string should be in the format "locator::fragment".
     *
     * @param coordinate String representation of a coordinate.
     * @return A concrete {@link Coordinate} based on given string representation.
     */
    public static Coordinate from(final String coordinate) {
        final Coordinate result;
        if (coordinate.contains("::")) {
            final String[] split = coordinate.split("::");
            result = new Coordinate(
                split[0],
                Arrays.stream(split).skip(1).collect(Collectors.joining("::"))
            );
        } else {
            result = new Coordinate(Locator.CONSTANT_VALUES, coordinate);
        }
        return result;
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
        if (!this.isResolved()) {
            this.resolveIn(context);
        }
        if (!this.isComputed()) {
            this.turnToConstantWith(this.valueIn(context));
        }
        return this;
    }

    public String valueIn(final ComputationContext context) throws DecitaException {
        this.resolveIn(context);
        final String result = context.valueFor(this.locator, this.fragment);
        context.logComputation(
            OutputTracker.EventType.ST,
            "%s => %s".formatted(this.asString(), result)
        );
        return result;
    }

    public ComputationContext setValueInContext(final String val, final ComputationContext target) {
        if (!this.isResolved()) {
            this.resolveIn(target);
        }
        return target.setValueFor(this.locator, this.fragment, val);
    }

    /**
     * Tests if {@link Coordinate} is already computed, i.e. its value is constant.
     *
     * @return True, if {@link Coordinate} points to a constant value.
     */
    public boolean isComputed() {
        return Locator.CONSTANT_VALUES.equals(this.locator);
    }

    /**
     * Tests if the {@link Coordinate} is resolved, i.e. it does not contain any placeholders.
     *
     * @return True if the {@link Coordinate} is resolved.
     */
    public Boolean isResolved() {
        return !this.locator.contains("${") && !this.fragment.contains("${");
    }

    /**
     * Resolves the {@link Coordinate} in the provided {@link ComputationContext}. The method
     * replaces all placeholders with the actual values.
     *
     * @param context The {@link ComputationContext} to resolve the {@link Coordinate} in.
     */
    public void resolveIn(final ComputationContext context) {
        final String description = "%s::%s".formatted(this.locator, this.fragment);
        String result = description;
        while (result.contains("${")) {
            final String coord = Coordinate.extractInnerMostCoordinate(result);
            final Coordinate coordinate = Coordinate.from(coord);
            result = result.replace("${%s}".formatted(coord), coordinate.valueIn(context));
        }
        if (!result.equals(description)) {
            context.logComputation(
                OutputTracker.EventType.DN,
                "%s => %s".formatted(description, result)
            );
        }
        this.updateWith(result);
    }

    /**
     * Returns the string representation of the {@link Coordinate}.
     *
     * @return The string in the "locator::fragment" format.
     */
    public String asString() {
        return "%s::%s".formatted(this.locator, this.fragment);
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

    /**
     * Turns the {@link Coordinate} into a constant {@link Coordinate} with provided value.
     *
     * @param constant The constant value to set.
     */
    private void turnToConstantWith(final String constant) {
        this.locator = Locator.CONSTANT_VALUES;
        this.fragment = constant;
    }

    private void updateWith(final String coordinate) {
        if (coordinate.contains("::")) {
            final String[] split = coordinate.split("::");
            this.locator = split[0];
            this.fragment = Arrays.stream(split).skip(1).collect(Collectors.joining("::"));
        } else {
            this.locator = Locator.CONSTANT_VALUES;
            this.fragment = coordinate;
        }
    }

    private static String extractInnerMostCoordinate(final String description) {
        final int start = description.lastIndexOf("${");
        final int end = description.indexOf('}', start);
        return description.substring(start + 2, end);
    }
}
