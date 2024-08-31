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

package ru.ewc.decisions.commands;

import java.util.ArrayList;
import java.util.List;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.core.Coordinate;

/**
 * I represent an assignment of the specific value to a target {@link Coordinate}. Both my parts are
 * resolved lazily.
 *
 * @since 0.8.0
 */
public final class Assignment {
    /**
     * The target {@link Coordinate} to assign the value to.
     */
    private final Coordinate target;

    /**
     * The {@link Coordinate} that provides the value to assign.
     */
    private final Coordinate value;

    /**
     * Ctor.
     *
     * @param target The target {@link Coordinate} to assign the value to.
     * @param value The {@link Coordinate} that provides the value to assign.
     */
    public Assignment(final Coordinate target, final Coordinate value) {
        this.target = target;
        this.value = value;
    }

    public Assignment(final String target, final String value) {
        this(Coordinate.from(target), Coordinate.from(value));
    }

    /**
     * The method that performs the assignment of the value to the target {@link Coordinate}.
     *
     * @param context The {@link ComputationContext} to work with.
     */
    public void performIn(final ComputationContext context) {
        this.target.setValueInContext(this.value.valueIn(context), context);
    }

    public List<String> commandArgs() {
        final List<String> result = new ArrayList<>(2);
        result.addAll(Assignment.unresolvedPartsFor(this.target.asString()));
        result.addAll(Assignment.unresolvedPartsFor(this.value.asString()));
        return result;
    }

    private static List<String> unresolvedPartsFor(final String description) {
        final List<String> result = new ArrayList<>(1);
        if (description.contains("${")) {
            result.add(extractInnerMostCoordinate(description.trim()));
        }
        return result.stream().filter(s -> s.contains("::")).toList();
    }

    private static String extractInnerMostCoordinate(final String description) {
        final int start = description.lastIndexOf("${");
        final int end = description.indexOf('}', start);
        return description.substring(start + 2, end);
    }
}
