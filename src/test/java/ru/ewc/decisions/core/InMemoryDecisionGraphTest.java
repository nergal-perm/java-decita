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

import java.util.Set;

/**
 * Tests for {@link InMemoryDecisionGraph}.
 *
 * @since 0.9.2
 */
final class InMemoryDecisionGraphTest {
    @Test
    void shouldCreateEmptyGraph() {
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(), Set.of());
        
        Assertions.assertThat(graph.nodes()).isEmpty();
        Assertions.assertThat(graph.edges()).isEmpty();
    }

    @Test
    void shouldCreateGraphWithNodes() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of());
        
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.nodes()).contains(node1, node2);
        Assertions.assertThat(graph.edges()).isEmpty();
    }

    @Test
    void shouldCreateGraphWithNodesAndEdges() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(node1, node2, "part_of");
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of(edge));
        
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.edges()).hasSize(1);
        Assertions.assertThat(graph.edges()).contains(edge);
    }

    @Test
    void shouldFilterNodesByType() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of());
        
        final Set<CoordinateNode> coordinateNodes = graph.nodesOfType(CoordinateNode.class);
        Assertions.assertThat(coordinateNodes).hasSize(2);
        Assertions.assertThat(coordinateNodes).contains(node1, node2);
    }

    @Test
    void shouldFilterEdgesByType() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(node1, node2, "part_of");
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of(edge));
        
        final Set<BasicGraphEdge> basicEdges = graph.edgesOfType(BasicGraphEdge.class);
        Assertions.assertThat(basicEdges).hasSize(1);
        Assertions.assertThat(basicEdges).contains(edge);
    }

    @Test
    void shouldFindEdgesFromNode() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(node1, node2, "part_of");
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of(edge));
        
        final Set<GraphEdge> edgesFromNode1 = graph.edgesFrom(node1);
        Assertions.assertThat(edgesFromNode1).hasSize(1);
        Assertions.assertThat(edgesFromNode1).contains(edge);
        
        final Set<GraphEdge> edgesFromNode2 = graph.edgesFrom(node2);
        Assertions.assertThat(edgesFromNode2).isEmpty();
    }

    @Test
    void shouldFindEdgesToNode() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(node1, node2, "part_of");
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of(edge));
        
        final Set<GraphEdge> edgesToNode1 = graph.edgesTo(node1);
        Assertions.assertThat(edgesToNode1).isEmpty();
        
        final Set<GraphEdge> edgesToNode2 = graph.edgesTo(node2);
        Assertions.assertThat(edgesToNode2).hasSize(1);
        Assertions.assertThat(edgesToNode2).contains(edge);
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
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(node1, node2, "part_of");
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(Set.of(node1, node2), Set.of(edge));
        
        final Set<GraphNode> nodes = graph.nodes();
        final Set<GraphEdge> edges = graph.edges();
        
        Assertions.assertThatThrownBy(() -> nodes.add(new CoordinateNode(Coordinate.from("test::value3"))))
            .isInstanceOf(UnsupportedOperationException.class);
        
        Assertions.assertThatThrownBy(() -> edges.add(new BasicGraphEdge(node1, node2, "test")))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}