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

import java.util.Map;
import ru.ewc.decisions.core.BaseLocators;
import ru.ewc.decisions.core.InMemoryLocator;

/**
 * I am a Locator that represents the incoming data (i.e. in a form of incoming request).
 *
 * @since 0.6.2
 */
public class RequestLocator extends BaseLocators {
    /**
     * The empty {@link RequestLocator}.
      */
    public static final RequestLocator EMPTY = new RequestLocator(InMemoryLocator.empty());

    /**
     * Ctor.
     *
     * @param locator The {@link Locator} to be managed by this instance.
     */
    public RequestLocator(final Locator locator) {
        super(Map.of("request", locator));
    }
}
