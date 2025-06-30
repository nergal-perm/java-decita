# 9. Authoritative Value-Typing via Contextual Providers

Date: 2025-06-30

## Status

Accepted

Supersedes [8. Type Inference for Constants](0008-type-inference-for-constants.md)

## Brief summary

We will require all `ValueProvider`s that source external data to return a flattened
`Map<String, ConstantValueProvider<?>>`. This makes the externally-sourced data the authoritative
source of type information for condition evaluations, simplifying the engine and eliminating the
need for complex type inference.

## Context

ADR-0006 introduced a `ValueProvider` design for composability, which led to the creation of
`ObjectPathDataProvider` to traverse complex object graphs. Concurrently, ADR-0008 addressed the
problem of determining the types of constant values within decision tables by proposing a type
inference mechanism based on syntax.

Both solutions, while functional, introduce complexity into the core engine: one for object
traversal and the other for type guessing. A simpler, more robust design is needed that aligns with
the goal of a pure functional core and pushes domain-specific logic (like data structure and type
knowledge) to the periphery (connectors).

The core problem is twofold:

1. How to access nested data from a complex object provided by a `ValueProvider`.
2. How to correctly type the values used in conditions, especially when one side is from an external
   source and the other is a literal in the decision table.

## Options

### 1. Status Quo: `ObjectPathDataProvider` and RHS Type Inference

Continue with the designs from ADR-0006 and ADR-0008.

- **Pros:** No new changes required.
- **Cons:** The core engine is complex, containing logic for both object traversal (
  `ObjectPathDataProvider`) and type inference (`RhsTypeGuesser`). This violates the principle of
  pushing domain-specific knowledge to connectors.

### 2. Flattened Data + RHS Type Inference

Require data providers to return a flat `Map<String, Object>` where keys are dot-separated paths (
e.g., "user.address.city"). This would eliminate the `ObjectPathDataProvider`. We would still use
the type inference from ADR-0008 for the RHS.

- **Pros:** Simplifies data access within the engine.
- **Cons:** Still requires the core engine to perform type inference, which can be fragile. The
  context would contain raw objects, and their types would need to be checked at evaluation time.

### 3. Authoritative LHS Typing via `ConstantValueProvider` (Chosen)

Require data providers to return a flat `Map<String, ConstantValueProvider<?>>`. The connector is
responsible for fetching data, flattening it, determining the correct Java type for each value, and
wrapping it in a `ConstantValueProvider` of the appropriate generic type.

- **Pros:**
    - **Simplifies the Core:** Eliminates both `ObjectPathDataProvider` and the need for RHS type
      inference.
    - **Authoritative Typing:** The LHS of a condition (sourced from the context) provides the
      definitive type for the comparison. The engine's job is simplified to coercing the RHS value
      to the LHS type.
    - **Robustness:** Type knowledge is located in the connector, which is the only component that
      should have it.
    - **Consistency:** The entire computation context becomes a consistent map of `ValueProvider`s.
- **Cons:** Places a slightly higher burden on connector developers to perform the typing and
  wrapping, but this is the correct location for this responsibility.

## Decision

We will adopt **Option 3**. All `ValueProvider`s sourcing external data (previously
`RemoteDataValueProvider`, now more accurately `ComplexObjectDataProvider`) must provide their data
as a `Map<String, ConstantValueProvider<?>>`.

The key of the map is the flattened, dot-notation path to a value (e.g., "user.age"). The value is a
`ConstantValueProvider` instance holding the correctly-typed data (e.g.,
`new ConstantValueProvider<Integer>(42)`).

This decision makes the LHS of a condition the authoritative source for type information during
evaluation. Consequently, `ObjectPathDataProvider` is no longer needed and will be removed. This
decision supersedes ADR-0008, as it provides a superior, more robust solution to the type-casting
problem.

## Consequences

- The `ObjectPathDataProvider` class and its tests will be deleted.
- The `ComplexObjectDataProvider` will be implemented to handle the new data structure.
- The core evaluation logic within conditions (e.g., `EqualsCondition`) will be simplified. It will
  retrieve the LHS value, use its class as the target type, and attempt to coerce the RHS value to
  that type before comparison.
- All existing and future data connectors must adhere to this new contract.
- ADR-0008 is now superseded.