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

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;

/**
 * I am a test for the {@link SimpleCommand}.
 *
 * @since 0.5.0
 */
final class SimpleCommandTest {
    @Test
    void shouldCreateState() {
        final ComputationContext actual = TestObjects.ticTacToeContext();
        ticTacToeTable().perform(actual);
        MatcherAssert.assertThat(
            "Table name should be updated to 'Tic-Tac-Toe'",
            actual.valueFor("table", "name"),
            Matchers.is("Tic-Tac-Toe")
        );
    }

    @Test
    void shouldUpdateState() {
        final ComputationContext actual = TestObjects.ticTacToeContext();
        ticTacToeTable().perform(actual);
        machiKoroTable().perform(actual);
        MatcherAssert.assertThat(
            "Table name should be updated to 'Machi-Koro'",
            actual.valueFor("table", "name"),
            Matchers.is("Machi-Koro")
        );
    }

    private static SimpleCommand machiKoroTable() {
        return new SimpleCommand(List.of("table::name -> Machi-Koro"));
    }

    private static SimpleCommand ticTacToeTable() {
        return new SimpleCommand(List.of("table::name -> Tic-Tac-Toe"));
    }
}
