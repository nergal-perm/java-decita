# java-decita

[![PDD status](https://www.0pdd.com/svg?name=nergal-perm/java-decita)](https://www.0pdd.com/p?name=nergal-perm/java-decita)
[![Hits-of-Code](https://hitsofcode.com/github/nergal-perm/java-decita)](https://hitsofcode.com/github/nergal-perm/java-decita/view)

## Instructions

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

### Adding as a dependency

To add this library as a dependency to your project, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.nergal-perm</groupId>
    <artifactId>java-decita</artifactId>
    <version>0.3.0</version>
</dependency>
```

### Manual testing

Run the following Maven command from the project root:

```shell
mvn clean compile -DskipTests exec:java -Dexec.mainClass=ru.ewc.decita.manual.Shell
```

The command line tool will allow you to:
- provide path to tables folder via `tables <path-to-tables>` command;
- provide path to a specific state yaml file via `state <path-to-state-file>` command;
- after those paths are set, it will be possible to make decisions on tables via `decide <table-name>` command.

### Auto testing

If the state yaml files contain two sections, separated with `---`, it is possible to use them as
autotests. It can be done with following Maven command:

```shell
mvn clean compile test -Dtest=StateBasedTest -Dtables=<path-to-tables-folder> -Dstates=<path-to-states-folder>
```

This command will scan all the yaml files in specified `states` folder and check if the expectations stated in
the second section of those files hold true.

The expectations should be stated in following format:

```yaml
table-name:
  outcome1: "expected value"
  outcome2: "expected value"
another-table:
  outcome1: "expected value"
  outcome2: "expected value"
```

All the expected values should be Strings.

Example of the state with expectation could be found in [state-test.yml](https://github.com/nergal-perm/java-decita/blob/master/src/test/resources/states/state-test.yml).

## Decision table DSL (CSV format)

For the decision table to be processed, it should be in CSV format with semicolon (`;`) as a separator.
The columns of the table represent rules, except for the first one, which contains the "base" of each
condition. The rows in the first section (before `---`) represent the conditions, and the rows in the second
section (after `---`) represent the outcomes.

Every value in the conditions section should refer to a specific data fragment, which can be located
by its description, called `Coordinate`. Coordinates consist of two parts: `Locator` - that is the
name of the data domain, and `Identifier` - that is the name of the data fragment. Those parts are separated
by `::`. For example, value `ui_button_create::enabled` refers to the `enabled` fragment of the
`ui_button_create` locator.

For the constant values, the `Locator` part can be omitted, so the value `5` refers to a constant value `5`.
For the sake of completeness, the `Locator` part for constant values is `constant`, so it is possible to
write `constant::5` instead of just `5` (but it is not necessary).

The table cells in the conditions section should contain the condition description, consisting of 
the logical operator (if any), followed by the comparison operator (if any), followed by the value.

If the comparison operator is omitted, it is assumed to be `==`, which means that the "base" value of 
the condition should be equal to the cell value for the condition to be true. Other comparison operators
are `>` and `<`.

The logical operators are used to modify the condition itself. The operator `~` means "any", so it is
effectively means that the condition is always true. The operator `!` means "not", so it negates the
specified condition.


