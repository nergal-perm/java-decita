# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [0.3.3](https://github.com/nergal-perm/java-decita/compare/v0.3.2...v0.3.3) (2024-05-04)


### Bug Fixes

* removed the state-based testing infrastructure ([b442743](https://github.com/nergal-perm/java-decita/commit/b442743770be82ee2f8b8acdebcfcdabddf25795))

### [0.3.2](https://github.com/nergal-perm/java-decita/compare/v0.3.1...v0.3.2) (2024-03-27)


### Bug Fixes

* it is now possible to create facade with supplied Locators ([e819c2f](https://github.com/nergal-perm/java-decita/commit/e819c2fbf06af910ad0d978a859d60a114a3e28e))

### [0.3.1](https://github.com/nergal-perm/java-decita/compare/v0.3.0...v0.3.1) (2024-03-26)


### Features

* ability to extend ComputationContext for each request ([70c93f6](https://github.com/nergal-perm/java-decita/commit/70c93f6e26f11ae065dc2e66897ece11d6d35a4f)), closes [#96](https://github.com/nergal-perm/java-decita/issues/96)
* ability to use a fresh set of decision tables for each computation ([650c8b5](https://github.com/nergal-perm/java-decita/commit/650c8b5834e0d2045d483301f6ba7ca105936199)), closes [#96](https://github.com/nergal-perm/java-decita/issues/96)
* complexity of decision making hidden behind the simple facade ([6d88f3c](https://github.com/nergal-perm/java-decita/commit/6d88f3c031e5deae6944ad456751332fae423cbd)), closes [#96](https://github.com/nergal-perm/java-decita/issues/96)
* throwing exception if multiple rules are resolved to true ([4a77a28](https://github.com/nergal-perm/java-decita/commit/4a77a28bb418489862af4f3d20a8ef0a141103b5)), closes [#94](https://github.com/nergal-perm/java-decita/issues/94)

## [0.3.0](https://github.com/nergal-perm/java-decita/compare/v0.2.3...v0.3.0) (2024-03-09)


### Features

* ability to explicitly specify Else-rule in tables ([ee97a7b](https://github.com/nergal-perm/java-decita/commit/ee97a7ba64689eea256e340ab0cb68ca5d152b22)), closes [#86](https://github.com/nergal-perm/java-decita/issues/86)
* ability to mark specific conditions as irrelevant to the rule ([5179248](https://github.com/nergal-perm/java-decita/commit/51792489aba83dcd2e84d2db2d0c6f14252bd509)), closes [#83](https://github.com/nergal-perm/java-decita/issues/83)
* Implemented in-cell condition negation ([c6aae65](https://github.com/nergal-perm/java-decita/commit/c6aae6509cf70cc6fd66b3099e9d67cd058141bb)), closes [#90](https://github.com/nergal-perm/java-decita/issues/90) [#87](https://github.com/nergal-perm/java-decita/issues/87)
* LessThan and GreaterThan conditions ([7e1bd9b](https://github.com/nergal-perm/java-decita/commit/7e1bd9b3150c5ca44c5c17c26a597e14871ba38c)), closes [#88](https://github.com/nergal-perm/java-decita/issues/88)
* The constant values in tables can be stated without the `constant::` qualifier ([a12e9c0](https://github.com/nergal-perm/java-decita/commit/a12e9c08981ef295fea0139a3b344051bd8db6f0)), closes [#54](https://github.com/nergal-perm/java-decita/issues/54)


### Bug Fixes

* it is possible to describe state with Strings, Boolean and Numbers in yaml files ([4deafa0](https://github.com/nergal-perm/java-decita/commit/4deafa096e26473afe71d514ce1b7ccab307505c))

### 0.2.3 (2024-02-20)


### Features

* added empty Locator and ConstantLocator classes ([27406e6](https://github.com/nergal-perm/java-decita/commit/27406e63765e1c2dfedcd14d63f1a601fec761af)), closes [#4](https://github.com/nergal-perm/java-decita/issues/4)
* compute and return the DecisionTable result ([b33190b](https://github.com/nergal-perm/java-decita/commit/b33190b6e0ce0c65fddaaa8455d27dc6db2feac6)), closes [#28](https://github.com/nergal-perm/java-decita/issues/28)
* Convert files to 2D-arrays of Strings ([9e7b4c1](https://github.com/nergal-perm/java-decita/commit/9e7b4c12d01227602de198fbc135bb91850e4bc5)), closes [#45](https://github.com/nergal-perm/java-decita/issues/45) [#49](https://github.com/nergal-perm/java-decita/issues/49)
* DecisionTable implements Locator interface ([d7322b8](https://github.com/nergal-perm/java-decita/commit/d7322b8b1286c092fc60010266664475f1a4823d)), closes [#36](https://github.com/nergal-perm/java-decita/issues/36)
* handling missing Locator ([ff7de47](https://github.com/nergal-perm/java-decita/commit/ff7de479b37f84870346435204d9380804c49e9c)), closes [#11](https://github.com/nergal-perm/java-decita/issues/11)
* Implement the lookup for the requested Fragment ([38f6f8d](https://github.com/nergal-perm/java-decita/commit/38f6f8d1f4ce93fae400bd83b58ba3ba1b06f40c)), closes [#8](https://github.com/nergal-perm/java-decita/issues/8)
* implemented a simple ComputationContext ([d966da7](https://github.com/nergal-perm/java-decita/commit/d966da736f9b07aa9cfa3e253250e1891f3df3d8))
* implemented a simple ConstantLocator class ([abe05d1](https://github.com/nergal-perm/java-decita/commit/abe05d10937a4d7c8624881b139d0d6f44b6b6ec))
* implemented a special case for EqualsCondition ([594cb73](https://github.com/nergal-perm/java-decita/commit/594cb7367f53f4f5bb1b046fd536d35de548ee68)), closes [#34](https://github.com/nergal-perm/java-decita/issues/34)
* implemented DecisionTable as a set of Rules with Outcomes ([3526186](https://github.com/nergal-perm/java-decita/commit/35261869f46c98bc114914e46713eaf172e629b3)), closes [#22](https://github.com/nergal-perm/java-decita/issues/22)
* Method to set up the tables folder ([1b0a57c](https://github.com/nergal-perm/java-decita/commit/1b0a57c2908328335f2c22bd0c1100e5b54f862f)), closes [#62](https://github.com/nergal-perm/java-decita/issues/62)
* naive rawcontent converter to decision table ([bbb566a](https://github.com/nergal-perm/java-decita/commit/bbb566a00e77bedcd3f1bfabf8dea59aaaeb4a73)), closes [#42](https://github.com/nergal-perm/java-decita/issues/42) [#52](https://github.com/nergal-perm/java-decita/issues/52)
* plain text file reader ([d549650](https://github.com/nergal-perm/java-decita/commit/d54965091b24edf3666906c7151997eee1aeb842)), closes [#41](https://github.com/nergal-perm/java-decita/issues/41) [#44](https://github.com/nergal-perm/java-decita/issues/44)
* rebuild completer after pointing to a tables folder ([a530f04](https://github.com/nergal-perm/java-decita/commit/a530f04af4eb1d720035e523d9975730c54e672c)), closes [#66](https://github.com/nergal-perm/java-decita/issues/66)
* reload state file before every computation ([34eb18a](https://github.com/nergal-perm/java-decita/commit/34eb18afb87d83db44b7b727d18535a777b3142c)), closes [#70](https://github.com/nergal-perm/java-decita/issues/70)
* return TableLocator for unknown Locator names ([1a43024](https://github.com/nergal-perm/java-decita/commit/1a430245692768889344f1d3a97aff57120cd699)), closes [#32](https://github.com/nergal-perm/java-decita/issues/32)
* simple "equals" Condition ([8f8d56e](https://github.com/nergal-perm/java-decita/commit/8f8d56e7977843789c4fc7f4c53b0886f6c4dce0)), closes [#15](https://github.com/nergal-perm/java-decita/issues/15)
* simple Rule with conditions and elimination check ([526615f](https://github.com/nergal-perm/java-decita/commit/526615feef5aa68e15e45fa901cf770d9725fbe4)), closes [#16](https://github.com/nergal-perm/java-decita/issues/16) [#21](https://github.com/nergal-perm/java-decita/issues/21)
* use DecisionTable results in Conditions ([d801948](https://github.com/nergal-perm/java-decita/commit/d801948d960fd6eeeaae7558c25e93d8edd72cbe)), closes [#25](https://github.com/nergal-perm/java-decita/issues/25)
* using computational state from yaml file ([e8c7d1a](https://github.com/nergal-perm/java-decita/commit/e8c7d1a6fa16bab357b6322fbb782f07c27ba2ab)), closes [#63](https://github.com/nergal-perm/java-decita/issues/63) [#64](https://github.com/nergal-perm/java-decita/issues/64)


### Bug Fixes

* always escape backwards slashes from cli ([38996ac](https://github.com/nergal-perm/java-decita/commit/38996ac0411ee4a1419d1c36ab7d6f12ba3c45f2))
* formatting for decision output + requiring the parameter ([09de703](https://github.com/nergal-perm/java-decita/commit/09de7039bc95a48af50e9881995a28e68602f810)), closes [#71](https://github.com/nergal-perm/java-decita/issues/71)
* handle DecitaExceptions in manual testing tool ([f10852c](https://github.com/nergal-perm/java-decita/commit/f10852cbd2ba1ea62a1d4738ca5052c80bdca8c4)), closes [#77](https://github.com/nergal-perm/java-decita/issues/77) [#79](https://github.com/nergal-perm/java-decita/issues/79)
* read system-independent filepaths from cli ([1aaa9cb](https://github.com/nergal-perm/java-decita/commit/1aaa9cb43656c03879b53c7232b10200fd7d01c2))
* run state and tables commands in any order ([3b6e8f8](https://github.com/nergal-perm/java-decita/commit/3b6e8f8b357d1e6cd02ff89a396003394f4ab815)), closes [#76](https://github.com/nergal-perm/java-decita/issues/76)
* system-independent filepaths ([c62d054](https://github.com/nergal-perm/java-decita/commit/c62d0549b0bf7bd6ac3089b1088e5e89f2daaa37)), closes [#69](https://github.com/nergal-perm/java-decita/issues/69)
* tables command remains in completion list after running ([17d8c8d](https://github.com/nergal-perm/java-decita/commit/17d8c8d9a681e3b5de331c7e2569a86e17709d86)), closes [#72](https://github.com/nergal-perm/java-decita/issues/72)
