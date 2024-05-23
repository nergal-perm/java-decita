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
import java.util.Map;
import java.util.function.Supplier;
import ru.ewc.decisions.input.DecisionTables;
import ru.ewc.decisions.input.PlainTextDecisionReader;
import ru.ewc.state.StoredState;

/**
 * I am the facade for the Decita library. My main responsibility is to provide a single entry point
 * for the library users.
 *
 * @since 0.3.1
 */
public final class DecitaFacade {
    /**
     * The function that provides a fresh set of uncomputed decision tables.
     */
    private final Supplier<DecisionTables> tables;

    /**
     * The basic set of {@link Locator} that represents the current stored state of the system.
     */
    private final StoredState state;

    /**
     * Ctor.
     *
     * @param tables The function that provides a fresh set of uncomputed decision tables.
     */
    DecitaFacade(final Supplier<DecisionTables> tables) {
        this(tables, StoredState.EMPTY);
    }

    /**
     * Ctor used by library clients to construct the facade based on a folder with decision tables.
     *
     * @param path Path to the folder with decision tables.
     * @param ext The extension of the files to read.
     * @param delimiter The delimiter used in the files.
     */
    @SuppressWarnings("unused")
    public DecitaFacade(final URI path, final String ext, final String delimiter) {
        this(
            () -> new PlainTextDecisionReader(path, ext, delimiter).allTables(),
            StoredState.EMPTY
        );
    }

    public DecitaFacade(final URI path, final StoredState state) {
        this(
            () -> new PlainTextDecisionReader(path, ".csv", ";").allTables(),
            state
        );
    }

    /**
     * Ctor.
     *
     * @param tables The function that provides a fresh set of uncomputed decision tables.
     * @param state The basic set of {@link Locator} used to obtain data from the system.
     */
    private DecitaFacade(final Supplier<DecisionTables> tables, final StoredState state) {
        this.tables = tables;
        this.state = state;
    }

    /**
     * Decides on the outcome of the table with the given name.
     *
     * @param table The name of the table to decide on.
     * @param request The request to decide on.
     * @return The outcome of the decision.
     */
    public Map<String, String> decisionFor(final String table, final BaseLocators request) {
        return this.contextWith(request).decisionFor(table);
    }

    /**
     * Merges together all the {@link BaseLocators} instances required to perform the computation.
     *
     * @param request The additional locators to merge with (usually coming in a form of external
     *  request).
     * @return An instance of {@link ComputationContext} with all the required locators.
     */
    public ComputationContext contextWith(final BaseLocators request) {
        return StoredState.EMPTY.mergedWith(
            StoredState.CONSTANT_LOCATORS,
            this.tables.get(),
            this.state,
            request
        );
    }
}
