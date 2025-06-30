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

/**
 * I am a simple publisher for the output data. I provide functionality to store the output data
 * while computing something and retrieve it later.
 *
 * @param <T> The type of the output data to track.
 * @since 0.7.2
 */
public final class OutputPublisher<T> {
    /**
     * The collection of all the subscribed trackers.
     */
    private final List<OutputTracker<T>> trackers = new ArrayList<>(1);

    /**
     * Stores the output data (event) in all the subscribed trackers.
     *
     * @param data The output data to store.
     */
    public void track(final T data) {
        this.trackers.forEach(tracker -> tracker.add(data));
    }

    /**
     * Creates a new tracker and subscribes it to the publisher.
     *
     * @return The new {@link OutputTracker} instance.
     */
    public OutputTracker<T> createTracker() {
        final OutputTracker<T> tracker = new OutputTracker<>();
        this.trackers.add(tracker);
        return tracker;
    }

}
