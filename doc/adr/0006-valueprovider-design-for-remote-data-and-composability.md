# 6. ValueProvider Design for Remote Data and Composability

Date: 2025-06-29

## Status

Accepted

realizes [2. Separation of Engine and Connectors](0002-separation-of-engine-and-connectors.md)

implements [3. Shared Component Management and Evaluation Optimization](0003-shared-component-management-and-evaluation-optimization.md)

upholds [5. Pure Functional Core](0005-pure-functional-core.md)

is refined by [7. Type Discovery at Graph Construction Time](0007-type-discovery-at-graph-construction-time.md)

## Brief Summary

We are going to implement a composable `ValueProvider` system, with separate providers for fetching
remote data and extracting fields from it, because we want to achieve efficient, non-redundant data
fetching while maintaining a declarative and optimizable dependency graph.

## Context

Decision tables often require data that is not directly available in the initial
`ExecutionContext` but must be fetched from external sources, such as remote
REST services or databases. This presents a two-fold challenge:

1. **Efficient Data Fetching:** Avoid redundant remote calls for the same data within a single
   decision session.
2. **Flexible Data Extraction:** Allow different parts of the decision logic to access various
   fields from a single fetched remote object (e.g., `user.age`, `user.salary` from a single user
   object).

The solution must integrate seamlessly with the `DecisionGraph` model (ADR-0002) and support the
modularity goals (ADR-0001).

## Options

### 1. Monolithic `Locator` / "Smarter `UserLocator`" (Rejected)

This approach would involve a single `Locator` (or `ValueProvider` in the new terminology)
implementation that is responsible for fetching the remote data, caching it internally, and then
extracting the specific requested field based on a parameter.

* **Pros:** Appears simpler on the surface, as it encapsulates all logic within one class.
* **Cons:**
    * **Violation of Single Responsibility Principle (SRP):** A single component handles fetching,
      caching, and field extraction.
    * **Poor Composability:** The internal logic for fetching and caching is tightly coupled with
      the extraction of a specific field. It's difficult to reuse the fetching/caching part
      independently.
    * **Limited Optimization:** The `DecisionGraph` and `DecisionEngine` treat this as a black box.
      They cannot see the internal dependencies or optimize based on which specific fields are being
      accessed. This limits global graph-based optimizations.
    * **Hidden Dependencies:** The specific field being accessed is often a runtime parameter,
      making the graph less declarative and harder to reason about.

### 2. Separate `ValueProvider` for Each Field (No Composition) (Rejected)

In this approach, each required field (e.g., `user.age`, `user.salary`) would be represented by its
own distinct `ValueProvider` instance. Each of these providers would be responsible for initiating
the remote call, caching the result, and extracting its specific field.

* **Pros:** Simple to understand and implement for individual fields.
* **Cons:**
    * **Inefficient Caching:** While each provider might cache its *own* result, there's no shared
      caching of the *entire* remote object. If a decision needs `user.age` and `user.salary`, it
      would potentially trigger two separate remote calls to fetch the same user object, leading to
      N+1 problem.
    * **Redundant Logic:** The fetching and caching logic would be duplicated across multiple
      `ValueProvider` implementations.

### 3. Composed `ValueProvider`s (`RemoteDataValueProvider` + `ObjectPathValueProvider`) (Chosen)

This approach separates the concerns into two distinct, composable `ValueProvider` types:

* **`RemoteDataValueProvider<T>`:** This provider is responsible for orchestrating the fetching of
  an entire remote object and caching it in the `ExecutionContext` for the current session.
  Crucially, it **delegates the actual data fetching** to an application-provided `RemoteDataSource`
  implementation, identified by a logical name. It takes a `ValueProvider` for its key (e.g.,
  `userId`).
* **`ObjectPathValueProvider<T>`:** This provider is responsible for taking a source
  `ValueProvider` (which would typically be a `RemoteDataValueProvider` or another complex object
  provider) and extracting a specific field from its resolved value using a path (e.g., `user.age`).

This solution introduces a new Service Provider Interface (SPI):

* **`RemoteDataSource<T, K>`:** An interface that application developers implement to provide
  concrete data fetching logic (e.g., HTTP calls, database queries). These implementations are
  registered by a logical name (e.g., "userService") at application bootstrap time and injected into
  the `ExecutionContext`.

* **Pros:**
    * **Optimal Caching:** The `RemoteDataValueProvider` ensures that the remote call is made only
      once per unique object per session, regardless of how many fields are subsequently extracted
      from it. All `ObjectPathValueProvider`s for the same remote object will share the cached
      result.
    * **True Decoupling:** The decision table configuration and the core library are completely
      unaware of the underlying data access technology (HTTP, database, etc.). This responsibility
      is pushed to the application layer via the `RemoteDataSource` SPI.
    * **Clear Separation of Concerns (SRP):** Fetching/caching orchestration (
      `RemoteDataValueProvider`) is separate from concrete data access (`RemoteDataSource`) and
      field extraction (`ObjectPathValueProvider`).
    * **High Composability:** These providers are designed to be chained and reused, forming a
      clear, declarative dependency graph.
    * **Transparent Dependencies:** The `DecisionGraph` explicitly shows the relationship between
      the remote call and the specific fields being extracted, enabling the `DecisionEngine` to
      perform global optimizations.
    * **Enhanced Extensibility:** Application developers can easily plug in custom data sources
      without modifying the core library or decision table definitions.

## Decision

We will adopt the **Composed `ValueProvider`s** approach, utilizing `RemoteDataValueProvider` for
fetching and caching entire remote objects (delegating to application-provided `RemoteDataSource`
implementations), and `ObjectPathValueProvider` for extracting specific fields.

This refined solution provides the most robust, performant, and maintainable design for handling
external data. It achieves true decoupling by moving concrete data access implementations to the
application layer, making the core library and decision tables agnostic to data fetching mechanisms.
It adheres to the Single Responsibility Principle, promotes high composability, and ensures
efficient caching by fetching the complete remote object only once per session. Crucially, it
creates a transparent and declarative `DecisionGraph` where all data dependencies are explicit,
allowing the `DecisionEngine` to perform powerful optimizations. This approach aligns perfectly with
the modular and graph-based architecture defined in previous ADRs, and it leverages the
`ComponentFactory` SPI for flexible instantiation based on declarative configuration.

## Consequences

* **Easier:**
    * **Efficient Caching:** It becomes trivial to avoid multiple remote calls for the same object
      within one evaluation session. The `RemoteDataValueProvider` naturally handles this.
    * **Decoupling:** The core engine and decision table definitions are now completely isolated
      from the implementation details of data fetching (e.g., REST vs. database).
    * **Extensibility:** Application developers can add new remote data sources by implementing the
      `RemoteDataSource` SPI without touching the core library.
    * **Optimization:** The `DecisionGraph` has full visibility into the data flow, allowing the
      engine to perform more advanced optimizations (e.g., parallel fetching of independent data
      sources).

* **More Difficult / Risks:**
    * **Increased Initial Complexity:** The initial setup is more involved. Developers must
      understand and implement the `RemoteDataSource` SPI and correctly configure the two separate
      `ValueProvider` types.
    * **Configuration Overhead:** Defining a simple data access now requires configuring two
      providers (`RemoteDataValueProvider` and `ObjectPathValueProvider`) instead of one, which
      could feel verbose for very simple cases. This is a trade-off for the flexibility and
      performance gains.
    * **Dependency Injection:** A mechanism for registering and injecting `RemoteDataSource`
      implementations into the evaluation context becomes a critical part of the application's
      bootstrap process.

