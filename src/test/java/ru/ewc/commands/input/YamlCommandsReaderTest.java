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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.ewc.commands.SimpleCommand;
import ru.ewc.decisions.TestObjects;
import ru.ewc.decisions.api.ComputationContext;

/**
 * Tests for {@link YamlCommandsReader}.
 *
 * @since 0.5.0
 */
final class YamlCommandsReaderTest {
    @Test
    void shouldRead() {
        final YamlCommandsReader reader = new YamlCommandsReader(commandsDirectory());
        final SimpleCommand command = reader.read().get(0);
        final ComputationContext context = command.perform(TestObjects.defaultContext());
        MatcherAssert.assertThat(
            "Table name should be set according to description",
            context.valueFor("table", "name"),
            Matchers.equalTo("Machi-Koro")
        );
    }

    private static URI commandsDirectory() {
        return Path.of(
            Paths.get("").toAbsolutePath().toString(),
            "src/test/resources/commands"
        ).toUri();
    }
}
