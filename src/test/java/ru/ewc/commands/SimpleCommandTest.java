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

package ru.ewc.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.core.InMemoryLocator;
import ru.ewc.state.State;

/**
 * I am a test for the {@link SimpleCommand}.
 *
 * @since 0.5.0
 */
final class SimpleCommandTest {
    @Test
    void shouldCreateState() {
        final ComputationContext actual = TestObjects.ticTacToeContext();
        ticTacToeCommand().performIn(actual);
        MatcherAssert.assertThat(
            "Table name should be updated to 'Tic-Tac-Toe'",
            actual.valueFor("table", "name"),
            Matchers.is("Tic-Tac-Toe")
        );
    }

    @Test
    void shouldUpdateState() {
        final ComputationContext actual = TestObjects.ticTacToeContext();
        ticTacToeCommand().performIn(actual);
        machiKoroCommand().performIn(actual);
        MatcherAssert.assertThat(
            "Table name should be updated to 'Machi-Koro'",
            actual.valueFor("table", "name"),
            Matchers.is("Machi-Koro")
        );
    }

    @Test
    void shouldExtractUnresolvedParts() {
        final SimpleCommand command = new SimpleCommand(
            List.of("table::${function::currentTable} -> ${request::tableName}")
        );
        MatcherAssert.assertThat(
            "Command should provide its unresolved parts",
            command.unresolvedParts(),
            Matchers.containsInAnyOrder("request::tableName", "function::currentTable")
        );
    }

    @Test
    void shouldResolveUnresolvedParts() {
        final SimpleCommand command = new SimpleCommand(
            List.of("table::${function::currentTable} -> ${request::tableName}")
        );
        final ComputationContext context = tableNameChangeContext();
        final OutputTracker<String> tracker = context.startTracking();
        command.performIn(context);
        MatcherAssert.assertThat(
            "Should have changed the table name",
            context.valueFor("table", "0000"),
            Matchers.is("Tic-Tac-Toe")
        );
        MatcherAssert.assertThat(
            "Should have logged all the computations",
            tracker.events(),
            Matchers.contains(
                "ST: function::currentTable => 0000",
                "DN: table::${function::currentTable} => table::0000",
                "ST: request::tableName => Tic-Tac-Toe",
                "DN: ${request::tableName} => Tic-Tac-Toe",
                "ST: constant::Tic-Tac-Toe => Tic-Tac-Toe"
            )
        );
    }

    private static ComputationContext tableNameChangeContext() {
        return new ComputationContext(
            new State(
                Map.of(
                    "table", new InMemoryLocator(new HashMap<>(Map.of("0000", "Machi Koro"))),
                    "function", new InMemoryLocator(Map.of("currentTable", "0000")),
                    "request", new InMemoryLocator(Map.of("tableName", "Tic-Tac-Toe"))
                )
            ),
            new DecisionTables(List.of())
        );
    }

    private static SimpleCommand machiKoroCommand() {
        return new SimpleCommand(List.of("table::name -> Machi-Koro"));
    }

    private static SimpleCommand ticTacToeCommand() {
        return new SimpleCommand(List.of("table::name -> Tic-Tac-Toe"));
    }
}
