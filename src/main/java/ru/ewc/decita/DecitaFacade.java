/*
 * MIT License
 *
 * Copyright (c) 2023-2023 Eugene Terekhov
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

/**
 * Main class for the outside users.
 *
 * @since 0.1
 */
public final class DecitaFacade {
    /**
     * Temporary field to make Qulice checks pass.
     */
    private final Map<String, String> content = Map.of("outcome", "true");

    // @todo #1 Return something more real from the method. It seems that the evaluation
    // method should use some kind of computation context to store all the intermediate
    // results, links to objects that retrieve data and so on.
    /**
     * Main method for evaluating the Decision Table.
     *
     * @param table Name of the table to evaluate.
     * @return A dictionary of all the outcomes and their values.
     */
    public Map<String, String> evaluateTable(final String table) {
        return this.content;
    }
}
