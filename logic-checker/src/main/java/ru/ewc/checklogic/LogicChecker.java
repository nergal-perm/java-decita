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

package ru.ewc.checklogic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.yaml.snakeyaml.Yaml;
import ru.ewc.checklogic.server.WebServer;
import ru.ewc.commands.CommandsFacade;
import ru.ewc.decisions.api.DecitaFacade;
import ru.ewc.state.StoredState;

/**
 * End-to-end tests based on yaml descriptions.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class LogicChecker {
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LogicChecker.class.getName());

    /**
     * The template for the assertion message.
     */
    private static final String AV_TEMPLATE = "Command '%s'[%d] should be available";

    private LogicChecker() {
        // Utility class
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please provide the path to the resources");
        }
        final String root = args[0];
        final StoredState state = stateFromAppConfig(Path.of(root, "application.yaml").toFile());
        final Computation computation = new Computation(
            new DecitaFacade(Path.of(root, "tables").toUri(), state),
            new CommandsFacade(Path.of(root, "commands").toUri(), state),
            state
        );
        if (args.length > 1 && "server".equals(args[1])) {
            new WebServer(computation).start();
        } else {
            System.setProperty("sources", root);
            readFileNames().forEach(test -> performTest(test, new SoftAssertions(), computation));
        }
    }

    @SneakyThrows
    static Stream<TestData> readFileNames() {
        return Files.walk(Paths.get(Computation.uriFrom(getFinalPathTo("states"))))
            .filter(Files::isRegularFile)
            .map(
                path -> path.toFile().getAbsolutePath()
            ).map(
                path -> {
                    final InputStream stream;
                    try {
                        stream = Files.newInputStream(Paths.get(path));
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                    return createTestData(path, stream);
                });
    }

    @SneakyThrows
    static void performTest(
        final TestData test,
        final SoftAssertions softly,
        final Computation initial
    ) {
        final Computation target = initial.withState(
            stateFromFile(Files.newInputStream(new File(test.file()).toPath()))
        );
        try {
            for (int idx = 0; idx < test.commands.size(); idx += 1) {
                final Transition command = test.commands.get(idx);
                Assertions.assertThat(target.decideFor(command.name(), command.request()))
                    .describedAs(String.format(LogicChecker.AV_TEMPLATE, command.name(), idx + 1))
                    .containsEntry("available", "true");
                target.perform(command);
            }
            for (final String table : test.expectations.keySet()) {
                if (target.hasStateFor(table)) {
                    softly
                        .assertThat(target.stateFor(table, test.expectations.get(table)))
                        .describedAs(String.format("State for entity '%s'", table))
                        .containsExactlyInAnyOrderEntriesOf(test.expectations.get(table));
                } else {
                    softly
                        .assertThat(target.decideFor(table))
                        .describedAs(String.format("Table '%s'", table))
                        .isEqualTo(test.expectations.get(table));
                }
            }
            softly.assertAll();
            LOGGER.info("Running test for %s... done".formatted(test.toString()));
        } catch (final AssertionError error) {
            LOGGER.severe("Running test for %s... failed".formatted(test.toString()));
            LOGGER.severe(error.getMessage());
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static StoredState stateFromAppConfig(final File file) {
        final Map<String, Object> config = new Yaml().load(Files.newInputStream(file.toPath()));
        final Stream<String> names = ((List<String>) config.get("locators")).stream();
        return new StoredState(
            names.collect(
                Collectors.toMap(
                    name -> name,
                    name -> new InMemoryStorage(new HashMap<>())
                )
            )
        );
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Object>> stateFromFile(final InputStream stream) {
        return (Map<String, Map<String, Object>>) new Yaml().loadAll(stream).iterator().next();
    }

    @SuppressWarnings("unchecked")
    private static TestData createTestData(final String path, final InputStream stream) {
        final Iterator<Object> iterator = new Yaml().loadAll(stream).iterator();
        iterator.next();
        List<Transition> commands = new ArrayList<>(1);
        if (iterator.hasNext()) {
            commands = extractCommands(iterator.next());
        }
        Map<String, Map<String, String>> expectations = new HashMap<>();
        if (iterator.hasNext()) {
            expectations = (Map<String, Map<String, String>>) iterator.next();
        }
        return new TestData(path, commands, expectations);
    }

    @SuppressWarnings("unchecked")
    private static List<Transition> extractCommands(final Object next) {
        final Map<String, List<Object>> commands = (Map<String, List<Object>>) next;
        return commands.getOrDefault("commands", List.of())
            .stream()
            .map(entry -> transitionFrom((Map<String, Object>) entry))
            .toList();
    }

    @SuppressWarnings("unchecked")
    private static Transition transitionFrom(final Map<String, Object> entry) {
        return new Transition(
            (String) entry.get("name"),
            requestLocator((Map<String, Object>) entry.get("request"))
        );
    }

    private static StoredState requestLocator(final Map<String, Object> entry) {
        return new StoredState(Map.of("request", new InMemoryStorage(entry)));
    }

    /**
     * Gets the path to a folder with specified resources.
     *
     * @param resource Resource name to get its folder.
     * @return Path to a resource folder as a String.
     */
    private static String getFinalPathTo(final String resource) {
        final String states;
        if (System.getProperties().containsKey("sources")) {
            states = String.format("%s/%s", System.getProperty("sources"), resource);
        } else if (System.getProperties().containsKey(resource)) {
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
     * @param file The path to the file containing state and expectations.
     * @param commands The collection of commands to execute before the decision.
     * @param expectations The collection of expected decision table results.
     * @since 0.2.3
     */
    public record TestData(
        String file,
        List<Transition> commands,
        Map<String, Map<String, String>> expectations) {

        @Override
        public String toString() {
            final String test = this.file
                .substring(Math.max(0, this.file.lastIndexOf('\\')))
                .substring(Math.max(0, this.file.lastIndexOf('/')));
            return String.format("file='%s'", test);
        }
    }
}
