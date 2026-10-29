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
    private final GraphNode origin;

    /**
     * The target node of this edge.
     */
    private final GraphNode destination;

    /**
     * The type of relationship this edge represents.
     */
    private final String relationship;

    /**
     * Constructor.
     *
     * @param source The source node of this edge.
     * @param target The target node of this edge.
     * @param relationship The type of relationship this edge represents.
     */
    public BasicGraphEdge(
        final GraphNode source, final GraphNode target, final String relationship
    ) {
        this.origin = Objects.requireNonNull(source, "Source node cannot be null");
        this.destination = Objects.requireNonNull(target, "Target node cannot be null");
        this.relationship = Objects.requireNonNull(
            relationship, "Relationship type cannot be null"
        );
    }

    @Override
    public GraphNode source() {
        return this.origin;
    }

    @Override
    public GraphNode target() {
        return this.destination;
    }

    @Override
    public String relationshipType() {
        return this.relationship;
    }

    // GraphEdge defines the identifier accessor name.
    @SuppressWarnings("PMD.ShortMethodName")
    @Override
    public String id() {
        return String.format(
            "%s--%s-->%s", this.origin.id(), this.relationship, this.destination.id()
        );
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equal = false;
        if (obj instanceof BasicGraphEdge) {
            final BasicGraphEdge other = (BasicGraphEdge) obj;
            equal = Objects.equals(this.origin, other.origin)
                && Objects.equals(this.destination, other.destination)
                && Objects.equals(this.relationship, other.relationship);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.origin, this.destination, this.relationship);
    }

    @Override
    public String toString() {
        return String.format(
            "BasicGraphEdge{source=%s, target=%s, relationshipType='%s'}",
            this.origin.id(), this.destination.id(), this.relationship
        );
    }
}
