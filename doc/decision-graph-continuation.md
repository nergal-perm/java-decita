# Immutable DecisionGraph continuation plan

Implements issue #168 and ADR-0003, ADR-0004, ADR-0005 on top of PR #169 head
`730b213fd5d7a11729f70656fa1fff3d07e8ab3a`. The user approved changing the unmerged graph API
to replace mutable legacy components with immutable descriptions.

## Design and boundaries

The graph represents source logic only. Coordinates are strings, conditions and source fragments
are immutable `RuleFragment` values, rules own copied fragment lists, and tables own copied rule
lists plus an explicit fallback. No node retains legacy `Coordinate`, `Condition`, `Rule`, or
`DecisionTable` instances. The existing legacy evaluator remains unchanged. Tests construct separate
legacy evaluations from the same immutable graph descriptions to verify independent sessions.

Input collections and all query results are immutable snapshots. A builder canonicalizes equivalent
nodes and reconnects edges to those canonical instances; conflicting definitions for one identifier
are rejected. Graph membership and identity must not change after a build, caller mutation, or
evaluation. Session caches use object identity per ADR-0004; graph identifiers are diagnostic and
construction-time lookup keys, never session cache keys.

The alternative of copying mutable legacy objects on every `component()` access was rejected:
it would hide evaluation lifetimes behind a graph getter and preserve the wrong representation.
The full evaluator, CSV-to-graph connector, and optimization work are separate from issue #168's
representation. This change supplies the immutable structure they require.

## Tasks and acceptance tests

- [x] Snapshot constructor node/edge sets; preserve graph membership after caller mutation.
- [x] Return unmodifiable sets from all six query methods.
- [x] Store immutable coordinate/condition descriptions; keep node/edge identity stable after
  evaluating descriptions in two contexts, in both orders and with dynamic addresses.
- [x] Store immutable rule/table descriptions; copying input lists protects nested snapshots.
- [x] Canonicalize shared nodes in the builder and all edge endpoints; reject conflicting
  definitions, dangling endpoints and cycles at graph construction boundaries.
- [x] Verify all four relationship kinds, repeated edges, disconnected/empty graphs, builder reuse,
  null handling and concurrent independent evaluations.
- [x] Update graph examples and test plan to describe actual supported behavior.
- [x] Run the complete tests and Qulice, then review the final diff.

For each behavior: add one regression, observe its expected failure, implement the smallest
change, and run all tests. Source from the prior audit is evidence, not an excuse to weaken
assertions. Existing assertions that specifically require the removed mutable API are migrated
to the approved immutable API with that compatibility change documented.

## Verification

Use JDK 21 with Maven 3.9.9, `-Dmaven.compiler.release=17`, and the existing dependency cache:

```shell
JAVA_HOME=/usr/lib/jvm/java-21-openjdk /tmp/apache-maven-3.9.9/bin/mvn \
  -o -B -ntp -Dmaven.repo.local=/tmp/java-decita-m2 \
  -Dmaven.compiler.release=17 clean verify -Pqulice
```

Baseline: 84 tests pass; Qulice fails with 342 violations in PR additions. Original checkout,
README edits, and audit artifacts remain untouched. No remote push, merge or issue update is
part of this local implementation.

## Verified result

The completed implementation passes **106 tests**, with zero failures/errors/skips, and
`clean verify -Pqulice` succeeds. The PR baseline had 84 passing tests and 342 lint violations;
the continuation adds 22 test cases and clears the graph-related lint debt. The README graph
example was compiled and executed against the generated JAR on the classpath.

The final review found two additional bypasses, both reproduced and repaired: nested rule
references did not share registered canonical instances, and direct construction admitted
duplicate node identifiers. Tests now cover canonical edge targets, nested fallback references,
conflicts across different tables, direct construction, and builder reuse. A focused follow-up
review found no additional blocking correctness issues.

`GraphNodes` is construction-scoped normalization owned by the builder; immutable graphs retain
only its completed snapshot. It does not introduce a runtime/global component registry. The
identity comparisons in structural validation ensure every reference targets the actual stored
instance, as required by ADR-0004's future session-cache boundary.

The session tests exercise independent legacy evaluations constructed from shared immutable
descriptions, in both input orders, on separate threads, and with dynamic bindings. They do not
claim to verify a new graph evaluator or session cache: neither is implemented in issue #168's
representation slice. The graph package remains unexported under JPMS; classpath use is supported.
