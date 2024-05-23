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

package ru.ewc.checklogic.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * I am a utility class for the web server.
 *
 * @since 0.3.0
 */
final class WebResource {
    /**
     * Ctor that prevents instantiation of this utility class.
     */
    private WebResource() {
        // Utility class
    }

    static String readFileFromResources(final String file) {
        final InputStream input = WebResource.threadClassLoader().getResourceAsStream(file);
        if (input == null) {
            throw new IllegalArgumentException("File not found! %s".formatted(file));
        } else {
            try (BufferedReader reader = WebResource.bufferedReaderOf(input)) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (final IOException exception) {
                throw new IllegalArgumentException("Failed to read file from resources", exception);
            }
        }
    }

    private static BufferedReader bufferedReaderOf(final InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private static ClassLoader threadClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
