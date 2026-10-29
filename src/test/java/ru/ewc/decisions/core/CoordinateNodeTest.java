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

package ru.ewc.decisions.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.api.InMemoryLocator;
import ru.ewc.state.State;

/**
 * Tests for {@link CoordinateNode}.
 *
 * @since 0.9.2
 */
final class CoordinateNodeTest {
    /**
     * A shared coordinate description.
     */
    private static final String VALUE = "test::value";

    @Test
    void retainsSourceDescriptionAfterOriginalCoordinateIsEvaluated() {
        final Coordinate coordinate = Coordinate.from("customer::age");
        final CoordinateNode node = new CoordinateNode(coordinate);
        final Set<GraphNode> index = new HashSet<>(Set.of(node));
        final ComputationContext context = new ComputationContext(
            new State(
                List.of(new InMemoryLocator("customer", Map.of("age", 21)))
            ),
            DecisionTables.using(List::of)
        );
        coordinate.locateIn(context);
        Assertions.assertThat(node.component()).isEqualTo("customer::age");
        Assertions.assertThat(node.id()).isEqualTo("coordinate:customer::age");
        Assertions.assertThat(index).contains(node);
    }

    @Test
    void shouldCreateNodeWithCoordinate() {
        final Coordinate coordinate = Coordinate.from(CoordinateNodeTest.VALUE);
        final CoordinateNode node = new CoordinateNode(coordinate);
        Assertions.assertThat(node.component()).isEqualTo(coordinate.asString());
        Assertions.assertThat(node.type()).isEqualTo("coordinate");
        Assertions.assertThat(node.id()).isEqualTo("coordinate:test::value");
    }

    @Test
    void shouldNotAllowNullCoordinate() {
        Assertions.assertThatThrownBy(() -> new CoordinateNode(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Coordinate cannot be null");
    }

    @Test
    void shouldBeEqualWhenCoordinatesAreEqual() {
        final Coordinate first = Coordinate.from(CoordinateNodeTest.VALUE);
        final Coordinate second = Coordinate.from(CoordinateNodeTest.VALUE);
        final CoordinateNode firstnode = new CoordinateNode(first);
        final CoordinateNode secondnode = new CoordinateNode(second);
        Assertions.assertThat(firstnode).isEqualTo(secondnode);
        Assertions.assertThat(firstnode.hashCode()).isEqualTo(secondnode.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenCoordinatesAreDifferent() {
        final Coordinate first = Coordinate.from("test::value1");
        final Coordinate second = Coordinate.from("test::value2");
        final CoordinateNode firstnode = new CoordinateNode(first);
        final CoordinateNode secondnode = new CoordinateNode(second);
        Assertions.assertThat(firstnode).isNotEqualTo(secondnode);
    }
}
