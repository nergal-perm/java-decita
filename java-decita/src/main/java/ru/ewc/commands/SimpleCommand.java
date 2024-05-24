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

import java.util.List;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.core.Coordinate;

/**
 * I am a simple Command, an intermediary class for testing purposes.
 *
 * @since 0.5.0
 */
public class SimpleCommand {
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
     * I am a simple method that sets the value of the "name" fragment in the "table" Locator to
     * "Tic-Tac-Toe".
     *
     * @param context The {@link ComputationContext} to work with.
     * @return An instance of updated {@link ComputationContext}.
     */
    public ComputationContext perform(final ComputationContext context) {
        ComputationContext target = context;
        for (final String description : this.operations) {
            final Coordinate coordinate = SimpleCommand.resolveLeft(description, context);
            final String value = SimpleCommand.resolveRight(description, context);
            target = coordinate.setValueInContext(value, target);
        }
        return target;
    }

    // @todo #23 Make templates resolutions the Coordinate's responsibility
    private static Coordinate resolveLeft(final String desc, final ComputationContext context) {
        return Coordinate.from(
            SimpleCommand.resolve(
                context,
                desc.split("->")[0].trim()
            )
        );
    }

    private static String resolveRight(final String desc, final ComputationContext context) {
        return Coordinate.from(
            SimpleCommand.resolve(
                context,
                desc.split("->")[1].trim()
            )
        ).valueIn(context);
    }

    private static String resolve(final ComputationContext context, final String description) {
        String result = description;
        while (result.contains("${")) {
            final String coord = SimpleCommand.extractInnerMostCoordinate(result);
            final Coordinate coordinate = Coordinate.from(coord);
            result = result.replace("${%s}".formatted(coord), coordinate.valueIn(context));
        }
        return result;
    }

    private static String extractInnerMostCoordinate(final String description) {
        final int start = description.lastIndexOf("${");
        final int end = description.indexOf('}');
        return description.substring(start + 2, end);
    }
}
