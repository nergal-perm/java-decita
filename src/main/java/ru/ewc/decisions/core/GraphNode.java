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

/**
 * I am a node in the DecisionGraph.
 * I represent immutable coordinate, condition, rule, and table descriptions.
 * I am immutable and provide a unique identifier for my position in the graph.
 *
 * @since 0.9.2
 */
public interface GraphNode {
    /**
     * Returns a unique identifier for this node.
     * This identifier locates the node in the graph.
     *
     * @return A unique string identifier for this node.
     */
    // The graph API retains its identifier accessor name.
    // @checkstyle MethodNameCheck (2 lines)
    @SuppressWarnings("PMD.ShortMethodName")
    String id();

    /**
     * Returns the type of this node.
     * This is used for filtering and categorizing nodes.
     *
     * @return A string representing the type of this node.
     */
    String type();

    /**
     * Returns the underlying component that this node represents.
     * This is a coordinate string, rule fragment, fragment list, or table description.
     *
     * @return The underlying component.
     */
    Object component();
}
