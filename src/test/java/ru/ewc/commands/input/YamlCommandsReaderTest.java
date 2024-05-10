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
import org.junit.jupiter.api.BeforeEach;
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
    /**
     * The reader that reads all the commands descriptions.
     */
    private YamlCommandsReader reader;

    @BeforeEach
    void setUp() {
        this.reader = new YamlCommandsReader(commandsDirectory());
    }

    @Test
    void shouldRead() {
        final SimpleCommand command = this.command("set-table-name");
        final ComputationContext context = command.perform(TestObjects.defaultContext());
        MatcherAssert.assertThat(
            "Table name should be set according to description",
            tableName(context),
            Matchers.equalTo("Machi-Koro")
        );
    }

    @Test
    void shouldPerformSeveralOperations() {
        final SimpleCommand command = this.command("see-the-flop");
        final ComputationContext context = command.perform(TestObjects.defaultContext());
        MatcherAssert.assertThat(
            "Table name should be set according to description",
            tableName(context),
            Matchers.equalTo("Texas Hold'em")
        );
        MatcherAssert.assertThat(
            "Table cards should be set",
            tableValueFor("flop", context),
            Matchers.equalTo("2c 3d 4h")
        );
    }

    @Test
    void shouldReadSeveralCommands() {
        ComputationContext context = this.command("set-table-name")
            .perform(TestObjects.defaultContext());
        context = this.command("set-max-players").perform(context);
        MatcherAssert.assertThat(
            "Table name should be set according to description",
            tableName(context),
            Matchers.equalTo("Machi-Koro")
        );
        final String fragment = "max-players";
        MatcherAssert.assertThat(
            "Max players should be set according to description",
            tableValueFor(fragment, context),
            Matchers.equalTo("5")
        );
    }

    @Test
    void shouldMakeTictactoeMove() {
        final ComputationContext initial = ticTacToeInitialContext();
        final ComputationContext context = this.command("make-a-move").perform(initial);
        MatcherAssert.assertThat(
            "Cell A1 should contain X",
            context.valueFor("cells", "A1"),
            Matchers.equalTo("X")
        );
        MatcherAssert.assertThat(
            "Current player should be O",
            tableValueFor("currentPlayer", context),
            Matchers.equalTo("O")
        );
        MatcherAssert.assertThat(
            "Next player should be X",
            tableValueFor("nextPlayer", context),
            Matchers.equalTo("X")
        );
    }

    private static ComputationContext ticTacToeInitialContext() {
        return TestObjects.defaultContext()
            .extendWithEmpty("table")
            .extendWithEmpty("request")
            .setValueFor("table", "nextPlayer", "O")
            .setValueFor("table", "currentPlayer", "X")
            .setValueFor("request", "move", "A1")
            .setValueFor("request", "player", "X");
    }

    private static String tableName(final ComputationContext context) {
        return tableValueFor("name", context);
    }

    private SimpleCommand command(final String command) {
        return this.reader.commands().get(command);
    }

    private static URI commandsDirectory() {
        return Path.of(
            Paths.get("").toAbsolutePath().toString(),
            "src/test/resources/commands"
        ).toUri();
    }

    private static String tableValueFor(final String fragment, final ComputationContext context) {
        return context.valueFor("table", fragment);
    }

}
