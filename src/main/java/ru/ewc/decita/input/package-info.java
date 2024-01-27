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

/**
 * All the input mechanisms reside here.
 *
 * @since 0.2
 */
package ru.ewc.decita.input;
// @todo #23 File system Decision Tables should be prototypes for every computation.
// It means that computing something once doesn't change the state of all the prototypes and the
// next computation will use clean, uncomputed Coordinates.

// @todo #23 Implement TableSources class to handle the implicit locator names.
// My idea is to allow short notation for coordinates in tables, like just "42" instead of
// "constant::42" for constants (omitting the "constant" locator name) or "is_expired" instead of
// "is_expired::outcome" for conditions (omitting the outcome name for the table with a single
// outcome). This can be made possible with the help of the intermediary object holding all the
// RawContents (thus knowing all the table names) and able to derive FQN for coordinates from the
// short notation.
