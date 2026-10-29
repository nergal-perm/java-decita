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
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link InMemoryDecisionGraph}.
 *
 * @since 0.9.2
 */
final class InMemoryDecisionGraphTest {
    /**
     * First coordinate description.
     */
    private static final String FIRST = "test::value1";

    /**
     * Second coordinate description.
     */
    private static final String SECOND = "test::value2";

    /**
     * Relationship used by graph examples.
     */
    private static final String RELATION = "part_of";

    @Test
    void returnsUnmodifiableQueryResults() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("query::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("query::target"));
        final DecisionGraph graph = new DecisionGraphBuilder()
            .addPartOfRelationship(source, target).build();
        org.junit.jupiter.api.Assertions.assertAll(
            () -> Assertions.assertThatThrownBy(
                () -> graph.nodesOfType(CoordinateNode.class).clear()
            ).isInstanceOf(UnsupportedOperationException.class),
            () -> Assertions.assertThatThrownBy(
                () -> graph.edgesOfType(PartOfEdge.class).clear()
            ).isInstanceOf(UnsupportedOperationException.class),
            () -> Assertions.assertThatThrownBy(
                () -> graph.edgesFrom(source).clear()
            ).isInstanceOf(UnsupportedOperationException.class),
            () -> Assertions.assertThatThrownBy(
                () -> graph.edgesTo(target).clear()
            ).isInstanceOf(UnsupportedOperationException.class)
        );
    }

    @Test
    void preservesMembershipAfterCallerClearsInputCollections() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("caller::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("caller::target"));
        final Set<GraphNode> nodes = new HashSet<>(Set.of(source, target));
        final Set<GraphEdge> edges = new HashSet<>(
            Set.of(new BasicGraphEdge(source, target, InMemoryDecisionGraphTest.RELATION))
        );
        final DecisionGraph graph = new InMemoryDecisionGraph(nodes, edges);
        nodes.clear();
        edges.clear();
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.edges()).hasSize(1);
    }

    @Test
    void shouldCreateEmptyGraph() {
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(), Set.of());
        Assertions.assertThat(graph.nodes()).isEmpty();
        Assertions.assertThat(graph.edges()).isEmpty();
    }

    @Test
    void shouldCreateGraphWithNodes() {
        final CoordinateNode first = new CoordinateNode(
            Coordinate.from(InMemoryDecisionGraphTest.FIRST)
        );
        final CoordinateNode second = new CoordinateNode(
            Coordinate.from(InMemoryDecisionGraphTest.SECOND)
        );
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
            Set.of(first, second), Set.of()
        );
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.nodes()).contains(first, second);
        Assertions.assertThat(graph.edges()).isEmpty();
    }

    @Test
    void shouldCreateGraphWithNodesAndEdges() {
        final CoordinateNode first = new CoordinateNode(
            Coordinate.from(InMemoryDecisionGraphTest.FIRST)
        );
        final CoordinateNode second = new CoordinateNode(
            Coordinate.from(InMemoryDecisionGraphTest.SECOND)
        );
        final BasicGraphEdge edge = new BasicGraphEdge(
            first, second, InMemoryDecisionGraphTest.RELATION
        );
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
            Set.of(first, second), Set.of(edge)
        );
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.edges()).hasSize(1);
        Assertions.assertThat(graph.edges()).contains(edge);
    }

    @Test
    void shouldNotAllowNullNodes() {
        Assertions.assertThatThrownBy(() -> new InMemoryDecisionGraph(null, Set.of()))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Nodes cannot be null");
    }

    @Test
    void shouldNotAllowNullEdges() {
        Assertions.assertThatThrownBy(() -> new InMemoryDecisionGraph(Set.of(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Edges cannot be null");
    }

    @Test
    void shouldReturnImmutableCollections() {
        final CoordinateNode first = new CoordinateNode(
            Coordinate.from(InMemoryDecisionGraphTest.FIRST)
        );
        final CoordinateNode second = new CoordinateNode(
            Coordinate.from(InMemoryDecisionGraphTest.SECOND)
        );
        final BasicGraphEdge edge = new BasicGraphEdge(
            first, second, InMemoryDecisionGraphTest.RELATION
        );
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
            Set.of(first, second), Set.of(edge)
        );
        final Set<GraphNode> nodes = graph.nodes();
        final Set<GraphEdge> edges = graph.edges();
        Assertions.assertThatThrownBy(
            () -> nodes.add(new CoordinateNode(Coordinate.from("test::value3")))
        )
            .isInstanceOf(UnsupportedOperationException.class);
        Assertions.assertThatThrownBy(() -> edges.add(new BasicGraphEdge(first, second, "test")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * Graph filtering and traversal queries.
     * @since 0.10.0
     */
    @Nested
    final class Queries {
        @Test
        void shouldFilterNodesByType() {
            final CoordinateNode first = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.FIRST)
            );
            final CoordinateNode second = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.SECOND)
            );
            final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
                Set.of(first, second), Set.of()
            );
            final Set<CoordinateNode> coordinates = graph.nodesOfType(CoordinateNode.class);
            Assertions.assertThat(coordinates).hasSize(2);
            Assertions.assertThat(coordinates).contains(first, second);
        }

        @Test
        void shouldFilterEdgesByType() {
            final CoordinateNode first = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.FIRST)
            );
            final CoordinateNode second = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.SECOND)
            );
            final BasicGraphEdge edge = new BasicGraphEdge(
                first, second, InMemoryDecisionGraphTest.RELATION
            );
            final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
                Set.of(first, second), Set.of(edge)
            );
            final Set<BasicGraphEdge> links = graph.edgesOfType(BasicGraphEdge.class);
            Assertions.assertThat(links).hasSize(1);
            Assertions.assertThat(links).contains(edge);
        }

        @Test
        void shouldFindEdgesFromNode() {
            final CoordinateNode first = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.FIRST)
            );
            final CoordinateNode second = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.SECOND)
            );
            final BasicGraphEdge edge = new BasicGraphEdge(
                first, second, InMemoryDecisionGraphTest.RELATION
            );
            final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
                Set.of(first, second), Set.of(edge)
            );
            final Set<GraphEdge> fromfirst = graph.edgesFrom(first);
            Assertions.assertThat(fromfirst).hasSize(1);
            Assertions.assertThat(fromfirst).contains(edge);
            final Set<GraphEdge> fromsecond = graph.edgesFrom(second);
            Assertions.assertThat(fromsecond).isEmpty();
        }

        @Test
        void shouldFindEdgesToNode() {
            final CoordinateNode first = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.FIRST)
            );
            final CoordinateNode second = new CoordinateNode(
                Coordinate.from(InMemoryDecisionGraphTest.SECOND)
            );
            final BasicGraphEdge edge = new BasicGraphEdge(
                first, second, InMemoryDecisionGraphTest.RELATION
            );
            final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(
                Set.of(first, second), Set.of(edge)
            );
            final Set<GraphEdge> tofirst = graph.edgesTo(first);
            Assertions.assertThat(tofirst).isEmpty();
            final Set<GraphEdge> tosecond = graph.edgesTo(second);
            Assertions.assertThat(tosecond).hasSize(1);
            Assertions.assertThat(tosecond).contains(edge);
        }
    }
}
