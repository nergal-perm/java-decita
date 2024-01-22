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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PlainTextContentReader}.
 *
 * @since 0.2
 */
class PlainTextContentReaderTest {
    @Test
    void testReadFilesFromFolder() {
        final ContentReader target = new PlainTextContentReader(tablesDirectory(), ".csv", ";");
        final List<RawContent> actual = target.readAllTables();
        MatcherAssert.assertThat(actual, Matchers.hasSize(2));
        checkConditions(actual);
        checkOutcomes(actual);
    }

    private static void checkOutcomes(final List<RawContent> actual) {
        actual.stream()
            .filter(rc -> rc.getTable().equalsIgnoreCase("sample-table"))
            .findFirst().ifPresent(
                rc -> {
                    MatcherAssert.assertThat(
                        "The number of outcomes: ",
                        rc.getOutcomes().length,
                        Matchers.is(1)
                    );
                    MatcherAssert.assertThat(
                        rc.getOutcomes()[0],
                        Matchers.arrayContaining("outcome", "hello", "world")
                    );
                }
            );
    }

    private static void checkConditions(final List<RawContent> actual) {
        actual.stream()
            .filter(rc -> rc.getTable().equalsIgnoreCase("sample-table"))
            .findFirst().ifPresent(
                rc -> {
                    MatcherAssert.assertThat(
                        "The number of conditions: ",
                        rc.getConditions().length,
                        Matchers.is(1)
                    );
                    MatcherAssert.assertThat(
                        rc.getConditions()[0],
                        Matchers.arrayContaining("sample-condition", "true", "false")
                    );
                }
            );
    }

    private static URI tablesDirectory() {
        return Path.of(
            Paths.get("").toAbsolutePath().toString(),
            "src/test/resources/tables"
        ).toUri();
    }
}
