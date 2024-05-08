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

package ru.ewc.commands.input;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.yaml.snakeyaml.Yaml;
import ru.ewc.commands.SimpleCommand;

/**
 * I am a reader for the commands that are written in the YAML format.
 *
 * @since 0.5.0
 */
public class YamlCommandsReader {
    /**
     * A folder where the command files are stored.
     */
    private final URI folder;

    /**
     * The YAML parser.
     */
    private final Yaml yaml;

    /**
     * Ctor.
     *
     * @param folder The folder where the command files are stored.
     */
    public YamlCommandsReader(final URI folder) {
        this.folder = folder;
        this.yaml = new Yaml();
    }

    /**
     * I read the commands from the files in the folder.
     *
     * @return The list of commands.
     */
    public List<SimpleCommand> read() {
        List<SimpleCommand> commands;
        try (Stream<Path> files = Files.walk(Paths.get(this.folder))) {
            commands = this.contentsOf(files);
        } catch (final IOException exception) {
            commands = Collections.emptyList();
        }
        return commands;
    }

    /**
     * This method is responsible for reading the contents of the files in the provided Stream of
     * Paths. It filters out the files that are not regular files and do not end with .yaml or .yml.
     * It then reads the content of each file and creates a SimpleCommand object from it.
     *
     * @param paths A Stream of Paths representing the files to be read.
     * @return A List of SimpleCommand objects created from the contents of the files.
     * @throws IOException If an I/O error occurs reading from the file.
     */
    private List<SimpleCommand> contentsOf(final Stream<Path> paths) throws IOException {
        final List<String> files = paths
            .filter(Files::isRegularFile)
            .filter(
                path -> {
                    final String name = path.getFileName().toString();
                    return name.endsWith(".yaml") || name.endsWith(".yml");
                })
            .map(Path::toString)
            .toList();
        final List<SimpleCommand> commands = new ArrayList<>(files.size());
        for (final String file : files) {
            commands.add(this.readContentFromFile(file));
        }
        return commands;
    }

    /**
     * This method is responsible for reading the content of a single file and creating a
     * SimpleCommand object from it. It first creates a Path object from the provided file string.
     * It then uses the yaml object to load the content of the file into a Map.
     * Finally, it creates a new SimpleCommand object using the "operation" value from the Map and
     * returns it.
     *
     * @param file A String representing the file to be read.
     * @return A SimpleCommand object created from the content of the file.
     * @throws IOException If an I/O error occurs reading from the file.
     */
    private SimpleCommand readContentFromFile(final String file) throws IOException {
        final Path path = Path.of(file);
        final Map<String, String> map = this.yaml.loadAs(Files.newInputStream(path), Map.class);
        return new SimpleCommand(map.get("operation"));
    }
}
