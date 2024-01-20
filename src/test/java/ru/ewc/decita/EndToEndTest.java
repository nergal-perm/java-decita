/*
 * MIT License
 *
 * Copyright (c) 2023-2024 Eugene Terekhov
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

package ru.ewc.decita;

import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Full scenario tests.
 *
 * @since 0.1
 */
class EndToEndTest {
    /**
     * Constant for the table outcome field.
     */
    public static final String OUTCOME = "outcome";

    @Test
    void shouldComputeTheWholeTable() throws DecitaException {
        final Map<String, String> actual = contextWithHelloWorld().decisionFor("target");
        MatcherAssert.assertThat(
            actual,
            Matchers.allOf(
                Matchers.hasEntry(Matchers.equalTo(EndToEndTest.OUTCOME), Matchers.equalTo("true")),
                Matchers.hasEntry(Matchers.equalTo("text"), Matchers.equalTo("hello world"))
            )
        );
    }

    /**
     * Convenience method to create a naive target table.
     *
     * @return The {@link DecisionTable} instance.
     */
    private static DecisionTable tableThatLinkToHelloWorld() {
        return new DecisionTable(
            List.of(
                new Rule()
                    .withCondition(TestObjects.helloWorldOutcomeIs("Hello"))
                    .withOutcome(EndToEndTest.OUTCOME, "true")
                    .withOutcome("text", "hello world"),
                new Rule()
                    .withCondition(TestObjects.helloWorldOutcomeIs("World"))
                    .withOutcome(EndToEndTest.OUTCOME, "false")
                    .withOutcome("text", "no text")
            ));
    }

    /**
     * Convenience method to create a context for naive tables.
     *
     * @return Prefilled {@link ComputationContext}.
     */
    private static ComputationContext contextWithHelloWorld() {
        return TestObjects.contextWithLocators(
            Map.of(
                "hello-world", helloWorldTable(),
                "target", tableThatLinkToHelloWorld()
            )
        );
    }

    /**
     * Convenience method to create a naive 'Hello world' table.
     *
     * @return The {@link DecisionTable} instance.
     */
    private static DecisionTable helloWorldTable() {
        return new DecisionTable(
            List.of(
                TestObjects.alwaysTrueEqualsTrueRule().withOutcome(EndToEndTest.OUTCOME, "Hello"),
                TestObjects.alwaysTrueEqualsFalseRule().withOutcome(EndToEndTest.OUTCOME, "World")
            )
        );
    }
}
