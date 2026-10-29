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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Checks the endpoints of a graph snapshot.
 * @since 0.10.0
 */
final class GraphStructure {
    /** Graph vertices. */
    private final Set<GraphNode> vertices;

    /** Graph connections. */
    private final Set<GraphEdge> connections;

    GraphStructure(final Set<GraphNode> vertices, final Set<GraphEdge> connections) {
        this.vertices = vertices;
        this.connections = connections;
    }

    Set<GraphEdge> validated() {
        this.requireUniqueIdentifiers();
        this.requireNestedReferences();
        for (final GraphEdge edge : this.connections) {
            this.requireEndpoint(edge.source());
            this.requireEndpoint(edge.target());
        }
        this.requireAcyclic();
        return this.connections;
    }

    private void requireUniqueIdentifiers() {
        final Set<String> identifiers = new HashSet<>(this.vertices.size());
        for (final GraphNode node : this.vertices) {
            if (!identifiers.add(node.id())) {
                throw new IllegalArgumentException(
                    String.format("Duplicate graph node identifier: %s", node.id())
                );
            }
        }
    }

    private void requireNestedReferences() {
        for (final GraphNode node : this.vertices) {
            if (node instanceof DecisionTableNode) {
                final TableDescription table = ((DecisionTableNode) node).component();
                table.rules().forEach(this::requireEndpoint);
                this.requireEndpoint(table.fallback());
            }
        }
    }

    private void requireEndpoint(final GraphNode node) {
        if (this.vertices.stream().noneMatch(stored -> stored == node)) {
            throw new IllegalArgumentException(
                String.format("Edge endpoint is not a stored graph node: %s", node.id())
            );
        }
    }

    private void requireAcyclic() {
        final Map<GraphNode, Integer> incoming = new IdentityHashMap<>();
        final Map<GraphNode, List<GraphNode>> outgoing = new IdentityHashMap<>();
        for (final GraphNode node : this.vertices) {
            incoming.put(node, 0);
            outgoing.put(node, new ArrayList<>(0));
        }
        for (final GraphEdge edge : this.connections) {
            incoming.put(edge.target(), incoming.get(edge.target()) + 1);
            outgoing.get(edge.source()).add(edge.target());
        }
        final Queue<GraphNode> ready = new ArrayDeque<>();
        incoming.forEach(
            (node, count) -> {
                if (count == 0) {
                    ready.add(node);
                }
            }
        );
        int visited = 0;
        while (!ready.isEmpty()) {
            final GraphNode node = ready.remove();
            visited += 1;
            for (final GraphNode target : outgoing.get(node)) {
                final int count = incoming.get(target) - 1;
                incoming.put(target, count);
                if (count == 0) {
                    ready.add(target);
                }
            }
        }
        if (visited != this.vertices.size()) {
            throw new IllegalArgumentException("Decision graph contains a cycle");
        }
    }
}
