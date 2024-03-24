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

package ru.ewc.decita;

import java.util.HashMap;
import java.util.Map;

/**
 * I am the collection of {@link Locator}s used to populate the {@link ComputationContext}s. My main
 * responsibility is to keep track of the {@link Locator}s, merge them together and provide the
 * merged result.
 *
 * @since 0.3.1
 */
public final class Locators {
    /**
     * The constant {@link Locator} that can be used in the {@link ComputationContext}.
     */
    public static final Locators CONSTANT = new Locators(
        Map.of(Locator.CONSTANT_VALUES, new ConstantLocator())
    );

    /**
     * The {@link Locator}s to be managed by this instance.
     */
    private final Map<String, Locator> collection;

    /**
     * Ctor.
     *
     * @param collection The {@link Locator}s to be managed by this instance.
     */
    public Locators(final Map<String, Locator> collection) {
        this.collection = collection;
    }

    /**
     * Returns a concrete {@link Locator} if it's found in an instance storage.
     *
     * @param locator The String identifier of the required {@link Locator}.
     * @return The instance of {@link Locator}.
     * @throws DecitaException If the specified {@link Locator} is missing.
     */
    public Locator locatorFor(final String locator) throws DecitaException {
        if (!this.collection.containsKey(locator)) {
            throw new DecitaException(
                String.format("Locator '%s' not found in computation context", locator)
            );
        }
        return this.collection.get(locator);
    }

    public Locators mergedWith(final Locators additional) {
        final Map<String, Locator> merged = new HashMap<>(this.collection);
        merged.putAll(additional.collection);
        return new Locators(merged);
    }
}
