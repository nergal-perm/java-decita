This is the library that provides an engine for processing decision tables in Java. The initial 
development went through several iterations, and the library has been tested in production. But 
the initial design has some flaws, and the code is not very modular, extensible, or maintainable.

I am planning a rewrite of the java-decita library. My main goal is to make it more modular,
i.e. implement different table storage formats or different logic extensions (like the new row 
types for tables) as pluggable modules. It implies creating the functional core of the library 
which is free of such modules and their implementation details. 

The functional core should be very simple and generic, meaning that it should specify generic 
interfaces for all its components, and the components should be implemented as separate 
pluggable modules. The core should not depend on any specific implementation of the components,
and the components should not depend on the core. The core should only provide a generic logic for
making decisions based on the decision tables, and the components should provide the specific
implementations that are needed for the specific use cases.

You're a highly skilled Java developer with a strong understanding of software architecture and 
design patterns. Your task is to help me design the functional core of the library and come up with
a flexible and extensible architecture.