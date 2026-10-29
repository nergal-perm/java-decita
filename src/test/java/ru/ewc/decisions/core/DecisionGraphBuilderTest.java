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

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.RuleFragment;

/**
 * Tests for {@link DecisionGraphBuilder}.
 *
 * @since 0.9.2
 */
final class DecisionGraphBuilderTest {
    /**
     * Condition fragment type.
     */
    private static final String CONDITION = "CND";

    @Test
    void rejectsConflictingDefinitionsForOneIdentifier() {
        final ConditionNode first = new ConditionNode(
            new RuleFragment(DecisionGraphBuilderTest.CONDITION, "customer::age", ">18"), "same"
        );
        final ConditionNode second = new ConditionNode(
            new RuleFragment(DecisionGraphBuilderTest.CONDITION, "customer::age", "<18"), "same"
        );
        final DecisionGraphBuilder builder = new DecisionGraphBuilder().addNode(first);
        Assertions.assertThatThrownBy(() -> builder.addNode(second))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void storesRepeatedRelationshipOnlyOnce() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("duplicate::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("duplicate::target"));
        final DecisionGraph graph = new DecisionGraphBuilder()
            .addPartOfRelationship(source, target)
            .addPartOfRelationship(source, target).build();
        Assertions.assertThat(graph.edges()).hasSize(1);
    }

    @Test
    void connectsEquivalentCoordinatesToOneSharedInstance() {
        final CoordinateNode first = new CoordinateNode(Coordinate.from("shared::value"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("shared::value"));
        final ConditionNode left = new ConditionNode(
            new RuleFragment(DecisionGraphBuilderTest.CONDITION, "shared::value", "1"), "left"
        );
        final ConditionNode right = new ConditionNode(
            new RuleFragment(DecisionGraphBuilderTest.CONDITION, "shared::value", "2"), "right"
        );
        final DecisionGraph graph = new DecisionGraphBuilder()
            .addLhsOfRelationship(first, left)
            .addLhsOfRelationship(second, right).build();
        final GraphNode shared = graph.nodesOfType(CoordinateNode.class).iterator().next();
        graph.edges().forEach(edge -> Assertions.assertThat(edge.source()).isSameAs(shared));
    }

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
        final CoordinateNode first = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("test::value2"));
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addNode(first)
            .addNode(second)
            .build();
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.nodes()).contains(first, second);
        Assertions.assertThat(graph.edges()).isEmpty();
        Assertions.assertThat(builder.nodeCount()).isEqualTo(2);
        Assertions.assertThat(builder.edgeCount()).isZero();
    }

    @Test
    void shouldAddEdgesAndAutomaticallyIncludeNodes() {
        final CoordinateNode first = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("test::value2"));
        final BasicGraphEdge edge = new BasicGraphEdge(first, second, "test_relationship");
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addEdge(edge)
            .build();
        Assertions.assertThat(graph.nodes()).hasSize(2);
        Assertions.assertThat(graph.nodes()).contains(first, second);
        Assertions.assertThat(graph.edges()).hasSize(1);
        Assertions.assertThat(graph.edges()).contains(edge);
        Assertions.assertThat(builder.nodeCount()).isEqualTo(2);
        Assertions.assertThat(builder.edgeCount()).isEqualTo(1);
    }

    @Test
    void shouldAddSpecificRelationshipTypes() {
        final CoordinateNode first = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("test::value2"));
        final ConditionNode condition = new ConditionNode(
            new RuleFragment(
                DecisionGraphBuilderTest.CONDITION, first.component(), second.component()
            ),
            "test_condition"
        );
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addNode(condition)
            .addLhsOfRelationship(first, condition)
            .addRhsOfRelationship(second, condition)
            .build();
        Assertions.assertThat(graph.nodes()).hasSize(3);
        Assertions.assertThat(graph.edges()).hasSize(2);
        final var leftedges = graph.edgesOfType(LhsOfEdge.class);
        final var rightedges = graph.edgesOfType(RhsOfEdge.class);
        Assertions.assertThat(leftedges).hasSize(1);
        Assertions.assertThat(rightedges).hasSize(1);
        Assertions.assertThat(leftedges.iterator().next().source()).isEqualTo(first);
        Assertions.assertThat(leftedges.iterator().next().target()).isEqualTo(condition);
        Assertions.assertThat(rightedges.iterator().next().source()).isEqualTo(second);
        Assertions.assertThat(rightedges.iterator().next().target()).isEqualTo(condition);
    }

    @Test
    void shouldCreateComplexGraphWithAllRelationshipTypes() {
        final CoordinateNode first = new CoordinateNode(Coordinate.from("customer::age"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("21"));
        final ConditionNode condition = new ConditionNode(
            new RuleFragment(
                DecisionGraphBuilderTest.CONDITION, first.component(), second.component()
            ),
            "age_check"
        );
        final RuleNode rule = new RuleNode(
            List.of(new RuleFragment("OUT", "outcome", "undefined")), "test_rule"
        );
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        final DecisionGraph graph = builder
            .addLhsOfRelationship(first, condition)
            .addRhsOfRelationship(second, condition)
            .addPartOfRelationship(condition, rule)
            .build();
        Assertions.assertThat(graph.nodes()).hasSize(4);
        Assertions.assertThat(graph.edges()).hasSize(3);
        Assertions.assertThat(graph.edgesOfType(LhsOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(RhsOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(PartOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesFrom(first)).hasSize(1);
        Assertions.assertThat(graph.edgesFrom(second)).hasSize(1);
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
