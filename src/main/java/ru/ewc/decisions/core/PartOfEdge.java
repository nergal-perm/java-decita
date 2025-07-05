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
 * I am a concrete edge in the DecisionGraph that represents a "part_of" relationship.
 * This relationship indicates that the source node is a part of the target node.
 * For example, a Rule is part of a DecisionTable, or a Fragment is part of a Rule.
 *
 * @since 0.9.2
 */
public final class PartOfEdge implements GraphEdge {
    /**
     * The relationship type for "part_of" edges.
     */
    public static final String RELATIONSHIP_TYPE = "part_of";

    /**
     * The underlying edge implementation.
     */
    private final BasicGraphEdge edge;

    /**
     * Constructor.
     *
     * @param source The source node that is part of the target.
     * @param target The target node that contains the source.
     */
    public PartOfEdge(final GraphNode source, final GraphNode target) {
        this.edge = new BasicGraphEdge(source, target, RELATIONSHIP_TYPE);
    }

    @Override
    public GraphNode source() {
        return this.edge.source();
    }

    @Override
    public GraphNode target() {
        return this.edge.target();
    }

    @Override
    public String relationshipType() {
        return this.edge.relationshipType();
    }

    @Override
    public String id() {
        return this.edge.id();
    }

    @Override
    public boolean equals(final Object obj) {
        return this.edge.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.edge.hashCode();
    }

    @Override
    public String toString() {
        return this.edge.toString();
    }
}