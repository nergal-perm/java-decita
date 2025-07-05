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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CoordinateNode}.
 *
 * @since 0.9.2
 */
final class CoordinateNodeTest {
    @Test
    void shouldCreateNodeWithCoordinate() {
        final Coordinate coordinate = Coordinate.from("test::value");
        final CoordinateNode node = new CoordinateNode(coordinate);
        
        Assertions.assertThat(node.component()).isEqualTo(coordinate);
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
        final Coordinate coordinate1 = Coordinate.from("test::value");
        final Coordinate coordinate2 = Coordinate.from("test::value");
        final CoordinateNode node1 = new CoordinateNode(coordinate1);
        final CoordinateNode node2 = new CoordinateNode(coordinate2);
        
        Assertions.assertThat(node1).isEqualTo(node2);
        Assertions.assertThat(node1.hashCode()).isEqualTo(node2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenCoordinatesAreDifferent() {
        final Coordinate coordinate1 = Coordinate.from("test::value1");
        final Coordinate coordinate2 = Coordinate.from("test::value2");
        final CoordinateNode node1 = new CoordinateNode(coordinate1);
        final CoordinateNode node2 = new CoordinateNode(coordinate2);
        
        Assertions.assertThat(node1).isNotEqualTo(node2);
    }
}