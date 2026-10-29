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
import ru.ewc.decisions.api.RuleFragment;

/**
 * I am a concrete node in the DecisionGraph that represents a condition description.
 * I am immutable and provide a unique identifier based on an explicit condition identifier.
 *
 * @since 0.9.2
 */
public final class ConditionNode implements GraphNode {
    /**
     * The condition description this node represents.
     */
    private final RuleFragment condition;

    /**
     * The unique identifier for this condition.
     */
    private final String identifier;

    /**
     * Constructor.
     *
     * @param condition The condition description this node represents.
     * @param identifier A unique identifier for this condition.
     */
    public ConditionNode(final RuleFragment condition, final String identifier) {
        this.condition = Objects.requireNonNull(condition, "Condition cannot be null");
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
    }

    // GraphNode defines the identifier accessor name.
    @SuppressWarnings("PMD.ShortMethodName")
    @Override
    public String id() {
        return String.format("condition:%s", this.identifier);
    }

    @Override
    public String type() {
        return "condition";
    }

    @Override
    public RuleFragment component() {
        return this.condition;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equal = false;
        if (obj instanceof ConditionNode) {
            final ConditionNode other = (ConditionNode) obj;
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
        return String.format("ConditionNode{identifier='%s'}", this.identifier);
    }
}
