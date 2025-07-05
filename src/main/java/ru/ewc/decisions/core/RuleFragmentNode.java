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
 * I am a concrete node in the DecisionGraph that represents a RuleFragment.
 * I am immutable and provide a unique identifier based on the fragment's properties.
 *
 * @since 0.9.2
 */
public final class RuleFragmentNode implements GraphNode {
    /**
     * The rule fragment this node represents.
     */
    private final RuleFragment fragment;

    /**
     * Constructor.
     *
     * @param fragment The rule fragment this node represents.
     */
    public RuleFragmentNode(final RuleFragment fragment) {
        this.fragment = Objects.requireNonNull(fragment, "RuleFragment cannot be null");
    }

    @Override
    public String id() {
        return "fragment:" + this.fragment.type() + ":" + this.fragment.left() + ":" + this.fragment.right();
    }

    @Override
    public String type() {
        return "fragment";
    }

    @Override
    public Object component() {
        return this.fragment;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RuleFragmentNode that = (RuleFragmentNode) obj;
        return Objects.equals(this.fragment, that.fragment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fragment);
    }

    @Override
    public String toString() {
        return "RuleFragmentNode{" + "fragment=" + this.fragment + '}';
    }
}