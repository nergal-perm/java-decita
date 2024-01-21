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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.ewc.decita.DecisionTable;

/**
 * I am the specific {@link ContentReader} that knows how to read CSV-files. My main responsibility
 * is to perform file-system read operation and transform the file lines into a format suitable for
 * constructing {@link DecisionTable}s.
 *
 * @since 0.2
 */
public final class PlainTextContentReader implements ContentReader {
    /**
     * Path to a folder with source files.
     */
    private final URI folder;

    /**
     * The extension of the source files.
     */
    private final String extension;

    /**
     * Ctor.
     *
     * @param folder The folder of source data files.
     * @param extension The extension of source data files.
     */
    public PlainTextContentReader(final URI folder, final String extension) {
        this.folder = folder;
        this.extension = extension;
    }

    @Override
    public List<RawContent> readAllTables() {
        List<RawContent> contents;
        try (Stream<Path> files = Files.walk(Paths.get(this.folder))) {
            contents = this.contentsOf(files);
        } catch (final IOException exception) {
            contents = Collections.emptyList();
        }
        return contents;
    }

    /**
     * Reads the contents of all the files, passed in as a stream of paths.
     *
     * @param files Stream of paths to read data from.
     * @return A collection of {@link RawContent}, each element corresponds to a single
     *  {@link DecisionTable}.
     */
    private List<RawContent> contentsOf(final Stream<Path> files) {
        return files.filter(Files::isRegularFile)
            .filter(path -> path.getFileName().toString().endsWith(this.extension))
            .map(Path::toString)
            .map(this::readContentFromFile)
            .collect(Collectors.toList());
    }

    /**
     * Reads the contents of the specified file as a {@link RawContent}.
     *
     * @param file File to read, represented as {@code String}.
     * @return The file's contents in a format suitable for constructing {@link DecisionTable}s.
     */
    private RawContent readContentFromFile(final String file) {
        final Path path = Path.of(file);
        return new RawContent(
            path.getFileName().toString().replace(this.extension, ""),
            contentsOf(path)
        );
    }

    /**
     * Reads the contents of the specified file as a collection of {@code String}s.
     *
     * @param file File to read, represented as {@code Path}.
     * @return The collection of {@code String}s read from file.
     */
    private static List<String> contentsOf(final Path file) {
        List<String> strings;
        try {
            strings = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            strings = Collections.emptyList();
        }
        return strings;
    }
}
