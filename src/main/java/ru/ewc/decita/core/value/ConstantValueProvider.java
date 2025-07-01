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

import java.util.Objects;
import ru.ewc.decisions.api.ComputationContext;

/**
 * A simple implementation of {@link ValueProvider} that always returns a constant value.
 *
 * @param <T> The type of the constant value provided.
 * @since 0.10.0
 */
public final class ConstantValueProvider<T> implements ValueProvider<T> {
    /**
     * The constant value to be provided.
     */
    private final T value;

    /**
     * Constructs a new {@link ConstantValueProvider} with the specified constant value.
     *
     * @param value The constant value to be provided.
     */
    public ConstantValueProvider(final T value) {
        this.value = Objects.requireNonNull(value, "Constant value cannot be null");
    }

    @Override
    public T valueFrom(final ComputationContext context) {
        return this.value;
    }
}

