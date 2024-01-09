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

/**
 * I hold different preconfigured objects for unit-tests.
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
     * @return A simple and always true {@link Condition}.
     */
    static SingleCondition alwaysTrueConstantCondition() {
        return new SingleCondition(
            valueTrue(), "=", valueTrue()
        );
    }

    /**
     * Convenience method to get a new instance of {@link Coordinate} with value {@code true}.
     * @return The concrete value {@link Locator}.
     */
    static Coordinate valueTrue() {
        return new Coordinate(Locator.CONSTANT_VALUES, "true");
    }

    /**
     * Convenience method to get a new instance of {@link ConditionsLocator}.
     * @return Prefilled instance of {@link ConditionsLocator}.
     */
    private static Locator conditionsLocator() {
        return new ConditionsLocator()
            .with("always_true", alwaysTrueConstantCondition());
    }
}
