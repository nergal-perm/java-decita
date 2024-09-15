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
import java.util.Map;
import java.util.stream.Collectors;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.api.RuleFragments;
import ru.ewc.decisions.commands.Assignment;
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.decisions.input.SourceLines;

/**
 * I am a dedicated collection of {@link RuleFragment}s. My main responsibility is to provide
 * convenient methods to filter and search the fragments.
 *
 * @since 0.9.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class DecisionRuleFragments {
    /**
     * The delegate {@link RuleFragments} to work with.
     */
    private final RuleFragments fragments;

    public DecisionRuleFragments(final List<RuleFragment> fragments) {
        this(new RuleFragments(fragments));
    }

    public DecisionRuleFragments(final RuleFragments fragments) {
        this.fragments = fragments;
    }

    public static DecisionRuleFragments from(final SourceLines lines, final int column) {
        return new DecisionRuleFragments(RuleFragments.listFrom(lines, column));
    }

    public List<Assignment> assignments() {
        return
            this.fragments.getFragments().stream()
                .filter(rf -> rf.nonEmptyOfType("ASG"))
                .map(rf -> new Assignment(rf.left(), rf.right()))
                .toList();
    }

    public List<Condition> conditions() {
        return
            this.fragments.getFragments().stream()
                .filter(rf -> rf.nonEmptyOfType("CND"))
                .map(Condition::from)
                .toList();
    }

    public Map<String, String> outcomes(final ComputationContext context) {
        return
            this.fragments.getFragments().stream()
                .filter(rf -> rf.nonEmptyOfType("OUT"))
                .collect(
                    Collectors.toMap(
                        RuleFragment::left,
                        rf -> Coordinate.from(rf.right()).valueIn(context)
                    )
                );
    }
}
