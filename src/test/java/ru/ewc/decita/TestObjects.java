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

package ru.ewc.decita;

import java.util.ArrayList;
import java.util.List;

/**
 * I hold different preconfigured objects for unit-tests.
 *
 * @since 0.1
 */
public final class TestObjects {
    /**
     * Ctor.
     */
    private TestObjects() {
        // empty
    }

    /**
     * The {@link ComputationContext} with some predefined {@link Locator}s.
     *
     * @return A prefilled {@link ComputationContext}.
     */
    static ComputationContext computationContext() {
        final ComputationContext target = new ComputationContext();
        new ConstantLocator().registerWith(target);
        conditionsLocator().registerWith(target);
        return target;
    }

    /**
     * The {@link Condition} that compares a constant to itself.
     *
     * @return A simple and always true {@link Condition}.
     */
    static SingleCondition alwaysTrueConstantCondition() {
        return new SingleCondition(
            valueTrue(), "=", valueTrue()
        );
    }

    /**
     * Convenience method to get a new instance of {@link Coordinate} with value {@code true}.
     *
     * @return The concrete value {@link Coordinate}.
     */
    static Coordinate valueTrue() {
        return new Coordinate(Locator.CONSTANT_VALUES, "true");
    }

    /**
     * Convenience method to get a new instance of {@link Coordinate} with value {@code false}.
     *
     * @return The concrete value {@link Coordinate}.
     */
    static Coordinate valueFalse() {
        return new Coordinate(Locator.CONSTANT_VALUES, "false");
    }

    /**
     * Convenience method to get an instance of {@link Coordinate} pointing to the {@link Condition}
     * that always resolves to {@code true}.
     *
     * @return A {@link Coordinate} of the always true {@link Condition}.
     */
    static Coordinate alwaysTrueConditionCoordinate() {
        return new Coordinate(Locator.CONDITIONS, "always_true");
    }

    /**
     * Convenience method to get an instance of {@link Rule} with the {@link Condition} that
     * always resolves to {@code true}.
     *
     * @return Always succeeding {@link Rule} instance.
     */
    static Rule alwaysTrueEqualsTrueRule() {
        return new Rule()
            .withCondition(new SingleCondition(alwaysTrueConditionCoordinate(), "=", valueTrue()))
            .withOutcome("outcome", "Hello");
    }

    /**
     * Convenience method to get an instance of {@link Rule} with the {@link Condition} that
     * always resolves to {@code false}.
     *
     * @return Always failing {@link Rule} instance.
     */
    static Rule alwaysTrueEqualsFalseRule() {
        return new Rule()
            .withCondition(new SingleCondition(alwaysTrueConditionCoordinate(), "=", valueFalse()))
            .withOutcome("outcome", "World");
    }

    /**
     * Convenience method to get a simple {@link Rule} list that can be used to populate a
     * {@link DecisionTable}.
     *
     * @return A list of {@link Rule}s.
     */
    static List<Rule> rulesList() {
        final List<Rule> rules = new ArrayList<>(2);
        rules.add(alwaysTrueEqualsTrueRule());
        rules.add(alwaysTrueEqualsFalseRule());
        return rules;
    }

    /**
     * Convenience method to get a new instance of {@link ConditionsLocator}.
     *
     * @return Prefilled instance of {@link ConditionsLocator}.
     */
    private static Locator conditionsLocator() {
        return new ConditionsLocator()
            .with("always_true", alwaysTrueConstantCondition());
    }
}
