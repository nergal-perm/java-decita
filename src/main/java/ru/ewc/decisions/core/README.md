# DecisionGraph Implementation

This directory contains the implementation of an immutable, in-memory representation of decision tables and their components as a graph structure.

## Overview

The DecisionGraph provides a thread-safe, immutable graph representation where:
- **Nodes** represent decision table components: Coordinates, Conditions, Rules, DecisionTables, and RuleFragments
- **Edges** represent relationships: "part_of", "lhs_of", "rhs_of", and "operator_of"

This implementation follows the architectural decision (ADR-0003) to separate static logic structure from per-session execution state.

## Core Components

### Interfaces

- `DecisionGraph` - Main interface for the graph structure
- `GraphNode` - Interface for all graph nodes
- `GraphEdge` - Interface for all graph edges

### Implementations

- `InMemoryDecisionGraph` - Concrete implementation of DecisionGraph
- `BasicGraphEdge` - Basic edge implementation

### Node Types

- `CoordinateNode` - Represents a Coordinate component
- `ConditionNode` - Represents a Condition component
- `RuleNode` - Represents a Rule component
- `DecisionTableNode` - Represents a DecisionTable component
- `RuleFragmentNode` - Represents a RuleFragment component

### Edge Types

- `PartOfEdge` - Represents "part_of" relationships (e.g., Rule is part of DecisionTable)
- `LhsOfEdge` - Represents "lhs_of" relationships (e.g., Coordinate is left-hand side of Condition)
- `RhsOfEdge` - Represents "rhs_of" relationships (e.g., Coordinate is right-hand side of Condition)
- `OperatorOfEdge` - Represents "operator_of" relationships (e.g., operator is part of Condition)

### Utilities

- `DecisionGraphBuilder` - Builder pattern for constructing graphs

## Usage Examples

### Basic Graph Creation

```java
// Create nodes
CoordinateNode coord1 = new CoordinateNode(Coordinate.from("customer::age"));
CoordinateNode coord2 = new CoordinateNode(Coordinate.from("21"));
ConditionNode condition = new ConditionNode(someCondition, "age_check");

// Create edges
LhsOfEdge lhsEdge = new LhsOfEdge(coord1, condition);
RhsOfEdge rhsEdge = new RhsOfEdge(coord2, condition);

// Build graph
DecisionGraph graph = new InMemoryDecisionGraph(
    Set.of(coord1, coord2, condition),
    Set.of(lhsEdge, rhsEdge)
);
```

### Using the Builder

```java
DecisionGraph graph = new DecisionGraphBuilder()
    .addLhsOfRelationship(coord1, condition)
    .addRhsOfRelationship(coord2, condition)
    .addPartOfRelationship(condition, rule)
    .build();
```

### Querying the Graph

```java
// Get all coordinate nodes
Set<CoordinateNode> coordinates = graph.nodesOfType(CoordinateNode.class);

// Get all part_of edges
Set<PartOfEdge> partOfEdges = graph.edgesOfType(PartOfEdge.class);

// Find edges from a specific node
Set<GraphEdge> outgoingEdges = graph.edgesFrom(someNode);

// Find edges to a specific node
Set<GraphEdge> incomingEdges = graph.edgesTo(someNode);
```

## Key Features

### Immutability
All graph components are immutable after creation. The graph structure cannot be modified once built.

### Thread Safety
The graph can be safely accessed from multiple threads concurrently without synchronization.

### Type Safety
Nodes and edges can be filtered by their concrete types using the `nodesOfType()` and `edgesOfType()` methods.

### Relationship Modeling
The graph explicitly models the relationships between decision table components, making dependencies clear and traversable.

## Design Principles

1. **Separation of Concerns** - Static structure is separated from execution state
2. **Immutability** - All components are immutable for thread safety
3. **Type Safety** - Strong typing prevents runtime errors
4. **Builder Pattern** - Convenient construction while maintaining immutability
5. **Explicit Relationships** - All component relationships are explicitly modeled

## Performance Considerations

- Graph construction is a one-time cost intended to be done during initialization
- Graph traversal operations are O(n) where n is the number of nodes/edges
- Memory usage is proportional to the number of decision table components
- No dynamic allocation during graph traversal operations