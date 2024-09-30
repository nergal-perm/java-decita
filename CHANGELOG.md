# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [0.9.2](https://github.com/nergal-perm/java-decita/compare/v0.9.1...v0.9.2) (2024-09-30)


### Features

* **decisions:** returning all the decision table names ([2758f9f](https://github.com/nergal-perm/java-decita/commit/2758f9f7073f58f689d990fe523b42fa1f569067))


### Bug Fixes

* **coordinates:** resolving multiple placeholders correctly ([932ff18](https://github.com/nergal-perm/java-decita/commit/932ff182a19f261c05ac34b68aad1054ff2b586a)), closes [#161](https://github.com/nergal-perm/java-decita/issues/161)

### [0.9.1](https://github.com/nergal-perm/java-decita/compare/v0.9.0...v0.9.1) (2024-09-16)


### Features

* **decisions:** outcomes now can use dynamic coordinates in them ([9ab1692](https://github.com/nergal-perm/java-decita/commit/9ab16927924605fb7e932b0f3b0940ca7f7d023f))

## [0.9.0](https://github.com/nergal-perm/java-decita/compare/v0.8.2...v0.9.0) (2024-09-12)


### Features

* **testing:** removed external testing functionality ([59cdec1](https://github.com/nergal-perm/java-decita/commit/59cdec1ad5b7fbac0c9f021f6507583382c65105))

### [0.8.2](https://github.com/nergal-perm/java-decita/compare/v0.8.1...v0.8.2) (2024-09-08)


### Bug Fixes

* **input:** trim whitespaces while reading source lines ([ff293f3](https://github.com/nergal-perm/java-decita/commit/ff293f33eb968765c943ac0547d5e0f01b9312e6))

### [0.8.1](https://github.com/nergal-perm/java-decita/compare/v0.8.0...v0.8.1) (2024-09-05)


### Features

* **tables:** support the HDR row for rules and checks names ([d289711](https://github.com/nergal-perm/java-decita/commit/d2897113b0228d33f49a3e1dd0d7ef5105e56f7d)), closes [#150](https://github.com/nergal-perm/java-decita/issues/150)
* **testing:** structured check failure descriptions ([115ecbc](https://github.com/nergal-perm/java-decita/commit/115ecbcca80e867fb286650b9aee780446d006f9)), closes [#151](https://github.com/nergal-perm/java-decita/issues/151)

## [0.8.0](https://github.com/nergal-perm/java-decita/compare/v0.7.2...v0.8.0) (2024-09-03)


### Features

* **commands:** simple Assignment class ([a031ac9](https://github.com/nergal-perm/java-decita/commit/a031ac9370e6d589fe5ea66b2b798918bfe272be)), closes [#140](https://github.com/nergal-perm/java-decita/issues/140)
* **commands:** unconditionally performing commands based on table description ([8304112](https://github.com/nergal-perm/java-decita/commit/8304112d59690f362d58cc535c8c418b5d2f4be6)), closes [#140](https://github.com/nergal-perm/java-decita/issues/140)
* **coordinates:** dynamic Coordinates resolution ([c83c7b9](https://github.com/nergal-perm/java-decita/commit/c83c7b9e32d4598f8c851a18e556012df46b9dd0)), closes [#138](https://github.com/nergal-perm/java-decita/issues/138)
* **input:** ability to select Conditions rows from the CSV-file ([dcf63e0](https://github.com/nergal-perm/java-decita/commit/dcf63e00a12061682693a82984a4c095f0b224d6)), closes [#139](https://github.com/nergal-perm/java-decita/issues/139)
* **input:** ability to select Outcome rows from the CSV-file ([c902be2](https://github.com/nergal-perm/java-decita/commit/c902be28709ee98c97597e53ac4d08cfacb39825)), closes [#139](https://github.com/nergal-perm/java-decita/issues/139)
* **input:** full-fledged Combined CSV file reader ([0755ba4](https://github.com/nergal-perm/java-decita/commit/0755ba40b587910ebc2f33485c68d59153135b8b)), closes [#139](https://github.com/nergal-perm/java-decita/issues/139)
* **input:** reading assignments from the csv files ([b25917e](https://github.com/nergal-perm/java-decita/commit/b25917e8f580d69ac44b0f10d3ccc7f75875e374)), closes [#140](https://github.com/nergal-perm/java-decita/issues/140)
* **testing:** naive instance of the single test ([24b21ed](https://github.com/nergal-perm/java-decita/commit/24b21ed33ed9ab213318fc12ac3e2bbb1f8a5502)), closes [#148](https://github.com/nergal-perm/java-decita/issues/148)
* **testing:** performing the tests and collecting the result ([d81b628](https://github.com/nergal-perm/java-decita/commit/d81b628abd2f4c1f503835845bdfb476dab740e7)), closes [#149](https://github.com/nergal-perm/java-decita/issues/149)
* **testing:** reading all the tests as the CheckSuite ([115da08](https://github.com/nergal-perm/java-decita/commit/115da08166726b67eccd666a8f40d7b53893898f)), closes [#149](https://github.com/nergal-perm/java-decita/issues/149)
* **testing:** reloading tables between test stages ([302fd1f](https://github.com/nergal-perm/java-decita/commit/302fd1f49069f6e29107a6bd84128bdeacde5868)), closes [#149](https://github.com/nergal-perm/java-decita/issues/149)

### [0.7.2](https://github.com/nergal-perm/java-decita/compare/v0.7.1...v0.7.2) (2024-08-24)


### Features

* added simple events tracker to ComputationContext ([b2e9427](https://github.com/nergal-perm/java-decita/commit/b2e94271597a58fadfaca71fa970fa3dab6ea10d)), closes [#137](https://github.com/nergal-perm/java-decita/issues/137)
* **coordinates:** method for Coordinate's string representation ([bd4f63a](https://github.com/nergal-perm/java-decita/commit/bd4f63a01ae3a735aaf3a97538958484a8b3f173)), closes [#133](https://github.com/nergal-perm/java-decita/issues/133)
* logging the dynamic Coordinate resolution ([4355fe1](https://github.com/nergal-perm/java-decita/commit/4355fe1ded72d73d17fd68f6fbc09001529879c2)), closes [#132](https://github.com/nergal-perm/java-decita/issues/132)
* **logging:** logging the Condition resolution ([afe8dfd](https://github.com/nergal-perm/java-decita/commit/afe8dfded936adeeaa7b1e7a41817ffd6e40a96f)), closes [#133](https://github.com/nergal-perm/java-decita/issues/133)
* **logging:** logging the DecisionTable's outcome ([5d32ad0](https://github.com/nergal-perm/java-decita/commit/5d32ad0bef3b0c826e55dd938d856f5918c2ab84)), closes [#136](https://github.com/nergal-perm/java-decita/issues/136)
* **logging:** logging the Rules computation ([910f263](https://github.com/nergal-perm/java-decita/commit/910f2636f65ff2280ac635c8e508aa6ffda91048)), closes [#134](https://github.com/nergal-perm/java-decita/issues/134) [#135](https://github.com/nergal-perm/java-decita/issues/135)

### [0.7.1](https://github.com/nergal-perm/java-decita/compare/v0.7.0...v0.7.1) (2024-07-03)


### Features

* exposing the list of commands resolvable parts ([8bec5d6](https://github.com/nergal-perm/java-decita/commit/8bec5d6600d90673c9be926b67ee0c6f159ef243))
* extracting the list of commands resolvable parts ([79a60db](https://github.com/nergal-perm/java-decita/commit/79a60dbe64c64834aca080ba9ec0e28242e93085))

## [0.7.0](https://github.com/nergal-perm/java-decita/compare/v0.6.1...v0.7.0) (2024-06-14)


### Features

* exposing command names to external clients ([1f73841](https://github.com/nergal-perm/java-decita/commit/1f738416cd71d5cdd322c784638992346d6e331a)), closes [#113](https://github.com/nergal-perm/java-decita/issues/113)
* **locators:** created a simple StoredStateFactory ([d34e24c](https://github.com/nergal-perm/java-decita/commit/d34e24ca1b44949b0b118bbb17d8f8ff479d8896)), closes [#127](https://github.com/nergal-perm/java-decita/issues/127)

### [0.6.1](https://github.com/nergal-perm/java-decita/compare/v0.6.0...v0.6.1) (2024-05-27)


### Features

* determining if a Coordinate is resolved ([8b408f8](https://github.com/nergal-perm/java-decita/commit/8b408f87e1c1887f4b2f5233b7adf5e3cf9acb7c)), closes [#120](https://github.com/nergal-perm/java-decita/issues/120)

### [0.5.2](https://github.com/nergal-perm/java-decita/compare/v0.5.1...v0.5.2) (2024-05-18)


### Features

* **locators:** exposed an interface to obtain Locator's state ([7ca3511](https://github.com/nergal-perm/java-decita/commit/7ca351192010a58d93143ed5c7ec1ccfa5706003)), closes [#112](https://github.com/nergal-perm/java-decita/issues/112)
* **locators:** removed the implicit creation of missing Locators ([ae0dd75](https://github.com/nergal-perm/java-decita/commit/ae0dd7510e32627deea0f485ba828327e335acab)), closes [#111](https://github.com/nergal-perm/java-decita/issues/111)

### [0.5.1](https://github.com/nergal-perm/java-decita/compare/v0.5.0...v0.5.1) (2024-05-11)


### Bug Fixes

* **commands:** exported the commands package ([0fd0ca6](https://github.com/nergal-perm/java-decita/commit/0fd0ca6785df6b3d4a908f71ff918c99da9b5d96))

## [0.5.0](https://github.com/nergal-perm/java-decita/compare/v0.4.0...v0.5.0) (2024-05-10)


### ⚠ BREAKING CHANGES

* Added new method to published interface for Locator

### Features

* **commands:** Implemented a simple Command Registry ([6753e05](https://github.com/nergal-perm/java-decita/commit/6753e0539a37313eac6a9d99d1d4775f51abce38)), closes [#107](https://github.com/nergal-perm/java-decita/issues/107)
* **commands:** Implemented a simple description resolver ([8121f34](https://github.com/nergal-perm/java-decita/commit/8121f3420384441d8ea4f3c8aa1f0301a6569cc7)), closes [#109](https://github.com/nergal-perm/java-decita/issues/109)
* **commands:** implemented a simple hard-coded command ([fa7ec52](https://github.com/nergal-perm/java-decita/commit/fa7ec52b698b919995db48e36f184588f78becd1)), closes [#104](https://github.com/nergal-perm/java-decita/issues/104)
* **commands:** Implemented a simple operation resolver ([369c734](https://github.com/nergal-perm/java-decita/commit/369c734b3ae124adf63215610e12ed37b07980e6)), closes [#105](https://github.com/nergal-perm/java-decita/issues/105)
* **commands:** Implemented the multiple operations in one command ([14878d6](https://github.com/nergal-perm/java-decita/commit/14878d67feb9385d4a42ae3d6a310a47bb9a4061)), closes [#108](https://github.com/nergal-perm/java-decita/issues/108)
* **commands:** Made context extensible with new empty Locator ([c8b3b5d](https://github.com/nergal-perm/java-decita/commit/c8b3b5d2b43cf1b31a47c579a3d7e7bef6cabb04)), closes [#110](https://github.com/nergal-perm/java-decita/issues/110)
* **commands:** Naive yaml command description reader ([894c17a](https://github.com/nergal-perm/java-decita/commit/894c17aa14fe652fee30526ef52b60e2063b1522)), closes [#106](https://github.com/nergal-perm/java-decita/issues/106)
* extended Locator with ability to set values ([05d36c2](https://github.com/nergal-perm/java-decita/commit/05d36c28820f1084129a95008f79f9647e702038)), closes [#103](https://github.com/nergal-perm/java-decita/issues/103)

## [0.4.0](https://github.com/nergal-perm/java-decita/compare/v0.3.3...v0.4.0) (2024-05-07)


### Features

* modularized the library ([9e730a3](https://github.com/nergal-perm/java-decita/commit/9e730a35388455eba78f77906ebdbb2fcff4c3a4)), closes [#101](https://github.com/nergal-perm/java-decita/issues/101)

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
* complexity of decision-making hidden behind the simple facade ([6d88f3c](https://github.com/nergal-perm/java-decita/commit/6d88f3c031e5deae6944ad456751332fae423cbd)), closes [#96](https://github.com/nergal-perm/java-decita/issues/96)
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
