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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.state.State;

/**
 * End-to-end tests for the {@link CheckSuite}.
 *
 * @since 0.8.0
 */
final class CheckSuiteTest {
    @Test
    void shouldPassSingleTest() {
        final CheckSuite target = TestObjects.readTestsFrom("single_passing");
        final ComputationContext context = TestObjects.tablesFolderWithState(
            State.withEmptyLocators(List.of("market", "data", "currentPlayer", "request"))
        );
        MatcherAssert.assertThat(
            "",
            target.perform(context),
            Matchers.allOf(
                Matchers.hasEntry("single-test::test_01", this.emptyList()),
                Matchers.hasEntry(
                    "single-test::test_02",
                    List.of(
                        "Expected:\n\tmarket::shop = constant::3\nbut was:\n\tconstant::4 = constant::3"
                    )
                )
            )
        );
    }

    private List<String> emptyList() {
        return List.of();
    }
}
