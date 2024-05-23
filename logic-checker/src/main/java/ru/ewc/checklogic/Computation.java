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

package ru.ewc.checklogic;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.ewc.commands.CommandsFacade;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.DecitaFacade;
import ru.ewc.decisions.api.Locator;
import ru.ewc.state.StoredState;

/**
 * I am a unique instance of a decision table computation.
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class Computation {
    /**
     * Facade for making all the decisions.
     */
    private final DecitaFacade decisions;

    /**
     * Facade for executing all the commands.
     */
    private final CommandsFacade commands;

    /**
     * The current state of the system.
     */
    private final StoredState state;

    /**
     * Ctor.
     *
     * @param decisions An instance of {@link DecitaFacade} to make decisions.
     * @param commands An instance of {@link CommandsFacade} to execute commands.
     * @param locators An instance of {@link StoredState} to store the state.
     */
    public Computation(
        final DecitaFacade decisions,
        final CommandsFacade commands,
        final StoredState locators
    ) {
        this.decisions = decisions;
        this.commands = commands;
        this.state = locators;
    }

    /**
     * Converts a string representation of the file system path to a correct URI.
     *
     * @param path File system path as a String.
     * @return URI that corresponds to a given path.
     */
    public static URI uriFrom(final String path) {
        final StringBuilder result = new StringBuilder("file:/");
        if (path.charAt(0) == '/') {
            result.append(path.replace('\\', '/').substring(1));
        } else {
            result.append(path.replace('\\', '/'));
        }
        return URI.create(result.toString());
    }

    /**
     * Computes the decision for a specified table.
     *
     * @param table Name of the tables to make a decision against.
     * @param locators The locators to use for the decision.
     * @return The collection of outcomes from the specified table.
     * @throws DecitaException If the table could not be found or computed.
     */
    public Map<String, String> decideFor(final String table, final StoredState locators)
        throws DecitaException {
        return this.decisions.decisionFor(table, locators);
    }

    public Map<String, String> decideFor(final String table) throws DecitaException {
        return this.decisions.decisionFor(table, this.state);
    }

    public void perform(final Transition command) {
        this.commands.perform(command.name(), command.request());
    }

    public boolean hasStateFor(final String table) {
        return this.state.hasLocator(table);
    }

    public Map<String, String> stateFor(final String table, final Map<String, String> entities) {
        final Locator locator = this.state.locatorFor(table);
        final ComputationContext context = new ComputationContext(this.state);
        final Map<String, String> actual = HashMap.newHashMap(entities.size());
        for (final String fragment : entities.keySet()) {
            actual.put(fragment, locator.fragmentBy(fragment, context));
        }
        return actual;
    }

    public Computation withState(final Map<String, Map<String, Object>> incoming) {
        return new Computation(this.decisions, this.commands, stateFrom(incoming));
    }

    public Map<String, Map<String, Object>> storedState() {
        return this.state.state();
    }

    /**
     * Loads the state from the specified {@code InputStream}.
     *
     * @param stream InputStream containing state info.
     * @return Collection of {@link Locator} objects, containing desired state.
     */
    private static StoredState stateFrom(final Map<String, Map<String, Object>> stream) {
        return new StoredState(
            stream
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entryToLocator()))
        );
    }

    /**
     * Converts a {@link Map.Entry} to a {@link Locator} object.
     *
     * @return A function that converts a {@link Map.Entry} to a {@link Locator} object.
     */
    private static Function<Map.Entry<String, Map<String, Object>>, Locator> entryToLocator() {
        return e -> new InMemoryStorage(e.getValue());
    }
}
