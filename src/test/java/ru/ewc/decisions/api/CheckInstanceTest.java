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
        final CheckInstance target = new CheckInstance(
            List.of(
                new Rule("assertOnly")
                    .withAssignment(CheckInstanceTest.TABLE_NAME, "tic-tac-toe")
                    .withCondition(CheckInstanceTest.TABLE_NAME, "tic-tac-toe")
            )
        );
        final Map<String, String> actual = target.outcome(context);
        MatcherAssert.assertThat(
            "should perform a check in a predefined context",
            actual.get("assertOnly"),
            Matchers.emptyString()
        );
    }

    @Test
    void multipleRulesShouldRunOnSeparateContexts() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final CheckInstance target = new CheckInstance(
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
        final Map<String, String> actual = target.outcome(context);
        MatcherAssert.assertThat(
            "Multiple rules should run on separate Contexts",
            actual.get("change max players"),
            Matchers.emptyString()
        );
    }

    @Test
    void shouldPerformCommandsDuringTheTest() {
        final State state = State.withEmptyLocators(
            List.of("data", "market", "currentPlayer", "request")
        );
        final ComputationContext context = TestObjects.tablesFolderWithState(state);
        final CheckInstance target = new CheckInstance(
            List.of(
                new Rule("run a command")
                    .withAssignment("market::shop", "2")
                    .withAssignment("data::is-stored", "true")
                    .withAssignment("currentPlayer::name", "Eugene")
                    .withAssignment("request::shop", "3")
                    .withOutcome("execute", "sample-table")
                    .withCondition("market::shop", "3")
            )
        );
        MatcherAssert.assertThat(
            "Should perform Command during the test",
            target.outcome(context).get("run a command"),
            Matchers.emptyString()
        );
    }
}
