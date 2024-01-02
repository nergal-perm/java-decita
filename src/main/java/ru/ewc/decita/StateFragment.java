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

import lombok.EqualsAndHashCode;

/**
 * I am a fragment of the system state. My responsibility is to hold a single primitive value of a
 * single state property. That value will eventually be used in comparisons, i.e. Conditions
 * computations.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public class StateFragment {
    /**
     * Inner representation of a given value.
     */
    private final String value;

    /**
     * Ctor.
     *
     * @param value String representation of requested value.
     */
    public StateFragment(final String value) {
        this.value = value;
    }

    /**
     * Determines whether the {@link StateFragment}'s value can be represented as {@code true}.
     *
     * @return True - if the {@link StateFragment}'s value is equal to "true".
     */
    public boolean asBoolean() {
        return "true".equalsIgnoreCase(this.value);
    }

    /**
     * Returns its value literally.
     *
     * @return The value of this {@link StateFragment}.
     */
    public String asString() {
        return this.value;
    }
}
