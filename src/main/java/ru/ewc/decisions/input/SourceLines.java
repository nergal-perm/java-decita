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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import ru.ewc.decisions.api.ComputableLocator;
import ru.ewc.decisions.api.MultipleOutcomes;
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.core.CheckInstance;
import ru.ewc.decisions.core.DecisionRuleFragments;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.core.Rule;

/**
 * I am a class that represents the lines from the source file grouped by the type of the line.
 *
 * @since 0.8.0
 */
public final class SourceLines implements Iterable<String[]> {
    /**
     * The name of the source file.
     */
    private final String file;

    /**
     * All the lines from the source file, preserving the order.
     */
    private final List<String> ungrouped;

    /**
     * The delimiter used in the source file.
     */
    private final String delimiter;

    public SourceLines(final String file, final List<String> lines, final String delimiter) {
        this.file = file;
        this.ungrouped = lines;
        this.delimiter = delimiter;
    }

    @Override
    public Iterator<String[]> iterator() {
        return Stream.of(this.toArray(this.ungrouped)).iterator();
    }

    public String fileName() {
        return this.file;
    }

    public ComputableLocator asDecisionTable() {
        return new DecisionTable(
            this.specifiedRules(),
            this.elseRule(),
            this.file
        );
    }

    public MultipleOutcomes asCheckInstance() {
        return new CheckInstance(this.specifiedRules());
    }

    String[][] asArrayOf(final String key) {
        return this.toArray(
            this.ungrouped.stream()
                .filter(line -> line.startsWith(key))
                .map(
                    line -> Arrays.stream(line.split(this.delimiter))
                        .skip(1)
                        .collect(Collectors.joining(this.delimiter))
                )
                .toList()
        );
    }

    private String[][] toArray(final List<String> source) {
        final String[][] result;
        if (source.isEmpty()) {
            result = new String[0][0];
        } else {
            final int width = source.get(0).split(this.delimiter).length;
            result = new String[source.size()][width];
            for (int idx = 0; idx < source.size(); idx = idx + 1) {
                result[idx] = source.get(idx).split(this.delimiter);
            }
        }
        return result;
    }

    private List<Rule> specifiedRules() {
        final int columns = this.ungrouped.get(0).split(this.delimiter).length;
        return IntStream.range(2, columns)
            .mapToObj(i -> Rule.from(this, i))
            .toList();
    }

    private Rule elseRule() {
        final String[][] outcomes = this.asArrayOf("OUT");
        final String[][] conditions = this.asArrayOf("CND");
        final DecisionRuleFragments fragments;
        if (outcomes[0].length > conditions[0].length) {
            fragments = DecisionRuleFragments.from(this, outcomes[0].length);
        } else {
            fragments = new DecisionRuleFragments(
                List.of(
                    new RuleFragment("OUT", "outcome", "undefined")
                )
            );
        }
        return new Rule("%s::else".formatted(this.file), fragments);
    }
}
