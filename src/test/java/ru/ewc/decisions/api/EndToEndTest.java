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

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.core.InMemoryLocator;
import ru.ewc.state.State;

/**
 * Full scenario tests for combined CSV files format.
 *
 * @since 0.8.0
 */
final class EndToEndTest {
    /**
     * Constant for the table outcome field.
     */
    public static final String OUT = "outcome";

    /**
     * Constant for the shop field.
     */
    public static final String SHOP = "shop";

    @Test
    void shouldComputeTheWholeTable() throws DecitaException {
        final State state = new State(
            List.of(
                new InMemoryLocator("data", Map.of("is-stored", "true")),
                new InMemoryLocator("market", Map.of(EndToEndTest.SHOP, 2)),
                new InMemoryLocator("currentPlayer", Map.of("name", "Eugene"))
            )
        );
        MatcherAssert.assertThat(
            "The table is computed correctly",
            TestObjects.tablesFolderWithState(state).decisionFor("sample-table"),
            Matchers.allOf(
                Matchers.hasEntry(Matchers.equalTo(EndToEndTest.OUT), Matchers.equalTo("true")),
                Matchers.hasEntry(Matchers.equalTo("text"), Matchers.equalTo("hello world"))
            )
        );
    }

    @Test
    void shouldComputeTheWholeTableWithElseRule() throws DecitaException {
        final State state = new State(
            List.of(
                new InMemoryLocator("data", Map.of("is-stored", false)),
                new InMemoryLocator("market", Map.of(EndToEndTest.SHOP, 3)),
                new InMemoryLocator("currentPlayer", Map.of("name", "Eugene"))
            )
        );
        final ComputationContext context = TestObjects.tablesFolderWithState(state);
        final OutputTracker<String> tracker = context.startTracking();
        MatcherAssert.assertThat(
            "The table is computed correctly",
            context.decisionFor("sample-table"),
            Matchers.allOf(
                Matchers.hasEntry(Matchers.equalTo(EndToEndTest.OUT), Matchers.equalTo("else")),
                Matchers.hasEntry(Matchers.equalTo("text"), Matchers.equalTo("no rule satisfied"))
            )
        );
        MatcherAssert.assertThat(
            "Should have logged all the computations",
            tracker.events().size(),
            Matchers.is(19)
        );
    }

    @Test
    void shouldResolveDynamicCoordinates() {
        final State state = new State(
            List.of(
                new InMemoryLocator("cells", Map.of("A1", "empty", "A2", "X", "A3", "O")),
                new InMemoryLocator("request", Map.of("move", "A1", "player", "X")),
                new InMemoryLocator("game", Map.of("currentPlayer", "X"))
            )
        );
        final ComputationContext context = TestObjects.tablesFolderWithState(state);
        MatcherAssert.assertThat(
            "Should resolve dynamic coordinates while computing a table",
            context.decisionFor("dynamic-coordinate"),
            Matchers.allOf(
                Matchers.hasEntry(Matchers.equalTo("moveAvailable"), Matchers.equalTo("true"))
            )
        );
    }

    @Test
    void shouldThrowIfSeveralRulesResolveToTrue() {
        final State state = new State(
            List.of(
                new InMemoryLocator("data", Map.of("value", 1))
            )
        );
        final ComputationContext context = TestObjects.tablesFolderWithState(state);
        Assertions
            .assertThatThrownBy(() -> context.decisionFor("multiple-rules"))
            .isInstanceOf(DecitaException.class)
            .hasMessageContaining("Multiple rules are satisfied");
    }

    @Test
    void shouldPerformCommandFromTable() {
        final State state = new State(
            List.of(
                new InMemoryLocator("data", Map.of("is-stored", "true")),
                new InMemoryLocator("market", Map.of(EndToEndTest.SHOP, 2)),
                new InMemoryLocator("currentPlayer", Map.of("name", "Eugene")),
                new InMemoryLocator("request", Map.of(EndToEndTest.SHOP, 3))
            )
        );
        final ComputationContext context = TestObjects.tablesFolderWithState(state);
        context.perform("sample-table");
        MatcherAssert.assertThat(
            "The table is computed correctly",
            state.locatorFor("market").state(),
            Matchers.hasEntry(EndToEndTest.SHOP, "3")
        );
    }
}
