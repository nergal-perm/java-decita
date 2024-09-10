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

import java.util.List;
import java.util.stream.IntStream;
import ru.ewc.decisions.api.ComputableLocator;
import ru.ewc.decisions.api.MultipleOutcomes;
import ru.ewc.decisions.core.CheckInstance;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.core.Rule;

/**
 * I am the unified source for building {@link DecisionTable}s. My main responsibility is to store
 * all the data needed to construct {@link Rule}s and fill the {@link DecisionTable}.
 *
 * @since 0.2
 */
// @todo #154 Get rid of RawContent in favor of SourceLines
public final class RawContent {
    /**
     * The name of the source file.
     */
    private final String name;

    /**
     * The source file contents as categorized lines.
     */
    private final SourceLines lines;

    /**
     * Ctor.
     *
     * @param lines A {@link SourceLines} object that contains the source file contents as
     *  categorized lines.
     */
    public RawContent(final SourceLines lines) {
        this.name = lines.fileName();
        this.lines = lines;
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

    public MultipleOutcomes asCheckInstance() {
        return new CheckInstance(this.specifiedRules());
    }

    private List<Rule> specifiedRules() {
        final int columns = this.lines.columns();
        return IntStream.range(2, columns)
            .mapToObj(i -> Rule.from(this.lines, i))
            .toList();
    }

    private Rule elseRule() {
        final Rule elserule = new Rule("%s::else".formatted(this.name));
        final String[][] outcomes = this.lines.asArrayOf("OUT");
        final String[][] conditions = this.lines.asArrayOf("CND");
        if (outcomes[0].length > conditions[0].length) {
            for (final String[] outcome : outcomes) {
                elserule.withOutcome(
                    outcome[0].trim(),
                    outcome[conditions[0].length].trim()
                );
            }
        } else {
            elserule.withOutcome("outcome", "undefined");
        }
        return elserule;
    }
}
