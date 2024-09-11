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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import ru.ewc.decisions.input.SourceLines;

/**
 * I am a collection of {@link RuleFragment}s. My main responsibility is to provide convenient
 * methods to access those fragments while leaving some domain-specific logic to the subclasses.
 *
 * @since 0.9.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class RuleFragments {
    /**
     * The collection of {@link RuleFragment}s.
     */
    private final List<RuleFragment> fragments;

    public RuleFragments(final List<RuleFragment> fragments) {
        this.fragments = fragments;
    }

    public static RuleFragments listFrom(final SourceLines lines, final int column) {
        final List<RuleFragment> list =
            StreamSupport
                .stream(lines.spliterator(), false)
                .filter(line -> line.length > column && !line[column].trim().isEmpty())
                .map(line -> new RuleFragment(line[0].trim(), line[1].trim(), line[column].trim()))
                .collect(Collectors.toList());
        if (list.stream().noneMatch(rf -> rf.nonEmptyOfType("HDR"))) {
            list.add(new RuleFragment("HDR", lines.fileName(), "rule_%02d".formatted(column - 1)));
        }
        return new RuleFragments(list);
    }

    public String header() {
        final RuleFragment header =
            this.fragments.stream()
                .filter(f -> f.nonEmptyOfType("HDR"))
                .findFirst()
                .orElse(new RuleFragment("HDR", "missing_title", "rule"));
        return "%s::%s".formatted(header.left(), header.right());
    }

    public List<RuleFragment> getFragments() {
        return this.fragments;
    }
}
