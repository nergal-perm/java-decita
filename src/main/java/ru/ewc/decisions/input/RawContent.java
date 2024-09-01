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

package ru.ewc.decisions.input;

import java.util.ArrayList;
import java.util.List;
import ru.ewc.decisions.api.ComputableLocator;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.core.Rule;

/**
 * I am the unified source for building {@link DecisionTable}s. My main responsibility is to store
 * all the data needed to construct {@link Rule}s and fill the {@link DecisionTable}.
 *
 * @since 0.2
 */
public final class RawContent {
    /**
     * The name of the source file.
     */
    private final String name;

    /**
     * Array of String values that form the Conditions part of a {@link DecisionTable}.
     */
    private final String[][] conditions;

    /**
     * Array of String values that form the Outcomes/Actions part of a {@link DecisionTable}.
     */
    private final String[][] outcomes;

    /**
     * The array of String values that form the Assignments part of a {@link DecisionTable}.
     */
    private final String[][] assignments;

    /**
     * Ctor.
     *
     * @param lines A {@link SourceLines} object that contains the source file contents as
     *  categorized lines.
     * @param name The name of the file.
     */
    public RawContent(final SourceLines lines, final String name) {
        this.name = name;
        this.conditions = lines.asArrayOf("CND").clone();
        this.outcomes = lines.asArrayOf("OUT").clone();
        this.assignments = lines.asArrayOf("ASG").clone();
    }

    /**
     * Answers the table name.
     *
     * @return The table name.
     */
    public String tableName() {
        return this.name;
    }

    /**
     * Creates a {@link DecisionTable} from the raw contents.
     *
     * @return The {@link DecisionTable} created from the source file.
     */
    public ComputableLocator asDecisionTable() {
        return new DecisionTable(
            this.specifiedRules(),
            this.elseRule(),
            this.name
        );
    }

    private List<Rule> specifiedRules() {
        final List<Rule> rules = new ArrayList<>(this.conditions[0].length - 1);
        for (int column = 1; column < this.conditions[0].length; column += 1) {
            final Rule rule = new Rule("%s::rule_%02d".formatted(this.name, column));
            for (final String[] condition : this.conditions) {
                rule.withCondition(condition[0], condition[column]);
            }
            for (final String[] outcome : this.outcomes) {
                rule.withOutcome(outcome[0], outcome[column]);
            }
            for (final String[] assignment : this.assignments) {
                rule.withAssignment(assignment[0], assignment[column]);
            }
            rules.add(rule);
        }
        return rules;
    }

    private Rule elseRule() {
        final Rule elserule = new Rule("%s::else".formatted(this.name));
        if (this.outcomes[0].length > this.conditions[0].length) {
            for (final String[] outcome : this.outcomes) {
                elserule.withOutcome(
                    outcome[0],
                    outcome[this.conditions[0].length]
                );
            }
        } else {
            elserule.withOutcome("outcome", "undefined");
        }
        return elserule;
    }
}
