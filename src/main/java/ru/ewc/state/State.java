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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.ewc.decisions.api.InMemoryLocator;
import ru.ewc.decisions.api.Locator;
import ru.ewc.decisions.core.BaseLocators;
import ru.ewc.decisions.core.ConstantLocator;

/**
 * I am a stored application state. My main responsibility is to provide access to the stored data
 * in order to make decisions or to change it via commands.
 *
 * @since 0.6.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class State extends BaseLocators {
    /**
     * The empty set of predefined {@link Locator}s that should be used to get data from a system.
     */
    public static final State EMPTY = new State(List.of());

    /**
     * Ctor.
     *
     * @param collection The collection of {@link Locator}s to be managed by this instance.
     */
    public State(final List<Locator> collection) {
        super(collection
            .stream()
            .collect(Collectors.toMap(Locator::locatorName, Function.identity()))
        );
    }

    public State extendedWithConstant() {
        if (!this.locators().containsKey("constant")) {
            this.locators().put("constant", new ConstantLocator());
        }
        return this;
    }

    public State emptyCopy() {
        return this.locators().keySet().stream()
            .filter(locator -> !"constant".equals(locator))
            .map(InMemoryLocator::empty)
            .collect(Collectors.collectingAndThen(Collectors.toList(), State::new));
    }
}
