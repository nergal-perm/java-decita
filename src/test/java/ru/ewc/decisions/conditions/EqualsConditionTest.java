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

/**
 * Tests for a single {@link Condition}.
 *
 * @since 0.1
 */
final class EqualsConditionTest {
    @Test
    void testInstantiating() {
        final Condition target = TestObjects.alwaysTrueConstantCondition();
        MatcherAssert.assertThat(
            "The condition is instantiated",
            target,
            Matchers.notNullValue()
        );
    }

    @Test
    void testEvaluatingToTrue() throws DecitaException {
        final Condition target = TestObjects.alwaysTrueConstantCondition();
        final ComputationContext context = TestObjects.defaultContext();
        MatcherAssert.assertThat(
            "The 'always true' condition evaluates to true",
            target.evaluate(context),
            Matchers.is(true)
        );
    }

    @Test
    void testNotComputedCondition() {
        final Condition target = new EqualsCondition(
            TestObjects.alwaysTrueConditionCoordinate(),
            TestObjects.valueTrue()
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
