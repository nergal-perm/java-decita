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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 * I am the shell for manual library testing. My main responsibility is to hide Java complexities
 * behind a simple interface.
 *
 * @since 0.2.2
 */
public final class Shell {
    /**
     * Set of predefined autocompletion options, contains all available commands.
     */
    public static final Set<String> COMMANDS = Set.of("tables", "decide", "state");

    /**
     * Object holding current terminal.
     */
    private final Terminal terminal;

    /**
     * Object that reads the user's input.
     */
    private LineReader reader;

    /**
     * Object that can write anything to console.
     */
    private final PrintWriter writer;

    /**
     * An instance of a manual computation.
     */
    private ManualComputation computation;

    /**
     * Ctor.
     *
     * @throws IOException If the terminal could not be created.
     */
    private Shell() throws IOException {
        this.terminal = TerminalBuilder.terminal();
        this.writer = this.terminal.writer();
        this.reader = LineReaderBuilder
            .builder()
            .terminal(this.terminal)
            .completer(new StringsCompleter(Shell.COMMANDS))
            .parser(new DefaultParser())
            .build();
        this.computation = new ManualComputation();
    }

    /**
     * The method to run a simplified manual testing utility.
     *
     * @param args Collection of String arguments to initialize Shell.
     * @throws IOException If the command line terminal could not be created.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static void main(final String[] args) throws IOException {
        new Shell().commandsCycle();
    }

    /**
     * Creates and runs the infinite cycle of handling user input.
     */
    private void commandsCycle() {
        String line;
        while (true) {
            line = this.reader.readLine("Decita manual > ").trim();
            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                break;
            }
            this.performCommand(this.reader.getParser().parse(line, 0));
        }
    }

    /**
     * The method to run a single user's command.
     *
     * @param parsed Structure holding the parsed user input.
     */
    private void performCommand(final ParsedLine parsed) {
        final String command = parsed.words().get(0);
        final String param = extractParameterFrom(parsed);
        switch (command) {
            case "tables":
                if (param == null) {
                    this.writer.println("Please give me a path to tables folder...");
                } else {
                    this.pointToSources(param);
                }
                break;
            case "state":
                if (param == null) {
                    this.writer.println("Please give me a path to current state file...");
                } else {
                    this.loadState(param);
                }
                break;
            case "decide":
                if (param == null) {
                    this.writer.println("Please give me a table name...");
                } else {
                    this.decideFor(param);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Store a path to decision tables folder for following computations.
     *
     * @param path The path to decision tables.
     */
    private void pointToSources(final String path) {
        this.computation = new ManualComputation(path);
        this.reader = LineReaderBuilder
            .builder()
            .terminal(this.terminal)
            .completer(this.commandsAndTableNames())
            .build();
        this.writer.printf("Let's import tables from %s%n", path);
    }

    /**
     * Computes a new completion set, including all the loaded table names.
     *
     * @return An instance of {@link StringsCompleter} with all the available commands and tables
     */
    private StringsCompleter commandsAndTableNames() {
        final Set<String> result = new HashSet<>(this.computation.tableNames());
        result.addAll(Shell.COMMANDS);
        return new StringsCompleter(result);
    }

    /**
     * Loads the computational state from a user-specified file.
     *
     * @param path Path to the yaml file, containing desired state.
     */
    @SneakyThrows
    private void loadState(final String path) {
        this.computation = this.computation.statePath(path);
        this.writer.printf("Let's use state from %s%n", path);
    }

    /**
     * Method that resolves the decision table in a specific Computation context.
     *
     * @param table The name of the table to resolve.
     */
    @SneakyThrows
    private void decideFor(final String table) {
        this.writer.printf("%n--== %s ==--%n", table.toUpperCase(Locale.getDefault()));
        this.computation.decideFor(table).forEach(
            (key, value) -> this.writer.printf("%s : %s%n", key, value)
        );
        this.writer.println();
    }

    /**
     * Extracts a single parameter for the user command.
     *
     * @param parsed The structure holding the parsed user command.
     * @return A single String parameter entered by user.
     */
    private static String extractParameterFrom(final ParsedLine parsed) {
        final String param;
        if (parsed.words().size() > 1) {
            param = parsed.words().get(1);
        } else {
            param = null;
        }
        return param;
    }
}
