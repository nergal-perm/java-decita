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

package ru.ewc.state;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.api.RequestLocator;

/**
 * I am a factory for the {@link Locator}s. I am used to create request-specific set of Locators
 * pointing to fragments of stored data.
 *
 * @since 0.7.0
 */
public class StoredStateFactory {
    /**
     * The factories to create {@link Locator}s.
     */
    private final Map<String, Function<RequestLocator, Locator>> factories;

    /**
     * Ctor.
     *
     * @param state The {@link StoredState} to create {@link Locator}s from.
     */
    public StoredStateFactory(final StoredState state) {
        this.factories = state.locators().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> r -> state.locatorFor(e.getKey())
                )
            );
    }

    /**
     * Makes a request-specific {@link Locator} for the specified name.
     *
     * @param name Name of the Locator to create.
     * @param request The incoming request data.
     * @return The {@link Locator} for the specified name.
     */
    public Locator locatorFor(final String name, final RequestLocator request) {
        return this.factories.get(name).apply(request);
    }
}
