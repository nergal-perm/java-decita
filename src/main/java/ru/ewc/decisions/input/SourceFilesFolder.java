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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * I am a simple wrapper around the folder on a disk. My main responsibility is to provide the file
 * wrappers collection for all the files with the specified extension. I am lazy-loaded and I
 * gather all the files recursively.
 *
 * @since 0.8.0
 */
public class SourceFilesFolder {
    /**
     * The path to the folder with source files.
     */
    private final URI folder;

    /**
     * The extension of the source files.
     */
    private final String extension;

    /**
     * Ctor.
     *
     * @param folder The path to the folder with source files.
     * @param extension The expected extension of the source files. The format is ".ext".
     */
    public SourceFilesFolder(final URI folder, final String extension) {
        this.folder = folder;
        this.extension = extension;
    }

    /**
     * Returns the list of file wrappers for all the files with the specified extension.
     *
     * @return The list of file wrappers for all the files with the specified extension.
     */
    public List<FileContents> files() {
        List<FileContents> result;
        try (Stream<Path> files = Files.walk(Paths.get(this.folder))) {
            result = files
                .filter(Files::isRegularFile)
                .map(path -> new FileContents(path, this.extension))
                .filter(FileContents::hasRightExtension)
                .toList();
        } catch (final IOException exception) {
            result = List.of();
        }
        return result;
    }
}
