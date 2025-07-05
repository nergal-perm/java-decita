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

import java.util.Set;

/**
 * I am an immutable graph representation of decision tables and their components.
 * My nodes are decision table components like Coordinates, Conditions, Rules, and DecisionTables.
 * My edges represent relationships like "part_of", "lhs_of", "rhs_of", and "operator_of".
 * I am thread-safe and built once from source files, then cached for reuse.
 *
 * @since 0.9.2
 */
public interface DecisionGraph {
    /**
     * Returns all nodes in this graph.
     *
     * @return An immutable set of all nodes in the graph.
     */
    Set<GraphNode> nodes();

    /**
     * Returns all edges in this graph.
     *
     * @return An immutable set of all edges in the graph.
     */
    Set<GraphEdge> edges();

    /**
     * Returns all nodes of a specific type.
     *
     * @param nodeType The class of nodes to return.
     * @param <T> The type of nodes to return.
     * @return An immutable set of nodes of the specified type.
     */
    <T extends GraphNode> Set<T> nodesOfType(Class<T> nodeType);

    /**
     * Returns all edges of a specific type.
     *
     * @param edgeType The class of edges to return.
     * @param <T> The type of edges to return.
     * @return An immutable set of edges of the specified type.
     */
    <T extends GraphEdge> Set<T> edgesOfType(Class<T> edgeType);

    /**
     * Returns all edges that have the specified node as their source.
     *
     * @param node The source node.
     * @return An immutable set of edges originating from the specified node.
     */
    Set<GraphEdge> edgesFrom(GraphNode node);

    /**
     * Returns all edges that have the specified node as their target.
     *
     * @param node The target node.
     * @return An immutable set of edges targeting the specified node.
     */
    Set<GraphEdge> edgesTo(GraphNode node);
}