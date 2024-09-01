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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.core.CheckInstance;
import ru.ewc.decisions.core.Rule;
import ru.ewc.state.State;

/**
 * Tests for a naive implementation of the {@link CheckInstance}.
 *
 * @since 0.8.0
 */
final class CheckInstanceTest {
    /**
     * The name of the data fragment pointing to the name of the game table in tests.
     */
    public static final String TABLE_NAME = "table::name";

    @Test
    void shouldPerformArrangeSection() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                new Rule("assertOnly")
                    .withAssignment(CheckInstanceTest.TABLE_NAME, "tic-tac-toe")
                    .withCondition(CheckInstanceTest.TABLE_NAME, "tic-tac-toe")
            )
        );
        final Map<String, List<String>> actual = target.testResult(context);
        MatcherAssert.assertThat(
            "should perform a check in a predefined context",
            actual.get("assertOnly"),
            Matchers.emptyCollectionOf(String.class)
        );
    }

    @Test
    void multipleRulesShouldRunOnSeparateContexts() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                new Rule("change name")
                    .withAssignment(CheckInstanceTest.TABLE_NAME, "tic-tac-toe")
                    .withCondition(CheckInstanceTest.TABLE_NAME, "tic-tac-toe"),
                new Rule("change max players")
                    .withAssignment("table::maxPlayers", "2")
                    .withCondition(CheckInstanceTest.TABLE_NAME, "undefined")
                    .withCondition("table::maxPlayers", "2")
            )
        );
        final Map<String, List<String>> actual = target.testResult(context);
        MatcherAssert.assertThat(
            "Multiple rules should run on separate Contexts",
            actual.get("change max players"),
            Matchers.emptyCollectionOf(String.class)
        );
    }

    @Test
    void multipleTestsWithCommand() {
        final ComputationContext context = TestObjects.tablesFolderWithState(initialState());
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                arrangeAndAct("first check")
                    .withAssignment("request::shop", "3")
                    .withCondition("market::shop", "3"),
                arrangeAndAct("second check")
                    .withAssignment("request::shop", "4")
                    .withCondition("market::shop", "4")
            )
        );
        MatcherAssert.assertThat(
            "Should perform Command during the test",
            target.testResult(context).get("second check"),
            Matchers.emptyCollectionOf(String.class)
        );
    }

    @Test
    void shouldPerformAllTheAssertsInTheTest() {
        final ComputationContext context = TestObjects.tablesFolderWithState(initialState());
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                arrangeAndAct("first check")
                    .withAssignment("request::shop", "3")
                    .withCondition("market::shop", "3")
                    .withCondition("data::is-stored", "false")
                    .withCondition("currentPlayer::name", "Alice")
            )
        );
        MatcherAssert.assertThat(
            "Should perform all the asserts in the test and report all the eliminated conditions",
            target.testResult(context).get("first check"),
            Matchers.hasSize(2)
        );
    }

    private static Rule arrangeAndAct(final String name) {
        return new Rule(name)
            .withAssignment("market::shop", "2")
            .withAssignment("data::is-stored", "true")
            .withAssignment("currentPlayer::name", "Eugene")
            .withOutcome("execute", "sample-table");
    }

    private static State initialState() {
        return State.withEmptyLocators(
            List.of("data", "market", "currentPlayer", "request")
        );
    }
}
