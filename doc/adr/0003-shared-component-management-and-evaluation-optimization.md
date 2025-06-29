# 3. Shared Component Management and Evaluation Optimization

Date: 2025-06-29

## Status

Accepted

is supported by [5. Pure Functional Core](0005-pure-functional-core.md)

is implemented by [6. ValueProvider Design for Remote Data and Composability](0006-valueprovider-design-for-remote-data-and-composability.md)

## Brief summary

We will implement a stateless, immutable `DecisionGraph` to represent the decision logic, which will
be built once and cached. For each decision-making session, a new, lightweight `ExecutionContext`
will be created to store the evaluation results, ensuring thread-safety and efficient reuse of
shared components.

## Context

The library needs to efficiently handle scenarios where components are reused across the system.
This includes:

1. **Chained Tables:** The outcome of one decision table can be the input for another.
2. **Shared Conditions:** The same condition (e.g., `customer.age > 21`) can appear in multiple
   rules, potentially across different tables.
3. **Shared Data Fragments:** The same piece of data (e.g., the variable `customer.age`) is used in
   many different conditions.

A naive implementation would create duplicate objects and re-evaluate the same logic repeatedly,
leading to poor performance and high memory usage. The solution must also be thread-safe to allow
for simultaneous decision computations in a multi-threaded environment.

## Options

### 1. Centralized Registry/Locator Model (Rejected)

This model uses a central, stateful `ComponentRegistry` to store and dispense unique instances of
all shared components (tables, conditions, value providers).

* **How it Works:** Components are identified by a canonical key. The registry is queried before
  creating any new component. If an instance already exists for a key, it's returned; otherwise, a
  new one is created and stored. Evaluation results are cached in a per-session `ExecutionContext`
  to avoid re-computation.
* **Pros:** Memory efficient (Flyweight pattern), simple caching logic.
* **Cons:** The registry can become a large, stateful "god object," complicating testing and
  reasoning. The logic for generating canonical keys is complex and potentially brittle.

### 2. Immutable Evaluation Graph Model (Rejected)

This model treats the entire set of tables and their components as a single, immutable Directed
Acyclic Graph (DAG) of dependencies.

* **How it Works:** The loading phase builds a graph where nodes are `ValueProvider`s and edges
  represent dependencies. Sharing is inherent, as multiple parent nodes can reference the same child
  node. The engine traverses this graph, and evaluation results are cached on the nodes themselves.
* **Pros:** Highly performant due to explicit dependency tracking, which allows for optimal
  evaluation paths (e.g., pruning dead branches). The graph is immutable and stateless.
* **Cons:** A naive implementation has two major flaws. First, the graph would need to be rebuilt
  from scratch for every decision session, which is inefficient. Second, caching results directly on
  the graph's nodes makes it stateful during evaluation, rendering it **not thread-safe**.

### 3. Stateless Graph Template with a Session Context (Chosen)

This model refines the graph approach to solve its flaws by strictly separating the static structure
from the per-session execution state.

* **How it Works:**
    1. **`DecisionGraph` (The Template):** An immutable, thread-safe graph representing all static
       logic is built **once** from the source files and cached. This is the expensive part, but
       it's only done once.
    2. **`ExecutionContext` (The Session State):** For **each** decision-making call, a new,
       lightweight `ExecutionContext` object is created. This object is a simple map that caches the
       evaluation results for the nodes of the `DecisionGraph` for that specific session only.
    3. **`DecisionEngine`:** The engine takes both the shared `DecisionGraph` and the private
       `ExecutionContext` as arguments. It uses the context to check for cached results before
       evaluating a node. All mutable state is confined to the thread-local context.

* **Pros:**
    * **Efficient:** The expensive graph construction is a one-time cost.
    * **Thread-Safe:** The shared `DecisionGraph` is immutable. The mutable state is confined to the
      `ExecutionContext`, which is never shared between threads.
    * **Clean Architecture:** Enforces a clear separation between the static logic (the template)
      and the dynamic execution state (the context).
* **Cons:** Higher initial implementation complexity than the registry model, but this is justified
  by the performance and safety gains.

## Decision

We will adopt the **Stateless Graph Template with a Session Context** model.

This solution provides the best of all worlds. It leverages the performance and explicit dependency
tracking of the graph model while solving its inherent reusability and concurrency problems. It
avoids the "god object" issue of the centralized registry and provides a clean, scalable, and
thread-safe architecture that is essential for a high-performance, modern library. The strict
separation of the immutable template from the per-session context is the key to achieving all our
stated goals.

## Consequences

* **What becomes easier:**
    * Concurrent execution is inherently safe due to the separation of immutable logic and
      per-session state.
    * Reasoning about and testing the decision logic is simpler because the `DecisionGraph` is a
      pure, stateless structure.
    * The system is more extensible, as new types of nodes can be added to the graph without
      affecting the core evaluation logic.
* **What becomes more difficult:**
    * The initial implementation is more complex compared to a simple stateful model.
    * Debugging might require inspecting both the static graph and the dynamic execution context,
      which could be more involved.
* **Risks to be mitigated:**
    * The initial construction of the `DecisionGraph` could be a performance bottleneck for very
      large sets of rules; this may require optimization.
    * Memory management for the `ExecutionContext` must be handled carefully to avoid leaks,
      especially in high-throughput scenarios.
