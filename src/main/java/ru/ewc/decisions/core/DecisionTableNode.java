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
 * I am a concrete node in the DecisionGraph that represents a DecisionTable.
 * I am immutable and provide a unique identifier based on the table's name.
 *
 * @since 0.9.2
 */
public final class DecisionTableNode implements GraphNode {
    /**
     * The decision table this node represents.
     */
    private final DecisionTable table;

    /**
     * The unique identifier for this table.
     */
    private final String identifier;

    /**
     * Constructor.
     *
     * @param table The decision table this node represents.
     * @param identifier A unique identifier for this table.
     */
    public DecisionTableNode(final DecisionTable table, final String identifier) {
        this.table = Objects.requireNonNull(table, "DecisionTable cannot be null");
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
    }

    @Override
    public String id() {
        return "table:" + this.identifier;
    }

    @Override
    public String type() {
        return "table";
    }

    @Override
    public Object component() {
        return this.table;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DecisionTableNode that = (DecisionTableNode) obj;
        return Objects.equals(this.identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier);
    }

    @Override
    public String toString() {
        return "DecisionTableNode{" + "identifier='" + this.identifier + '\'' + '}';
    }
}