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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * I am the container for all the things, required for TruthTable evaluation. My main responsibility
 * is to provide the set of {@link Locator}s in order to find all the required {@link Coordinate}s.
 *
 * @since 0.1
 */
public final class ComputationContext {
    /**
     * The set of all the available {@link Locator}s.
     */
    private final Map<String, Locator> locators;

    /**
     * Ctor.
     *
     * @param locators A single dictionary of predefined (required) locators, or an array of such
     *  dictionaries.
     */
    @SafeVarargs
    public ComputationContext(final Map<String, Locator>... locators) {
        this.locators = Arrays.stream(locators).reduce(
            new HashMap<>(), (acc, map) -> {
                acc.putAll(map);
                return acc;
            }
        );
    }

    /**
     * Extends the current {@link ComputationContext} with additional {@link Locator}s.
     *
     * @param additional A dictionary of additional {@link Locator}s to add.
     * @return The new {@link ComputationContext} with extended set of {@link Locator}s.
     */
    public ComputationContext extendedWith(final Map<String, Locator> additional) {
        return new ComputationContext(this.locators, additional);
    }

    /**
     * Finds a {@link Coordinate}'s value using internal set of {@link Locator}'s.
     *
     * @param locator String identifier of the {@link Locator} to use.
     * @param fragment String identifier of the value to find.
     * @param context The {@link ComputationContext} to get the value.
     * @return The {@code String} value containing requested state.
     * @throws DecitaException If the {@link Locator} wasn't found in the context.
     */
    public String valueFor(
        final String locator, final String fragment, final ComputationContext context)
        throws DecitaException {
        return this.locatorFor(locator).fragmentBy(fragment, context);
    }

    /**
     * Computes the specified {@link DecisionTable} result as a Dictionary.
     *
     * @param name The name of the table to compute.
     * @return The Dictionary containing the decision result.
     * @throws DecitaException If the table could not be found or computed.
     */
    public Map<String, String> decisionFor(final String name) throws DecitaException {
        return this.locatorFor(name).outcome(this);
    }

    /**
     * Returns a concrete {@link Locator} if it's found in an instance storage.
     *
     * @param locator The String identifier of the required {@link Locator}.
     * @return The instance of {@link Locator}.
     * @throws DecitaException If the specified {@link Locator} is missing.
     */
    private Locator locatorFor(final String locator) throws DecitaException {
        if (this.locators.containsKey(locator)) {
            return this.locators.get(locator);
        }
        throw new DecitaException(
            String.format("Locator '%s' not found in computation context", locator)
        );
    }
}
