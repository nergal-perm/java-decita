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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * I am a simple wrapper around the file on a disk. My main responsibility is to read and cache the
 * file contents as an ordered collection of strings (i.e. each line is a separate string).
 *
 * @since 0.8.0
 */
public final class FileContents {
    /**
     * Path to the file.
     */
    private final Path path;

    /**
     * The extension of the source file.
     */
    private final String extension;

    /**
     * The file contents as a list of strings. I am lazy-loaded.
     */
    private List<String> contents;

    /**
     * Ctor.
     *
     * @param path The path to the file.
     * @param extension The extension of the source file.
     */
    public FileContents(final Path path, final String extension) {
        this.path = path;
        this.extension = extension;
    }

    /**
     * Checks if the file has the expected (passed to constructor) extension.
     *
     * @return True, if the file has the expected extension.
     */
    public boolean hasRightExtension() {
        return this.path.getFileName().toString().endsWith(this.extension);
    }

    /**
     * Returns the name of the file without the extension.
     *
     * @return The name of the file without the extension.
     */
    public String nameWithoutExtension() {
        return this.path.getFileName().toString().replace(this.extension, "");
    }

    /**
     * Returns the file contents as a list of strings. If the contents are not yet read, reads them
     * from the file. In case of any I/O error, returns an empty list.
     *
     * @return File contents as a list of strings.
     */
    public List<String> asStrings() {
        if (this.contents == null) {
            this.contents = this.readAllLines();
        }
        return List.copyOf(this.contents);
    }

    private List<String> readAllLines() {
        List<String> strings;
        try {
            strings = Files.readAllLines(this.path, StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            strings = Collections.emptyList();
        }
        return strings;
    }
}
