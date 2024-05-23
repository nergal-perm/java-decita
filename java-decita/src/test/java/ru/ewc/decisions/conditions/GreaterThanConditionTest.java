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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.core.Coordinate;

/**
 * Unit tests for {@link GreaterThanCondition}.
 *
 * @since 0.3
 */
final class GreaterThanConditionTest {
    /**
     * The context for the tests.
     */
    private ComputationContext context;

    @BeforeEach
    void setUp() {
        this.context = TestObjects.defaultContext();
    }

    @Test
    void shouldNotCompareStrings() {
        final GreaterThanCondition target = new GreaterThanCondition(
            new Coordinate(Locator.CONSTANT_VALUES, "true"),
            new Coordinate(Locator.CONSTANT_VALUES, "false")
        );
        Assertions.assertThatThrownBy(() -> target.evaluate(this.context))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot compare strings");
    }

    @Test
    void shouldCompareNumbers() {
        final GreaterThanCondition target = new GreaterThanCondition(
            new Coordinate(Locator.CONSTANT_VALUES, "5"),
            new Coordinate(Locator.CONSTANT_VALUES, "3.5")
        );
        Assertions.assertThat(target.evaluate(this.context)).isTrue();
    }
}
