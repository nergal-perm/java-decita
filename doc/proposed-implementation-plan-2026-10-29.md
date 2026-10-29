# Proposed implementation plan after the immutable graph milestone

Date: 2026-10-29

Baseline: master `989d1c9d3fcc5fc86c3a052179b9f0a7e34f5c94`

Status: proposal for review. This document does not accept or amend an ADR, authorize implementation, or replace the next-test workflow in [plan.md](../plan.md).

## 1. Intended outcome and limits

Deliver a source-independent decision engine that evaluates one immutable compiled graph against an explicit input snapshot, returns a decision without application-side effects, and safely reuses that graph across independent calls. Then connect the existing table dialect, support instance-specific bindings, and prove external extensibility. Measure real evaluation before introducing pruning.

This plan expands and corrects the [earlier roadmap](development-roadmap-2026-10-29.md). In particular, it makes the cache/purity conflict, ordered assignment semantics, public API timing, and limits of plugin and profiling claims explicit. Recommendations below are conditional where an accepted ADR needs clarification. They must not become architectural authority merely because this document exists.

The first useful milestone is one real decision through the new graph evaluator. Completing every future syntax, plugin, and optimization decision is not a prerequisite for that milestone.

## 2. Evidence and what is already implemented

The baseline is the merged result of [PR #169](https://github.com/nergal-perm/java-decita/pull/169), closing [#168](https://github.com/nergal-perm/java-decita/issues/168). Its source tree matches the previously verified PR head `401a815`: 106 tests and Qulice passed during that implementation. Those results establish the tested baseline, not completeness of the future engine. This documentation review did not rerun the full build.

Evidence used:

- [Current backlog review](issue-review-after-168-2026-10-29.md): seven open issues, no open PRs at the recorded 2026-10-29 inspection. Issue status can change after that inspection.
- [Immutable graph implementation](../src/main/java/ru/ewc/decisions/core/InMemoryDecisionGraph.java), [normalization](../src/main/java/ru/ewc/decisions/core/GraphNodes.java), and [structural validation](../src/main/java/ru/ewc/decisions/core/GraphStructure.java): copied collections, canonical references, duplicate/conflict checks, typed edges, and DAG validation.
- [GraphSessionIsolationTest](../src/test/java/ru/ewc/decisions/core/GraphSessionIsolationTest.java): shared descriptions generate fresh legacy evaluations. This does not prove evaluation by a new engine or a session cache.
- [Legacy ComputationContext](../src/main/java/ru/ewc/decisions/api/ComputationContext.java): CSV/state/output concerns remain combined with evaluation.
- [ValueProvider](../src/main/java/ru/ewc/decita/core/value/ValueProvider.java): still receives the legacy context. [ConstantValueProvider](../src/main/java/ru/ewc/decita/core/value/ConstantValueProvider.java) returns its supplied value reference.
- [Current module descriptor](../src/main/java/module-info.java): graph and new value packages are not exported.

No new DecisionEngine, ExecutionContext, complex-object provider, undefined provider, source-description compiler, or ServiceLoader composition was found in the inspected production sources.

Two focused probes supplement source inspection. They are observations about this baseline, not new acceptance tests committed by this plan:

1. Wrapping an ArrayList in ConstantValueProvider, then modifying the caller's list, changes the returned value from `[original]` to `[original, changed]`. Final reference fields alone do not establish immutable inputs.
2. Starting with state `x=1, y=2`, performing ordered assignments `x := y`, then `y := x`, produces `x=2, y=2`. The second assignment reads the first assignment's update. Evaluating both right-hand sides against the initial snapshot would instead produce `x=2, y=1`.

The second observation follows the actual [Rule](../src/main/java/ru/ewc/decisions/core/Rule.java) and [Assignment](../src/main/java/ru/ewc/decisions/commands/Assignment.java) path. Preserve it or explicitly approve a language change.

## 3. ADR obligations and gaps

An Accepted label records a decision; it does not establish implementation or resolve conflicts with later records.

| Record | Baseline and remaining obligation |
| --- | --- |
| [0001: decision records](adr/0001-record-architecture-decisions.md) | Process exists. Record architectural changes through it; this proposal cannot silently supersede another decision. |
| [0002: engine/connectors](adr/0002-separation-of-engine-and-connectors.md) | Requires source separation and extensibility through SPI/ServiceLoader, including formats and logic/new row types/result interpreters. A legacy reader seam or one operator plugin alone is insufficient. |
| [0003: shared components/session](adr/0003-shared-component-management-and-evaluation-optimization.md) | Immutable graph representation is implemented. Actual graph execution and session-local memoization are missing. |
| [0004: identity keys](adr/0004-execution-context-key-strategy.md) | Canonical graph references exist. Reference-identity evaluation caching is missing; ordinary value-keyed HashMap does not establish this contract. |
| [0005: pure core](adr/0005-pure-functional-core.md) | Legacy execution remains effectful. Its literal requirement that core components be immutable/stateless also needs reconciliation with 0003's mutable session context. |
| [0006: remote/composable providers](adr/0006-valueprovider-design-for-remote-data-and-composability.md) | Original remote-fetch orchestration is unimplemented and conflicts with a strictly pure call if invoked during evaluation. Reconcile its object-path approach with 0009. |
| [0007: construction-time typing](adr/0007-type-discovery-at-graph-construction-time.md) | Reflection/object-path type discovery is unimplemented and still Accepted. 0009 does not explicitly supersede it. |
| [0008: constant type inference](adr/0008-type-inference-for-constants.md) | Explicitly superseded by 0009. Do not implement it as a separate outstanding feature. |
| [0009: authoritative input types](adr/0009-authoritative-value-typing-via-contextual-providers.md) | Requires typed flattened inputs and contextual literal conversion. A generic constant wrapper is only a starting point. |
| [0010: missing fields](adr/0010-handling-missing-fields-in-complexobjectvalueprovider.md) | Requires the singleton UndefinedValueProvider on missing lookup. Lookup absence, null policy, comparison behavior, and conversion errors still need separate contracts. |

## 4. Proposed delivery order and ownership

| Work item | Deliverable | Depends on | Review evidence |
| --- | --- | --- | --- |
| P0 | Focused contract decisions and explicit ADR relationships | Baseline inspection | Written choices and affected ADRs; no contradictory completion claims |
| P1 | Typed input snapshot and missing lookup | Relevant P0 input choices | Original provider scenarios plus aliasing/type tests |
| P2 | Minimum source-independent descriptions and public boundary | Relevant P0 typing/purity choices | Equivalent programmatic rule; external API sketch/test |
| P3 | Description compiler and static dependency query | P2 | Derived relationships, sharing, unresolved references, cycle tests |
| P4 | Pure graph evaluation and private session memo | P1, P3; purity decision before mutable cache | Real decisions, independent calls, identity-cache evidence |
| P5 | CSV connector and explicit legacy compatibility | P4; assignment policy before ASG migration | Old/new cases with outcomes, errors, commands and final state |
| P6 | Reusable behavior with instance bindings | P4; P5 only for legacy-dialect binding support | Two factories, independent owners/workers, stable graph |
| P7 | External plugins and packaging proof | P2/P4 contracts; P5 for CSV plugin example | Independent consumer and each claimed extension family |
| M1 | Counts, bounded timing, then pruning | P4; P3 dependency query for dependency-based pruning | Baseline workload and semantic equivalence |
| H1 | Documentation/backlog housekeeping | Relevant current evidence | Correct examples and accurate issue/ADR status |

P1 and P2 can progress independently once their own contracts are settled. Static P3 need not wait for dynamic binding syntax. P5 and P6 need not wait for pruning. Public API review starts in P2 and is exercised in P4; it is not postponed to P7.

For every selected increment, the implementer prepares the failing test, code and evidence; the reviewer checks the linked ADR clauses and observable behavior; the project maintainer decides architectural changes and compatibility breaks. These are proposed responsibilities, not claims that named people have accepted assignments. Use the repository's ADR process for approval and recording; do not mark a proposal Accepted merely after code review.

Each work item below has its own readiness and completion conditions. An unresolved decision blocks the behavior that depends on it, not unrelated provider tests, source inspection, or documentation corrections.

## 5. P0 — Resolve the contracts that would otherwise contradict implementation

**Purpose and authority:** clarify 0003–0007, 0009 and 0010 before committing to incompatible APIs. The output is a short set of proposed/accepted decision changes with explicit references. Do not redesign all architecture in one discussion.

### P0.1 Private mutable memo versus immutable core

**Finding:** 0003 proposes a mutable per-call context; 0005 literally describes immutable/stateless core components. Private mutation can preserve externally pure behavior, but that interpretation is not yet stated consistently.

**Options:**

- Recommend a fresh private mutable memo for each evaluation and clarify 0005's immutability scope: graph, inputs and shared components immutable; no observable cross-call state. This follows 0003 economically but needs an accepted clarification.
- Thread immutable memo values through evaluation. This follows stricter internal immutability at added allocation and implementation cost; amend the mutable-context wording in 0003.
- Put memo management in an outer orchestrator with explicit returned cache updates. This separates mutation but introduces another evaluation protocol.

**Ready for P4 memo:** the maintainer records which interpretation applies. Before that, a deliberately uncached pure evaluation slice may proceed. Do not implement a mutable context and claim literal compliance with both records without this decision.

### P0.2 Construction-time types versus authoritative runtime inputs

**Finding:** 0007 relies on remote schemas/reflection, while 0009 moves flattening and typing to connectors. Only 0008 is explicitly superseded.

**Options:**

- Recommend explicitly superseding or narrowing 0007 for the first flattened-input path; literal conversion uses the authoritative input type under 0009.
- Retain optional connector-supplied schema for early validation, while keeping object traversal outside the core. Amend 0007's scope and state what happens when runtime types differ.
- Preserve mandatory schema-based compilation, paying the schema acquisition/versioning cost and reconciling 0009 accordingly.

**Ready for typed comparisons:** record typing time, conversion time and schema disagreement behavior. Constant lookup tests do not require a whole schema system.

### P0.3 Remote acquisition versus pure evaluation

**Finding:** a provider that causes HTTP/file access during the call remains effectful even when it delegates that access to an SPI.

**Options:**

- Recommend connectors acquire, flatten and type a complete snapshot before the first core call. Fetch caches, timeouts, retries and freshness belong to the outer application/connector.
- If keys depend on intermediate decisions, return an explicit request for additional inputs and let the outer application acquire them before resuming/restarting under a defined protocol. Specify which cached results remain valid when inputs expand.
- Permit effectful remote evaluation only through a separately agreed boundary and an amendment to 0005; do not describe that path as strictly pure.

**Ready for initial engine:** support prefetched snapshots and state that limitation. Defer adaptive acquisition until a real scenario requires it; do not imply prefetching solves every remote dependency.

### P0.4 Value lookup, missingness and immutable leaves

**Finding:** storing `Map<String, ConstantValueProvider<?>>` does not prevent a lookup method from returning a wider provider type that includes UndefinedValueProvider. This is an API gap, not an unavoidable contradiction.

**Options:**

- Recommend retaining constant-only storage and returning a provider contract capable of constants and undefined values.
- Introduce an explicit resolved-value result with separate present/missing cases; reconcile its relationship with 0010's required provider.
- Widen stored provider types only if deferred/computed entries are needed; this expands the 0009 contract and must not accidentally admit remote effects.

Choose the single name ComplexObjectValueProvider used by 0010 and plan.md unless there is a reason to retain the older name. Treat naming reconciliation as editorial unless it changes behavior.

For leaf values, recommend an initial supported immutable set matching original scenarios: String, Boolean, Double and LocalDate. Normalize/copy data at the connector boundary. Alternatives are an explicit caller-immutability contract with a limited guarantee, or defensive adapters for specified mutable types. Generic deep copying of arbitrary Java objects is not a credible implicit guarantee.

**Ready for P1:** agreed lookup type, storage/leaf policy, null rejection or explicit null representation. **Ready for P4:** also agree comparison behavior below.

### P0.5 Missing comparison and error policy

0010 determines missing lookup, not the full expression language. Record a truth table for equality, ordering, negation and any explicit missing-value test.

Options include propagating an unknown result that does not match a rule, treating comparisons with missing values as false, or producing a defined evaluation error when a missing value is used. The false option needs special care: negating false may make a missing field match. Select after checking fallback and ambiguity examples; this plan does not silently adopt one.

Keep these separate: absent external field, real String `"undefined"`, null input, invalid table reference, malformed expression, and failed type conversion. Define diagnostics and where each error is detected. Do not turn all failures into missing values. This choice blocks affected comparisons, not the empty-provider test.

**P0 completion:** each necessary decision has status, affected ADRs, chosen option, rejected alternatives, compatibility impact, and an example that distinguishes the options. Reopen it if runtime schemas, dynamic acquisition or accepted plugin behavior invalidate the initial assumptions.

## 6. P1 — Complete typed inputs from the original test plan

**Sources:** original [plan.md](../plan.md), 0009, 0010, and 0005's input isolation. **Input:** P0.4 contract. **Output:** immutable typed input snapshot and provider lookup, usable without legacy ComputationContext.

Proceed one failing test at a time:

1. An empty provider can be constructed under the agreed input contract.
2. Looking up an absent key returns the singleton UndefinedValueProvider required by 0010; repeated absent lookups share that instance.
3. An existing String entry resolves without changing its type.
4. Boolean, Double, String and LocalDate entries preserve their types in one snapshot.
5. Mutating the caller's map after construction cannot change lookup results.
6. Null and mutable-leaf inputs are rejected or normalized according to the selected policy.
7. Missing value remains distinct from the literal String `"undefined"`.

**Finding:** the current ConstantValueProvider has a final field but retains mutable aliases; ValueProvider also depends on the effectful legacy context. Merely copying the map leaves both issues unresolved.

**Correction options:** introduce the minimum new immutable input interface and adapt legacy callers, or temporarily support both APIs with a clear retirement boundary. Recommend the first for the new path, keeping legacy adaptation outside it. An API-wide rewrite before lookup behavior is demonstrated adds unnecessary risk.

**Done:** tests exercise consumer-visible lookup and aliasing; the new input path needs no State, file reader or output publisher. Unsupported leaves and null policy are documented. Do not check off comparison semantics or remote support because lookup passes.

## 7. P2 — Introduce the smallest source-independent rule description

**Sources:** [#170](https://github.com/nergal-perm/java-decita/issues/170), 0002, 0005, 0009. **Input:** agreed initial expression/type scope. **Output:** one programmatically constructed rule with a named outcome, plus a small public contract sketch.

Start with a typed external-field reference compared with a literal. Then add numeric comparison and negation as separate increments. Specify how table-output references differ from external fields, preserve literal text until the selected conversion point, and state whether reference/reference and literal-left expressions are initially unsupported.

**Finding:** immutable RuleFragment payloads still encode operators inside strings such as `">18"`. Immutability does not make that expression model source-independent.

**Options:**

- Recommend explicit operand/operator descriptions with the smallest necessary contracts. Keep operator lookup replaceable so a closed enum/switch does not become the only extension route.
- Retain legacy RuleFragment as connector input and translate it to the new description. This minimizes compatibility disruption and can coexist with the first option.
- A general-purpose expression tree can support more future language features but should wait for concrete expressions that require it.

A separate operator object is a proposal, not a class structure mandated by #170. YAML support is not required to prove source independence. Do not change the public legacy RuleFragment record merely to fit the new model without a compatibility decision.

**Public API review now:** choose a stable new package/API or selective exports of deliberately stable types. Alternatively declare a temporary classpath-only experimental API with a migration limitation. Recommend a small new boundary and legacy adapter; avoid exporting the entire legacy core by convenience. P4 must include a consumer outside implementation packages.

**Done:** a rule can be described without CSV cells, file paths or legacy Condition.from; supported expression forms and unsupported cases are explicit; operator and input contracts can be exercised by an external consumer. Reopen if an actual custom operator or source format cannot be expressed without modifying the core.

## 8. P3 — Compile descriptions and expose real static dependencies

**Sources:** 0003, 0004, [#162](https://github.com/nergal-perm/java-decita/issues/162), completed #168. **Input:** P2 descriptions. **Output:** validated graph built from rules, and a bounded dependency query.

Test one rule, shared operands across two rules, explicit fallback, and one table depending on another. Reuse graph canonicalization and validation; preserve duplicate/conflict checks and reconnect nested references to canonical objects.

**Finding:** validation of supplied edges cannot prove that a compiler extracted every semantic dependency. Four edge kinds alone do not establish source-level completeness.

**Options:** derive static table dependencies during compilation and expose direct/transitive queries; or create a separate analyzer over the same descriptions and cross-check its result with compilation. Recommend shared derivation initially to avoid two disagreeing interpretations. Keep presentation/diagram rendering outside the core.

Specify edge direction and what each relationship means. Diagnose cycles and broken known table references with source information where available. A missing external field follows the undefined policy. A potentially absent output field needs an explicit evaluation rule, rather than being confused with an unknown table.

**Dynamic references:** return unresolved dependency descriptions until a binding is provided, or initially reject dynamic references explicitly. Both are honest limited contracts. Claiming a complete concrete dependency set before resolution is not.

**Done:** tests derive dependencies from descriptions, show shared canonical identities and missing/cycle diagnostics, and distinguish static completeness from unresolved dynamic targets. Static dependency support can be delivered before #163 syntax or visualization UI.

## 9. P4 — Execute the new graph and prove independent sessions

**Sources:** 0003–0005, 0009–0010, the correctness portion of [#165](https://github.com/nergal-perm/java-decita/issues/165). **Input:** P1/P3, expression policies; P0.1 before mutable memo. **Output:** one real pure decision API, then session-local memoization.

### P4.1 First real decision

Evaluate a compiled graph with one typed condition and one named outcome. Then add fallback, no-match policy, multiple-match ambiguity, chained table outputs and failed literal conversion. Use the new graph/typed provider path. Recreating legacy Conditions from graph metadata does not meet this milestone.

Inputs and compiled descriptions must remain unchanged. Return a typed decision and, when supported, action descriptions. Keep logging, clocks, filesystem/network operations and application-state writes outside this call.

### P4.2 Session lifetime and identity

Use explicit identity-key semantics for memoization; an IdentityHashMap is one implementation option, not a required public API. A memo belongs to one fixed combination of graph, input snapshot, binding snapshot and operator registry. Any relevant change starts a new session or follows a separately specified invalidation protocol.

**Finding:** a cache on shared nodes or reused across calls can return another session's result. A value-keyed map can also conflate distinct computation objects within one session. Graph normalization is necessary but does not itself implement session isolation.

**Options:** recommend a fresh private memo per call after P0.1 is settled; use an uncached evaluator first if that decision is pending; use persistent immutable memo passing if selected instead. Never hide a shared cache behind the word context.

### P4.3 Observable proof

Require the following tests:

- One graph with age 21 and age 17 selects the appropriate result in both call orders.
- The same calls on separate threads remain independent; graph/input snapshots are unchanged.
- A shared node requested through two paths computes once within a session; a new session computes again.
- Distinct cache-key objects that compare equal by value remain separate in a focused identity-contract test. Valid compiled graphs still follow their canonicalization constraints.
- Two factories use the same logical field names and shared graph, but different owners/workers and results.
- An external consumer uses the intended public/module boundary.

Use a deterministic evaluation trace or a test operator that records invocations to verify actual computation; disclose that the recording operator is test instrumentation. Do not add production cache getters solely for tests or pass a fake cache test while the real engine ignores it.

**Done:** evidence follows the actual engine path, including error/fallback/ambiguity cases. Inputs alone do not determine results when graph/operator definitions differ: the pure call depends on all explicit compiled definitions and snapshots. Do not claim arbitrary third-party Java plugins are pure just because the engine API is.

## 10. P5 — Add the CSV connector without silently changing behavior

**Sources:** 0002, 0005, #170, and the existing dialect. **Input:** working P4. **Output:** source reader/parser outside the core that produces the same descriptions as programmatic input, plus a bounded compatibility adapter.

Begin with one CND/OUT table. Add HDR, ASG, explicit/implicit fallback, table references, nested/multiple placeholders and ambiguity cases one at a time. Use the [backlog review](issue-review-after-168-2026-10-29.md) to locate closed issues #139, #140, #150, #152, #155 and #161 as compatibility history; closed status alone is not proof of complete engine support.

Compare old/new outcomes, relevant errors, command order and final state on named fixtures. Keep old string-map clients behind an explicit adapter while the new decision representation becomes typed. Preserve existing table-name precedence in that adapter until a deliberate namespace migration is accepted.

### P5.1 Ordered assignments need an explicit interpreter

**Finding:** returning already evaluated assignment values can change sequential semantics. For initial `x=1,y=2`, old `x:=y; y:=x` yields `2,2`; initial-snapshot evaluation yields `2,1`.

**Options:**

- Recommend ordered symbolic assignment descriptions. The outer interpreter evaluates each right-hand side against the state visible at that step, then applies the write. If a right-hand side requires a decision/table lookup, use the corresponding fresh input snapshot/session; stale memo reuse would be incorrect.
- Model state transitions purely and return next state plus effects. Decide intermediate visibility, application timing and failure/atomicity rules explicitly.
- Adopt simultaneous assignments as a deliberate language change with versioned migration. This is not transparent compatibility.

Add named compatibility fixtures for a later assignment failing after earlier writes, and for repeated table-dependent right-hand sides across writes. Inspect the legacy caching/evaluation path for those fixtures rather than assuming that fresh evaluation automatically matches it. Preserve the observed behavior or record a deliberate change. Returning a pure Decision does not by itself decide whether the outer interpreter is transactional.

### P5.2 Connector failure handling

**Finding:** existing source reading can hide I/O failures as empty input; malformed/empty rows can fail indirectly through array assumptions.

**Options:** recommend structured connector diagnostics with file/table/row information; retain legacy behavior only in a named compatibility mode. A full CSV quoting/dialect upgrade is a separate feature, not an implicit rewrite requirement.

**Done:** equivalent descriptions feed the same evaluator; migration fixtures cover the supported dialect and ordered effects; unsupported syntax and intentional differences are documented. No State mutation or source loading enters the new engine. Reopen when a real legacy fixture reveals an unrecorded semantic difference.

## 11. P6 — Bind shared behavior to separate instances

**Sources:** [#164](https://github.com/nergal-perm/java-decita/issues/164), exploratory [#163](https://github.com/nergal-perm/java-decita/issues/163), 0003–0005. **Input:** P4; early two-factory example already exists there. **Output:** explicit instance-binding behavior for owners/workers without mutating shared graph descriptions.

Test two factories of the same kind, shared rules and logical names, different bound data, repeated calls and concurrent calls. Define binding lifetime and how it participates in session validity. If dependencies vary with binding, expose the distinction between static possibilities and resolved concrete targets.

**Finding:** #163's `this::property` is tentative research, not an accepted language requirement. Data/table collisions and dynamic references cannot be solved safely by an undocumented naming convention.

**Options:**

- Recommend an explicit binding map and distinct reference kinds in descriptions first. It supports the #164 scenario without committing to surface syntax.
- Add reserved `this` syntax in the connector after defining grammar, collision/precedence and migration behavior.
- Use explicit qualified instance references throughout. This is simpler to inspect but may duplicate rule descriptions and weaken reuse if bindings are expanded too early.

**Done:** graph identity is shared while binding/input state stays private; dependency queries describe their scope honestly. Implement #163 syntax only if the accepted scenario needs it. No blanket renaming of existing coordinates is implied.

## 12. P7 — Prove external extensibility and package the boundary

**Sources:** 0002, 0005; public boundary started in P2/P4. **Input:** stable minimal evaluator/description contracts and CSV example. **Output:** external consumer and independently provided extensions.

Compose plugins with ServiceLoader outside evaluation. Define duplicate-provider handling, discovery failures and supported compatibility/version policy. Freeze selected operator definitions for a compiled graph/session; hot reload requires a new registry/version and valid cache lifetime.

**Finding:** an operator demo alone does not close all of 0002. Arbitrary custom payloads/callbacks can also reintroduce mutable aliases and effects.

**Options:**

- Recommend incremental demonstrations: external source connector, custom pure operator, then a meaningful new row/action description with an outer result interpreter. Validate/copy accepted descriptions and state extension obligations.
- Limit the first release to named extension families and explicitly leave the others open in ADR/issue tracking.
- Split into multiple Maven artifacts immediately only if an external consumer proves that a single artifact with deliberate exports is insufficient.

Test both the intended module path and any promised classpath use. Do not export internal implementation packages merely to make an example compile. Document that callback purity is an extension obligation, with contract tests; unrestricted Java code cannot be mechanically guaranteed pure by these interfaces.

**Done:** each claimed extension family works from a separate consumer without edits to core format/operator switches. Discovery runs before evaluation. Unimplemented families remain visible; no claim that all of 0002 is complete based on one plugin.

## 13. M1 — Count real work, then optimize without changing decisions

**Sources:** [#166](https://github.com/nergal-perm/java-decita/issues/166), [#165](https://github.com/nergal-perm/java-decita/issues/165), 0003–0005. **Input:** actual P4 evaluator. **Output:** bounded baseline followed by justified pruning. This branch does not block P5.

### M1.1 Deterministic counts first

Distinguish requested-node visits, actual computations, cache hits and remote acquisitions. The last belongs to connectors. Define graph version, input/binding set, session count and grouping before aggregating observations. Use cases with shared operands, late failure, multiple matching rules, fallback and missing inputs.

**Finding:** a lower computation count alone does not prove equivalent behavior or faster execution.

**Options:** recommend deterministic counts/trace returned with evaluation evidence or collected in a separately specified diagnostic mode; alternatively use test-only instrumentation for the first baseline. Record overhead and avoid promising an unobserved speedup.

### M1.2 Timing boundary

**Finding:** invoking a clock/logging decorator during evaluation introduces effects even if its class lives outside the core package.

**Options:** recommend outside-the-call total timing plus deterministic internal counts first; use a clearly separated effectful diagnostic evaluator for per-condition timing; or benchmark individual operators outside the production call with narrower conclusions. Per-condition timings require an explicit purity decision, not a package-placement argument.

Record JDK, warmup, workload and run conditions. Do not aggregate unrelated graphs or include network acquisition in pure evaluation time without labeling the scope.

### M1.3 Pruning only after a baseline

Use known input/resolved coordinates to avoid work only after defining dependencies and undefined/error semantics. Preserve outcomes, fallback, ambiguity detection, errors and command order. Stopping at the first true rule is unsafe under unique-match behavior.

**Options:** recommend conservative dependency-based elimination with before/after semantic comparisons; alternatively retain the unoptimized evaluator as the initial release if benefits are unclear. Record a specific optimization question and workload before scheduling implementation.

**Done:** comparisons show equivalent behavior and separately report counts/timing changes for the stated workload. Keep #165's implemented memoization scope separate from any still-open pruning scope.

## 14. H1 — Keep documentation and backlog claims accurate

**Sources:** 0001, completed #168, current source/API and issue inspection. **Output:** bounded documentation corrections and proposed backlog updates.

- Correct the [core README](../src/main/java/ru/ewc/decisions/core/README.md): old constructor examples and the claim of allocation-free traversal do not describe current query implementations.
- Update merged/development wording in the [top README](../README.md), preserving accurate statements about the still-unimplemented evaluator.
- Review [#23](https://github.com/nergal-perm/java-decita/issues/23): its five child puzzles are closed. Propose closure or rewrite with genuinely outstanding acceptance conditions; do not infer that every historical feature is complete in the new engine.
- Keep historical review artifacts labeled by baseline. Do not reuse pre-merge graph findings as current defects.
- Promote only the next selected provider slice into plan.md; retain completed constant and graph history. Add observable tests instead of checking off constructor existence.

**Finding:** issue closure, accepted ADR status, test count and a proposed plan are different kinds of evidence. None substitutes for runtime proof of an unimplemented capability.

**Options:** recommend small corrections beside the affected work; alternatively make one documentation-only follow-up. Remote issue edits/closure require a separate execution instruction; this document only proposes them.

**Done:** examples match current API, current and target behavior are distinguished, and each completion statement names its scope. Recheck live issue status before editing GitHub.

## 15. Review conclusions by work item

Every proposed item was checked for agreement with the cited ADRs, contradictions with other steps, and whether its claimed result would have observable evidence. No item is declared implemented by this review.

| Item | Review conclusion | Weakness and required treatment |
| --- | --- | --- |
| P0 | Necessary reconciliation, with unresolved choices | 0003/0005 and 0007/0009 cannot be treated as automatically reconciled. Record the selected interpretation; gate only affected work. |
| P1 | Consistent with 0009/0010 after input choices | Copying the map does not freeze leaves. Preserve the singleton absence contract and decide null/mutable-value policy. |
| P2 | Fits 0002/#170 as a bounded first model | CSV-shaped immutable fragments remain coupled. Separate descriptions, retain an adapter, and define the public boundary early. |
| P3 | Fits the static portion of #162 and graph reuse | Structural checks do not establish extracted dependency completeness. Derive and test relationships; report unresolved dynamic scope. |
| P4 | Fits 0003/0004; conditional on 0005 clarification | Graph reuse tests do not prove new execution. Exercise the actual evaluator, private session lifetime and identity keys. |
| P5 | Fits 0002/0005 only with an explicit effect interpreter | Precomputed assignment values can change legacy results. Compare ordered state transitions, failures and table-dependent reads. |
| P6 | Fits #164; #163 remains a design investigation | Tentative syntax and namespace precedence need explicit choices; begin with bindings without graph mutation. |
| P7 | Fits 0002 in separately proven extension families | One plugin does not prove all extensibility. Test external consumers and do not infer purity of arbitrary callbacks. |
| M1 | Fits #165/#166 after actual execution exists | Clock callbacks affect purity; pruning can change ambiguity/errors. Separate timing scope and compare full behavior. |
| H1 | Fits decision/evidence discipline in 0001 | Issue status and test counts are insufficient runtime evidence. Correct bounded claims without declaring target capabilities complete. |

The recommended sequence is therefore usable as a proposal with local decision gates. It is not an unconditional assertion that every proposed design already conforms to every Accepted ADR.

## 16. Issue coverage and explicit deferrals

| Issue | Planned treatment | What must not be claimed prematurely |
| --- | --- | --- |
| #170 | P2 descriptions, P5 connector, P7 extension proof | Immutable CSV-shaped payloads alone do not establish source independence. |
| #162 | P3 static extraction/query; P6 binding resolution | Graph traversal alone is not dependency extraction; dynamic results may be incomplete until bound. |
| #165 | P4 correct session memo; M1 later pruning | Memoization is not completion of early rule elimination. |
| #166 | M1 counts, aggregation and bounded timing | Counts do not establish per-condition timing or general performance. |
| #164 | P4 early acceptance scenario, P6 full binding | Different snapshots alone do not define all dynamic binding semantics. |
| #163 | P6 investigation and optional grammar decision | Tentative this syntax is not an accepted requirement. |
| #23 | H1 maintenance recommendation | Closed child issues do not establish new-core coverage. |
| #168 / PR #169 | Completed immutable graph baseline | They do not provide the new engine/cache/compiler. |

Do not prioritize by issue number or ADR status alone. Defer general expression languages, YAML, adaptive remote fetching, full CSV dialect replacement, mandatory artifact splitting, hot reload and aggressive pruning until a selected scenario justifies them. If scope changes, update the affected acceptance conditions and ADR links before calling the expanded item complete.

## 17. Execution and review protocol for the selected slice

This document is a proposal, not a batch implementation order. Once the maintainer selects a slice:

1. Recheck current master, working-tree changes, relevant ADR status and linked issue acceptance conditions.
2. Record only decisions needed for that slice. Prepare architectural options for the maintainer through the ADR process; routine editorial choices do not need a new architectural approval.
3. Add the next smallest observable test to plan.md. Follow one failing test, minimal implementation, then improvement while green. Separate structural and behavior changes in reviewable commits.
4. Run focused tests while developing, then the repository's required test and Qulice checks before a code completion claim. The previously working environment is Java 21 with Maven compiler release 17; recheck the actual environment rather than assuming it. Required code checks are `mvn clean test -Dmaven.compiler.release=17` and `mvn clean verify -Pqulice -Dmaven.compiler.release=17` under that JDK.
5. Report changed behavior, tested cases, unsupported cases, remaining decisions and links to the relevant commits/tests. Do not report a percentage of architectural compliance.
6. Update this proposal only when evidence or accepted decisions change the sequence. Reopen affected choices if types, bindings, plugins, remote needs or legacy behavior invalidate their assumptions.

For this documentation work, verification is source/link/consistency review; no production behavior changed. The full build is not evidence that prose resolves an architectural conflict.

## 18. Recommended next work product

Prepare the focused P0.4 input contract and ADR relationship corrections, then continue the first unfinished provider scenario in plan.md under P1. In parallel as planning work, settle the P0.1 purity/cache interpretation and P0.2 typing-time choice needed for P2–P4.

The next integration target is a programmatically supplied rule compiled into the shared graph and evaluated with typed snapshots by the new public API. Demonstrate two independent calls before investing in advanced optimization. CSV compatibility follows with an explicit ordered-assignment interpreter decision; instance binding and extension proofs then build on an already exercised engine.
