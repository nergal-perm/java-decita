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

import java.util.HashMap;
import java.util.Map;

/**
 * I am the collection of {@link Locator}s used to populate the {@link ComputationContext}s. My main
 * responsibility is to keep track of the {@link Locator}s, merge them together and provide the
 * merged result.
 *
 * @since 0.3.1
 */
// @todo #115 Make Locators abstract in order to extend it with the custom Locators
public class BaseLocators {

    /**
     * The empty set of predefined {@link Locator}s that should be used to get data from a system.
     */
    public static final BaseLocators EMPTY = new BaseLocators(Map.of());

    /**
     * The {@link Locator}s to be managed by this instance.
     */
    private final Map<String, Locator> collection;

    /**
     * Ctor.
     *
     * @param collection The {@link Locator}s to be managed by this instance.
     */
    public BaseLocators(final Map<String, Locator> collection) {
        this.collection = collection;
    }

    /**
     * Returns a concrete {@link Locator} if it's found in an instance storage.
     *
     * @param locator The String identifier of the required {@link Locator}.
     * @return The instance of {@link Locator}.
     * @throws DecitaException If the specified {@link Locator} is missing.
     */
    public final Locator locatorFor(final String locator) throws DecitaException {
        if (!this.collection.containsKey(locator)) {
            throw new DecitaException(
                String.format("Locator '%s' not found in computation context", locator)
            );
        }
        return this.collection.get(locator);
    }

    public final ComputationContext mergedWith(final BaseLocators... additional) {
        BaseLocators merged = this;
        for (final BaseLocators locators : additional) {
            merged = merged.mergedWith(locators);
        }
        return new ComputationContext(merged);
    }

    public final boolean hasLocator(final String locator) {
        return this.collection.containsKey(locator);
    }

    /**
     * Obtains the locators state. Will be used by library's clients for debugging and testing
     * purposes.
     *
     * @return A copy of all the Locators states.
     */
    public Map<String, Map<String, Object>> state() {
        final Map<String, Map<String, Object>> state = new HashMap<>(this.collection.size());
        for (final Map.Entry<String, Locator> entry : this.collection.entrySet()) {
            if (entry.getValue().state().isEmpty()) {
                continue;
            }
            state.put(entry.getKey(), entry.getValue().state());
        }
        return state;
    }

    private BaseLocators mergedWith(final BaseLocators additional) {
        final Map<String, Locator> merged = new HashMap<>(this.collection);
        merged.putAll(additional.collection);
        return new BaseLocators(merged);
    }
}
