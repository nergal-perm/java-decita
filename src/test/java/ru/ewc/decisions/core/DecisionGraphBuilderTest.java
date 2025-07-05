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
import ru.ewc.decisions.conditions.EqualsCondition;

/**
 * Tests for {@link DecisionGraphBuilder}.
 *
 * @since 0.9.2
 */
final class DecisionGraphBuilderTest {
    @Test
    void shouldCreateEmptyGraph() {
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder.build();
        
        Assertions.assertThat(graph.nodes()).isEmpty();
        Assertions.assertThat(graph.edges()).isEmpty();
        Assertions.assertThat(builder.nodeCount()).isZero();
        Assertions.assertThat(builder.edgeCount()).isZero();
    }

    @Test
    void shouldAddNodesAndBuildGraph() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addNode(node1)
            .addNode(node2)
            .build();
        
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.nodes()).contains(node1, node2);
        Assertions.assertThat(graph.edges()).isEmpty();
        Assertions.assertThat(builder.nodeCount()).isEqualTo(2);
        Assertions.assertThat(builder.edgeCount()).isZero();
    }

    @Test
    void shouldAddEdgesAndAutomaticallyIncludeNodes() {
        final CoordinateNode node1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode node2 = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(node1, node2, "test_relationship");
        
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addEdge(edge)
            .build();
        
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.nodes()).contains(node1, node2);
        Assertions.assertThat(graph.edges()).hasSize(1);
        Assertions.assertThat(graph.edges()).contains(edge);
        Assertions.assertThat(builder.nodeCount()).isEqualTo(2);
        Assertions.assertThat(builder.edgeCount()).isEqualTo(1);
    }

    @Test
    void shouldAddSpecificRelationshipTypes() {
        final CoordinateNode coord1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode coord2 = new CoordinateNode(Coordinate.from("test::value2"));
        final ConditionNode condition = new ConditionNode(
            new EqualsCondition((Coordinate) coord1.component(), (Coordinate) coord2.component()), 
            "test_condition"
        );
        
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addNode(condition)
            .addLhsOfRelationship(coord1, condition)
            .addRhsOfRelationship(coord2, condition)
            .build();
        
        Assertions.assertThat(graph.nodes()).hasSize(3);
        Assertions.assertThat(graph.edges()).hasSize(2);
        
        final var lhsEdges = graph.edgesOfType(LhsOfEdge.class);
        final var rhsEdges = graph.edgesOfType(RhsOfEdge.class);
        
        Assertions.assertThat(lhsEdges).hasSize(1);
        Assertions.assertThat(rhsEdges).hasSize(1);
        
        Assertions.assertThat(lhsEdges.iterator().next().source()).isEqualTo(coord1);
        Assertions.assertThat(lhsEdges.iterator().next().target()).isEqualTo(condition);
        Assertions.assertThat(rhsEdges.iterator().next().source()).isEqualTo(coord2);
        Assertions.assertThat(rhsEdges.iterator().next().target()).isEqualTo(condition);
    }

    @Test
    void shouldCreateComplexGraphWithAllRelationshipTypes() {
        final CoordinateNode coord1 = new CoordinateNode(Coordinate.from("customer::age"));
        final CoordinateNode coord2 = new CoordinateNode(Coordinate.from("21"));
        final ConditionNode condition = new ConditionNode(
            new EqualsCondition((Coordinate) coord1.component(), (Coordinate) coord2.component()), 
            "age_check"
        );
        final RuleNode rule = new RuleNode(Rule.elseRule("test_rule"), "test_rule");
        
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addLhsOfRelationship(coord1, condition)
            .addRhsOfRelationship(coord2, condition)
            .addPartOfRelationship(condition, rule)
            .build();
        
        Assertions.assertThat(graph.nodes()).hasSize(4);
        Assertions.assertThat(graph.edges()).hasSize(3);
        
        // Verify specific relationships
        Assertions.assertThat(graph.edgesOfType(LhsOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(RhsOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(PartOfEdge.class)).hasSize(1);
        
        // Verify graph connectivity
        Assertions.assertThat(graph.edgesFrom(coord1)).hasSize(1);
        Assertions.assertThat(graph.edgesFrom(coord2)).hasSize(1);
        Assertions.assertThat(graph.edgesFrom(condition)).hasSize(1);
        Assertions.assertThat(graph.edgesTo(condition)).hasSize(2);
        Assertions.assertThat(graph.edgesTo(rule)).hasSize(1);
    }

    @Test
    void shouldNotAllowNullNode() {
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        Assertions.assertThatThrownBy(() -> builder.addNode(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Node cannot be null");
    }

    @Test
    void shouldNotAllowNullEdge() {
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        Assertions.assertThatThrownBy(() -> builder.addEdge(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Edge cannot be null");
    }
}