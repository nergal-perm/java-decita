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

package ru.ewc.commands;

import java.util.ArrayList;
import java.util.List;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.commands.Assignment;
import ru.ewc.decisions.core.Coordinate;

/**
 * I am a simple Command, my main responsibility is to perform a set of operations that change the
 * inner state of the {@link ComputationContext}.
 *
 * @since 0.5.0
 */
public final class SimpleCommand {
    /**
     * The operation to perform.
     */
    private final List<String> operations;

    /**
     * Ctor.
     *
     * @param operations The {@code String} description of the operations to perform.
     */
    public SimpleCommand(final List<String> operations) {
        this.operations = operations;
    }

    /**
     * The method that performs all the operations that constitute the command. Every operation
     * changes some aspect of the state. The aspect is defined by the {@link Coordinate} that is
     * the left part of the operation. The value to set is defined by the right part of the
     * operation.
     *
     * @param context The {@link ComputationContext} to work with.
     */
    public void performIn(final ComputationContext context) {
        for (final String description : this.operations) {
            new Assignment(
                Coordinate.from(description.split("->")[0].trim()),
                Coordinate.from(description.split("->")[1].trim())
            ).performIn(context);
        }
    }

    public List<String> unresolvedParts() {
        final List<String> result = new ArrayList<>(this.operations.size() * 2);
        for (final String description : this.operations) {
            final String[] split = description.split("->");
            if (split[0].contains("${")) {
                result.add(extractInnerMostCoordinate(split[0].trim()));
            }
            if (split[1].contains("${")) {
                result.add(extractInnerMostCoordinate(split[1].trim()));
            }
        }
        return result.stream().filter(s -> s.contains("::")).toList();
    }

    private static String extractInnerMostCoordinate(final String description) {
        final int start = description.lastIndexOf("${");
        final int end = description.indexOf('}', start);
        return description.substring(start + 2, end);
    }
}
