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
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.ewc.decisions.api.BaseLocators;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.core.DecisionTable;

/**
 * I am a set of decision tables.
 *
 * @since 0.6.0
 */
public class DecisionTables extends BaseLocators {
    /**
     * Ctor.
     *
     * @param collection The {@link Locator}s to be managed by this instance.
     */
    public DecisionTables(final List<DecisionTable> collection) {
        super(collection
            .stream()
            .collect(Collectors.toMap(DecisionTable::tableName, Function.identity()))
        );
    }
}
