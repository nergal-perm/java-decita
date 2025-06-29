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

The important thing to note is that the core should be pure, i.e. it should not produce any side 
effects. It should only provide a way to process the decision tables and return the results. 
It's the application's responsibility to handle the results and produce side effects if needed.

The second important thing is that the core should use some kind of snapshot of all the loaded 
decision tables, their rules and conditions. This snapshot should be immutable and should be 
used as a basis for concrete decision-making sessions. Probably it will be better to represent 
this snapshot as a graph, where nodes are decision tables, rules, conditions and specific data 
accessors, and edges are the relationships between them.

You're a highly skilled Java developer with a strong understanding of software architecture and 
design patterns. Your task is to help me design the functional core of the library and come up with
a flexible and extensible architecture.