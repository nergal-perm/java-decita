/*
 * MIT License
 *
 * Copyright (c) 2024-2025 Eugene Terekhov
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

package ru.ewc.decisions;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.api.InMemoryLocator;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.decisions.conditions.EqualsCondition;
import ru.ewc.decisions.core.BaseLocators;
import ru.ewc.decisions.core.Coordinate;
import ru.ewc.decisions.input.CombinedCsvFileReader;
import ru.ewc.decisions.input.ContentsReader;
import ru.ewc.decisions.input.SourceLines;
import ru.ewc.state.State;

/**
 * I hold different preconfigured objects for unit-tests.
 *
 * @since 0.1
 */
@SuppressWarnings({"PMD.ProhibitPublicStaticMethods", "PMD.TestClassWithoutTestCases"})
public final class TestObjects {
    /**
     * Ctor.
     */
    private TestObjects() {
        // empty
    }

    /**
     * The {@link ComputationContext} with some predefined {@link Locator}s.
     *
     * @return A prefilled {@link ComputationContext}.
     */
    public static ComputationContext defaultContext() {
        return defaultLocatorsWith(State.EMPTY);
    }

    /**
     * The {@link ComputationContext} with some predefined {@link Locator}s for the Tic-Tac-Toe
     * game. There are empty {@link Locator}s for the {@code request}, {@code table} and
     * {@code cells}.
     *
     * @return A prefilled {@link ComputationContext} for the Tic-Tac-Toe game.
     */
    public static ComputationContext ticTacToeContext() {
        final List<Locator> locators = List.of(
            InMemoryLocator.empty("request"),
            InMemoryLocator.empty("table"),
            InMemoryLocator.empty("cells")
        );
        return defaultLocatorsWith(
            new State(locators)
        );
    }

    /**
     * The {@link Condition} that compares a constant to itself.
     *
     * @return A simple and always true {@link Condition}.
     */
    public static Condition alwaysTrueConstantCondition() {
        return new EqualsCondition(Coordinate.TRUE, Coordinate.TRUE);
    }

    public static ComputationContext tablesFolderWithState(final State state) {
        return new ComputationContext(
            state, DecisionTables.using(new CombinedCsvFileReader(uriTo("tables"), ".csv", ";"))
        );
    }

    /**
     * A collection of default (required) {@link Locator}s.
     *
     * @param state Additional {@link BaseLocators} to merge with.
     * @return A collection of required {@link Locator}s.
     */
    private static ComputationContext defaultLocatorsWith(final State state) {
        return new ComputationContext(state, DecisionTables.using(alwaysTrueTableReader()));
    }

    private static ContentsReader alwaysTrueTableReader() {
        return () -> List.of(
            new SourceLines(
                "always_true",
                List.of("CND;constant::true;true", "OUT;outcome;Hello"),
                ";"
            )
        );
    }

    private static URI uriTo(final String folder) {
        return Path.of(
            Paths.get("").toAbsolutePath().toString(),
            "src/test/resources/%s".formatted(folder)
        ).toUri();
    }
}
