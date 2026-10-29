# PR #169: immutability and two-session isolation audit

Historical audit of the original PR head pinned below. Its findings were addressed before merge;
see the [post-merge plan](../../proposed-implementation-plan-2026-10-29.md) for the current baseline.
The accompanying Java file targets that original head, not the current API or active test suite.

Reviewed on 2026-10-29. **Recommendation: request changes before accepting this as an immutable,
session-independent graph.** The counterexamples below were executed, not merely inferred.

## Revision and scope

- [PR #169](https://github.com/nergal-perm/java-decita/pull/169), still open and draft at review time.
- Head: `730b213fd5d7a11729f70656fa1fff3d07e8ab3a`.
- Base: `b771a032f194318aaa6e9b2a7b6cbd8d40315acd`.
- [Issue #168](https://github.com/nergal-perm/java-decita/issues/168) requests an immutable graph
  representation. [ADR-0003](../../adr/0003-shared-component-management-and-evaluation-optimization.md),
  [ADR-0004](../../adr/0004-execution-context-key-strategy.md), and
  [ADR-0005](../../adr/0005-pure-functional-core.md) require immutable shared logic with evaluation
  results confined to a session.

The archive was extracted into `/tmp/java-decita-pr169-review`; production sources were unchanged.
The PR does not introduce an executable graph engine or a new `ExecutionContext`. Session tests
therefore obtain the represented `Condition` or `Coordinate` through the graph's public
`component()` API and evaluate it against distinct legacy `ComputationContext` instances.
This tests whether those shared components are safe to reuse. It does not claim to exercise a
nonexistent graph evaluator. The absence of that evaluator is not itself a violation of #168.

## Executed evidence

The original PR suite passed: **84 tests, 0 failures, 0 errors**.
The additional [review test source](DecisionGraphIsolationReviewTest.java) contains twelve
independent probes: **9 failures, 3 passes, 0 errors**. A subsequent clean run of all tests gave
**96 tests, 9 failures, 0 errors**; all failures belong to the review class.

| Review probe | Result | Observation |
| --- | --- | --- |
| Caller-owned node set cleared after construction | FAIL | Graph loses its node: expected 1, actual 0 |
| Caller-owned edge set cleared after construction | FAIL | Graph loses its edge: expected 1, actual 0 |
| Node/edge identity after condition evaluation | FAIL | Both IDs and hashes change |
| Node retained in a hash index across evaluation | FAIL | `HashSet.contains(theSameNode)` becomes false |
| Session A age=21, then B age=17, condition `age > 18` | FAIL | B returns true; expected false |
| Session A age=17, then B age=21 | FAIL | B returns false; expected true |
| Dynamic `cells::${request::move}` with A1 then A2 | FAIL | B reads X from A1; expected O from A2 |
| Four query methods promise unmodifiable sets | FAIL | All four returned sets allow `clear()` |
| A evaluated on worker thread, then B on test thread | FAIL | B retains A's value even with ordered execution |
| Builder reused after `build()` | PASS | Existing graph membership stays unchanged |
| `nodes()` and `edges()` reject mutation | PASS | Both top-level returned sets are unmodifiable |
| Fresh components for each distinct context | PASS | Both contexts produce their correct result |

The thread probe is deterministic: it waits for A to finish before running B on a different
thread. It demonstrates cross-thread state retention without relying on a race. No concurrent
stress test was needed to disprove safe shared reuse; this is not a comprehensive race audit.

## Standards

Three findings against documented architecture/API contracts, within the requested scope.

### S1 — P1: graph nodes contain mutable evaluation state and unstable identity

[`CoordinateNode` lines 46–79](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/CoordinateNode.java#L46-L79)
retains and exposes the original `Coordinate`; its ID and hash follow that object's current state.
[`ConditionNode` lines 53–70](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/ConditionNode.java#L53-L70)
also exposes the original condition, which retains its operands. Evaluation changes the coordinate
from `customer::age` to `constant::21`. The test observed:

```text
node id: coordinate:customer::age -> coordinate:constant::21
node hash: 1404698337 -> 858884803
edge id: coordinate:customer::age--lhs_of-->condition:adult
      -> coordinate:constant::21--lhs_of-->condition:adult
edge hash: -1230044954 -> -1770841016
hash index contains same node: true -> false
```

The edge instability follows from
[`BasicGraphEdge` lines 80–100](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/BasicGraphEdge.java#L80-L100).
This breaches ADR-0003's separation of static graph and session results and ADR-0005's immutable,
stateless core requirement. Merely caching a node ID would not fix the retained evaluation state.

### S2 — P1: the public constructor does not snapshot its input collections

[`InMemoryDecisionGraph` lines 55–57](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/InMemoryDecisionGraph.java#L55-L57)
retains caller-provided sets directly. Clearing either original mutable set changes graph
membership. Returning copies later cannot remove that constructor alias.

Scope distinction: [`DecisionGraphBuilder.build()` lines 132–133](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/DecisionGraphBuilder.java#L132-L133)
does copy membership. Its reuse control passed. S2 concerns direct construction, while S1 also
affects graphs created through the builder.

### S3 — P2: four query methods return mutable sets contrary to their contract

[`InMemoryDecisionGraph` lines 71–97](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/InMemoryDecisionGraph.java#L71-L97)
uses `Collectors.toSet()` for `nodesOfType`, `edgesOfType`, `edgesFrom`, and `edgesTo`.
All four permit mutation, although the
[`DecisionGraph` interface](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/DecisionGraph.java#L51-L84)
promises immutable returned sets. These are detached copies: mutating them does not change the
graph's membership. This is a separate return-contract defect, not evidence of constructor aliasing.

## Spec

Three findings against the requested immutable representation and the PR's session-separation
claims. They overlap the Standards findings and must not be summed as six independent defects.

### Spec1 — P1: represented conditions and dynamic coordinates leak first-session data

ADR-0003 requires that all mutable state be confined to the session context. The PR description
claims that its shared graph separates static logic from per-session execution state.
Actual execution of the same represented condition against distinct contexts gives the first
session's result in both directions. Dynamic addresses also retain the first session's binding.

The mechanism is explicit in legacy
[`BinaryCondition.evaluate()`](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/conditions/BinaryCondition.java#L75-L84)
and [`Coordinate.locateIn()` / `resolveIn()`](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/Coordinate.java#L113-L197):
resolved values and addresses replace the source fields on the shared objects. That behavior
predates the PR; wrapping those objects does not make them immutable or session-independent.
This finding concerns shared graph components, not a claim that every normal table evaluation
is stale: the current table path creates fresh condition objects.

### Spec2 — P1: #168's immutable snapshot is not established

Issue #168 expressly asks for an "immutable DecisionGraph". Executed constructor-aliasing and
node-identity tests disprove that property, including node mutation on the builder path. Read-only
top-level sets are insufficient for a graph containing mutable exposed components.

### Spec3 — P2: immutable query-result requirements are only partially implemented

The graph interface promises immutable results for all six collection-returning methods.
The top-level two satisfy the contract; the remaining four do not. Existing PR tests check only
the top-level pair. No added test evaluates represented conditions in two sessions or on two threads;
the PR's general claims of thread-safety verification are not supported by those tests.

## Reproduction

Extract the pinned archive into a separate directory, then copy
`DecisionGraphIsolationReviewTest.java` into `src/test/java/ru/ewc/decisions/core/` there.
Run the original suite before copying the review test if reproducing the baseline as well.
With Maven installed and dependencies available:

```shell
JAVA_HOME=/usr/lib/jvm/java-21-openjdk mvn \
  -Dmaven.compiler.release=17 -Dmaven.buildNumber.skip=true \
  -Dtest=DecisionGraphIsolationReviewTest test
```

`maven.buildNumber.skip=true` skips Git metadata collection because the archive has no `.git`.
It does not skip compilation or tests. The observed local commands additionally used Maven 3.9.9,
`-o -B -ntp`, and `-Dmaven.repo.local=/tmp/java-decita-m2`. Full-suite commands omitted `-Dtest`
and used `clean test`. The review run is expected to exit 1 due to nine assertion failures.

Local evidence: `/tmp/java-decita-pr169-baseline.log`, `/tmp/java-decita-pr169-isolation.log`,
`/tmp/java-decita-pr169-full-review.log`, and the archive's `target/surefire-reports/`.

## Acceptance direction

Copy input collections at the graph boundary and return unmodifiable query sets. More importantly,
represent source coordinates/expressions as immutable descriptions and keep resolved bindings,
values, and memoized results in per-session state. Re-run these probes and the original suite
after any proposed repair. New evaluator/session API decisions should follow the existing ADRs;
this audit does not introduce or implement those decisions.

Standards: 3 findings; worst is mutable shared node state (S1).
Spec: 3 findings; worst is reproduced first-session state leakage (Spec1).
