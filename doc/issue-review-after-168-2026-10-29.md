# GitHub backlog after merging DecisionGraph

Reviewed live on 2026-10-29 after merge commit
[`989d1c9d3fcc5fc86c3a052179b9f0a7e34f5c94`](https://github.com/nergal-perm/java-decita/commit/989d1c9d3fcc5fc86c3a052179b9f0a7e34f5c94).
This is a source-linked research snapshot and sequencing recommendation, not a new architectural
decision or accepted implementation plan. It supplements the historical
[earlier issue review](issue-review-2026-10-29.md); that report's open-PR observations describe
the earlier state and must not be applied to the merged implementation.

## Current evidence and coverage

- The connected GitHub API returned **seven open issues**: #23, #162, #163, #164, #165, #166,
  and #170. All seven bodies and discussions were read. #23 has eight comments, #165 one;
  the remaining five have none. [Open issue query](https://github.com/nergal-perm/java-decita/issues?q=is%3Aissue+is%3Aopen).
- The open-PR search returned **zero open PRs**. Twenty recently updated PRs and twenty
  latest-created issues were screened; no issue newer than #170 appeared.
  [Open PR query](https://github.com/nergal-perm/java-decita/pulls?q=is%3Apr+is%3Aopen),
  [latest-created issues](https://github.com/nergal-perm/java-decita/issues?q=is%3Aissue+sort%3Acreated-desc).
- [PR #169](https://github.com/nergal-perm/java-decita/pull/169) is merged, not draft. GitHub
  records merge time **2026-10-29 17:47:02 UTC**, head `401a815e9709f13fa2f914030e472f524c337aad`,
  merge commit above, five commits and 31 changed files. Its discussion endpoint returned no
  comments. [#168](https://github.com/nergal-perm/java-decita/issues/168) is closed as completed
  at **17:47:03 UTC**. The old PR description's broad coverage/performance claims are author
  claims, not measurements established by this research.
- Eighteen relevant closed issue bodies/statuses were fetched: #26, #53, #54, #85, #120,
  #139, #140, #150, #152–#161. Full discussions for #153 and #160 were also read.
  This is a targeted historical check, not an exhaustive audit of every closed issue or PR.
- No Maven build, benchmark, or runtime experiment was performed for this report. The parent
  analysis covers current local implementation and ADR completeness separately. No remote
  changes, commits, or edits to the original report or `plan.md` are part of this research.

## Remaining issues: requirement versus proposed solution

| Issue and last update (UTC) | Documented request | Planning implication / inference |
| --- | --- | --- |
| [#170](https://github.com/nergal-perm/java-decita/issues/170), 2025-08-12 | Make the core independent of source table data so different readers, e.g. YAML, can be plugged in. Expressions with type/lhs/rhs and possibly separate operator are an initial idea. | Strong next integration slice: define a minimum source-neutral description and map existing CSV to it. `Expression` naming, a separate operator field, and adding YAML immediately are not settled acceptance requirements. |
| [#162](https://github.com/nergal-perm/java-decita/issues/162), 2024-09-29 | Provide table dependencies for visualization, prerequisite checks and future computation planning. | Can provide the first useful observable graph result before a full evaluator. Start with static dependencies and describe unresolved dynamic targets honestly. Graph traversal methods alone do not prove full dependencies were extracted from tables. |
| [#165](https://github.com/nergal-perm/java-decita/issues/165), 2025-08-12 | Track resolved/computed coordinates to permit early rule elimination; proposes the DAG from ADR PR #167. | Graph representation is a prerequisite, not the cache/evaluator itself. Separate session correctness, cache behavior and pruning into distinct increments. Its [only comment](https://github.com/nergal-perm/java-decita/issues/165#issuecomment-3038167718) announces an OpenHands attempt, not delivered functionality. |
| [#166](https://github.com/nergal-perm/java-decita/issues/166), 2025-08-12 | Condition execution counts and timings, with aggregation across computations/tests. | Introduce a small observable baseline before optimization; counts can precede clocks. Keep measurement outside the pure core in accordance with the chosen ADR boundary. Do not postpone all measurement until after pruning. |
| [#164](https://github.com/nergal-perm/java-decita/issues/164), 2024-09-29 | Research dynamic binding of shared behavior and distinct state: several Puerto Rico factories have the same rules but different owners/workers. | Preserve this concrete acceptance scenario for shared-graph/session work. Research unambiguous instance binding early; implementation can follow the minimal evaluator. The scenario does not establish a particular namespace design. |
| [#163](https://github.com/nergal-perm/java-decita/issues/163), 2024-09-29 | Research shared State/DecisionTable names and something like `this::property`; author explicitly questions irrelevant declarations. | Domain question and tentative syntax. Resolve name/binding behavior with examples before changing the parser. It need not block #170 or static dependencies. |
| [#23](https://github.com/nergal-perm/java-decita/issues/23), 2024-05-27 | Historical umbrella for refactoring puzzles. | Backlog maintenance candidate rather than an implementation feature. Its [latest bot comment](https://github.com/nergal-perm/java-decita/issues/23#issuecomment-2132762281) reports all five puzzles solved; #26/#53/#54/#85/#120 are individually closed/completed. Maintainer can close or repurpose it explicitly. |

Only #170 has a milestone identifier in the fetched issue metadata (`17`); the response does not
give its title. The other six have no milestone. This does not establish a complete release plan.

## Historical requirements worth retaining

- [#139](https://github.com/nergal-perm/java-decita/issues/139) replaced section dividers with
  typed `CND`/`OUT` rows; [#140](https://github.com/nergal-perm/java-decita/issues/140) added `ASG`;
  [#150](https://github.com/nergal-perm/java-decita/issues/150) added optional headers. All are
  closed/completed. Preserve these migration scenarios when implementing #170, including
  assignment interpretation distinct from comparison and meaningful rule names.
- [#153](https://github.com/nergal-perm/java-decita/issues/153) intended typed source lines
  usable by different interpreters; [#154](https://github.com/nergal-perm/java-decita/issues/154),
  [#158](https://github.com/nergal-perm/java-decita/issues/158) and
  [#159](https://github.com/nergal-perm/java-decita/issues/159) concern rule fragments and their
  collections. These closed tasks are antecedents for #170, not proof that its complete
  format-independent contract exists. [#155](https://github.com/nergal-perm/java-decita/issues/155)
  also preserves the explicit fallback-rule scenario.
- [#152](https://github.com/nergal-perm/java-decita/issues/152) requires concatenation inside
  dynamic coordinates; [#161](https://github.com/nergal-perm/java-decita/issues/161) fixes
  multiple dynamic parts. Keep those cases across parser/evaluator migration.
- [#160](https://github.com/nergal-perm/java-decita/issues/160) is closed/completed, but its
  [maintainer comment](https://github.com/nergal-perm/java-decita/issues/160#issuecomment-2345908324)
  says the functionality was implemented client-side and was not needed in the library then.
  Closure alone cannot establish an engine feature or a requirement to reimplement it now.
- [#156](https://github.com/nergal-perm/java-decita/issues/156) and
  [#157](https://github.com/nergal-perm/java-decita/issues/157) are closed related RawContent
  removal puzzles. Do not recreate them as two new pieces of rewrite backlog.

## Proposed sequence, subject to the local ADR/code audit

1. Reconcile the current provider checklist and unresolved typed-value/missing-value contracts
   against the actual code. This is the original plan's continuation, independent of GitHub's
   issue numbering. Clarify conflicting ADR assumptions before implementing an abandoned design.
2. Take **#170 as the next substantial integration objective**: one source-neutral equality rule,
   constructed without I/O, and the same description from one tiny CSV table. Preserve output,
   fallback and assignment behavior incrementally; a second file format can wait.
3. Build the immutable graph from those descriptions automatically, reusing merged #168 rather
   than rebuilding it. The missing integration may merit its own concrete acceptance slice;
   do not silently treat #168's closure as a promise of an automatic connector or evaluator.
4. Deliver **#162 static dependencies**, then transitive dependencies and explicit diagnostics.
   Cycle/dangling-edge graph validation is useful infrastructure; source-level dependency
   extraction and dynamically bound targets are separate observable behaviors.
5. Deliver the **minimal evaluator/session part of #165**: a graph shared across sessions reads
   each session's data independently; repeated requests reuse that session's computation by
   identity. Include a small #164 shared-behavior/different-state example as an acceptance case.
6. Add **#166 execution counts and an aggregation boundary**, then prove **#165 early elimination**
   preserves outcomes, fallback and ambiguity behavior. Add timing where it answers a specific
   optimization question. Measurement and pruning can be adjacent increments rather than one
   large profiler project.
7. Complete **#164 instance binding** after its research examples identify the missing behavior.
   Decide **#163 namespace/`this` semantics** explicitly if needed; tentative syntax is not a
   reason to rewrite working coordinate parsing early. Maintain #23 separately.

This order is a recommendation inferred from the issues' desired outcomes and dependencies.
GitHub does not mandate it. In particular, #162 need not wait for #165's full evaluator, and
#164's motivating scenario can shape session design before its full binding feature is delivered.
