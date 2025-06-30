/*
 * MIT License
 *
 * Copyright (c) 2024-2025 Eugene Terekhov
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

import java.util.HashMap;
import java.util.Map;

/**
 * I am a simple in-memory key-value storage. My main responsibility is to store and return the
 * specified data for testing purposes.
 *
 * @since 0.2
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class InMemoryLocator implements Locator {
    /**
     * Simple key-value storage.
     */
    private final Map<String, Object> storage;

    /**
     * The name of the locator.
     */
    private final String name;

    /**
     * Ctor.
     *
     * @param name The name of the locator.
     * @param storage The pre-filled key-value storage to start with.
     */
    public InMemoryLocator(final String name, final Map<String, Object> storage) {
        this.storage = new HashMap<>(storage);
        this.name = name;
    }

    /**
     * I am a simple method that creates an empty {@link InMemoryLocator}.
     *
     * @param name The name of the locator.
     * @return An instance of {@link InMemoryLocator} with empty storage.
     */
    public static InMemoryLocator empty(final String name) {
        return new InMemoryLocator(name, new HashMap<>());
    }

    @Override
    public String fragmentBy(final String fragment, final ComputationContext context) {
        return this.storage.getOrDefault(fragment, "undefined").toString();
    }

    @Override
    public void setFragmentValue(final String fragment, final String value) {
        this.storage.put(fragment, value);
    }

    @Override
    public Map<String, Object> state() {
        return new HashMap<>(this.storage);
    }

    @Override
    public String locatorName() {
        return this.name;
    }

    public void reset() {
        this.storage.clear();
    }
}
