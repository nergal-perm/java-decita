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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for a single {@link Condition}.
 *
 * @since 0.1
 */
class SingleConditionTest {
    @Test
    void testInstantiating() {
        final Condition target = simpleConstantCondition();
        MatcherAssert.assertThat(
            target,
            Matchers.notNullValue()
        );
    }

    @Test
    void testEvaluatingToTrue() throws DecitaException {
        final Condition target = simpleConstantCondition();
        MatcherAssert.assertThat(
            target.evaluate(computationContext()),
            Matchers.is(true)
        );
    }

    @Test
    void testNotComputedCondition() {
        final Condition target = new SingleCondition(
            TestObjects.alwaysTrueConditionCoordinate(),
            "=",
            TestObjects.valueTrue()
        );
        MatcherAssert.assertThat(
            target.isEvaluated(),
            Matchers.is(false)
        );
    }

    @Test
    void testBecomeComputedAfterEvaluation() throws DecitaException {
        final Condition target = new SingleCondition(
            TestObjects.alwaysTrueConditionCoordinate(),
            "=",
            TestObjects.valueTrue()
        );
        target.evaluate(TestObjects.computationContext());
        MatcherAssert.assertThat(
            target.isEvaluated(),
            Matchers.is(true)
        );
    }

    private static SingleCondition simpleConstantCondition() {
        return TestObjects.alwaysTrueConstantCondition();
    }

    private static ComputationContext computationContext() {
        return TestObjects.computationContext();
    }
}
