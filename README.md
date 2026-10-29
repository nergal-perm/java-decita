# java-decita

[![PDD status](https://www.0pdd.com/svg?name=nergal-perm/java-decita)](https://www.0pdd.com/p?name=nergal-perm/java-decita)
[![Hits-of-Code](https://hitsofcode.com/github/nergal-perm/java-decita)](https://hitsofcode.com/github/nergal-perm/java-decita/view)

Java library for evaluating decision tables stored as semicolon-separated text files.
Tables can refer to application data and other tables, return named outcomes, and perform
assignments to application state. This repository builds a library JAR; it has no standalone
application or server entry point.

## Requirements and build

- JDK 17 (the version used by the repository's CI).
- Maven. The repository does not include a Maven Wrapper.

Run tests and quality checks:

```shell
mvn clean verify -Pqulice
```

The JAR is written to `target/java-decita-0.9.2.jar`. To run only the tests, use `mvn clean test`.
On JDK 21, pass `-Dmaven.compiler.release=17` to both commands. This was verified locally;
without it, the inherited warnings-as-errors configuration rejects the compiler warning produced
by the project's `source`/`target` settings. Compatibility with other JDK versions is not verified.

## Adding as a dependency

```xml
<dependency>
    <groupId>io.github.nergal-perm</groupId>
    <artifactId>java-decita</artifactId>
    <version>0.9.2</version>
</dependency>
```

## Quick start

Create a folder named `tables` containing `eligibility.csv`:

```text
CND;customer::age;>17
OUT;eligible;true;false
ASG;customer::status;adult;minor
```

The regular rule matches customers older than 17. The final outcome and assignment cells belong
to the fallback (`else`) rule. The table name is its file name without `.csv`.

Use the library from your application:

```java
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.InMemoryLocator;
import ru.ewc.state.State;

public class Example {
    public static void main(String[] args) {
        var customer = new InMemoryLocator("customer", Map.of("age", 20));
        var context = new ComputationContext(
            new State(List.of(customer)),
            Path.of("tables").toAbsolutePath().toUri()
        );

        System.out.println(context.decisionFor("eligibility")); // {eligible=true}
        context.perform("eligibility");
        System.out.println(customer.state()); // contains age=20 and status=adult
    }
}
```

`decisionFor()` returns a `Map<String, String>` of outcomes. `perform()` selects the rule and
executes its assignments; calling `decisionFor()` alone does not execute them. Application data
is supplied through `Locator` implementations; `InMemoryLocator` is a mutable in-memory store.
To collect an evaluation trace, call `context.startTracking()` before evaluation and read the
returned tracker's `events()` afterwards.

## Decision table DSL (CSV format)

Files are read recursively from the supplied folder. The default reader uses `.csv` files and
semicolon (`;`) separators. This is a simple line-based format using `String.split`, without CSV
quoting or escaping: do not put semicolons or line breaks inside values.

Each row starts with a type and a left-hand identifier, followed by one cell per rule:

| Row type | Second column | Rule cells |
| --- | --- | --- |
| `CND` | Coordinate to check | Condition applied to that coordinate |
| `OUT` | Outcome name | Coordinate or constant providing the outcome |
| `ASG` | Coordinate to update | Coordinate or constant providing the assigned value |
| `HDR` (optional) | Table label for tracing | Rule names for tracing |

Rules are columns, starting with the third column. All specified conditions in a rule must be
true. Empty cells and cells containing exactly `~` are ignored. There is no `---` section divider.

The first row determines the number of regular rule columns. To supply an explicit `else` rule,
add one extra final column to the `OUT` rows, beyond the condition columns; its `ASG` cells may
also specify fallback assignments. Keep regular rule columns aligned across rows and include at
least one `CND` and one `OUT` row. A table containing only outcomes is currently unsupported.

Exactly one regular rule may match. If several match, evaluation throws `DecitaException`
with `Multiple rules are satisfied`. If none match, the `else` rule is used. Without an explicit
`else`, the result is `{outcome=undefined}` and no assignments are performed.

### Coordinates and values

A coordinate has the form `locator::fragment`, for example `customer::age` or
`eligibility::eligible`. The locator can identify application data or another decision table.
Table locators take precedence over application data locators with the same name.

A value without `::` is a constant: `5` and `constant::5` refer to the same constant.
Values returned by the current engine are strings. Equality compares their string representation;
`>` and `<` compare decimal numbers using `double`. Missing fields in an `InMemoryLocator`
return the string `undefined`; a missing locator raises `DecitaException`.

Coordinates support `${...}` placeholders, including nested placeholders. For example,
`cells::${request::move}` reads the cell whose identifier is supplied by `request::move`.
An assignment such as `ASG;market::shop;${request::shop}` copies the request's shop value
into application state.

### Conditions

| Cell | Meaning |
| --- | --- |
| `Katie` | Equal to the constant `Katie` |
| `request::player` | Equal to the value at that coordinate |
| `>17` / `<18` | Numeric comparison |
| `!Katie` | Not equal to `Katie` |
| `!>17` | Negation of the numeric comparison |
| `~` or an empty cell | No condition for this row and rule |

Equality has no explicit operator prefix: use `5`, not `==5`. Operators such as `>=` and `<=`
are not supported.

See [sample tables](src/test/resources/tables) and
[end-to-end tests](src/test/java/ru/ewc/decisions/api/EndToEndTest.java) for table references,
dynamic coordinates, fallback rules, and assignments.

## Development status

The current engine and public API live in `ru.ewc.decisions` and `ru.ewc.state`.
The [architectural decisions](doc/adr) describe a planned rewrite around a pure functional core,
an immutable decision graph, separate evaluation state, and pluggable connectors.

The graph representation described in issue [#168](https://github.com/nergal-perm/java-decita/issues/168)
is implemented in `ru.ewc.decisions.core` on this development branch. Its nodes hold immutable
descriptions: coordinate strings, `RuleFragment` values, copied fragment lists, and
`TableDescription` values with copied rule lists and a fallback. They do not retain mutable
legacy conditions or decision tables.

`DecisionGraphBuilder` reuses equivalent nodes, including rules nested inside table descriptions,
and reconnects edges to those canonical instances. Conflicting definitions for one identifier,
cycles, and references outside the graph are rejected. All graph query results are unmodifiable.
When constructing `InMemoryDecisionGraph` directly, supply the exact stored instances for edge
endpoints and nested rule references; the builder performs this normalization for you.

For example, a condition's structural dependencies can be represented as follows:

```java
import ru.ewc.decisions.api.RuleFragment;
import ru.ewc.decisions.core.ConditionNode;
import ru.ewc.decisions.core.Coordinate;
import ru.ewc.decisions.core.CoordinateNode;
import ru.ewc.decisions.core.DecisionGraphBuilder;

var age = new CoordinateNode(Coordinate.from("customer::age"));
var threshold = new CoordinateNode(Coordinate.from("18"));
var condition = new ConditionNode(new RuleFragment("CND", "customer::age", ">18"), "adult");
var graph = new DecisionGraphBuilder()
    .addLhsOfRelationship(age, condition)
    .addRhsOfRelationship(threshold, condition)
    .build();
```

`ConditionNode.component()` returns an immutable `RuleFragment`, rather than an executable legacy
`Condition`; `CoordinateNode.component()` returns a coordinate string. The graph stores source
logic only. Applications can create independent legacy evaluations from these descriptions.
The graph evaluator, identity-based session cache, and plugin system described in the ADRs are
not yet implemented. This unmerged graph API replaces the mutable-component API from PR #169.
See the [continuation plan and verification notes](doc/decision-graph-continuation.md).

The value-provider rewrite has also started in `ru.ewc.decita.core.value` with `ValueProvider`
and `ConstantValueProvider`. Both that package and the graph's core package are currently
unexported in the Java module descriptor; graph examples use the classpath. See
[plan.md](plan.md) for the incremental test plan.

## Maintainer instructions

### Publishing a site

First of all, the site should be built with the following Maven command:

```shell
mvn clean verify site -Pjacoco && mvn site:site site:stage -Psite
```

Then I `rsync` the newly created `target/staging` folder to another copy of the repository (which has
the `gh-pages` branch checked out) with the following command (run from the project root):

```shell
rsync -avz --delete target/staging/* ../java-decita-gh-pages/
```

After that, I 'cd' to the repository's copy, commit and push the changes to the `gh-pages` branch.
Done!

### Releasing

To create a new commit with a new version, run the following `npx` command (you need to have Node.js installed): 

```shell
npx @dwmkerr/standard-version --package-files pom.xml --bumpFiles pom.xml
```

After that run the following Maven command to push the new version to the Maven Central:

```shell
mvn clean deploy -Pjcabi-gpg -PnewSonatype
```

The publishing process is still very manual, so it's needed to check out the 
[Sonatype Nexus](https://central.sonatype.com/publishing/deployments) page for the deployed 
artifacts and to release them manually.

