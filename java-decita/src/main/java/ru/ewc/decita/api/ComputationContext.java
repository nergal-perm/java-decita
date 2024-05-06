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

package ru.ewc.decita.api;

import java.util.Map;
import ru.ewc.decita.core.Coordinate;
import ru.ewc.decita.core.DecisionTable;

/**
 * I am the container for all the things, required for TruthTable evaluation. My main responsibility
 * is to provide the set of {@link Locator}s in order to find all the required {@link Coordinate}s.
 *
 * @since 0.1
 */
public final class ComputationContext {
    /**
     * The instance of {@link Locators} that provides the required {@link Locator}s.
     */
    private final Locators collection;

    /**
     * Ctor.
     *
     * @param locators The {@link Locators} instance to use.
     */
    public ComputationContext(final Locators locators) {
        this.collection = locators;
    }

    /**
     * Finds a {@link Coordinate}'s value using internal set of {@link Locator}'s.
     *
     * @param locator String identifier of the {@link Locator} to use.
     * @param fragment String identifier of the value to find.
     * @return The {@code String} value containing requested state.
     * @throws DecitaException If the {@link Locator} wasn't found in the context.
     */
    public String valueFor(final String locator, final String fragment) throws DecitaException {
        return this.collection.locatorFor(locator).fragmentBy(fragment, this);
    }

    /**
     * Computes the specified {@link DecisionTable} result as a Dictionary. This method is used by
     * unit-tests and the library's clients.
     *
     * @param name The name of the table to compute.
     * @return The Dictionary containing the decision result.
     * @throws DecitaException If the table could not be found or computed.
     */
    @SuppressWarnings("unused")
    public Map<String, String> decisionFor(final String name) throws DecitaException {
        return this.collection.locatorFor(name).outcome(this);
    }
}
