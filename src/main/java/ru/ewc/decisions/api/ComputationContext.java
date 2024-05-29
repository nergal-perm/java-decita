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

package ru.ewc.decisions.api;

import java.util.Map;
import ru.ewc.decisions.core.BaseLocators;
import ru.ewc.decisions.core.ConstantLocator;
import ru.ewc.decisions.core.Coordinate;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.core.RequestLocator;
import ru.ewc.decisions.input.DecisionTables;
import ru.ewc.state.StoredState;

/**
 * I am the container for all the things, required for TruthTable evaluation. My main responsibility
 * is to provide the set of {@link Locator}s in order to find all the required {@link Coordinate}s.
 *
 * @since 0.1
 */
// @todo #127 Create Locator-specific fields for ComputationContext
public final class ComputationContext {
    /**
     * The always available Locator that returns constant values.
     */
    private static final ConstantLocator CONSTANT_LOCATOR = new ConstantLocator();

    /**
     * The instance of {@link BaseLocators} that provides the required {@link Locator}s.
     */
    private final StoredState state;

    /**
     * The storage of incoming request data.
     */
    private final RequestLocator request;

    /**
     * The storage of decision tables.
     */
    private final DecisionTables tables;

    /**
     * Ctor.
     *
     * @param state The {@link StoredState} instance to use.
     * @param request The {@link RequestLocator} instance to use.
     * @param tables The {@link DecisionTables} instance to use.
     */
    public ComputationContext(
        final StoredState state,
        final RequestLocator request,
        final DecisionTables tables
    ) {
        this.state = state;
        this.request = request;
        this.tables = tables;
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
        final Locator found;
        if ("constant".equals(locator)) {
            found = ComputationContext.CONSTANT_LOCATOR;
        } else if ("request".equals(locator)) {
            found = this.request.locatorFor(locator);
        } else if (this.tables.hasLocator(locator)) {
            found = this.tables.locatorFor(locator);
        } else {
            found = this.state.locatorFor(locator);
        }
        return found.fragmentBy(fragment, this);
    }

    /**
     * Computes the specified {@link DecisionTable} result as a Dictionary. This method is used by
     * unit-tests and the library's clients.
     *
     * @param name The name of the table to compute.
     * @return The Dictionary containing the decision result.
     * @throws DecitaException If the table could not be found or computed.
     */
    public Map<String, String> decisionFor(final String name) throws DecitaException {
        return this.tables.locatorFor(name).outcome(this);
    }

    /**
     * Sets the value of the fragment in the context.
     *
     * @param loc The name of the locator to set the value of.
     * @param frag The name of the fragment to set the value of.
     * @param value The value to set.
     * @return The updated {@link ComputationContext} instance.
     */
    public ComputationContext setValueFor(final String loc, final String frag, final String value) {
        final Locator found;
        if ("request".equals(loc)) {
            found = this.request.locatorFor(loc);
        } else {
            found = this.state.locatorFor(loc);
        }
        found.setFragmentValue(frag, value);
        return this;
    }
}
