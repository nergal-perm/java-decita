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
    @Test
    void shouldCreateEdgeWithSourceTargetAndRelationship() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("test::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("test::target"));
        final BasicGraphEdge edge = new BasicGraphEdge(source, target, "part_of");
        
        Assertions.assertThat(edge.source()).isEqualTo(source);
        Assertions.assertThat(edge.target()).isEqualTo(target);
        Assertions.assertThat(edge.relationshipType()).isEqualTo("part_of");
        Assertions.assertThat(edge.id()).isEqualTo("coordinate:test::source--part_of-->coordinate:test::target");
    }

    @Test
    void shouldNotAllowNullSource() {
        final CoordinateNode target = new CoordinateNode(Coordinate.from("test::target"));
        Assertions.assertThatThrownBy(() -> new BasicGraphEdge(null, target, "part_of"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Source node cannot be null");
    }

    @Test
    void shouldNotAllowNullTarget() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("test::source"));
        Assertions.assertThatThrownBy(() -> new BasicGraphEdge(source, null, "part_of"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Target node cannot be null");
    }

    @Test
    void shouldNotAllowNullRelationshipType() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("test::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("test::target"));
        Assertions.assertThatThrownBy(() -> new BasicGraphEdge(source, target, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Relationship type cannot be null");
    }

    @Test
    void shouldBeEqualWhenAllPropertiesAreEqual() {
        final CoordinateNode source1 = new CoordinateNode(Coordinate.from("test::source"));
        final CoordinateNode target1 = new CoordinateNode(Coordinate.from("test::target"));
        final CoordinateNode source2 = new CoordinateNode(Coordinate.from("test::source"));
        final CoordinateNode target2 = new CoordinateNode(Coordinate.from("test::target"));
        
        final BasicGraphEdge edge1 = new BasicGraphEdge(source1, target1, "part_of");
        final BasicGraphEdge edge2 = new BasicGraphEdge(source2, target2, "part_of");
        
        Assertions.assertThat(edge1).isEqualTo(edge2);
        Assertions.assertThat(edge1.hashCode()).isEqualTo(edge2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenSourceIsDifferent() {
        final CoordinateNode source1 = new CoordinateNode(Coordinate.from("test::source1"));
        final CoordinateNode source2 = new CoordinateNode(Coordinate.from("test::source2"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("test::target"));
        
        final BasicGraphEdge edge1 = new BasicGraphEdge(source1, target, "part_of");
        final BasicGraphEdge edge2 = new BasicGraphEdge(source2, target, "part_of");
        
        Assertions.assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void shouldNotBeEqualWhenTargetIsDifferent() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("test::source"));
        final CoordinateNode target1 = new CoordinateNode(Coordinate.from("test::target1"));
        final CoordinateNode target2 = new CoordinateNode(Coordinate.from("test::target2"));
        
        final BasicGraphEdge edge1 = new BasicGraphEdge(source, target1, "part_of");
        final BasicGraphEdge edge2 = new BasicGraphEdge(source, target2, "part_of");
        
        Assertions.assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void shouldNotBeEqualWhenRelationshipTypeIsDifferent() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("test::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("test::target"));
        
        final BasicGraphEdge edge1 = new BasicGraphEdge(source, target, "part_of");
        final BasicGraphEdge edge2 = new BasicGraphEdge(source, target, "lhs_of");
        
        Assertions.assertThat(edge1).isNotEqualTo(edge2);
    }
}