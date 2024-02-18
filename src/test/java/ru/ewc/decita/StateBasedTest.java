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

package ru.ewc.decita;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;
import ru.ewc.decita.manual.ManualComputation;

/**
 * Tests for {@link DecisionTable}.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
class StateBasedTest {
    @SneakyThrows
    public static Stream<TestData> readFileNames() {
        return Files.walk(Paths.get(ManualComputation.uriFrom(getFinalPathTo("states"))))
            .filter(Files::isRegularFile)
            .map(path -> path.toFile().getAbsolutePath())
            .map(
                path -> {
                    final InputStream stream;
                    try {
                        stream = Files.newInputStream(Paths.get(path));
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                    return createTestData(path, stream);
                })
            .filter(Objects::nonNull);
    }

    // @todo #78 Make SoftAssertions in state-based tests and gather failure data
    @SneakyThrows
    @ParameterizedTest(name = "{0}")
    @MethodSource("readFileNames")
    void testPerformingFileBasedTest(final TestData test) {
        final ManualComputation target = new ManualComputation()
            .tablePath(getFinalPathTo("tables"))
            .statePath(test.file);
        for (final String table : test.expectations.keySet()) {
            MatcherAssert.assertThat(
                String.format("%nTable '%s'", table),
                target.decideFor(table),
                Matchers.equalTo(test.expectations.get(table))
            );
        }
    }

    @SuppressWarnings("unchecked")
    private static TestData createTestData(final String path, final InputStream stream) {
        final Iterator<Object> iterator = new Yaml().loadAll(stream).iterator();
        iterator.next();
        TestData result = null;
        if (iterator.hasNext()) {
            result = new TestData(path, (Map<String, Map<String, String>>) iterator.next());
        }
        return result;
    }

    /**
     * Gets the path to a folder with specified resources.
     *
     * @param resource Resource name to get its folder.
     * @return Path to a resource folder as a String.
     */
    private static String getFinalPathTo(final String resource) {
        final String states;
        if (System.getProperties().containsKey(resource)) {
            states = System.getProperty(resource);
        } else {
            states = String.format(
                "%s%s%s",
                System.getProperty("user.dir"),
                "/src/test/resources/",
                resource
            );
        }
        return states;
    }

    /**
     * I am the helper class containing the data for parameterized state tests.
     *
     * @since 0.2.3
     */
    @Getter
    public static class TestData {
        /**
         * Path to the file containing state and expectations.
         */
        private final String file;

        /**
         * The collection of expected decision table results.
         */
        private final Map<String, Map<String, String>> expectations;

        TestData(final String file, final Map<String, Map<String, String>> expectations) {
            this.file = file;
            this.expectations = expectations;
        }

        @Override
        public String toString() {
            final String test = this.file
                .substring(Math.max(0, this.file.lastIndexOf('\\')))
                .substring(Math.max(0, this.file.lastIndexOf('/')));
            return String.format("file='%s'", test);
        }
    }
}
