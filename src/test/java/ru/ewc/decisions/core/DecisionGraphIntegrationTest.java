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
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.RuleFragment;

/**
 * Integration tests for DecisionGraph showing various node types and relationships.
 *
 * @since 0.9.2
 */
final class DecisionGraphIntegrationTest {
    @Test
    void shouldCreateComplexGraph() {
        final Coordinate first = Coordinate.from("customer::age");
        final Coordinate second = Coordinate.from("21");
        final CoordinateNode firstnode = new CoordinateNode(first);
        final CoordinateNode secondnode = new CoordinateNode(second);
        final RuleFragment condition = new RuleFragment(
            "CND", first.asString(), second.asString()
        );
        final ConditionNode check = new ConditionNode(condition, "age_check");
        final RuleFragment fragment = new RuleFragment("CND", "customer::age", "21");
        final RuleFragmentNode piece = new RuleFragmentNode(fragment);
        final RuleNode rule = new RuleNode(
            List.of(new RuleFragment("OUT", "outcome", "undefined")), "test_rule"
        );
        final TableDescription table = new TableDescription(List.of(rule), rule);
        final DecisionTableNode tablenode = new DecisionTableNode(table, "test_table");
        final LhsOfEdge lhs = new LhsOfEdge(firstnode, check);
        final RhsOfEdge rhs = new RhsOfEdge(secondnode, check);
        final PartOfEdge rulepart = new PartOfEdge(piece, rule);
        final PartOfEdge tablepart = new PartOfEdge(rule, tablenode);
        final Set<GraphNode> nodes = Set.of(
            firstnode, secondnode, check, piece, rule, tablenode
        );
        final Set<GraphEdge> edges = Set.of(lhs, rhs, rulepart, tablepart);
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(nodes, edges);
        Assertions.assertThat(graph.nodesOfType(CoordinateNode.class)).hasSize(2);
        Assertions.assertThat(graph.nodesOfType(ConditionNode.class)).hasSize(1);
        Assertions.assertThat(graph.nodesOfType(RuleFragmentNode.class)).hasSize(1);
        Assertions.assertThat(graph.nodesOfType(RuleNode.class)).hasSize(1);
        Assertions.assertThat(graph.nodesOfType(DecisionTableNode.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(LhsOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(RhsOfEdge.class)).hasSize(1);
        Assertions.assertThat(graph.edgesOfType(PartOfEdge.class)).hasSize(2);
        final Set<GraphEdge> fromfirst = graph.edgesFrom(firstnode);
        Assertions.assertThat(fromfirst).hasSize(1);
        Assertions.assertThat(fromfirst.iterator().next().relationshipType()).isEqualTo("lhs_of");
        final Set<GraphEdge> tocheck = graph.edgesTo(check);
        Assertions.assertThat(tocheck).hasSize(2);
        final Set<GraphEdge> fromrule = graph.edgesFrom(rule);
        Assertions.assertThat(fromrule).hasSize(1);
        Assertions.assertThat(fromrule.iterator().next().relationshipType()).isEqualTo("part_of");
        Assertions.assertThat(graph.nodes()).hasSize(6);
        Assertions.assertThat(graph.edges()).hasSize(4);
    }

    @Test
    void shouldCreateGraphWithSpecificRelationshipTypes() {
        final CoordinateNode first = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("test::value2"));
        final ConditionNode condition = new ConditionNode(
            new RuleFragment("CND", first.component(), second.component()), "test_condition"
        );
        final PartOfEdge part = new PartOfEdge(first, condition);
        final LhsOfEdge lhs = new LhsOfEdge(first, condition);
        final RhsOfEdge rhs = new RhsOfEdge(second, condition);
        final Set<GraphNode> nodes = Set.of(first, second, condition);
        final Set<GraphEdge> edges = Set.of(part, lhs, rhs);
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(nodes, edges);
        final Set<GraphEdge> all = graph.edges();
        Assertions.assertThat(all).hasSize(3);
        final Set<String> relations = all.stream()
            .map(GraphEdge::relationshipType)
            .collect(Collectors.toSet());
        Assertions.assertThat(relations).containsExactlyInAnyOrder("part_of", "lhs_of", "rhs_of");
    }
}
