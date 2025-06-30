# 7. Type Discovery at Graph Construction Time

Date: 2025-06-30

## Status

Accepted

refines [6. ValueProvider Design for Remote Data and Composability](0006-valueprovider-design-for-remote-data-and-composability.md)

upholds [5. Pure Functional Core](0005-pure-functional-core.md)

upholds [2. Separation of Engine and Connectors](0002-separation-of-engine-and-connectors.md)

## Brief summary

We are going to discover the concrete generic types for `ValueProvider`s at graph construction time by introspecting registered `RemoteDataSource` SPIs and using reflection because we want to maintain a strongly-typed, type-safe `DecisionGraph` without coupling the decision logic to implementation-specific class names..

## Context

The `ValueProvider` system, particularly `RemoteDataValueProvider<T>` and `ObjectPathValueProvider<T>`, is designed to work with generic types to ensure type safety within the `DecisionEngine` (ADR-0006). However, the decision tables themselves should remain declarative and decoupled from Java-specific types. They should refer to data sources by logical names (e.g., "userService") rather than fully qualified class names.

This creates a dilemma: how does the `DecisionGraph` builder determine the concrete type `T` for a `ValueProvider` at construction time if the configuration is implementation-agnostic? A mechanism is needed to bridge the declarative world of the decision tables with the strongly-typed world of the Java-based execution engine without creating tight coupling.

## Options

### 1. Using Raw Types (Rejected)

Instantiate all `ValueProvider`s with raw types (e.g., `ValueProvider<Object>`) and perform runtime casting.

*   **Pros:** Simple to implement initially.
*   **Cons:**
    *   **Loses Type Safety:** This completely subverts Java's generic type system, effectively deferring type checking to runtime.
    *   **Brittle:** Prone to `ClassCastException`s if the data returned from a source does not match what a `Condition` expects.
    *   **Poor Maintainability:** Makes the code harder to reason about and refactor, as the compiler can no longer guarantee type correctness.

### 2. Type Information in Configuration (Rejected)

Specify the fully qualified class name of the return type directly in the decision table or its accompanying configuration files.

*   **Pros:** Makes the type explicit in the configuration.
*   **Cons:**
    *   **Tight Coupling:** This directly violates the core principle of separating logic from implementation (ADR-0002). The decision logic is now tied to a specific Java class in the application's data model.
    *   **Reduced Portability:** The decision assets are no longer portable between different systems or applications that might use different data models.

### 3. Type Discovery via SPI Introspection (Chosen)

The type information is discovered at graph construction time by introspecting the registered `RemoteDataSource` SPI implementations.

*   **How it Works:**
    1.  The `RemoteDataSource<T, K>` SPI is enhanced with a `getReturnType()` method that returns `Class<T>`.
    2.  The application developer implements this interface, providing the concrete return type (e.g., `UserDto.class`).
    3.  When building the `DecisionGraph`, the factory responsible for creating `ValueProvider`s looks up the required `RemoteDataSource` by its logical name.
    4.  It calls `getReturnType()` to find the root object's type.
    5.  For `ObjectPathValueProvider`s, it uses reflection on the parent provider's return type to determine the type of the extracted field.
*   **Pros:**
    *   **Maintains Decoupling:** Decision logic remains free of implementation details.
    *   **Preserves Type Safety:** The `DecisionGraph` is constructed with fully generic, strongly-typed providers.
    *   **Co-locates Concerns:** The responsibility for declaring a data type resides with the code that provides that data.
*   **Cons:**
    *   Adds a minor requirement to the `RemoteDataSource` SPI implementation.
    *   Relies on reflection for field type discovery, which can be slightly less performant than direct calls, but this is a one-time cost during graph construction.

## Decision

We will adopt the **Type Discovery via SPI Introspection** approach.

The `RemoteDataSource<T, K>` interface will be modified to include a `getReturnType()` method. The `DecisionGraph` builder will use this method to determine the root type for a `RemoteDataValueProvider`. For `ObjectPathValueProvider`, it will use Java reflection on the parent provider's type to determine the specific field's type. This ensures that the graph is constructed with full type information, derived from the data-providing components themselves, not from the decision logic configuration.

## Consequences

*   **Easier:**
    *   Maintaining a type-safe core engine becomes automatic.
    *   Refactoring the application's data model (e.g., renaming a DTO class) does not require changing the decision table files.
    *   Reasoning about data flow is easier as the types are explicit within the constructed graph.
*   **More Difficult:**
    *   Implementing a `RemoteDataSource` becomes slightly more verbose, as it must now explicitly provide its return type.
*   **Risks:**
    *   The reliance on reflection for `ObjectPathValueProvider` means that errors in the path string (e.g., a typo in a field name) will be caught at graph construction time rather than compile time. This is an acceptable trade-off and can be mitigated with robust testing of the configuration.
