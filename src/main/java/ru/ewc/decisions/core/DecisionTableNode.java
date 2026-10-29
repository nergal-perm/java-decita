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
 * I am a concrete node in the DecisionGraph that represents an immutable table description.
 * I am immutable and provide a unique identifier based on an explicit table identifier.
 *
 * @since 0.9.2
 */
public final class DecisionTableNode implements GraphNode {
    /**
     * The immutable table description this node represents.
     */
    private final TableDescription table;

    /**
     * The unique identifier for this table.
     */
    private final String identifier;

    /**
     * Constructor.
     *
     * @param table The immutable table description this node represents.
     * @param identifier A unique identifier for this table.
     */
    public DecisionTableNode(final TableDescription table, final String identifier) {
        this.table = Objects.requireNonNull(table, "DecisionTable cannot be null");
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
    }

    // GraphNode defines the identifier accessor name.
    @SuppressWarnings("PMD.ShortMethodName")
    @Override
    public String id() {
        return String.format("table:%s", this.identifier);
    }

    @Override
    public String type() {
        return "table";
    }

    @Override
    public TableDescription component() {
        return this.table;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equal = false;
        if (obj instanceof DecisionTableNode) {
            final DecisionTableNode other = (DecisionTableNode) obj;
            equal = Objects.equals(this.identifier, other.identifier);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier);
    }

    @Override
    public String toString() {
        return String.format("DecisionTableNode{identifier='%s'}", this.identifier);
    }

    /**
     * Describes this table with canonical rule references.
     * @param description Canonical rule definitions
     * @return A table node retaining the same identifier
     */
    DecisionTableNode withDescription(final TableDescription description) {
        return new DecisionTableNode(description, this.identifier);
    }
}
