/*
 * MIT License
 *
 * Copyright (c) 2024 Eugene Terekhov
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

import java.util.List;
import ru.ewc.decisions.input.SourceLines;

public class RuleWrapper {
    private final Rule rule;

    private final List<Triplet> internals;

    private RuleWrapper(final Rule rule, final List<Triplet> pairs) {
        this.rule = rule;
        this.internals = pairs;
    }

    public static RuleWrapper from(final SourceLines source, final int index) {
        return new RuleWrapper(
            RuleWrapper.namedRule(source, index),
            source.asTriplets(index)
        );
    }

    private static Rule namedRule(SourceLines source, int index) {
        Rule rule;
        final String[][] headers = source.asArrayOf("HDR");
        if (headers.length > 0 && headers[0].length >= index) {
            rule = new Rule(headers[0][index]);
        } else {
            rule = new Rule("Rule_%02d".formatted(index));
        }
        return rule;
    }

    public Rule rule() {
        this.internals.forEach(triplet -> {
            switch (triplet.type()) {
                case "CND" -> this.rule.withCondition(triplet.left(), triplet.right());
                case "OUT" -> this.rule.withOutcome(triplet.left(), triplet.right());
                case "ASG" -> this.rule.withAssignment(triplet.left(), triplet.right());
                default -> {
                    // do nothing
                }
            }
        });
        return this.rule;
    }
}
