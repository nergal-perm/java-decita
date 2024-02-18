# java-decita

[![PDD status](https://www.0pdd.com/svg?name=nergal-perm/java-decita)](https://www.0pdd.com/p?name=nergal-perm/java-decita)
[![Hits-of-Code](https://hitsofcode.com/github/nergal-perm/java-decita)](https://hitsofcode.com/github/nergal-perm/java-decita/view)

## Instructions

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