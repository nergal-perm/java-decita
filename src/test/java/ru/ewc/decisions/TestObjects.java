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

package ru.ewc.decisions;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.conditions.AlwaysTrueCondition;
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.decisions.conditions.EqualsCondition;
import ru.ewc.decisions.conditions.NotCondition;
import ru.ewc.decisions.core.BaseLocators;
import ru.ewc.decisions.core.Coordinate;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.core.InMemoryLocator;
import ru.ewc.decisions.core.Rule;
import ru.ewc.decisions.input.CombinedCsvFileReader;
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

    public static ComputationContext ticTacToeContext() {
        final Map<String, Locator> locators = Map.of(
            "request", InMemoryLocator.empty(),
            "table", InMemoryLocator.empty(),
            "cells", InMemoryLocator.empty()
        );
        return defaultLocatorsWith(new State(locators));
    }

    /**
     * The {@link Condition} that compares a constant to itself.
     *
     * @return A simple and always true {@link Condition}.
     */
    public static Condition alwaysTrueConstantCondition() {
        return new EqualsCondition(Coordinate.TRUE, Coordinate.TRUE);
    }

    /**
     * Convenience method to get an instance of {@link Coordinate} pointing to the {@link Condition}
     * that always resolves to {@code true}.
     *
     * @return A {@link Coordinate} of the always true {@link Condition}.
     */
    public static Coordinate alwaysTrueConditionCoordinate() {
        return Coordinate.from("always_true::outcome");
    }

    public static ComputationContext tablesFolderWithState(final State state) {
        return TestObjects.createContextFrom(state, "tables");
    }

    /**
     * Convenience method to get an instance of {@link Rule} with the {@link Condition} that
     * always resolves to {@code true}.
     *
     * @return Always succeeding {@link Rule} instance.
     */
    static Rule alwaysTrueEqualsTrueRule() {
        return new Rule("always_true")
            .withCondition(new AlwaysTrueCondition())
            .withOutcome("outcome", "Hello");
    }

    /**
     * Convenience method to get an instance of {@link Rule} with the {@link Condition} that
     * always resolves to {@code false}.
     *
     * @return Always failing {@link Rule} instance.
     */
    static Rule alwaysTrueEqualsFalseRule() {
        return new Rule("always_false")
            .withCondition(new NotCondition(new AlwaysTrueCondition()))
            .withOutcome("outcome", "World");
    }

    /**
     * A collection of default (required) {@link Locator}s.
     *
     * @param state Additional {@link BaseLocators} to merge with.
     * @return A collection of required {@link Locator}s.
     */
    private static ComputationContext defaultLocatorsWith(final State state) {
        final DecisionTable truthy = new DecisionTable(
            List.of(alwaysTrueEqualsTrueRule()),
            "always_true"
        );
        return new ComputationContext(state, new DecisionTables(Map.of("always_true", truthy)));
    }

    private static ComputationContext createContextFrom(final State state, final String folder) {
        return new ComputationContext(
            state, DecisionTables.using(new CombinedCsvFileReader(uriTo(folder), ".csv", ";"))
        );
    }

    private static URI uriTo(final String folder) {
        return Path.of(
            Paths.get("").toAbsolutePath().toString(),
            "src/test/resources/%s".formatted(folder)
        ).toUri();
    }
}
