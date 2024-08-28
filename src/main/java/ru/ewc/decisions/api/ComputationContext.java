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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ewc.commands.Commands;
import ru.ewc.decisions.core.ConstantLocator;
import ru.ewc.decisions.core.Coordinate;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.input.CombinedCsvFileReader;
import ru.ewc.state.State;

/**
 * I am the facade for all TruthTable evaluations and Commands executions.
 *
 * @since 0.1
 */
public final class ComputationContext {

    /**
     * The storage of decision tables.
     */
    private final DecisionTables tables;

    /**
     * The storage of the current state of the system.
     */
    private final State state;

    /**
     * The storage of all the commands.
     */
    private final Commands commands;

    /**
     * The publisher of the computation events.
     */
    private final OutputPublisher<String> publisher;

    public ComputationContext(final State state, final URI tables) {
        this(state, ComputationContext.getAllTables(tables));
    }

    public ComputationContext(final State state, final URI tables, final URI commands) {
        this(state, ComputationContext.getAllTables(tables), new Commands(commands));
    }

    public ComputationContext(final State state, final DecisionTables tables) {
        this(state, tables, new Commands(Map.of()));
    }

    /**
     * Primary Ctor.
     *
     * @param state The {@link State} instance to use.
     * @param tables The {@link DecisionTables} instance to use.
     * @param commands The {@link Commands} instance to use.
     */
    public ComputationContext(
        final State state,
        final DecisionTables tables,
        final Commands commands
    ) {
        this.state = ComputationContext.extendedWithConstant(state);
        this.tables = tables;
        this.commands = commands;
        this.publisher = new OutputPublisher<>();
    }

    public OutputTracker<String> startTracking() {
        return this.publisher.createTracker();
    }

    public void logComputation(final OutputTracker.EventType type, final String message) {
        this.publisher.track("%s: %s".formatted(type.name(), message));
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

    @SuppressWarnings("unused")
    public void perform(final String command) {
        ((DecisionTable) this.tables.locatorFor(command)).perform(this);
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
        if (this.tables.hasLocator(locator)) {
            found = this.tables.locatorFor(locator);
        } else {
            found = this.state.locatorFor(locator);
        }
        return found.fragmentBy(fragment, this);
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
        final Locator found = this.state.locatorFor(loc);
        found.setFragmentValue(frag, value);
        return this;
    }

    @SuppressWarnings("unused")
    public Map<String, Map<String, Object>> storedState() {
        return this.state.state();
    }

    @SuppressWarnings("unused")
    @Deprecated
    public List<String> commandNames() {
        return this.commands.names();
    }

    @SuppressWarnings("unused")
    public Map<String, List<String>> commandData() {
        return this.commands.args();
    }

    private static State extendedWithConstant(final State state) {
        final Map<String, Locator> locators = new HashMap<>(state.locators());
        locators.put("constant", new ConstantLocator());
        return new State(locators);
    }

    private static DecisionTables getAllTables(final URI tables) {
        return new CombinedCsvFileReader(tables, ".csv", ";").allTables();
    }
}
