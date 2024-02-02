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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import ru.ewc.decita.input.PlainTextContentReader;

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
    public static final List<String> COMMANDS = List.of("sources", "decide");

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
     * Holds the location of decision table sources.
     */
    private URI folder;

    /**
     * Ctor.
     *
     * @throws IOException If the terminal could not be created.
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    private Shell() throws IOException {
        this.terminal = TerminalBuilder.terminal();
        this.writer = this.terminal.writer();
        this.reader = LineReaderBuilder
            .builder()
            .terminal(this.terminal)
            .completer(new StringsCompleter("sources", "hello"))
            .parser(new DefaultParser())
            .build();
        this.folder = URI.create(
            String.format(
                "file://%s/src/test/resources/tables",
                System.getProperty("user.dir")
            )
        );
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
            case "sources":
                this.pointToSources(param);
                break;
            case "decide":
                this.decideFor(param);
                break;
            default:
                break;
        }
    }

    /**
     * Method that resolves the decision table in a specific Computation context.
     *
     * @param table The name of the table to resolve.
     */
    private void decideFor(final String table) {
        this.writer.printf(
            "Let's decide for a table '%s'%nfrom %s%n",
            table,
            this.folder.toString()
        );
    }

    /**
     * Import decision tables from a folder in file system.
     *
     * @param path The path to decision tables.
     */
    private void pointToSources(final String path) {
        this.folder = URI.create(String.format("file://%s", path.replace("'", "")));
        final Set<String> tables = this.extractFilenamesFromPath();
        this.reader = LineReaderBuilder
            .builder()
            .terminal(this.terminal)
            .completer(new StringsCompleter(completionOptionsFor(tables)))
            .build();
        this.writer.printf("Let's import tables from %s%n", path);
    }

    /**
     * Returns a complete set of all the table names, read from the user-specified folder.
     *
     * @return Set of Strings, representing decision table names.
     */
    private Set<String> extractFilenamesFromPath() {
        return new PlainTextContentReader(this.folder, ".csv", ";").allTables().keySet();
    }

    /**
     * Computes a list of all autocompletion options base on a predefined set of available commands
     * and a list of tables in a user-specified folder.
     *
     * @param tables List of table names.
     * @return A collection of string elements, each of which is the autocomplete option.
     */
    private static List<String> completionOptionsFor(final Set<String> tables) {
        final List<String> result = new ArrayList<>(tables);
        result.addAll(Shell.COMMANDS);
        return result;
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
