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

package ru.ewc.decisions.api;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.ewc.decisions.core.BaseLocators;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.input.ContentsReader;
import ru.ewc.decisions.input.SourceLines;

/**
 * I am a set of decision tables.
 *
 * @since 0.6.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class DecisionTables extends BaseLocators {
    /**
     * The reader providing the source data for the decision tables.
     */
    private final ContentsReader contents;

    private DecisionTables(final ContentsReader contents, final Map<String, Locator> locators) {
        super(locators);
        this.contents = contents;
    }

    public static DecisionTables using(final ContentsReader contents) {
        return new DecisionTables(
            contents,
            contents.readAll().stream().map(SourceLines::asDecisionTable)
                .collect(Collectors.toMap(Locator::locatorName, Function.identity()))
        );
    }

    public Map<String, List<String>> commandsData() {
        return this.locators().values().stream()
            .filter(DecisionTable.class::isInstance)
            .map(DecisionTable.class::cast)
            .filter(DecisionTable::describesCommand)
            .collect(Collectors.toMap(Locator::locatorName, DecisionTable::commandArgs));
    }

    public DecisionTables reset() {
        return DecisionTables.using(this.contents);
    }
}
