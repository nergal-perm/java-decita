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

package ru.ewc.decisions;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.core.ConstantLocator;

/**
 * Tests for the {@link ConstantLocator} class.
 *
 * @since 0.1
 */
final class ConstantLocatorTest {
    /**
     * Simple instantiation test.
     */
    @Test
    void shouldInstantiate() {
        final Locator target = new ConstantLocator();
        MatcherAssert.assertThat(
            "The constant locator is instantiated",
            target, Matchers.not(Matchers.nullValue())
        );
    }

    @Test
    void shouldLocateBooleanFragment() throws DecitaException {
        final String actual = new ConstantLocator().fragmentBy("true", defaultContext());
        MatcherAssert.assertThat(
            "The constant locator should find boolean values",
            actual, Matchers.is("true")
        );
    }

    @Test
    void shouldLocateStringFragment() throws DecitaException {
        final String actual = new ConstantLocator().fragmentBy("test value", defaultContext());
        MatcherAssert.assertThat(
            "The constant locator should find string values",
            actual, Matchers.is("test value")
        );
    }

    private static ComputationContext defaultContext() {
        return TestObjects.defaultContext();
    }
}
