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

/**
 * I am a simple data structure describing the required {@link StateFragment}. My responsibility is
 * to provide everything that is needed to retrieve the single {@link StateFragment}. The position
 * of every {@link StateFragment} is described by the {@link Locator}'s ID and the
 * {@link StateFragment}'s ID, so it looks like a 2D coordinate.
 *
 * @since 0.1
 */
public final class Coordinate {
    /**
     * String identifier of the concrete {@link Locator} responsible for retrieving the
     * {@link StateFragment}.
     */
    private final String locator;

    /**
     * String identifier of the requested state property's value.
     */
    private final String fragment;

    /**
     * Ctor.
     *
     * @param locator The {@link Locator} identifier.
     * @param fragment The {@link StateFragment} identifier.
     */
    public Coordinate(final String locator, final String fragment) {
        this.locator = locator;
        this.fragment = fragment;
    }

    /**
     * Locates the required {@link StateFragment} in the provided {@link ComputationContext}.
     * @param context Provided {@link ComputationContext}.
     * @return Found {@link StateFragment}.
     * @throws DecitaException If the specified {@link Locator} is missing.
     */
    public StateFragment fragmentFrom(final ComputationContext context) throws DecitaException {
        return context.fragmentFor(this.locator, this.fragment);
    }
}
