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

package ru.ewc.decita.manual;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import ru.ewc.decita.ComputationContext;
import ru.ewc.decita.DecisionTable;
import ru.ewc.decita.Locator;
import ru.ewc.decita.input.PlainTextContentReader;

/**
 * I am a unique instance of a {@link DecisionTable} computation.
 *
 * @since 0.2.2
 */
public class ManualComputation {
    /**
     * An instance of object that reads all the tables from disk.
     */
    private final PlainTextContentReader tables;

    /**
     * Ctor.
     */
    public ManualComputation() {
        this(String.format("%s/src/test/resources/tables", System.getProperty("user.dir")));
    }

    /**
     * Ctor with path to tables.
     *
     * @param path A path to tables files.
     */
    public ManualComputation(final String path) {
        this.tables = new PlainTextContentReader(
            URI.create(String.format("file://%s", path.replace("'", ""))),
            ".csv",
            ";"
        );
    }

    /**
     * Reads all the tables from disk in a format suitable to construct {@link ComputationContext}.
     *
     * @return A dictionary of {@link DecisionTable}s.
     */
    public Map<String, Locator> tablesAsLocators() {
        return this.tables.allTables();
    }

    /**
     * Returns a complete set of all the table names, read from the user-specified folder.
     *
     * @return Set of Strings, representing decision table names.
     */
    Set<String> tableNames() {
        return this.tablesAsLocators().keySet();
    }
}
