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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * I am a builder for creating DecisionGraph instances.
 * I provide convenient methods to construct graphs from decision table components.
 * I ensure that all nodes and edges are properly connected and valid.
 *
 * @since 0.9.2
 */
public final class DecisionGraphBuilder {
    /**
     * The set of nodes being built.
     */
    private final Set<GraphNode> nodes;

    /**
     * The set of edges being built.
     */
    private final Set<GraphEdge> edges;

    /**
     * Constructor.
     */
    public DecisionGraphBuilder() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    /**
     * Adds a node to the graph.
     *
     * @param node The node to add.
     * @return This builder for method chaining.
     */
    public DecisionGraphBuilder addNode(final GraphNode node) {
        this.nodes.add(Objects.requireNonNull(node, "Node cannot be null"));
        return this;
    }

    /**
     * Adds an edge to the graph.
     * The source and target nodes of the edge will be automatically added to the graph.
     *
     * @param edge The edge to add.
     * @return This builder for method chaining.
     */
    public DecisionGraphBuilder addEdge(final GraphEdge edge) {
        Objects.requireNonNull(edge, "Edge cannot be null");
        this.edges.add(edge);
        this.nodes.add(edge.source());
        this.nodes.add(edge.target());
        return this;
    }

    /**
     * Adds a "part_of" relationship between two nodes.
     *
     * @param source The source node that is part of the target.
     * @param target The target node that contains the source.
     * @return This builder for method chaining.
     */
    public DecisionGraphBuilder addPartOfRelationship(final GraphNode source, final GraphNode target) {
        return this.addEdge(new PartOfEdge(source, target));
    }

    /**
     * Adds a "lhs_of" relationship between two nodes.
     *
     * @param source The source node that is the left-hand side of the target.
     * @param target The target node that has the source as its left-hand side.
     * @return This builder for method chaining.
     */
    public DecisionGraphBuilder addLhsOfRelationship(final GraphNode source, final GraphNode target) {
        return this.addEdge(new LhsOfEdge(source, target));
    }

    /**
     * Adds a "rhs_of" relationship between two nodes.
     *
     * @param source The source node that is the right-hand side of the target.
     * @param target The target node that has the source as its right-hand side.
     * @return This builder for method chaining.
     */
    public DecisionGraphBuilder addRhsOfRelationship(final GraphNode source, final GraphNode target) {
        return this.addEdge(new RhsOfEdge(source, target));
    }

    /**
     * Adds an "operator_of" relationship between two nodes.
     *
     * @param source The source node that is the operator of the target.
     * @param target The target node that has the source as its operator.
     * @return This builder for method chaining.
     */
    public DecisionGraphBuilder addOperatorOfRelationship(final GraphNode source, final GraphNode target) {
        return this.addEdge(new OperatorOfEdge(source, target));
    }

    /**
     * Builds the DecisionGraph from the accumulated nodes and edges.
     *
     * @return A new immutable DecisionGraph instance.
     */
    public DecisionGraph build() {
        return new InMemoryDecisionGraph(Set.copyOf(this.nodes), Set.copyOf(this.edges));
    }

    /**
     * Returns the current number of nodes in the builder.
     *
     * @return The number of nodes.
     */
    public int nodeCount() {
        return this.nodes.size();
    }

    /**
     * Returns the current number of edges in the builder.
     *
     * @return The number of edges.
     */
    public int edgeCount() {
        return this.edges.size();
    }
}