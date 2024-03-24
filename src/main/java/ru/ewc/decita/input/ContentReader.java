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

import java.util.Map;
import ru.ewc.decita.ComputationContext;
import ru.ewc.decita.DecisionTable;
import ru.ewc.decita.Locator;

/**
 * I am the interface for all the source data Readers. My implementors should know how to get data
 * from their sources and then transform it into a format suitable for constructing
 * {@code DecisionTables}.
 *
 * @since 0.2
 */
public interface ContentReader {
    /**
     * Reads all the source data and returns the {@link Locator}s collection, that can be used to
     * initialize the {@link ComputationContext}.
     *
     * @return A collection of {@link Locator}s , representing the contents of
     *  {@link DecisionTable}s data sources.
     */
    Map<String, Locator> allTables();
}
