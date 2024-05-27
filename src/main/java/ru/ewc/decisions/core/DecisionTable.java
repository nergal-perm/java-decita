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

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecitaException;
import ru.ewc.decisions.api.Locator;

/**
 * I am a collection of {@link Rule}s used to compute any kind of decision. My main responsibility
 * is to orchestrate the computation and choose the only satisfied {@link Rule}.
 *
 * @since 0.1
 */
public final class DecisionTable implements Locator {
    /**
     * A collection of table's {@link Rule}s.
     */
    private final Iterable<Rule> rules;

    /**
     * The special {@link Rule} that gets satisfied only if no other {@link Rule} is satisfied.
     * Basically it means that for every table there's always one satisfied {@link Rule}.
     */
    private final Rule elserule;

    /**
     * The name of the table.
     */
    private final String name;

    /**
     * Ctor.
     *
     * @param rules A collection of {@link Rule}s for this table.
     * @param name The name of the table.
     */
    public DecisionTable(final Iterable<Rule> rules, final String name) {
        this(rules, new Rule().withOutcome("outcome", "undefined"), name);
    }

    /**
     * Ctor.
     *
     * @param rules A collection of {@link Rule}s for this table.
     * @param elserule The special {@link Rule} that gets satisfied only if no other {@link Rule} is
     *  satisfied.
     * @param name The name of the table.
     */
    public DecisionTable(final Iterable<Rule> rules, final Rule elserule, final String name) {
        this.rules = rules;
        this.elserule = elserule;
        this.name = name;
    }

    public String tableName() {
        return this.name;
    }

    @Override
    public String fragmentBy(final String fragment, final ComputationContext context)
        throws DecitaException {
        return this.outcome(context).getOrDefault(fragment, "undefined");
    }

    @Override
    public void setFragmentValue(final String fragment, final String value) {
        // no-op method, nothing changes.
    }

    @Override
    public Map<String, String> outcome(final ComputationContext context) throws DecitaException {
        for (final Rule rule : this.rules) {
            rule.check(context);
        }
        if (this.satisfiedRules().count() > 1) {
            throw new DecitaException("Multiple rules are satisfied");
        }
        return this.satisfiedRules()
            .findFirst().orElse(this.elserule)
            .outcome();
    }

    /**
     * Converts the collection of {@link Rule}s into a {@link Stream}.
     *
     * @return A {@link Stream} of {@link Rule}s.
     */
    private Stream<Rule> satisfiedRules() {
        return StreamSupport.stream(this.rules.spliterator(), false).filter(Rule::isSatisfied);
    }

}
