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

package ru.ewc.decisions.api;

import java.util.Collections;
import java.util.Map;
import ru.ewc.decisions.core.Coordinate;
import ru.ewc.decisions.core.Rule;

/**
 * I am the Locator service. My main responsibility is to find requested Fragment of the application
 * state.
 *
 * @since 0.1
 */
public interface Locator {

    /**
     * Name for computed values {@link Locator}.
     */
    String CONSTANT_VALUES = "constant";

    /**
     * Determines the system's state - the value of a single property, described by its name.
     *
     * @param fragment The String identifier of the required property.
     * @param context The {@link ComputationContext} to use in property retrieval.
     * @return The value of the requested property as a {@code String}.
     * @throws DecitaException When the requested {@link Coordinate} cannot be found.
     */
    String fragmentBy(String fragment, ComputationContext context) throws DecitaException;

    /**
     * Computes this table's outcomes by checking all of its {@link Rule}s.
     *
     * @param context The specific {@link ComputationContext} to make a decision in.
     * @return The simple dictionary of the table's outcomes.
     * @throws DecitaException If any of the {@link Rule}s cannot be checked.
     */
    default Map<String, String> outcome(final ComputationContext context) throws DecitaException {
        return Collections.singletonMap("outcome", "undefined");
    }
}
