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

package ru.ewc.decisions.input;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * I am the test for {@link SourceLines}.
 *
 * @since 0.8.0
 */
final class SourceLinesTest {

    @ValueSource(strings = {"OUT;outcome", "EXE;command", "ASG:assign", "---", " ", ""})
    @ParameterizedTest(name = "{index}: Line \"{0}\" is not a condition")
    void whenThereAreNoConditionsShouldReturnEmptyArray(final String input) {
        MatcherAssert.assertThat(
            "Should return empty array of Conditions when there are no CND lines in the file",
            sourceLinesFor(input).lines().get("CND"),
            Matchers.emptyCollectionOf(String.class)
        );
    }

    @ValueSource(strings = {"CND;condition", "EXE;command", "ASG:assign", "---", " ", ""})
    @ParameterizedTest(name = "{index}: Line \"{0}\" is not an outcome")
    void whenThereAreNoOutcomesShouldReturnEmptyArray(final String input) {
        MatcherAssert.assertThat(
            "Should return empty array of Outcomes when there are no OUT lines in the file",
            sourceLinesFor(input).lines().get("OUT"),
            Matchers.emptyCollectionOf(String.class)
        );
    }

    @ValueSource(strings = {"CND;condition", "EXE;command", "OUT:outcome", "---", " ", ""})
    @ParameterizedTest(name = "{index}: Line \"{0}\" is not an assignment")
    void whereThereAreNoAssignmentsShouldReturnEmptyArray(final String input) {
        MatcherAssert.assertThat(
            "Should return empty array of Assignments when there are no ASG lines in the file",
            sourceLinesFor(input).lines().get("ASG"),
            Matchers.emptyCollectionOf(String.class)
        );
    }

    @Test
    void whenThereIsAConditionShouldReturnFilledArray() {
        MatcherAssert.assertThat(
            "",
            sourceLinesFor("CND;hello").lines().get("CND"),
            Matchers.contains("hello")
        );
    }

    @Test
    void whenThereIsAnOutcomeShouldReturnFilledArray() {
        MatcherAssert.assertThat(
            "",
            sourceLinesFor("OUT;hello").lines().get("OUT"),
            Matchers.contains("hello")
        );
    }

    @Test
    void whenThereIsAnAssignmentShouldReturnFilledArray() {
        MatcherAssert.assertThat(
            "",
            sourceLinesFor("ASG;hello").lines().get("ASG"),
            Matchers.contains("hello")
        );
    }

    private static SourceLines sourceLinesFor(final String input) {
        return SourceLines.fromLinesWithDelimiter(Collections.singletonList(input), ";");
    }
}
