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

import java.util.Map;
import java.util.function.Supplier;

/**
 * I am the facade for the Decita library. My main responsibility is to provide a single entry point
 * for the library users.
 *
 * @since 0.3.0
 */
public final class DecitaFacade {
    /**
     * The constant {@link Locator} that provides the constant values.
     */
    public static final Map<String, Locator> CONSTANT_LOCATOR =
        Map.of(Locator.CONSTANT_VALUES, new ConstantLocator());

    /**
     * The function that provides a fresh set of uncomputed decision tables.
     */
    private final Supplier<Map<String, Locator>> tables;

    /**
     * Ctor.
     *
     * @param tables The function that provides a fresh set of uncomputed decision tables.
     */
    public DecitaFacade(final Supplier<Map<String, Locator>> tables) {
        this.tables = tables;
    }
    // @todo #96 Wrap Map<String, Locator> into a dedicated class responsible for merging Locators

    /**
     * Creates a new {@link ComputationContext} with the provided {@link Locator}s.
     *
     * @param locators A single dictionary of additional locators, or an array of such dictionaries.
     * @return A new {@link ComputationContext} with the provided {@link Locator}s.
     */
    public ComputationContext contextExtendedWith(final Map<String, Locator> locators) {
        return new ComputationContext(this.tables.get(), DecitaFacade.CONSTANT_LOCATOR)
            .extendedWith(locators);
    }
}
