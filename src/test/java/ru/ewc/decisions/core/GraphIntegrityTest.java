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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.RuleFragment;

/**
 * Validation of immutable graph structure.
 * @since 0.10.0
 */
final class GraphIntegrityTest {
    @Test
    void storesAllFourRelationshipKindsWithoutDuplicates() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("kinds::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("kinds::target"));
        final List<GraphEdge> links = List.of(
            new PartOfEdge(source, target), new LhsOfEdge(source, target),
            new RhsOfEdge(source, target), new OperatorOfEdge(source, target)
        );
        final DecisionGraphBuilder builder = new DecisionGraphBuilder();
        for (final GraphEdge edge : links) {
            builder.addEdge(edge).addEdge(edge);
        }
        final DecisionGraph graph = builder.build();
        Assertions.assertThat(graph.edges()).hasSize(4);
        Assertions.assertThat(graph.edges().stream().map(GraphEdge::relationshipType))
            .containsExactlyInAnyOrder("part_of", "lhs_of", "rhs_of", "operator_of");
    }

    @Test
    void preservesBuiltGraphWhenBuilderIsReused() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("reuse::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("reuse::target"));
        final DecisionGraphBuilder builder = new DecisionGraphBuilder().addNode(source);
        final DecisionGraph original = builder.build();
        builder.addPartOfRelationship(source, target);
        Assertions.assertThat(original.nodes()).containsExactly(source);
        Assertions.assertThat(original.edges()).isEmpty();
        Assertions.assertThat(builder.build().edges()).hasSize(1);
    }

    @Test
    void rejectsUnstoredRulesInsideDirectTableDefinitions() {
        final RuleNode rule = new RuleNode(
            List.of(new RuleFragment("OUT", "value", "nested")), "nested"
        );
        final DecisionTableNode table = new DecisionTableNode(
            new TableDescription(List.of(rule), rule), "nested"
        );
        Assertions.assertThatThrownBy(
            () -> new InMemoryDecisionGraph(Set.of(table), Set.of())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsDuplicateIdentifiersInDirectConstruction() {
        final RuleNode first = new RuleNode(
            List.of(new RuleFragment("OUT", "value", "first")), "duplicate"
        );
        final RuleNode second = new RuleNode(
            List.of(new RuleFragment("OUT", "value", "second")), "duplicate"
        );
        Assertions.assertThatThrownBy(
            () -> new InMemoryDecisionGraph(Set.of(first, second), Set.of())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void distinguishesFragmentIdentifiersContainingSeparators() {
        final RuleFragmentNode first = new RuleFragmentNode(
            new RuleFragment("CND", "data::value", "expected")
        );
        final RuleFragmentNode second = new RuleFragmentNode(
            new RuleFragment("CND", "data", ":value:expected")
        );
        Assertions.assertThat(first.id()).isNotEqualTo(second.id());
        Assertions.assertThat(
            new DecisionGraphBuilder().addNode(first).addNode(second).build().nodes()
        ).hasSize(2);
    }

    @Test
    void rejectsCyclicDefinitions() {
        final CoordinateNode first = new CoordinateNode(Coordinate.from("cycle::first"));
        final CoordinateNode second = new CoordinateNode(Coordinate.from("cycle::second"));
        final DecisionGraphBuilder builder = new DecisionGraphBuilder()
            .addPartOfRelationship(first, second)
            .addPartOfRelationship(second, first);
        Assertions.assertThatThrownBy(builder::build)
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsEdgesWithoutStoredEndpoints() {
        final CoordinateNode source = new CoordinateNode(Coordinate.from("integrity::source"));
        final CoordinateNode target = new CoordinateNode(Coordinate.from("integrity::target"));
        final Set<GraphEdge> edges = Set.of(new PartOfEdge(source, target));
        Assertions.assertThatThrownBy(
            () -> new InMemoryDecisionGraph(Set.of(source), edges)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
