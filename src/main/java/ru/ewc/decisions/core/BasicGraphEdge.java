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

/**
 * I am a concrete edge in the DecisionGraph that represents a basic relationship.
 * I am immutable and connect two nodes with a specific relationship type.
 *
 * @since 0.9.2
 */
public final class BasicGraphEdge implements GraphEdge {
    /**
     * The source node of this edge.
     */
    private final GraphNode source;

    /**
     * The target node of this edge.
     */
    private final GraphNode target;

    /**
     * The type of relationship this edge represents.
     */
    private final String relationshipType;

    /**
     * Constructor.
     *
     * @param source The source node of this edge.
     * @param target The target node of this edge.
     * @param relationshipType The type of relationship this edge represents.
     */
    public BasicGraphEdge(final GraphNode source, final GraphNode target, final String relationshipType) {
        this.source = Objects.requireNonNull(source, "Source node cannot be null");
        this.target = Objects.requireNonNull(target, "Target node cannot be null");
        this.relationshipType = Objects.requireNonNull(relationshipType, "Relationship type cannot be null");
    }

    @Override
    public GraphNode source() {
        return this.source;
    }

    @Override
    public GraphNode target() {
        return this.target;
    }

    @Override
    public String relationshipType() {
        return this.relationshipType;
    }

    @Override
    public String id() {
        return String.format("%s--%s-->%s", this.source.id(), this.relationshipType, this.target.id());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final BasicGraphEdge that = (BasicGraphEdge) obj;
        return Objects.equals(this.source, that.source) &&
            Objects.equals(this.target, that.target) &&
            Objects.equals(this.relationshipType, that.relationshipType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.source, this.target, this.relationshipType);
    }

    @Override
    public String toString() {
        return "BasicGraphEdge{" +
            "source=" + this.source.id() +
            ", target=" + this.target.id() +
            ", relationshipType='" + this.relationshipType + '\'' +
            '}';
    }
}