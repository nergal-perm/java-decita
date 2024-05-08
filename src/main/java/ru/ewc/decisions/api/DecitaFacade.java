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
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.input.PlainTextDecisionReader;

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
    private final Supplier<Locators> tables;

    /**
     * The basic set of {@link Locator} used to obtain data from the system.
     */
    private final Locators locators;

    /**
     * Ctor.
     *
     * @param tables The function that provides a fresh set of uncomputed decision tables.
     */
    public DecitaFacade(final Supplier<Locators> tables) {
        this(tables, Locators.EMPTY);
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
        this(() -> new PlainTextDecisionReader(path, ext, delimiter).allTables(), Locators.EMPTY);
    }

    /**
     * Ctor.
     *
     * @param tables The function that provides a fresh set of uncomputed decision tables.
     * @param locators The basic set of {@link Locator} used to obtain data from the system.
     */
    public DecitaFacade(final Supplier<Locators> tables, final Locators locators) {
        this.tables = tables;
        this.locators = locators;
    }

    /**
     * Decides on the outcome of the table with the given name.
     *
     * @param table The name of the table to decide on.
     * @param request The request to decide on.
     * @return The outcome of the decision.
     */
    public Map<String, String> decisionFor(final String table, final Locators request) {
        final Locator locator = this.tables.get().locatorFor(table);
        if (locator instanceof DecisionTable) {
            return locator.outcome(
                new ComputationContext(this.defaultLocators().mergedWith(request))
            );
        }
        throw new DecitaException("The table with the name %s is not found.".formatted(table));
    }

    /**
     * Returns a new {@link ComputationContext} with the default {@link Locator}s.
     *
     * @return A set of base {@link Locator}s.
     */
    private Locators defaultLocators() {
        return this.tables.get()
            .mergedWith(this.locators)
            .mergedWith(Locators.CONSTANT);
    }
}
