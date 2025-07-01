# 10. Handling Missing Fields in ComplexObjectValueProvider

Date: 2025-07-01

## Status

Accepted

Amends [ADR-0006](0006-valueprovider-design-for-remote-data-and-composability.md)

is a consequence of [5. Pure Functional Core](0005-pure-functional-core.md)

## Brief summary

We will handle requests for non-existent fields in a `ComplexObjectValueProvider` by returning a dedicated `UndefinedValueProvider`. This approach ensures that the provider's methods remain pure, avoid exceptions for control flow, and return a consistent, non-null type, thereby preventing `NullPointerException`s and aligning with the functional core principles.

## Context

The `ComplexObjectValueProvider` serves as a container for various data fragments, accessible via string keys. A critical design decision is how the provider should behave when a client requests a key that does not exist in its internal data map. This behavior is a core part of the provider's public contract and has significant implications for the purity, safety, and usability of the decision engine. The decision must align with the principles of a pure functional core, as established in [ADR-0005](0005-pure-functional-core.md).

## Options

### 1. Throw a Runtime Exception

-   **Summary:** Throw an `IllegalArgumentException` or a custom `FieldNotFoundException`.
-   **Pros:** Fails fast and makes the error condition explicit.
-   **Cons:** Introduces side effects (exceptions for control flow), which contradicts the principles of a pure functional core. It forces verbose `try-catch` blocks on the caller.

### 2. Return `null`

-   **Summary:** Return `null` to indicate absence.
-   **Pros:** Simple to implement.
-   **Cons:** Prone to causing `NullPointerException`s downstream. The method signature `ValueProvider get(String key)` becomes misleading, as it hides the possibility of a `null` return.

### 3. Return `Optional<ValueProvider>`

-   **Summary:** Change the method signature to `Optional<ValueProvider> get(String key)`.
-   **Pros:** Provides an explicit, type-safe contract that forces the caller to handle the possibility of an absent value. Aligns well with modern Java functional practices.
-   **Cons:** Adds a layer of `Optional` wrapping that can feel slightly more verbose for clients.

### 4. Return a "Null Object" (`UndefinedValueProvider`)

-   **Summary:** Implement a specific `UndefinedValueProvider` that is returned for any missing key. This object is a valid implementation of the `ValueProvider` interface.
-   **Pros:**
    -   **Purely Functional:** Avoids both exceptions and `null`s.
    -   **Consistent Interface:** The `get` method always returns a valid, non-null `ValueProvider`.
    -   **Domain-Specific Logic:** Allows the engine to define how "undefined" values behave in comparisons and computations, enabling graceful evaluation paths.
    -   **Cohesive Design:** Aligns with the existing concept of `constant::undefined` seen elsewhere in the codebase.
-   **Cons:** May mask certain programming errors, as a request for a misspelled key will not fail immediately but will instead propagate an "undefined" value.

## Decision

We choose **Option 4: Return a "Null Object" (`UndefinedValueProvider`)**.

This choice is the best fit for our architectural goal of a pure functional core. It provides the highest level of safety and predictability within the decision engine by treating "absence" as a first-class, well-defined state rather than an exceptional circumstance. It avoids the impurity of exceptions and the dangers of `null`. While `Optional` (Option 3) is a strong alternative, the `UndefinedValueProvider` offers a more seamless integration with the engine's evaluation logic.

## Consequences

-   A new class, `UndefinedValueProvider`, will be created as a singleton implementation of the `ValueProvider` interface.
-   The `ComplexObjectValueProvider` will be implemented to return this singleton instance whenever a requested key is not found.
-   The core engine logic must be aware of the `UndefinedValueProvider` and define its behavior in conditions and expressions.
-   Developers must be mindful that typos in field names will not result in immediate exceptions but in propagated "undefined" values. This necessitates robust testing of the rules that rely on the `ComplexObjectValueProvider`.
