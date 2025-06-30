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

package ru.ewc.decisions.api;

import java.util.ArrayList;
import java.util.List;
import ru.ewc.decisions.core.DecisionTable;
import ru.ewc.decisions.core.Rule;

/**
 * I am a simple tracker for the events. I provide functionality to store events while computing
 * something and retrieve them later, making me a simple 'ad hoc' logger.
 *
 * @param <T> The type of the events to track.
 * @since 0.7.2
 */
public final class OutputTracker<T> {
    /**
     * The storage of all the tracked events and their associated data.
     */
    private final List<T> tracked = new ArrayList<>(5);

    public List<T> events() {
        return List.copyOf(this.tracked);
    }

    void add(final T event) {
        this.tracked.add(event);
    }

    public enum EventType {
        /**
         * The computation of the static (i.e. without resolvable parts) Coordinate.
         */
        ST,
        /**
         * The computation of the dynamic (i.e. with resolvable parts) Coordinate.
         */
        DN,
        /**
         * The computation of the {@link Rule}.
         */
        RL,
        /**
         * The computation of the {@link DecisionTable}.
         */
        TB,
        /**
         * The computation of the Check or Test.
         */
        CH,
        /**
         * The computation of the Condition.
         */
        CN
    }

}
