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
package ru.ewc.decisions.input;

import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.OutputTracker;

/**
 * Tests for {@link RawContent}.
 *
 * @since 0.3
 */
final class RawContentTest {
    @Test
    void shouldParseDifferentConditions() {
        final RawContent target = new RawContent(RawContentTest.sourceLines(), "sample-table");
        MatcherAssert.assertThat(
            "The decision table is parsed correctly",
            target.asDecisionTable().outcome(TestObjects.defaultContext()),
            Matchers.is(Map.of("outcome", "true"))
        );
    }

    @Test
    void shouldUseHeaderRow() {
        final RawContent target = new RawContent(RawContentTest.sourceWithHeader(), "sample-table");
        final ComputationContext context = TestObjects.defaultContext();
        final OutputTracker<String> tracker = context.startTracking();
        target.asDecisionTable().outcome(context);
        MatcherAssert.assertThat(
            "",
            tracker.events(),
            Matchers.hasItems(
                "RL: sample-table::evaluate to false => false",
                "RL: sample-table::evaluate to true => true"
            )
        );
    }

    private static SourceLines sourceWithHeader() {
        return SourceLines.fromLinesWithDelimiter(
            List.of(
                "HDR;header-example;evaluate to false;evaluate to true",
                "CND;10;!>5;<20",
                "CND;20;!<30;~",
                "CND;true;false;!false",
                "OUT;outcome;false;true;else"
            ),
            ";"
        );
    }

    private static SourceLines sourceLines() {
        return SourceLines.fromLinesWithDelimiter(
            List.of(
                "CND;10;!>5;<20",
                "CND;20;!<30;~",
                "CND;true;false;!false",
                "OUT;outcome;false;true;else"
            ),
            ";"
        );
    }

}
