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
 * I am a concrete node in the DecisionGraph that represents a Rule.
 * I am immutable and provide a unique identifier based on the rule's name.
 *
 * @since 0.9.2
 */
public final class RuleNode implements GraphNode {
    /**
     * The rule this node represents.
     */
    private final Rule rule;

    /**
     * The unique identifier for this rule.
     */
    private final String identifier;

    /**
     * Constructor.
     *
     * @param rule The rule this node represents.
     * @param identifier A unique identifier for this rule.
     */
    public RuleNode(final Rule rule, final String identifier) {
        this.rule = Objects.requireNonNull(rule, "Rule cannot be null");
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
    }

    @Override
    public String id() {
        return "rule:" + this.identifier;
    }

    @Override
    public String type() {
        return "rule";
    }

    @Override
    public Object component() {
        return this.rule;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RuleNode that = (RuleNode) obj;
        return Objects.equals(this.identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier);
    }

    @Override
    public String toString() {
        return "RuleNode{" + "identifier='" + this.identifier + '\'' + '}';
    }
}