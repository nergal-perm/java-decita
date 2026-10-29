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

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.RuleFragment;

/**
 * Immutable rule and table descriptions.
 * @since 0.10.0
 */
final class GraphDescriptionSnapshotTest {
    /** Outcome row type. */
    private static final String OUTCOME = "OUT";

    /** Shared identifier and value in aliasing fixtures. */
    private static final String SHARED = "shared";

    @Test
    void sharesCanonicalRulesInsideTableDescriptions() {
        final RuleFragment fragment = new RuleFragment(
            GraphDescriptionSnapshotTest.OUTCOME, "value", GraphDescriptionSnapshotTest.SHARED
        );
        final RuleNode original = new RuleNode(List.of(fragment), "original");
        final RuleNode equivalent = new RuleNode(List.of(fragment), "original");
        final DecisionTableNode table = new DecisionTableNode(
            new TableDescription(List.of(equivalent), equivalent), "nested"
        );
        final DecisionGraph graph = new DecisionGraphBuilder()
            .addNode(original).addPartOfRelationship(equivalent, table).build();
        final DecisionTableNode saved = graph.nodesOfType(DecisionTableNode.class)
            .iterator().next();
        final TableDescription stored = saved.component();
        Assertions.assertThat(stored.rules().get(0)).isSameAs(original);
        Assertions.assertThat(stored.fallback()).isSameAs(original);
        Assertions.assertThat(graph.edges().iterator().next().target()).isSameAs(saved);
    }

    @Test
    void rejectsRuleConflictsAcrossDifferentTables() {
        final RuleNode first = new RuleNode(
            List.of(new RuleFragment(GraphDescriptionSnapshotTest.OUTCOME, "item", "one")),
            "rule"
        );
        final RuleNode second = new RuleNode(
            List.of(new RuleFragment(GraphDescriptionSnapshotTest.OUTCOME, "item", "two")),
            "rule"
        );
        final DecisionGraphBuilder builder = new DecisionGraphBuilder().addNode(
            new DecisionTableNode(new TableDescription(List.of(first), first), "one")
        );
        Assertions.assertThatThrownBy(
            () -> builder.addNode(
                new DecisionTableNode(new TableDescription(List.of(second), second), "two")
            )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsConflictingNestedTableDefinitions() {
        final RuleNode first = new RuleNode(
            List.of(
                new RuleFragment(GraphDescriptionSnapshotTest.OUTCOME, "status", "first")
            ), GraphDescriptionSnapshotTest.SHARED
        );
        final RuleNode second = new RuleNode(
            List.of(
                new RuleFragment(GraphDescriptionSnapshotTest.OUTCOME, "status", "second")
            ), GraphDescriptionSnapshotTest.SHARED
        );
        final DecisionTableNode original = new DecisionTableNode(
            new TableDescription(List.of(first), first), GraphDescriptionSnapshotTest.SHARED
        );
        final DecisionTableNode changed = new DecisionTableNode(
            new TableDescription(List.of(second), second), GraphDescriptionSnapshotTest.SHARED
        );
        final DecisionGraphBuilder builder = new DecisionGraphBuilder().addNode(original);
        Assertions.assertThatThrownBy(() -> builder.addNode(changed))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void snapshotsTableRulesBeforeCallerMutation() {
        final RuleNode rule = new RuleNode(
            List.of(
                new RuleFragment(GraphDescriptionSnapshotTest.OUTCOME, "status", "yes")
            ), "regular"
        );
        final RuleNode fallback = new RuleNode(
            List.of(
                new RuleFragment(GraphDescriptionSnapshotTest.OUTCOME, "status", "no")
            ), "otherwise"
        );
        final List<RuleNode> rules = new ArrayList<>(List.of(rule));
        final DecisionTableNode node = new DecisionTableNode(
            new TableDescription(rules, fallback), "eligibility"
        );
        rules.clear();
        Assertions.assertThat(node.component().rules()).containsExactly(rule);
        Assertions.assertThat(node.component().fallback()).isSameAs(fallback);
        Assertions.assertThatThrownBy(() -> node.component().rules().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void snapshotsRuleFragmentsBeforeCallerMutation() {
        final RuleFragment fragment = new RuleFragment(
            GraphDescriptionSnapshotTest.OUTCOME, "result", "accepted"
        );
        final List<RuleFragment> fragments = new ArrayList<>(List.of(fragment));
        final RuleNode node = new RuleNode(fragments, "accepted");
        fragments.clear();
        Assertions.assertThat(node.component()).containsExactly(fragment);
        Assertions.assertThatThrownBy(() -> node.component().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
