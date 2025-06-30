# 8. Type Inference for Constants

Date: 2025-06-30

## Status

Accepted

refines [7. Type Discovery at Graph Construction Time](0007-type-discovery-at-graph-construction-time.md)

upholds [5. Pure Functional Core](0005-pure-functional-core.md)

## Brief summary

We are going to infer the type of a constant literal based on the type of the other operand in a binary expression because we want to maintain a clean, declarative syntax in decision tables without sacrificing type safety..

## Context

In decision table expressions like `user.age > 40`, the literal value "40" is parsed as a string. For the `DecisionEngine` to perform a correct, type-safe comparison, it must know the intended type of this literal (e.g., `Integer`, not `String` or `Double`). The engine needs a reliable strategy to determine the correct type for such constants without requiring explicit type declarations in the decision table, which would clutter the syntax and couple the logic to specific types. This is the logical continuation of the type-safety problem addressed in ADR-0007.

## Options

### 1. Type Suffixes in Configuration (Rejected)

Require explicit type hints in the decision table syntax, such as `40:int` or `40L`.

*   **Pros:** Unambiguous and explicit.
*   **Cons:**
    *   **Clutters Syntax:** Makes the decision logic harder to read and write for business users.
    *   **Adds Coupling:** Introduces implementation-specific type names (`int`, `long`) into the otherwise declarative logic.
    *   **Brittle:** A change in the application's data model (e.g., from `int` to `long`) would require updating the decision table files.

### 2. "Best Guess" Parsing (Rejected)

Attempt to guess the type of the literal by trying to parse it into different formats in a predefined order (e.g., try `Integer`, then `Double`, then `LocalDate`, etc.).

*   **Pros:** Seems simple and avoids syntax clutter.
*   **Cons:**
    *   **Ambiguous and Unreliable:** The literal "40" could be a valid `Integer`, `Double`, `Long`, or even a `String`. The engine cannot know the user's intent.
    *   **Error-Prone:** This can lead to subtle and difficult-to-diagnose errors. For example, if `user.level` is a `Double`, comparing it to an inferred `Integer` `4` might produce an incorrect result if the level is `4.5`.

### 3. Contextual Type Inference (Chosen)

Infer the type of the constant literal from the context of the expression, specifically from the type of the other operand.

*   **How it Works:**
    1.  In a binary expression (e.g., `LHS > RHS`), the graph builder first resolves the type of the non-literal operand (e.g., the `ValueProvider` for `LHS`).
    2.  The operator (`>`) dictates that both operands must be of a compatible, `Comparable` type.
    3.  The type of the non-literal operand becomes the target type for the literal operand.
    4.  The graph builder then coerces the literal string into the inferred target type (e.g., parses "40" into an `Integer` because the LHS was resolved to an `Integer`).
*   **Pros:**
    *   **Maintains Clean Syntax:** No type hints are needed in the decision tables.
    *   **Ensures Type Safety:** The comparison is guaranteed to be type-safe because the type is derived from the application's actual data model.
    *   **Unambiguous and Reliable:** The inference is based on the explicit type of the other operand, not on guesswork.
*   **Cons:**
    *   The logic for the `ComponentFactory` that builds conditions is slightly more complex, as it must perform this inference step.

## Decision

We will adopt the **Contextual Type Inference** approach.

The `ComponentFactory` responsible for creating `Condition` nodes will be implemented to infer the type of a constant literal based on the resolved type of the other `ValueProvider` in the expression. The factory will first determine the type of the variable part of the expression and then use that type to parse and instantiate the constant part, wrapping it in a correctly-typed `ConstantValueProvider`.

## Consequences

*   **Easier:**
    *   Writing decision rules remains simple and declarative for users.
    *   The core engine can guarantee type-safe comparisons for all binary conditions.
    *   It becomes trivial to support a wide range of data types (numerics, dates, custom objects) without changing the parsing logic, as long as they can be represented as strings.
*   **More Difficult:**
    *   The implementation of the condition-building logic in the `DecisionGraph` constructor is more complex.
*   **Risks:**
    *   If a literal cannot be coerced into the inferred type (e.g., `user.age > "abc"`), it will result in a graph construction failure. This is the desired behavior, as it catches configuration errors early, but it requires clear error messaging to the user.
