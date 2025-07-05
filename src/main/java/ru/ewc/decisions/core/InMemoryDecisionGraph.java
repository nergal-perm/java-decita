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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * I am an immutable implementation of DecisionGraph.
 * I contain a fixed set of nodes and edges that represent the decision table structure.
 * I am thread-safe and built once from source files, then cached for reuse.
 *
 * @since 0.9.2
 */
public final class InMemoryDecisionGraph implements DecisionGraph {
    /**
     * The immutable set of all nodes in this graph.
     */
    private final Set<GraphNode> nodes;

    /**
     * The immutable set of all edges in this graph.
     */
    private final Set<GraphEdge> edges;

    /**
     * Constructor.
     *
     * @param nodes The nodes that make up this graph.
     * @param edges The edges that connect the nodes.
     */
    public InMemoryDecisionGraph(final Set<GraphNode> nodes, final Set<GraphEdge> edges) {
        this.nodes = Objects.requireNonNull(nodes, "Nodes cannot be null");
        this.edges = Objects.requireNonNull(edges, "Edges cannot be null");
    }

    @Override
    public Set<GraphNode> nodes() {
        return Set.copyOf(this.nodes);
    }

    @Override
    public Set<GraphEdge> edges() {
        return Set.copyOf(this.edges);
    }

    @Override
    public <T extends GraphNode> Set<T> nodesOfType(final Class<T> nodeType) {
        return this.nodes.stream()
            .filter(nodeType::isInstance)
            .map(nodeType::cast)
            .collect(Collectors.toSet());
    }

    @Override
    public <T extends GraphEdge> Set<T> edgesOfType(final Class<T> edgeType) {
        return this.edges.stream()
            .filter(edgeType::isInstance)
            .map(edgeType::cast)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<GraphEdge> edgesFrom(final GraphNode node) {
        return this.edges.stream()
            .filter(edge -> edge.source().equals(node))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<GraphEdge> edgesTo(final GraphNode node) {
        return this.edges.stream()
            .filter(edge -> edge.target().equals(node))
            .collect(Collectors.toSet());
    }
}