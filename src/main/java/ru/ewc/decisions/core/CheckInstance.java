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

package ru.ewc.decisions.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.MultipleOutcomes;
import ru.ewc.decisions.api.OutputTracker;

/**
 * I am a collection of {@link Rule}s to check.
 *
 * @since 0.8.0
 */
public final class CheckInstance implements MultipleOutcomes {
    /**
     * The collection of {@link Rule}s to check.
     */
    private final List<Rule> rules;

    public CheckInstance(final List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public Map<String, List<String>> testResult(final ComputationContext context)
        throws DecitaException {
        return this.rules.stream().collect(
            Collectors.toMap(
                Rule::asString,
                rule -> CheckInstance.performAndLog(context, rule)
            )
        );
    }

    private static List<String> performAndLog(final ComputationContext context, final Rule rule) {
        logCheckpoint(context, "%s - started".formatted(rule.asString()));
        final List<String> result = rule.test(context);
        logCheckpoint(context, "%s - %s".formatted(rule.asString(), CheckInstance.desc(result)));
        return result;
    }

    private static String desc(final List<String> messages) {
        final String result;
        if (messages.isEmpty()) {
            result = "PASSED";
        } else {
            result = "FAILED";
        }
        return result;
    }

    private static void logCheckpoint(final ComputationContext context, final String formatted) {
        context.logComputation(OutputTracker.EventType.CH, formatted);
    }
}
