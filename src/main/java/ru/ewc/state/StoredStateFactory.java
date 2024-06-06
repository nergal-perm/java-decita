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

import java.util.HashMap;
import java.util.Map;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.api.RequestLocator;
import ru.ewc.decisions.core.ConstantLocator;

/**
 * I am a factory for the {@link Locator}s. I am used to create request-specific set of Locators
 * pointing to fragments of stored data.
 *
 * @since 0.7.0
 */
public class StoredStateFactory {
    /**
     * The always available Locator that returns constant values.
     */
    private static final ConstantLocator CONSTANT_LOCATOR = new ConstantLocator();

    /**
     * The {@link Locator}s to be managed by this instance.
     */
    private final Map<String, Locator> locators;

    /**
     * Ctor.
     *
     * @param state The {@link State} to create {@link Locator}s from.
     * @param req The incoming request data.
     */
    // @todo #127 Pass the StateFactories to the StoredStateFactory from outside
    public StoredStateFactory(final State state, final RequestLocator req) {
        this.locators = StoredStateFactory.locatorsFrom(state, req);
    }

    /**
     * Makes a request-specific {@link Locator} for the specified name.
     *
     * @param name Name of the Locator to create.
     * @return The {@link Locator} for the specified name.
     */
    public Locator locatorFor(final String name) {
        return this.locators.get(name);
    }

    private static Map<String, Locator> locatorsFrom(final State state, final RequestLocator req) {
        final Map<String, Locator> locators = new HashMap<>(state.locators());
        locators.put("constant", StoredStateFactory.CONSTANT_LOCATOR);
        locators.put("request", req.locatorFor("request"));
        return locators;
    }
}
