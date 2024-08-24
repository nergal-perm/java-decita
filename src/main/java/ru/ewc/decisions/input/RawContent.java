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
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.decisions.conditions.EqualsCondition;
import ru.ewc.decisions.conditions.GreaterThanCondition;
import ru.ewc.decisions.conditions.LessThanCondition;
import ru.ewc.decisions.conditions.NotCondition;
import ru.ewc.decisions.core.Coordinate;
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
     * The name of the source table.
     */
    private final String table;

    /**
     * Array of String values that form the Conditions part of a {@link DecisionTable}.
     */
    private final String[][] conditions;

    /**
     * Array of String values that form the Outcomes/Actions part of a {@link DecisionTable}.
     */
    private final String[][] outcomes;

    /**
     * Ctor.
     *
     * @param conditions A 2D-array of Strings describing the Conditions part of the
     *  {@link DecisionTable}.
     * @param outcomes A 2D-array of Strings describing the Outcomes part of the
     *  {@link DecisionTable}.
     * @param table Name of the source table.
     */
    public RawContent(final String[][] conditions, final String[][] outcomes, final String table) {
        this.table = table;
        this.conditions = conditions.clone();
        this.outcomes = outcomes.clone();
    }

    /**
     * Answers the table name.
     *
     * @return The table name.
     */
    public String tableName() {
        return this.table;
    }

    /**
     * Creates a {@link DecisionTable} from the raw contents.
     *
     * @return The {@link DecisionTable} created from the source file.
     */
    public DecisionTable asDecisionTable() {
        final List<Rule> rules = new ArrayList<>(this.conditions[0].length - 1);
        for (int column = 1; column < this.conditions[0].length; column += 1) {
            final Rule rule = new Rule("%s::rule_%02d".formatted(this.table, column));
            for (final String[] condition : this.conditions) {
                rule.withCondition(
                    fullConditionFrom(Coordinate.from(condition[0]), condition[column])
                );
            }
            for (final String[] outcome : this.outcomes) {
                rule.withOutcome(
                    outcome[0],
                    outcome[column]
                );
            }
            rules.add(rule);
        }
        final Rule elserule = new Rule("%s::else".formatted(this.table));
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
        return new DecisionTable(rules, elserule, this.table);
    }

    /**
     * Creates a {@link Condition} based on a string representation.
     *
     * @param base The base {@link Coordinate} for the condition.
     * @param argument String representation of a condition.
     * @return A concrete {@link Condition} based on given string representation.
     */
    private static Condition fullConditionFrom(final Coordinate base, final String argument) {
        final Condition result;
        final char operation = argument.charAt(0);
        if (operation == '~') {
            result = new EqualsCondition(base, base);
        } else if (operation == '!') {
            result = new NotCondition(fullConditionFrom(base, argument.substring(1)));
        } else if (operation == '>') {
            result = new GreaterThanCondition(base, Coordinate.from(argument.substring(1)));
        } else if (operation == '<') {
            result = new LessThanCondition(base, Coordinate.from(argument.substring(1)));
        } else {
            result = new EqualsCondition(base, Coordinate.from(argument));
        }
        return result;
    }
}
