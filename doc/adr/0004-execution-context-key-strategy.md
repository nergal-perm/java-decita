# 4. Execution Context Key Strategy

Date: 2025-06-29

## Status

Accepted

is informed by [5. Pure Functional Core](0005-pure-functional-core.md)

## Brief summary

We will use `Object` identity (the object reference itself) as the key for caching node evaluation
results in the `ExecutionContext` because it provides the highest performance, simplest
implementation, and lowest memory overhead.

## Context

As per ADR-0003, the `DecisionEngine` uses a per-session `ExecutionContext` to cache (memoize) the
results of node evaluations within the `DecisionGraph`. A critical implementation detail is the
strategy for creating the keys used in this cache. The keying strategy must be performant,
memory-efficient, and robust, ensuring that each node in the shared, immutable `DecisionGraph` can
be uniquely and quickly identified.

## Options

### 1. Canonical `String` Keys (Rejected)

This approach involves generating a unique string representation for each node (e.g.,
`"cond:equals:var(A):const(5)"`) and using that string as the key in the `ExecutionContext` map.

* **Pros:** Human-readable keys can be useful for debugging.
* **Cons:** Poor performance due to string generation overhead. The key creation logic is complex
  and brittle, making it prone to bugs. It also has a higher memory footprint.

### 2. Pre-assigned `long` IDs (Rejected)

This approach involves assigning a unique `long` ID to every node during graph construction and
using that ID as the key. The `ExecutionContext` map would be `Map<Long, Object>`.

* **Pros:** Extremely high performance for hashing and lookups.
* **Cons:** Adds complexity to the graph construction process, requiring a centralized, thread-safe
  counter to issue IDs. It also requires the node objects themselves to be modified to store this
  ID.

### 3. `Object` Identity (Node Reference) (Chosen)

This approach uses the reference to the node object from the `DecisionGraph` as the key itself. It
relies on the default `Object.hashCode()` and `Object.equals()` methods, which are based on memory
address (identity).

* **Pros:**
    * **Maximum Performance:** There is zero overhead for key generation. Hashing is extremely fast.
    * **Simplicity and Robustness:** The implementation is trivial and not prone to bugs from
      complex key-generation logic.
    * **Memory Efficient:** No extra memory is consumed for the keys.
* **Cons:** The keys are not human-readable in debuggers (e.g., `Condition@7a5d012c`), which is an
  acceptable trade-off.

## Decision

We will use the **`Object` Identity (Node Reference)** strategy for the `ExecutionContext` cache
keys.

This decision is a direct and logical consequence of ADR-0003, which mandates that the
`DecisionGraph` is immutable and uses the Flyweight pattern to ensure a single, unique object
instance for each logical component. Given this prerequisite, using the object's identity as its key
is the most efficient, simple, and robust solution. It avoids the significant performance and
maintenance penalties of string-based keys and the added complexity of manually assigning numeric
IDs. The performance and simplicity benefits far outweigh the minor inconvenience of
non-human-readable keys in debugging scenarios.

## Consequences

* **Easier:**
    * The implementation of the `ExecutionContext`'s caching mechanism is significantly simplified,
      as no key generation logic is required.
    * The process of building the `DecisionGraph` remains lean, without the added complexity of
      assigning and managing unique IDs for each node.
    * Achieving high performance during decision evaluation is more straightforward due to the
      near-zero cost of key lookups.
* **More Difficult:**
    * Debugging the `ExecutionContext` cache becomes less intuitive, as the keys are object
      references (e.g., `Condition@7a5d012c`) rather than human-readable strings. This may require
      specialized tooling or debugging techniques to map keys back to their logical nodes.
* **Risks:**
    * This strategy is critically dependent on the guarantee from ADR-0002 and ADR-0003 that the
      `DecisionGraph` is a true flyweight, with exactly one unique object instance for each logical
      node. Any bug in the graph construction that violates this uniqueness principle will lead to
      cache misses, causing severe performance degradation and potentially incorrect evaluations.
      This risk must be mitigated with thorough testing of the graph construction and component
      sharing logic.
