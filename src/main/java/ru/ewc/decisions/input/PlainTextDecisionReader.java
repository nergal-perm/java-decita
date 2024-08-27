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

import java.net.URI;
import java.util.List;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.core.DecisionTable;

/**
 * I am the specific {@link DecisionReader} that knows how to read CSV-files. My main responsibility
 * is to perform file-system read operation and transform the file lines into a format suitable for
 * constructing {@link DecisionTable}s.
 *
 * @since 0.2
 */
public final class PlainTextDecisionReader implements DecisionReader {
    /**
     * Path to a folder with source files.
     */
    private final SourceFilesFolder folder;

    /**
     * The symbol that separates CSV-record fields.
     */
    private final String delimiter;

    /**
     * Ctor with specified delimiter.
     *
     * @param dir The folder of source data files.
     * @param extension The extension of source data files.
     * @param delimiter The symbol that separates CSV-record fields.
     */
    public PlainTextDecisionReader(final URI dir, final String extension, final String delimiter) {
        this.folder = new SourceFilesFolder(dir, extension);
        this.delimiter = delimiter;
    }

    @Override
    public DecisionTables allTables() {
        return new DecisionTables(
            this.folder.files().stream()
                .map(this::readAsDecisionTable)
                .toList()
        );
    }

    /**
     * Reads the contents of the specified file as a {@link DecisionTable}.
     *
     * @param file The file to read.
     * @return The file's contents as a {@link DecisionTable}.
     */
    private DecisionTable readAsDecisionTable(final FileContents file) {
        return new RawContent(
            this.conditionsFrom(file.asStrings()),
            this.outcomesFrom(file.asStrings()),
            file.nameWithoutExtension()
        ).asDecisionTable();
    }

    /**
     * Creates the 2D-array representation of the {@link DecisionTable}'s Conditions part.
     *
     * @param contents Raw contents of a file as a Strings collection.
     * @return The 2D-array of Strings that holds the {@link DecisionTable}'s Conditions.
     */
    private String[][] conditionsFrom(final List<String> contents) {
        return this.toArray(contents.subList(0, separatorIndex(contents)));
    }

    /**
     * Creates the 2D-array representation of the {@link DecisionTable}'s Outcomes part.
     *
     * @param contents Raw contents of a file as a Strings collection.
     * @return The 2D-array of Strings that holds the {@link DecisionTable}'s Outcomes.
     */
    private String[][] outcomesFrom(final List<String> contents) {
        return this.toArray(contents.subList(1 + separatorIndex(contents), contents.size()));
    }

    /**
     * Finds the position of the line separating Conditions from Outcomes in a source file.
     *
     * @param contents A collection of source data file lines.
     * @return An {@code Integer} corresponding to the separating line index.
     */
    private static int separatorIndex(final List<String> contents) {
        return contents.indexOf("---");
    }

    /**
     * Transforms the list of Strings to a 2D-array of Strings suitable for {@link DecisionTable}
     * generation.
     *
     * @param lines A collection of Strings representing the file contents.
     * @return A 2D-array of Strings
     */
    private String[][] toArray(final List<String> lines) {
        final int width = lines.get(0).split(this.delimiter).length;
        final String[][] result = new String[lines.size()][width];
        for (int idx = 0; idx < lines.size(); idx = idx + 1) {
            result[idx] = lines.get(idx).split(this.delimiter);
        }
        return result;
    }
}
