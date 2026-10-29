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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Construction-scoped canonical definitions, separate from immutable graph snapshots.
 * @since 0.10.0
 */
final class GraphNodes {
    /** Definitions indexed only during graph construction. */
    private final Map<String, GraphNode> definitions;

    GraphNodes() {
        this.definitions = new HashMap<>();
    }

    GraphNode canonical(final GraphNode node) {
        final GraphNode definition = this.withCanonicalReferences(node);
        final GraphNode previous = this.definitions.putIfAbsent(definition.id(), definition);
        if (previous != null && (previous.getClass() != definition.getClass()
            || !Objects.equals(previous.component(), definition.component()))) {
            throw new IllegalArgumentException(
                String.format("Conflicting graph node: %s", node.id())
            );
        }
        GraphNode canonical = definition;
        if (previous != null) {
            canonical = previous;
        }
        return canonical;
    }

    Set<GraphNode> snapshot() {
        return Set.copyOf(this.definitions.values());
    }

    int size() {
        return this.definitions.size();
    }

    private GraphNode withCanonicalReferences(final GraphNode node) {
        GraphNode result = node;
        if (node instanceof DecisionTableNode) {
            final DecisionTableNode table = (DecisionTableNode) node;
            final TableDescription description = table.component();
            result = table.withDescription(
                new TableDescription(
                    description.rules().stream()
                        .map(rule -> (RuleNode) this.canonical(rule)).toList(),
                    (RuleNode) this.canonical(description.fallback())
                )
            );
        }
        return result;
    }
}
