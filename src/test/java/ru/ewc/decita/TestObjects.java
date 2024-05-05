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

package ru.ewc.decita;

import java.util.List;
import java.util.Map;
import ru.ewc.decita.api.ComputationContext;
import ru.ewc.decita.api.Locator;
import ru.ewc.decita.api.Locators;
import ru.ewc.decita.conditions.AlwaysTrueCondition;
import ru.ewc.decita.conditions.Condition;
import ru.ewc.decita.conditions.EqualsCondition;
import ru.ewc.decita.conditions.NotCondition;
import ru.ewc.decita.core.ConstantLocator;
import ru.ewc.decita.core.Coordinate;
import ru.ewc.decita.core.DecisionTable;
import ru.ewc.decita.core.Rule;

/**
 * I hold different preconfigured objects for unit-tests.
 *
 * @since 0.1
 */
@SuppressWarnings({"PMD.ProhibitPublicStaticMethods", "PMD.TestClassWithoutTestCases"})
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
    public static ComputationContext defaultContext() {
        return new ComputationContext(defaultLocators());
    }

    /**
     * The {@link Condition} that compares a constant to itself.
     *
     * @return A simple and always true {@link Condition}.
     */
    public static Condition alwaysTrueConstantCondition() {
        return new EqualsCondition(valueTrue(), valueTrue());
    }

    /**
     * Convenience method to get a new instance of {@link Coordinate} with value {@code true}.
     *
     * @return The concrete value {@link Coordinate}.
     */
    public static Coordinate valueTrue() {
        return new Coordinate(Locator.CONSTANT_VALUES, "true");
    }

    /**
     * Convenience method to get an instance of {@link Coordinate} pointing to the {@link Condition}
     * that always resolves to {@code true}.
     *
     * @return A {@link Coordinate} of the always true {@link Condition}.
     */
    public static Coordinate alwaysTrueConditionCoordinate() {
        return new Coordinate("always_true", "outcome");
    }

    /**
     * Convenience method to get an instance of {@link Rule} with the {@link Condition} that
     * always resolves to {@code true}.
     *
     * @return Always succeeding {@link Rule} instance.
     */
    static Rule alwaysTrueEqualsTrueRule() {
        return new Rule()
            .withCondition(new AlwaysTrueCondition())
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
            .withCondition(new NotCondition(new AlwaysTrueCondition()))
            .withOutcome("outcome", "World");
    }

    /**
     * A collection of default (required) {@link Locator}s.
     *
     * @return A collection of required {@link Locator}s.
     */
    private static Locators defaultLocators() {
        return new Locators(
            Map.of(
                "always_true", new DecisionTable(List.of(alwaysTrueEqualsTrueRule())),
                Locator.CONSTANT_VALUES, new ConstantLocator()
            )
        );
    }

}
