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
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.conditions.EqualsCondition;

import java.util.List;
import java.util.Set;

/**
 * Integration tests for DecisionGraph showing various node types and relationships.
 *
 * @since 0.9.2
 */
final class DecisionGraphIntegrationTest {
    @Test
    void shouldCreateComplexGraph() {
        // Create some coordinates
        final Coordinate coord1 = Coordinate.from("customer::age");
        final Coordinate coord2 = Coordinate.from("21");
        final CoordinateNode coordNode1 = new CoordinateNode(coord1);
        final CoordinateNode coordNode2 = new CoordinateNode(coord2);
        
        // Create a condition
        final EqualsCondition condition = new EqualsCondition(coord1, coord2);
        final ConditionNode conditionNode = new ConditionNode(condition, "age_check");
        
        // Create a rule fragment
        final RuleFragment fragment = new RuleFragment("CND", "customer::age", "21");
        final RuleFragmentNode fragmentNode = new RuleFragmentNode(fragment);
        
        // Create a rule
        final Rule rule = Rule.elseRule("test_rule");
        final RuleNode ruleNode = new RuleNode(rule, "test_rule");
        
        // Create a decision table
        final DecisionTable table = new DecisionTable(List.of(rule), rule, "test_table");
        final DecisionTableNode tableNode = new DecisionTableNode(table, "test_table");
        
        // Create edges showing relationships
        final LhsOfEdge lhsEdge = new LhsOfEdge(coordNode1, conditionNode);
        final RhsOfEdge rhsEdge = new RhsOfEdge(coordNode2, conditionNode);
        final PartOfEdge partOfEdge1 = new PartOfEdge(fragmentNode, ruleNode);
        final PartOfEdge partOfEdge2 = new PartOfEdge(ruleNode, tableNode);
        
        // Create the graph
        final Set<GraphNode> nodes = Set.of(coordNode1, coordNode2, conditionNode, fragmentNode, ruleNode, tableNode);
        final Set<GraphEdge> edges = Set.of(lhsEdge, rhsEdge, partOfEdge1, partOfEdge2);
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(nodes, edges);
        
        // Test node filtering
        final Set<CoordinateNode> coordinateNodes = graph.nodesOfType(CoordinateNode.class);
        Assertions.assertThat(coordinateNodes).hasSize(2);
        
        final Set<ConditionNode> conditionNodes = graph.nodesOfType(ConditionNode.class);
        Assertions.assertThat(conditionNodes).hasSize(1);
        
        final Set<RuleFragmentNode> fragmentNodes = graph.nodesOfType(RuleFragmentNode.class);
        Assertions.assertThat(fragmentNodes).hasSize(1);
        
        final Set<RuleNode> ruleNodes = graph.nodesOfType(RuleNode.class);
        Assertions.assertThat(ruleNodes).hasSize(1);
        
        final Set<DecisionTableNode> tableNodes = graph.nodesOfType(DecisionTableNode.class);
        Assertions.assertThat(tableNodes).hasSize(1);
        
        // Test edge filtering
        final Set<LhsOfEdge> lhsEdges = graph.edgesOfType(LhsOfEdge.class);
        Assertions.assertThat(lhsEdges).hasSize(1);
        
        final Set<RhsOfEdge> rhsEdges = graph.edgesOfType(RhsOfEdge.class);
        Assertions.assertThat(rhsEdges).hasSize(1);
        
        final Set<PartOfEdge> partOfEdges = graph.edgesOfType(PartOfEdge.class);
        Assertions.assertThat(partOfEdges).hasSize(2);
        
        // Test relationship traversal
        final Set<GraphEdge> edgesFromCoord1 = graph.edgesFrom(coordNode1);
        Assertions.assertThat(edgesFromCoord1).hasSize(1);
        Assertions.assertThat(edgesFromCoord1.iterator().next().relationshipType()).isEqualTo("lhs_of");
        
        final Set<GraphEdge> edgesToCondition = graph.edgesTo(conditionNode);
        Assertions.assertThat(edgesToCondition).hasSize(2);
        
        final Set<GraphEdge> edgesFromRule = graph.edgesFrom(ruleNode);
        Assertions.assertThat(edgesFromRule).hasSize(1);
        Assertions.assertThat(edgesFromRule.iterator().next().relationshipType()).isEqualTo("part_of");
        
        // Test that the graph is complete
        Assertions.assertThat(graph.nodes()).hasSize(6);
        Assertions.assertThat(graph.edges()).hasSize(4);
    }

    @Test
    void shouldCreateGraphWithSpecificRelationshipTypes() {
        final CoordinateNode coord1 = new CoordinateNode(Coordinate.from("test::value1"));
        final CoordinateNode coord2 = new CoordinateNode(Coordinate.from("test::value2"));
        final ConditionNode condition = new ConditionNode(new EqualsCondition((Coordinate) coord1.component(), (Coordinate) coord2.component()), "test_condition");
        
        final PartOfEdge partOfEdge = new PartOfEdge(coord1, condition);
        final LhsOfEdge lhsEdge = new LhsOfEdge(coord1, condition);
        final RhsOfEdge rhsEdge = new RhsOfEdge(coord2, condition);
        
        final Set<GraphNode> nodes = Set.of(coord1, coord2, condition);
        final Set<GraphEdge> edges = Set.of(partOfEdge, lhsEdge, rhsEdge);
        final InMemoryDecisionGraph graph = new InMemoryDecisionGraph(nodes, edges);
        
        // Verify relationship types
        final Set<GraphEdge> allEdges = graph.edges();
        Assertions.assertThat(allEdges).hasSize(3);
        
        final Set<String> relationshipTypes = allEdges.stream()
            .map(GraphEdge::relationshipType)
            .collect(java.util.stream.Collectors.toSet());
        
        Assertions.assertThat(relationshipTypes).containsExactlyInAnyOrder("part_of", "lhs_of", "rhs_of");
    }
}