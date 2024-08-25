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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.core.Coordinate;

/**
 * Tests for {@link Coordinate} class.
 *
 * @since 0.1
 */
final class CoordinateTest {
    /**
     * Tests for Constant coordinates, i.e. coordinates created from the specified value.
     *
     * @since 0.8.0
     */
    @Nested
    final class ConstantCoordinateTest {
        @Test
        void testConstantCoordinateIsAlwaysComputed() {
            final Coordinate target = Coordinate.from("true");
            MatcherAssert.assertThat(
                "The constant coordinate is always computed",
                target.isComputed(),
                Matchers.is(true)
            );
        }
    }

    /**
     * Tests for Dynamic coordinates, i.e. coordinates that have to resolve their own address
     * (locator, or fragment, or both) before being computed.
     *
     * @since 0.8.0
     */
    @Nested
    final class DynamicCoordinateTest {
        @Test
        void whenWholeAddressIsDynamicThenShouldResolveToConstantCoordinate() {
            final Coordinate target = Coordinate.from("${cells::A1}");
            final ComputationContext context = TestObjects.ticTacToeContext();
            context.setValueFor("cells", "A1", "empty");
            target.resolveIn(context);
            MatcherAssert.assertThat(
                "Dynamic coordinate defined by a single placeholder is resolved to a constant coordinate",
                target.asString(),
                Matchers.is("constant::empty")
            );
        }

        @Test
        void whenLocatorIsDynamicThenShouldResolveToStaticConstant() {
            final Coordinate target = Coordinate.from("${request::locator}::A1");
            final ComputationContext context = TestObjects.ticTacToeContext();
            context.setValueFor("request", "locator", "cells");
            target.resolveIn(context);
            MatcherAssert.assertThat(
                "Dynamic coordinate with a single placeholder is resolved to a static coordinate",
                target.asString(),
                Matchers.is("cells::A1")
            );
        }

        @Test
        void whenFragmentIsDynamicThenShouldResolveToStaticConstant() {
            final Coordinate target = Coordinate.from("cells::${request::fragment}");
            final ComputationContext context = TestObjects.ticTacToeContext();
            context.setValueFor("request", "fragment", "A1");
            target.resolveIn(context);
            MatcherAssert.assertThat(
                "Dynamic coordinate with a single placeholder is resolved to a static coordinate",
                target.asString(),
                Matchers.is("cells::A1")
            );
        }

        @Test
        void testDynamicCoordinateNotResolvedAfterCreation() {
            final Coordinate target = Coordinate.from("outcome::${dynamically::defined}");
            MatcherAssert.assertThat(
                "The dynamic coordinate is not resolved right after creation",
                target.isResolved(),
                Matchers.is(false)
            );
            MatcherAssert.assertThat(
                "The dynamic coordinate is not computed right after creation",
                target.isComputed(),
                Matchers.is(false)
            );
        }
    }

    /**
     * Tests for Static coordinates, i.e. coordinates that have their address known in advance.
     *
     * @since 0.8.0
     */
    @Nested
    final class StaticCoordinateTest {
        @Test
        void testStaticCoordinateResolvedButNotComputedAfterCreation() {
            final Coordinate target = Coordinate.from("always_true::outcome");
            MatcherAssert.assertThat(
                "The static coordinate is resolved right after creation",
                target.isResolved(),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                "The static coordinate is not computed right after creation",
                target.isComputed(),
                Matchers.is(false)
            );
        }

        @Test
        void testChangesUponLocation() throws DecitaException {
            final Coordinate target = Coordinate.from("always_true::outcome");
            final ComputationContext context = TestObjects.defaultContext();
            target.locateIn(context);
            MatcherAssert.assertThat(
                "Locating the coordinate means computing its constant value",
                target.isComputed(),
                Matchers.is(true)
            );
        }
    }
}
