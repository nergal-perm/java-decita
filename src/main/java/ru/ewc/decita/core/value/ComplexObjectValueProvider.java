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

package ru.ewc.decita.core.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides access to named value fragments from a complex object.
 * @since 0.10.0
 */
public final class ComplexObjectValueProvider {
    /**
     * Fragments map.
     */
    private final Map<String, ConstantValueProvider<?>> fragments;

    /**
     * Ctor for a single fragment.
     * @param name Fragment name
     * @param value Value provider
     */
    public ComplexObjectValueProvider(final String name, final ConstantValueProvider<?> value) {
        this.fragments = new HashMap<>();
        this.fragments.put(
            Objects.requireNonNull(name, "Fragment name must not be null"),
            Objects.requireNonNull(value, "Value provider must not be null")
        );
    }

    /**
     * Get a fragment by name.
     * @param name Fragment name
     * @return Value provider or null if not found
     */
    public ConstantValueProvider<?> get(final String name) {
        return this.fragments.get(name);
    }
}
