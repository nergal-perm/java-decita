# GitHub issue review for the next development plan

Reviewed on 2026-10-29 against local commit `b771a032f194318aaa6e9b2a7b6cbd8d40315acd`.
This is a research report and a proposed sequence, not an accepted implementation plan or a new ADR.
No issues, pull requests, ADRs, or `plan.md` were changed during the review.

Follow-up: [executable PR #169 audit](reviews/pr169/README.md) reproduces the immutability and
two-session failures discussed as static concerns below, and includes runnable review tests.

## Scope and evidence

The connected GitHub API returned eight open issues for
[`repo:nergal-perm/java-decita is:issue is:open`](https://github.com/nergal-perm/java-decita/issues?q=is%3Aissue+is%3Aopen).
All eight issue bodies and metadata were read. The only non-empty discussions among them were
#23 (eight comments) and #165 (one comment); both were read in full. The remaining six reported
zero comments. The latest 100 closed issues were screened; 15 relevant closed issues were fetched
individually, with full discussions for #153 and #160. Twenty recent PR search results were
screened, and PRs #167 and #169 were fetched individually. The complete changed-file list and
diff of #169 were inspected for architectural relevance; its discussion endpoint returned no
comments. This was not an exhaustive historical issue/PR audit or an executable PR review.

Issue status is a GitHub snapshot on the review date. Code observations below refer to the local
commit or the explicitly pinned PR head. Statements in issue and PR descriptions are proposals
or author claims unless independently supported by code.

## Most consequential discovery: an existing graph implementation candidate

[#168](https://github.com/nergal-perm/java-decita/issues/168) requests an immutable in-memory
`DecisionGraph`. It already has an **open, draft, unmerged** implementation:
[#169](https://github.com/nergal-perm/java-decita/pull/169), created 2025-07-05, last updated
2025-07-05. Its head is `copilot/fix-168` at
`730b213fd5d7a11729f70656fa1fff3d07e8ab3a`; its base matches the inspected local commit. The PR
adds 21 files / 2,152 lines, including graph interfaces, node and edge wrappers, a builder, tests,
and documentation. It does not establish that the current master has a graph or new evaluator.

**Recommendation:** review this candidate before commissioning another graph implementation.
Treat its thread-safety, full coverage, and immutability claims as unverified; the PR was not built
or run in this review. Static inspection gives concrete acceptance-test targets:

- [`CoordinateNode`](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/CoordinateNode.java#L46-L79)
  retains a supplied legacy `Coordinate`, exposes it through `component()`, derives `id()` from
  its current string, and delegates equality/hash to that coordinate. The retained type is mutable:
  [`Coordinate.locateIn()`](https://github.com/nergal-perm/java-decita/blob/b771a032f194318aaa6e9b2a7b6cbd8d40315acd/src/main/java/ru/ewc/decisions/core/Coordinate.java#L114-L121)
  converts a reference to a constant by
  [updating its fields](https://github.com/nergal-perm/java-decita/blob/b771a032f194318aaa6e9b2a7b6cbd8d40315acd/src/main/java/ru/ewc/decisions/core/Coordinate.java#L219-L232).
  **Inference:** evaluating an exposed component can change a graph node's identity representation
  and hash, threatening set membership and the immutable-template contract. Test this explicitly.
- [`InMemoryDecisionGraph`](https://github.com/nergal-perm/java-decita/blob/730b213fd5d7a11729f70656fa1fff3d07e8ab3a/src/main/java/ru/ewc/decisions/core/InMemoryDecisionGraph.java#L55-L67)
  stores caller-provided sets directly, while accessors return copies. **Inference:** a caller
  retaining a mutable input set can alter the graph after construction. Add a constructor-aliasing
  test; immutable returned collections alone do not prove an immutable snapshot.
- Compare the candidate with [ADR-0003](adr/0003-shared-component-management-and-evaluation-optimization.md),
  [ADR-0004](adr/0004-execution-context-key-strategy.md), and
  [ADR-0005](adr/0005-pure-functional-core.md): the required separation concerns immutable logic
  and per-session evaluation results, not just read-only collection accessors. Node sharing and
  stable identity need tests independent of the container API.

For distinction: the earlier local exploratory run reproduced a reused legacy condition retaining
its first value after the context changed. That establishes a legacy lifecycle concern; it does
not itself prove a runtime failure in the unexecuted PR.

## All open issues and their planning value

Dates below are GitHub `updated_at` dates, not estimates of implementation progress.

| Issue | Updated | What it contributes | Suggested treatment |
| --- | --- | --- | --- |
| [#170: source blocks / expressions](https://github.com/nergal-perm/java-decita/issues/170) | 2025-08-12 | A source-independent intermediate representation with row kind, operands, and possibly a separate operator | High value: define the minimum representation consumed by a graph builder. Current `RuleFragment` is a useful precursor, but conditions still parse operators from cell text. Do not equate it with a finished connector-neutral model. |
| [#168: immutable DecisionGraph](https://github.com/nergal-perm/java-decita/issues/168) | 2025-07-05 | Graph nodes and `part_of`, `lhs_of`, `rhs_of`, `operator_of` relationships | High value: first review draft #169; then split remaining acceptance criteria into small tests. The issue's branch/PR instruction is historical task context, not a requirement to reuse that workflow. |
| [#162: dependency information](https://github.com/nergal-perm/java-decita/issues/162) | 2024-09-29 | Prerequisites for visualization, bootstrap checks, and future execution planning | A useful observable result of the graph slice. Begin with static dependencies; distinguish dynamic coordinates whose concrete targets require session data. |
| [#165: resolved/computed coordinates and early elimination](https://github.com/nergal-perm/java-decita/issues/165) | 2025-08-12 | Session caching and eventual pruning | Depends on graph and session isolation. Preserve coordinate descriptions in the template; put computed values in the session. Its [only comment](https://github.com/nergal-perm/java-decita/issues/165#issuecomment-3038167718) announces an OpenHands attempt, not completion. |
| [#166: condition profiler](https://github.com/nergal-perm/java-decita/issues/166) | 2025-08-12 | Condition execution counts/timings aggregated across runs | Useful after a measurable evaluation path exists. Instrument outside the pure core or interpret returned evaluation evidence; avoid adding a clock or mutable global statistics to the core, per ADR-0005. |
| [#163: same name for State and DecisionTable](https://github.com/nergal-perm/java-decita/issues/163) | 2024-09-29 | Associated state/behavior and a possible `this::property` reference | Research question, not a settled syntax contract. Current `ComputationContext.valueFor()` gives tables precedence on name collisions, so this needs an explicit resolution rule. |
| [#164: dynamically bind table and state instances](https://github.com/nergal-perm/java-decita/issues/164) | 2024-09-29 | Concrete scenario: multiple Puerto Rico factories share behavior but have separate owners/workers | Strong acceptance scenario for one shared graph and separate bindings/contexts. Keep the scenario; do not assume it mandates merging table and state namespaces as suggested in #163. |
| [#23: improvement umbrella](https://github.com/nergal-perm/java-decita/issues/23) | 2024-05-27 | Historical PDD tracking | Maintenance candidate, not a concrete next implementation task: its [latest comment](https://github.com/nergal-perm/java-decita/issues/23#issuecomment-2132762281) says all five child puzzles are solved. Their current issue records (#26, #53, #54, #85, #120) are closed. Consider closing or refreshing the umbrella after maintainer review. |

## Historical work to retain or avoid repeating

- [#139](https://github.com/nergal-perm/java-decita/issues/139) is closed and explicitly replaces
  the old section-divider CSV with typed `CND` / `OUT` rows.
  [#140](https://github.com/nergal-perm/java-decita/issues/140) adds `ASG` rows;
  [#150](https://github.com/nergal-perm/java-decita/issues/150) adds optional headers. These are
  existing behavior, supported by `SourceLines`, `RuleFragments`, `DecisionRuleFragments`,
  and end-to-end tests, rather than new rewrite backlog.
- [#153](https://github.com/nergal-perm/java-decita/issues/153) and
  [#159](https://github.com/nergal-perm/java-decita/issues/159) establish typed source lines and
  rule fragments. #170 can build on their separation intent, but should specify the missing
  source-independent contract. [#156](https://github.com/nergal-perm/java-decita/issues/156) and
  [#157](https://github.com/nergal-perm/java-decita/issues/157) are closed PDD tasks with the same
  RawContent-removal title and related source puzzles; do not create two new tasks from them.
- [#152](https://github.com/nergal-perm/java-decita/issues/152) and
  [#161](https://github.com/nergal-perm/java-decita/issues/161) are closed dynamic-coordinate
  work. Preserve concatenation, multiple placeholders, and nested placeholders as migration
  cases; `CoordinateTest` contains current coverage. #161 is marked completed, and the
  changelog records its fix in 0.9.2.
- [#160](https://github.com/nergal-perm/java-decita/issues/160) is especially instructive:
  the [maintainer's closing comment](https://github.com/nergal-perm/java-decita/issues/160#issuecomment-2345908324)
  says the functionality was implemented client-side and was not needed in the library then.
  A closed/completed issue does not establish that its requested feature exists in the engine.
- [#167](https://github.com/nergal-perm/java-decita/pull/167) is a **merged PR**, merged
  2025-06-30, containing architectural decisions. It is referenced by #165, but is neither an
  open issue nor evidence that those decisions have been implemented.

## Suggested revision of the incremental test plan

This order is the reviewer's proposal, not an ordering mandated by GitHub issues or ADRs.
It retains the existing one-test-at-a-time workflow and separates documentation corrections from
behavior changes. Do not paste all graph tasks into the current provider checklist as one large test.

1. **Repair the baseline checklist.** `plan.md` marks creation of an empty
   `ComplexObjectValueProvider` complete, but neither that class nor its test exists in inspected
   sources. Confirm the intended class name and return shape before resetting the checkbox.
   Only `ConstantValueProvider` and its two tests are present in the new value package.
2. **Review #169 before fresh graph work.** Start with aliasing of input collections, mutation of
   retained components, stable identity/hash during evaluation, and one graph used with two sessions.
   Decide whether to repair, reuse selected parts, or replace this candidate after that review.
3. **Finish the current provider contract in small steps.** Suggested test increments: empty
   provider; missing key returns an undefined provider; existing String entry preserves its value;
   entries of different types preserve those types; input-map mutation cannot change the provider
   snapshot. Missing-key semantics come from ADR-0010; snapshot/input-map protection is a proposed
   test derived from the immutability goal. Resolve the contract questions below first.
4. **Specify one format-independent rule slice (#170).** Suggested increments: represent one
   literal equality with separate operands/operator; build it without file I/O; adapt a tiny CSV
   fixture to the same representation. Extend to outcomes and fallback rules one case at a time.
5. **Complete a minimal immutable graph (#168).** One condition in one rule, then shared operand
   identity across two rules, then a table-to-table dependency. Preserve graph descriptions across
   evaluation. Check the candidate PR's coverage before adding duplicate tests or code.
6. **Expose dependencies (#162).** First direct static dependencies, then transitive dependencies.
   Proposed validation tests include missing references and cycles, since the planned structure is
   a DAG. Dynamic coordinates should retain unresolved dependency descriptions until binding.
7. **Add session-local evaluation/cache (#165).** Same node requested twice in one session is
   evaluated once; the same graph in another session reads that session's data; evaluation does not
   mutate graph nodes. Only then test early elimination preserves outcomes and ambiguity errors.
8. **Use #164 as an acceptance scenario, clarify #163 separately.** Two factory instances share
   rules but produce independent outcomes from different state. The namespacing and `this` syntax
   question should be settled explicitly before accepting parser changes.
9. **Measure before optimizing further (#166).** Test externally observable evaluation counts,
   then aggregation across sessions. Add timing only at the chosen instrumentation boundary.

## Contract questions before turning recommendations into accepted tests

- [ADR-0009](adr/0009-authoritative-value-typing-via-contextual-providers.md) explicitly supersedes
  ADR-0008 and replaces the object-path/type-guessing approach with flat typed values supplied by
  connectors. [ADR-0007](adr/0007-type-discovery-at-graph-construction-time.md) still says Accepted
  and describes reflection-based object-path typing. Clarify how much of ADR-0007 remains intended
  before implementing reflection or a type-discovery SPI. This is an unresolved documentation
  relationship, not a decision in this report to supersede ADR-0007.
- ADR-0009 uses `ComplexObjectDataProvider`, ADR-0010 and `plan.md` use
  `ComplexObjectValueProvider`. Choose one contract/name before adding production types.
- ADR-0009 specifies `Map<String, ConstantValueProvider<?>>`, whereas ADR-0010 requires a missing
  key to yield an `UndefinedValueProvider`. Define the lookup return type and undefined value
  semantics, including comparison with a real String value `"undefined"`.
- The current [`ValueProvider`](../src/main/java/ru/ewc/decita/core/value/ValueProvider.java) still
  accepts the legacy `ComputationContext`. The graph/session design should clarify that boundary;
  do not infer that the new execution context already exists from its planned name.
- #163 and #164 need a domain-level binding/resolution example before committing to shared names
  or a new reserved `this` locator. Their motivating use case is clearer than their proposed syntax.

## Local code references used for comparison

- [Current test plan](../plan.md), [constant provider tests](../src/test/java/ru/ewc/decita/core/value/ConstantValueProviderTest.java).
- [ComputationContext](../src/main/java/ru/ewc/decisions/api/ComputationContext.java),
  [Coordinate](../src/main/java/ru/ewc/decisions/core/Coordinate.java),
  [DecisionRuleFragments](../src/main/java/ru/ewc/decisions/core/DecisionRuleFragments.java).
- [SourceLines](../src/main/java/ru/ewc/decisions/input/SourceLines.java),
  [RuleFragments](../src/main/java/ru/ewc/decisions/api/RuleFragments.java),
  [coordinate tests](../src/test/java/ru/ewc/decisions/CoordinateTest.java),
  [end-to-end tests](../src/test/java/ru/ewc/decisions/api/EndToEndTest.java), and [changelog](../CHANGELOG.md).
