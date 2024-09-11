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
import ru.ewc.decisions.input.SourceLines;
import ru.ewc.state.State;

/**
 * Tests for a naive implementation of the {@link CheckInstance}.
 *
 * @since 0.8.0
 */
final class CheckInstanceTest {

    @Test
    void shouldPerformArrangeSection() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                CheckInstanceTest.ticTacToeTableNameCheckRule()
            )
        );
        final Map<String, List<CheckFailure>> actual = target.testResult(context);
        MatcherAssert.assertThat(
            "should perform a check in a predefined context",
            actual.get("nameCheck::rule_01"),
            Matchers.emptyCollectionOf(CheckFailure.class)
        );
    }

    @Test
    void multipleRulesShouldRunOnSeparateContexts() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                CheckInstanceTest.ticTacToeTableNameCheckRule(),
                CheckInstanceTest.ticTacToeTableMaxPlayersRule()
            )
        );
        final Map<String, List<CheckFailure>> actual = target.testResult(context);
        MatcherAssert.assertThat(
            "Multiple rules should run on separate Contexts",
            actual.get("change max players::rule_01"),
            Matchers.emptyCollectionOf(CheckFailure.class)
        );
    }

    @Test
    void multipleTestsWithCommand() {
        final ComputationContext context = TestObjects.tablesFolderWithState(initialState());
        final SourceLines source = new SourceLines(
            "sample check",
            List.of(
                "ASG;market::shop;2;2",
                "ASG;data::is-stored;true;true",
                "ASG;currentPlayer::name;Eugene;Eugene",
                "ASG;request::shop;3;4",
                "OUT;execute;sample-table;sample-table",
                "CND;market::shop;3;4"
            ),
            ";"
        );
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                new Rule(source.specifiedRulesFragments().get(0)),
                new Rule(source.specifiedRulesFragments().get(1))
            )
        );
        MatcherAssert.assertThat(
            "Should perform Command during the test",
            target.testResult(context).get("sample check::rule_01"),
            Matchers.emptyCollectionOf(CheckFailure.class)
        );
    }

    @Test
    void shouldPerformAllTheAssertsInTheTest() {
        final ComputationContext context = TestObjects.tablesFolderWithState(initialState());
        final MultipleOutcomes target = new CheckInstance(
            List.of(
                new Rule(
                    new SourceLines(
                        "first check",
                        List.of(
                            "ASG;market::shop;2",
                            "ASG;data::is-stored;true",
                            "ASG;currentPlayer::name;Eugene",
                            "ASG;request::shop;3",
                            "OUT;execute;sample-table",
                            "CND;market::shop;3",
                            "CND;data::is-stored;false",
                            "CND;currentPlayer::name;Alice"
                        ),
                        ";"
                    ).specifiedRulesFragments().get(0)
                )
            )
        );
        MatcherAssert.assertThat(
            "Should perform all the asserts in the test and report all the eliminated conditions",
            target.testResult(context).get("first check::rule_01"),
            Matchers.hasSize(2)
        );
    }

    private static State initialState() {
        return State.withEmptyLocators(
            List.of("data", "market", "currentPlayer", "request")
        );
    }

    private static Rule ticTacToeTableNameCheckRule() {
        return new Rule(
            new SourceLines(
                "nameCheck",
                List.of("ASG;table::name;tic-tac-toe", "CND;table::name;tic-tac-toe"),
                ";"
            ).specifiedRulesFragments().get(0)
        );
    }

    private static Rule ticTacToeTableMaxPlayersRule() {
        return new Rule(
            new SourceLines(
                "change max players",
                List.of(
                    "ASG;table::maxPlayers;2",
                    "CND;table::maxPlayers;2",
                    "CND;table::name;undefined"
                ),
                ";"
            ).specifiedRulesFragments().get(0)
        );
    }
}
