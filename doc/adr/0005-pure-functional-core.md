# 5. Pure Functional Core

Date: 2025-06-29

## Status

Accepted

amends [2. Separation of Engine and Connectors](0002-separation-of-engine-and-connectors.md)

supports [3. Shared Component Management and Evaluation Optimization](0003-shared-component-management-and-evaluation-optimization.md)

informs [4. Execution Context Key Strategy](0004-execution-context-key-strategy.md)

is upheld by [6. ValueProvider Design for Remote Data and Composability](0006-valueprovider-design-for-remote-data-and-composability.md)

is upheld by [7. Type Discovery at Graph Construction Time](0007-type-discovery-at-graph-construction-time.md)

## Brief Summary

We are going to design the core decision engine as a pure, side-effect-free function because we want
to achieve maximum testability, predictability, and extensibility.

## Context

The previous version of the library mixed evaluation logic with data loading and execution of
outcomes. This resulted in a monolithic architecture that was difficult to test, maintain, and
extend. A primary goal of the rewrite is to create a highly modular system where the central logic
is isolated and predictable. The core engine's only job should be to process a given set of rules
against a given context and determine the correct outcome. It should not be concerned with how the
rules were loaded or what the side effects of the outcome are.

## Options

### 1. Pure Functional Core

The engine is a pure function. It takes a decision table snapshot and an evaluation context as input
and returns a representation of the result (e.g., an `Decision` object). It performs no I/O, has no
internal state that changes between calls, and produces no side effects (no logging, no database
calls, etc.).

* **Pros:**
    * Extremely easy to test and verify.
    * Inherently thread-safe and highly concurrent.
    * Completely decoupled from connectors and application logic.
    * Predictable: the same input always yields the same output.
* **Cons:**
    * The calling application bears the full responsibility of executing the returned action, which
      can add a small layer of indirection.

### 2. Core with Controlled Side Effects

The engine can perform a limited, well-defined set of side effects, such as internal logging or
metrics tracking, but delegates major side effects (like database writes) to the caller.

* **Pros:**
    * Can encapsulate some cross-cutting concerns like logging within the core itself.
* **Cons:**
    * Violates the single responsibility principle.
    * Makes testing more complex as side effects need to be mocked or verified.
    * The line between "allowed" and "disallowed" side effects can become blurry over time.

### 3. Fully Integrated Core (Legacy Approach)

The engine manages its own data loading, evaluation, and execution of outcomes. It is a
self-contained but monolithic component.

* **Pros:**
    * Appears simpler from the caller's perspective for a single, specific use case.
* **Cons:**
    * Nearly impossible to test in isolation.
    * Not extensible; adding new data formats or action types requires modifying the core.
    * This is the architecture we are moving away from.

## Decision

We will implement the **Pure Functional Core**.

This choice is foundational to the new architecture. It directly supports the primary goals of
modularity, testability, and maintainability. The engine's responsibility will be strictly limited
to evaluating decisions. It will receive all necessary data from its inputs and will return a
description of the outcome, leaving the execution of any side effects to the caller.

## Consequences

* The core engine will have no dependencies on I/O, logging frameworks, or any other external
  systems.
* The application or "connector" code that invokes the engine is responsible for preparing the
  `EvaluationContext` and interpreting/executing the returned `Decision`.
* This enforces a clean separation of concerns, which is a key objective of ADR-0002.
* All components within the core must be designed to be immutable and stateless to uphold this
  principle.
