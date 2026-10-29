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
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.api.InMemoryLocator;
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.state.State;

/**
 * Shared graph descriptions evaluated in independent application contexts.
 * @since 0.10.0
 */
final class GraphSessionIsolationTest {
    @ParameterizedTest
    @CsvSource({"21, 17, true", "17, 21, false"})
    void sharesConditionDescriptionAcrossIndependentContexts(
        final int first, final int second, final boolean before
    ) {
        final RuleFragment fragment = new RuleFragment("CND", "customer::age", ">18");
        final ConditionNode node = new ConditionNode(fragment, "age");
        final DecisionGraph graph = new DecisionGraphBuilder().addNode(node).build();
        final ConditionNode shared = graph.nodesOfType(ConditionNode.class).iterator().next();
        Assertions.assertThat(
            Condition.from(shared.component()).evaluate(this.context(first))
        ).isEqualTo(before);
        Assertions.assertThat(
            Condition.from(shared.component()).evaluate(this.context(second))
        ).isEqualTo(!before);
        Assertions.assertThat(shared.component()).isEqualTo(fragment);
    }

    @Test
    void evaluatesSharedDescriptionInParallelContexts() throws Exception {
        final ConditionNode node = new ConditionNode(
            new RuleFragment("CND", "customer::age", ">18"), "parallel"
        );
        final DecisionGraph graph = new DecisionGraphBuilder().addNode(node).build();
        final ConditionNode shared = graph.nodesOfType(ConditionNode.class).iterator().next();
        final var executor = Executors.newFixedThreadPool(2);
        try {
            final var adult = executor.submit(
                () -> Condition.from(shared.component()).evaluate(this.context(21))
            );
            final var minor = executor.submit(
                () -> Condition.from(shared.component()).evaluate(this.context(17))
            );
            Assertions.assertThat(adult.get(5, TimeUnit.SECONDS)).isTrue();
            Assertions.assertThat(minor.get(5, TimeUnit.SECONDS)).isFalse();
            Assertions.assertThat(node.component().left()).isEqualTo("customer::age");
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void resolvesDynamicDescriptionsSeparatelyForEachContext() {
        final CoordinateNode node = new CoordinateNode(
            Coordinate.from("cells::${request::move}")
        );
        final DecisionGraph graph = new DecisionGraphBuilder().addNode(node).build();
        final CoordinateNode shared = graph.nodesOfType(CoordinateNode.class).iterator().next();
        Assertions.assertThat(
            Coordinate.from(shared.component()).valueIn(this.dynamicContext("A1"))
        ).isEqualTo("X");
        Assertions.assertThat(
            Coordinate.from(shared.component()).valueIn(this.dynamicContext("A2"))
        ).isEqualTo("O");
        Assertions.assertThat(shared.component()).isEqualTo("cells::${request::move}");
    }

    private ComputationContext dynamicContext(final String move) {
        return new ComputationContext(
            new State(
                List.of(
                    new InMemoryLocator("request", Map.of("move", move)),
                    new InMemoryLocator("cells", Map.of("A1", "X", "A2", "O"))
                )
            ),
            DecisionTables.using(List::of)
        );
    }

    private ComputationContext context(final int age) {
        return new ComputationContext(
            new State(List.of(new InMemoryLocator("customer", Map.of("age", age)))),
            DecisionTables.using(List::of)
        );
    }
}
