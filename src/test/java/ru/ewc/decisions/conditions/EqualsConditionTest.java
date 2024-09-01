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

package ru.ewc.decisions.conditions;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.OutputTracker;
import ru.ewc.decisions.core.Coordinate;

/**
 * Tests for a single {@link Condition}.
 *
 * @since 0.1
 */
final class EqualsConditionTest {
    @Test
    void testEvaluatingToTrue() throws DecitaException {
        final Condition target = TestObjects.alwaysTrueConstantCondition();
        final ComputationContext context = TestObjects.defaultContext();
        final OutputTracker<String> tracker = context.startTracking();
        MatcherAssert.assertThat(
            "The 'always true' condition evaluates to true",
            target.evaluate(context),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Should log correct number of events",
            tracker.events().size(),
            Matchers.is(1)
        );
    }

    @Test
    void testNotComputedCondition() {
        final Condition target = new EqualsCondition(
            Coordinate.from("always_true::outcome"),
            Coordinate.TRUE
        );
        MatcherAssert.assertThat(
            "The condition is not yet evaluated right after creation",
            target.isEvaluated(),
            Matchers.is(false)
        );
    }

    @Test
    void testBecomeComputedAfterEvaluation() throws DecitaException {
        final Condition target = TestObjects.alwaysTrueConstantCondition();
        target.evaluate(TestObjects.defaultContext());
        MatcherAssert.assertThat(
            "The condition is evaluated after the first evaluation",
            target.isEvaluated(),
            Matchers.is(true)
        );
    }
}
