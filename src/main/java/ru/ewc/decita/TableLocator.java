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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I am the concrete {@link Locator} responsible for finding and computing {@link DecisionTable}s'
 * results.
 *
 * @since 0.1
 */
public final class TableLocator implements Locator {
    /**
     * Null-object representing the missing {@link DecisionTable}.
     */
    private static final DecisionTable DEFAULT_VALUE =
        new DecisionTable("undefined", List.of(new Rule().withOutcome("outcome", "undefined")));

    /**
     * Inner storage of all the registered {@link DecisionTable}s.
     */
    private final Map<String, DecisionTable> tables = new HashMap<>();

    @Override
    public String fragmentBy(final String fragment, final ComputationContext context)
        throws DecitaException {
        final String[] address = fragment.split("::");
        if (address.length != 2) {
            throw new DecitaException(
                String.format("Outcome coordinate '%s' can not be understood", fragment)
            );
        }
        return this.tables.getOrDefault(address[0], TableLocator.DEFAULT_VALUE).outcome(context)
            .get(address[1]);
    }

    @Override
    public void registerWith(final ComputationContext context) {
        context.registerLocator(Locator.TABLE, this);
    }

    /**
     * Registers an instance of {@link DecisionTable} with this {@link TableLocator} thus making
     * the table discoverable by the engine.
     *
     * @param table The {@link DecisionTable}'s instance to register.
     * @return Itself, in order to implement a fluent API.
     */
    public Locator withTable(final DecisionTable table) {
        this.tables.put(table.tableName(), table);
        return this;
    }
}
