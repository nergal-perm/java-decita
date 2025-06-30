/*
 * MIT License
 *
 * Copyright (c) 2024-2025 Eugene Terekhov
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
package ru.ewc.decisions.conditions;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.commands.Assignment;
import ru.ewc.decisions.core.Coordinate;

/**
 * Unit tests for {@link NotCondition}.
 *
 * @since 0.3
 */
final class NotConditionTest {
    @Test
    void shouldNegateBaseCondition() {
        final Condition target = TestObjects.alwaysTrueConstantCondition();
        final ComputationContext context = TestObjects.defaultContext();
        final OutputTracker<String> tracker = context.startTracking();
        MatcherAssert.assertThat(
            "The negated 'always true' condition resolves to 'false'",
            new NotCondition(target).evaluate(context),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            "Should log correct number of events",
            tracker.events().size(),
            Matchers.is(1)
        );
    }

    @Test
    void testNotUndefined() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final Condition target = Condition.from(Coordinate.from("cells::A1"), "!undefined");
        final OutputTracker<String> tracker = context.startTracking();
        final boolean actual = target.evaluate(context);
        tracker.events().forEach(System.out::println);
        MatcherAssert.assertThat(
            "Should evaluate '!undefined' as false",
            actual,
            Matchers.is(false)
        );
    }

    @Test
    void testUndefined() {
        final ComputationContext context = TestObjects.ticTacToeContext();
        final Coordinate cell = Coordinate.from("cells::A1");
        new Assignment(cell, Coordinate.from("X")).performIn(context);
        final Condition target = Condition.from(cell, "!undefined");
        final OutputTracker<String> tracker = context.startTracking();
        final boolean actual = target.evaluate(context);
        tracker.events().forEach(System.out::println);
        MatcherAssert.assertThat(
            "Should evaluate '!undefined' as false",
            actual,
            Matchers.is(true)
        );
    }
}
