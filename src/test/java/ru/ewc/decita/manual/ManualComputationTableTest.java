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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import ru.ewc.decita.ComputationContext;
import ru.ewc.decita.DecisionTable;
import ru.ewc.decita.DecitaException;
import ru.ewc.decita.InMemoryStorage;
import ru.ewc.decita.TestObjects;
import ru.ewc.decita.manual.ManualComputation;

/**
 * Tests for {@link DecisionTable}.
 *
 * @since 0.1
 */
class ManualComputationTableTest {
    public static final String TEST_RESOURCES_PATH = System.getProperty("user.dir") + "/src/test/resources";

    @SneakyThrows
    @Test
    void testPaths() {
        final URI actual = ManualComputation.uriFrom("C:\\Users\\test");
        MatcherAssert.assertThat(
            actual,
            Matchers.is(new URI("file:/C:/Users/test"))
        );
    }

    @Test
    void testComputingOnAState() {
        final ManualComputation target = new ManualComputation()
            .tablePath(TEST_RESOURCES_PATH + "/tables")
            .statePath(TEST_RESOURCES_PATH + "/states/state.yml");
        MatcherAssert.assertThat(
            target.decideFor("sample-table"),
            Matchers.hasEntry(Matchers.is("text"), Matchers.is("the world is not enough"))
        );
        MatcherAssert.assertThat(
            target.decideFor("sample-table"),
            Matchers.hasEntry(Matchers.is("outcome"), Matchers.is("false"))
        );
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Test
    void testPerformingFileBasedTest() {
        final ManualComputation target = new ManualComputation()
            .tablePath(TEST_RESOURCES_PATH + "/tables")
            .statePath(TEST_RESOURCES_PATH + "/states/state-test.yml");
        final InputStream stream = Files.newInputStream(new File(ManualComputation.uriFrom(TEST_RESOURCES_PATH + "/states/state-test.yml")).toPath());
        final Iterator<Object> iterator = new Yaml().loadAll(stream).iterator();
        iterator.next();
        Assumptions.assumeTrue(iterator.hasNext(), "Test file has no expectations in it");
        Map<String, Map<String, String>> expectations = (Map<String, Map<String, String>>) iterator.next();
        for (String table : expectations.keySet()) {
            MatcherAssert.assertThat(
                target.decideFor(table),
                Matchers.equalTo(expectations.get(table))
            );
        }
    }
}
