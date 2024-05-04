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

package ru.ewc.decita.manual;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;
import ru.ewc.decita.ComputationContext;
import ru.ewc.decita.DecisionTable;
import ru.ewc.decita.DecitaException;
import ru.ewc.decita.DecitaFacade;
import ru.ewc.decita.InMemoryStorage;
import ru.ewc.decita.Locator;
import ru.ewc.decita.Locators;
import ru.ewc.decita.input.ContentReader;
import ru.ewc.decita.input.PlainTextContentReader;

/**
 * I am a unique instance of a {@link DecisionTable} computation.
 *
 * @since 0.2.2
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public class ManualComputation {
    /**
     * An instance of object that reads all the tables from disk.
     */
    private final ContentReader tables;

    /**
     * A URI pointing to a state yaml file.
     */
    private final URI state;

    /**
     * Default Ctor.
     */
    public ManualComputation() {
        this(() -> new Locators(Map.of()), null);
    }

    /**
     * Ctor.
     *
     * @param tables Reader that can read tables data from the file system.
     * @param state Path to yaml describing the current system's state.
     */
    private ManualComputation(final ContentReader tables, final URI state) {
        this.tables = tables;
        this.state = state;
    }

    /**
     * Converts a string representation of the file system path to a correct URI.
     *
     * @param path File system path as a String.
     * @return URI that corresponds to a given path.
     */
    public static URI uriFrom(final String path) {
        final StringBuilder result = new StringBuilder("file:/");
        if (path.charAt(0) == '/') {
            result.append(path.replace('\\', '/').substring(1));
        } else {
            result.append(path.replace('\\', '/'));
        }
        return URI.create(result.toString());
    }

    /**
     * Reads all the tables from disk in a format suitable to construct {@link ComputationContext}.
     *
     * @return A dictionary of {@link DecisionTable}s.
     */
    public Locators tablesAsLocators() {
        return this.tables.allTables();
    }

    /**
     * Creates a copy of this instance with a new path to state yaml.
     *
     * @param path Path to a file that holds the current state's description.
     * @return A new instance of {@link ManualComputation}.
     */
    public ManualComputation statePath(final String path) {
        return new ManualComputation(
            this.tables,
            uriFrom(path)
        );
    }

    /**
     * Creates a copy of this instance with a new path to tables folder.
     *
     * @param path Path to a folder containing all the decision tables.
     * @return A new instance of {@link ManualComputation}.
     */
    public ManualComputation tablePath(final String path) {
        return new ManualComputation(
            new PlainTextContentReader(uriFrom(path), ".csv", ";"),
            this.state
        );
    }

    /**
     * Converts yaml data read from input stream to a correct {@link InMemoryStorage} object.
     *
     * @return The collection of {@link InMemoryStorage} objects.
     */
    @SneakyThrows
    public Locators currentState() {
        try (InputStream stream = Files.newInputStream(new File(this.state).toPath())) {
            return stateFrom(stream);
        }
    }

    /**
     * Computes the decision for a specified table.
     *
     * @param table Name of the tables to make a decision against.
     * @return The collection of outcomes from the specified table.
     * @throws DecitaException If the table could not be found or computed.
     */
    public Map<String, String> decideFor(final String table) throws DecitaException {
        final DecitaFacade facade = new DecitaFacade(this::tablesAsLocators);
        return facade.decisionFor(table, this.currentState());
    }

    /**
     * Returns a complete set of all the table names, read from the user-specified folder.
     *
     * @return Set of Strings, representing decision table names.
     */
    Set<String> tableNames() {
        final Set<String> result;
        if (this.tables == null) {
            result = Collections.emptySet();
        } else {
            result = this.tablesAsLocators().names();
        }
        return result;
    }

    /**
     * Loads the state from the specified {@code InputStream}.
     *
     * @param stream InputStream containing state info.
     * @return Collection of {@link Locator} objects, containing desired state.
     */
    @SuppressWarnings("unchecked")
    private static Locators stateFrom(final InputStream stream) {
        return new Locators(
            ((Map<String, Map<String, Object>>) new Yaml().loadAll(stream).iterator().next())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entryToLocator()))
        );
    }

    /**
     * Converts a {@link Map.Entry} to a {@link Locator} object.
     *
     *@return A function that converts a {@link Map.Entry} to a {@link Locator} object.
     */
    private static Function<Map.Entry<String, Map<String, Object>>, Locator> entryToLocator() {
        return e -> new InMemoryStorage(e.getValue());
    }
}
