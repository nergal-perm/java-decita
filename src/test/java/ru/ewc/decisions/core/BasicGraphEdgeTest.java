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
 * Tests for {@link BasicGraphEdge}.
 *
 * @since 0.9.2
 */
final class BasicGraphEdgeTest {
    /**
     * Source coordinate description.
     */
    private static final String SOURCE = "test::source";

    /**
     * Target coordinate description.
     */
    private static final String TARGET = "test::target";

    /**
     * Relationship used in the equality examples.
     */
    private static final String RELATION = "part_of";

    @Test
    void shouldCreateEdgeWithSourceTargetAndRelationship() {
        final CoordinateNode source = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        final CoordinateNode target = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        final BasicGraphEdge edge = new BasicGraphEdge(source, target, BasicGraphEdgeTest.RELATION);
        Assertions.assertThat(edge.source()).isEqualTo(source);
        Assertions.assertThat(edge.target()).isEqualTo(target);
        Assertions.assertThat(edge.relationshipType()).isEqualTo(BasicGraphEdgeTest.RELATION);
        Assertions.assertThat(edge.id())
            .isEqualTo("coordinate:test::source--part_of-->coordinate:test::target");
    }

    @Test
    void shouldNotAllowNullSource() {
        final CoordinateNode target = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        Assertions.assertThatThrownBy(
            () -> new BasicGraphEdge(null, target, BasicGraphEdgeTest.RELATION)
        )
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Source node cannot be null");
    }

    @Test
    void shouldNotAllowNullTarget() {
        final CoordinateNode source = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        Assertions.assertThatThrownBy(
            () -> new BasicGraphEdge(source, null, BasicGraphEdgeTest.RELATION)
        )
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Target node cannot be null");
    }

    @Test
    void shouldNotAllowNullRelationshipType() {
        final CoordinateNode source = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        final CoordinateNode target = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        Assertions.assertThatThrownBy(() -> new BasicGraphEdge(source, target, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Relationship type cannot be null");
    }

    @Test
    void shouldBeEqualWhenAllPropertiesAreEqual() {
        final CoordinateNode firstsource = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        final CoordinateNode firsttarget = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        final CoordinateNode nextsource = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        final CoordinateNode nexttarget = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        final BasicGraphEdge firstedge = new BasicGraphEdge(
            firstsource, firsttarget, BasicGraphEdgeTest.RELATION
        );
        final BasicGraphEdge secondedge = new BasicGraphEdge(
            nextsource, nexttarget, BasicGraphEdgeTest.RELATION
        );
        Assertions.assertThat(firstedge).isEqualTo(secondedge);
        Assertions.assertThat(firstedge.hashCode()).isEqualTo(secondedge.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenSourceIsDifferent() {
        final CoordinateNode firstsource = new CoordinateNode(Coordinate.from("test::firstsource"));
        final CoordinateNode nextsource = new CoordinateNode(Coordinate.from("test::nextsource"));
        final CoordinateNode target = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        final BasicGraphEdge firstedge = new BasicGraphEdge(
            firstsource, target, BasicGraphEdgeTest.RELATION
        );
        final BasicGraphEdge secondedge = new BasicGraphEdge(
            nextsource, target, BasicGraphEdgeTest.RELATION
        );
        Assertions.assertThat(firstedge).isNotEqualTo(secondedge);
    }

    @Test
    void shouldNotBeEqualWhenTargetIsDifferent() {
        final CoordinateNode source = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        final CoordinateNode firsttarget = new CoordinateNode(Coordinate.from("test::firsttarget"));
        final CoordinateNode nexttarget = new CoordinateNode(Coordinate.from("test::nexttarget"));
        final BasicGraphEdge firstedge = new BasicGraphEdge(
            source, firsttarget, BasicGraphEdgeTest.RELATION
        );
        final BasicGraphEdge secondedge = new BasicGraphEdge(
            source, nexttarget, BasicGraphEdgeTest.RELATION
        );
        Assertions.assertThat(firstedge).isNotEqualTo(secondedge);
    }

    @Test
    void shouldNotBeEqualWhenRelationshipTypeIsDifferent() {
        final CoordinateNode source = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.SOURCE)
        );
        final CoordinateNode target = new CoordinateNode(
            Coordinate.from(BasicGraphEdgeTest.TARGET)
        );
        final BasicGraphEdge firstedge = new BasicGraphEdge(
            source, target, BasicGraphEdgeTest.RELATION
        );
        final BasicGraphEdge secondedge = new BasicGraphEdge(source, target, "lhs_of");
        Assertions.assertThat(firstedge).isNotEqualTo(secondedge);
    }
}
