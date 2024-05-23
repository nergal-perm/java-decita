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

package ru.ewc.checklogic.server;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * I am the class encapsulating the stored state of the application. My main responsibility is to
 * present that state in a human-readable form.
 *
 * @since 0.3.0
 */
public final class StoredState {
    /**
     * The state to be encapsulated.
     */
    private final Map<String, Map<String, Object>> state;

    /**
     * Ctor.
     *
     * @param state The state to be encapsulated.
     */
    public StoredState(final Map<String, Map<String, Object>> state) {
        this.state = state;
    }

    public String asHtmlList() {
        return "<ul>%s</ul>".formatted(this.state.entrySet().stream()
            .map(
                entry -> "<li><h4>%s</h4>%s</li>".formatted(
                    entry.getKey(),
                    StoredState.toInnerList(entry.getValue())
                )
            )
            .collect(Collectors.joining())
        );
    }

    private static String toInnerList(final Map<String, Object> value) {
        return "<ul>%s</ul>".formatted(value.entrySet().stream()
            .map(entry -> "<li>%s: %s</li>".formatted(entry.getKey(), entry.getValue()))
            .collect(Collectors.joining())
        );
    }
}
