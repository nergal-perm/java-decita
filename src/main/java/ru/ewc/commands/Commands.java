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

package ru.ewc.commands;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.ewc.commands.input.YamlCommandsReader;
import ru.ewc.decisions.api.ComputationContext;

/**
 * I am a facade for the commands that are written in the YAML format.
 *
 * @since 0.5.1
 */
public class Commands {
    /**
     * The command registry that stores the commands.
     */
    private final Map<String, SimpleCommand> registry;

    /**
     * Ctor.
     *
     * @param folder The path to the folder with the commands descriptions.
     */
    public Commands(final URI folder) {
        this(new YamlCommandsReader(folder).commands());
    }

    public Commands(final Map<String, SimpleCommand> registry) {
        this.registry = registry;
    }

    /**
     * I am a method that performs the command.
     *
     * @param command The command to perform.
     * @param context Current context of the computation, containing the state of the system, the
     *  decision tables and the incoming data (i.e. the incoming user request).
     */
    public void perform(final String command, final ComputationContext context) {
        this.registry.get(command).performIn(context);
    }

    /**
     * I am a method that returns the names of the commands.
     *
     * @return The list of the command names.
     */
    public List<String> names() {
        return this.registry.keySet().stream().toList();
    }

    /**
     * I am a method that returns the unresolved parts of the commands.
     *
     * @return The map of the command names and their unresolved parts.
     */
    public Map<String, List<String>> args() {
        return this.registry.entrySet().stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().unresolvedParts()
            )
        );
    }
}
