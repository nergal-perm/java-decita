/*
 * MIT License
 *
 * Copyright (c) 2023-2024 Eugene Terekhov
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

package ru.ewc.decita.input;

import java.util.List;
import lombok.Getter;
import ru.ewc.decita.DecisionTable;
import ru.ewc.decita.Rule;

/**
 * I am the unified source for building {@link DecisionTable}s. My main responsibility is to store
 * all the data needed to construct {@link Rule}s and fill the {@link DecisionTable}.
 *
 * @since 0.2
 */
@Getter
public class RawContent {
    /**
     * The name of the source table.
     */
    private final String table;

    /**
     * The contents of the source entity.
     */
    private final List<String> contents;

    /**
     * Ctor.
     * @param table Name of the source table.
     * @param contents Contents of the source.
     */
    public RawContent(final String table, final List<String> contents) {
        this.table = table;
        this.contents = contents;
    }
}
