# 2. Separation of Engine and Connectors

Date: 2025-06-29

## Status

Accepted

is amended by [5. Pure Functional Core](0005-pure-functional-core.md)

## Brief summary

We will separate the core decision-making engine from the data connectors to enhance modularity and
extensibility, allowing for the easy integration of various data sources.

## Context

The current version of the `java-decita` library has a monolithic architecture. The lack of a clear
separation of concerns also hinders maintainability and testing. The library needs a rewrite to
improve its flexibility and maintainability. The primary goals are:

1. **Decouple Storage from Logic:** Allow decision tables to be loaded from various sources (e.g.,
   CSV, JSON, databases) without affecting the core decision-making logic.
2. **Extensible Core Logic:** Enable the addition of new functionalities, such as custom condition
   types (e.g., "matches regex") or new result interpreters (like generic side effect producers,
   i.e. actions changing context state) without modifying the core library.

## Options

1. **Monolithic Architecture:** Continue with the existing design where the engine and data access
   logic are tightly coupled.
    * Pros: Simple for the current use case. No major architectural refactoring is required.
    * Cons: Difficult to extend, hard to maintain, testing is more complex. Fails to meet the
      primary goals. Adding new storage formats or logic types would require modifying the core
      library, leading to a brittle and complex codebase over time.
2. **Abstract Class-based Extension**. Provide abstract base classes for engine's components. Users
   would extend these classes to implement custom functionality.
    * Pros: Relatively simple to implement and understand.
    * Cons: Creates a tighter coupling between the core and its extensions compared to an
      interface-based approach. It can lead to fragile and complex inheritance hierarchies.
3. **Configuration-based Modules**. Define modules and their implementations in a central
   configuration file (e.g., XML or JSON). The core library would read this configuration to wire up
   the application.
    * Pros: Makes the module composition very explicit.
    * Cons: Adds a layer of complexity for both the library maintainers and its users. It can be
      cumbersome to manage, and runtime errors due to misconfiguration can be hard to debug.
4. **Modular Architecture with Service Provider Interface (SPI)**. Redesign the library around a
   minimal core and a set of well-defined Service Provider Interfaces (SPIs). Extensions (plugins)
   are implemented against these interfaces and discovered at runtime using Java's standard
   `ServiceLoader`.
    * Pros:
        * **Maximum Decoupling:** The core is completely isolated from implementation details.
        * **High Extensibility:** New features can be added simply by dropping a new JAR onto the
          classpath.
        * **Clear Separation of Concerns:** Enforces a clean architecture.
        * **Standard Approach:** Uses `ServiceLoader`, a standard and well-understood mechanism in
          the Java ecosystem.
    * Cons: Requires a more significant upfront design and implementation effort compared to other
      options.

## Decision

We have decided to adopt the 'Modular Architecture with Service Provider Interface (SPI)' approach.
The core engine will be responsible for the decision-making logic and will operate on an abstract
data model. Data connectors will be responsible for loading decision tables from various sources and
transforming them into the abstract model consumed by the engine. This will be enforced by a defined
Service Provider Interface (SPI).

This approach directly and effectively addresses all the stated goals. It provides the highest
degree of decoupling and extensibility, which is crucial for the long-term health and evolution of
the library. By relying on standard Java mechanisms (`ServiceLoader`), we ensure that the plugin
model is robust and familiar to Java developers. The initial investment in a clean, modular design
will pay significant dividends by making future development faster, easier, and safer.

## Consequences

This decision will lead to a more modular and extensible architecture. It will be much easier to add
support for new data formats in the future without changing the core library. The separation will
also improve the testability of the core engine and the connectors in isolation. The initial
development effort might be slightly higher due to the need to define clear interfaces and a data
abstraction layer. We will need to create at least one connector (e.g., for CSV files) as part of
the initial implementation.
