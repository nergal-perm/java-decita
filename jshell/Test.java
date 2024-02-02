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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.hamcrest.Matchers;
import ru.ewc.decita.input.PlainTextContentReader;
import ru.ewc.decita.ComputationContext;
import ru.ewc.decita.Locator;
import ru.ewc.decita.ConstantLocator;
import ru.ewc.decita.InMemoryStorage;
import ru.ewc.decita.DecitaException;

// @todo #61 Add method to set the tables source directory
URI dir = URI.create("file://" + System.getProperty("user.dir") + "/src/test/resources/tables");

// @todo #61 Add method to load computational state from yaml
// That is, read the structured yaml file (1st level - Locator name, 2nd level - field names) as
// the Locators for the ComputationContext. Move those initializations inside that method.
Map<String, String> storage = new HashMap<>(Map.of("is-stored", "true"));
Map<String, Map<String, String>> dataLocators = new HashMap<>(Map.of("data", storage));

void decideFor(String tableName) throws DecitaException {
    final ComputationContext context = new ComputationContext(
        new PlainTextContentReader(dir, ".csv", ";").allTables(),
        // @todo #61 Initialize ComputationContext with the computation state read from yaml
        Map.of(
            Locator.CONSTANT_VALUES, new ConstantLocator(),
            "data", new InMemoryStorage(storage))
    );
    final Map<String, String> actual = context.decisionFor(tableName);
    System.out.printf("%n--== %s ==--%n", tableName.toUpperCase());
    actual.entrySet().forEach(e -> {
        System.out.printf("%s : %s%n",e.getKey(),e.getValue());
    });
}
