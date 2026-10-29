package ru.ewc.decisions.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ewc.decisions.api.ComputationContext;
import ru.ewc.decisions.api.DecisionTables;
import ru.ewc.decisions.api.InMemoryLocator;
import ru.ewc.decisions.conditions.Condition;
import ru.ewc.decisions.conditions.GreaterThanCondition;
import ru.ewc.state.State;

/**
 * Review-only contract probes for PR #169, head 730b213.
 * Copy into that revision's src/test/java/ru/ewc/decisions/core/ to run.
 * Failures document unmet contracts; production code is deliberately unchanged.
 */
final class DecisionGraphIsolationReviewTest {
    @Test
    void snapshotsCallerOwnedNodeMembership() {
        final Set<GraphNode> nodes = new HashSet<>();
        nodes.add(new CoordinateNode(Coordinate.from("customer::age")));
        final DecisionGraph graph = new InMemoryDecisionGraph(nodes, Set.of());
        nodes.clear();
        Assertions.assertEquals(1, graph.nodes().size(), "Caller clearing input must not empty graph");
    }

    @Test
    void snapshotsCallerOwnedEdgeMembership() {
        final GraphNode left = new CoordinateNode(Coordinate.from("1"));
        final GraphNode right = new CoordinateNode(Coordinate.from("2"));
        final Set<GraphEdge> edges = new HashSet<>();
        edges.add(new BasicGraphEdge(left, right, "part_of"));
        final DecisionGraph graph = new InMemoryDecisionGraph(Set.of(left, right), edges);
        edges.clear();
        Assertions.assertEquals(1, graph.edges().size(), "Caller clearing input must not remove edges");
    }

    @Test
    void preservesNodeAndEdgeIdentityDuringEvaluation() {
        final DecisionGraph graph = this.graph();
        final CoordinateNode node = this.variable(graph);
        final GraphEdge edge = graph.edgesFrom(node).iterator().next();
        final String nodeid = node.id();
        final int nodehash = node.hashCode();
        final String edgeid = edge.id();
        final int edgehash = edge.hashCode();
        this.condition(graph).evaluate(this.context(21));
        Assertions.assertAll(
            () -> Assertions.assertEquals(nodeid, node.id(), "Node id must describe static logic"),
            () -> Assertions.assertEquals(nodehash, node.hashCode(), "Node hash must stay stable"),
            () -> Assertions.assertEquals(edgeid, edge.id(), "Edge id must stay stable"),
            () -> Assertions.assertEquals(edgehash, edge.hashCode(), "Edge hash must stay stable")
        );
    }

    @Test
    void keepsEvaluatedNodesReachableInHashCollections() {
        final DecisionGraph graph = this.graph();
        final CoordinateNode node = this.variable(graph);
        final Set<GraphNode> index = new HashSet<>(128);
        index.add(node);
        Assertions.assertTrue(index.contains(node), "Control: node is present before evaluation");
        this.condition(graph).evaluate(this.context(21));
        Assertions.assertTrue(index.contains(node), "Same node must remain findable after evaluation");
    }

    @Test
    void readsSecondSessionsOwnValueAfterFirstSessionMatched() {
        final DecisionGraph graph = this.graph();
        final ComputationContext first = this.context(21);
        final ComputationContext second = this.context(17);
        Assertions.assertTrue(this.condition(graph).evaluate(first), "21 > 18");
        Assertions.assertFalse(this.condition(graph).evaluate(second), "Session B must use 17, not A's 21");
    }

    @Test
    void readsSecondSessionsOwnValueAfterFirstSessionDidNotMatch() {
        final DecisionGraph graph = this.graph();
        Assertions.assertFalse(this.condition(graph).evaluate(this.context(17)), "17 is not > 18");
        Assertions.assertTrue(this.condition(graph).evaluate(this.context(21)), "Session B must use 21, not A's 17");
    }

    @Test
    void resolvesDynamicAddressSeparatelyForEachSession() {
        final CoordinateNode node = new CoordinateNode(Coordinate.from("cells::${request::move}"));
        final DecisionGraph graph = new DecisionGraphBuilder().addNode(node).build();
        final Coordinate shared = (Coordinate) graph.nodes().iterator().next().component();
        Assertions.assertEquals("X", shared.valueIn(this.dynamicContext("A1")));
        Assertions.assertEquals("O", shared.valueIn(this.dynamicContext("A2")), "B must resolve its own request::move");
    }

    @Test
    void returnsUnmodifiableQueryCollections() {
        final DecisionGraph graph = this.graph();
        final CoordinateNode node = this.variable(graph);
        final ConditionNode condition = graph.nodesOfType(ConditionNode.class).iterator().next();
        Assertions.assertAll(
            () -> Assertions.assertThrows(UnsupportedOperationException.class,
                () -> graph.nodesOfType(CoordinateNode.class).clear(), "nodesOfType"),
            () -> Assertions.assertThrows(UnsupportedOperationException.class,
                () -> graph.edgesOfType(LhsOfEdge.class).clear(), "edgesOfType"),
            () -> Assertions.assertThrows(UnsupportedOperationException.class,
                () -> graph.edgesFrom(node).clear(), "edgesFrom"),
            () -> Assertions.assertThrows(UnsupportedOperationException.class,
                () -> graph.edgesTo(condition).clear(), "edgesTo")
        );
    }

    @Test
    void isolatesOrderedSessionsOnDifferentThreads() throws Exception {
        final DecisionGraph graph = this.graph();
        final ComputationContext first = this.context(21);
        final ComputationContext second = this.context(17);
        final var worker = Executors.newSingleThreadExecutor();
        try {
            Assertions.assertTrue(worker.submit(() -> this.condition(graph).evaluate(first)).get(5, TimeUnit.SECONDS));
            // B runs on the JUnit thread, after A completes: deterministic, without a race or sleep.
            Assertions.assertFalse(this.condition(graph).evaluate(second), "Separate thread/context must not inherit A's value");
        } finally {
            worker.shutdownNow();
        }
    }

    @Test
    void builderSnapshotsItsOwnMembership() {
        final CoordinateNode one = new CoordinateNode(Coordinate.from("1"));
        final CoordinateNode two = new CoordinateNode(Coordinate.from("2"));
        final DecisionGraphBuilder builder = new DecisionGraphBuilder().addNode(one);
        final DecisionGraph graph = builder.build();
        builder.addPartOfRelationship(one, two);
        Assertions.assertEquals(Set.of(one), graph.nodes());
        Assertions.assertTrue(graph.edges().isEmpty());
    }

    @Test
    void returnsUnmodifiableTopLevelCollections() {
        final DecisionGraph graph = this.graph();
        Assertions.assertAll(
            () -> Assertions.assertThrows(UnsupportedOperationException.class, () -> graph.nodes().clear()),
            () -> Assertions.assertThrows(UnsupportedOperationException.class, () -> graph.edges().clear())
        );
    }

    @Test
    void independentlyCreatedComponentsReadTheirOwnContexts() {
        Assertions.assertTrue(this.condition(this.graph()).evaluate(this.context(21)));
        Assertions.assertFalse(this.condition(this.graph()).evaluate(this.context(17)));
    }

    private DecisionGraph graph() {
        final Coordinate left = Coordinate.from("customer::age");
        final Coordinate right = Coordinate.from("18");
        final CoordinateNode lhs = new CoordinateNode(left);
        final CoordinateNode rhs = new CoordinateNode(right);
        final ConditionNode condition = new ConditionNode(new GreaterThanCondition(left, right), "adult");
        return new DecisionGraphBuilder()
            .addLhsOfRelationship(lhs, condition)
            .addRhsOfRelationship(rhs, condition)
            .build();
    }

    private Condition condition(final DecisionGraph graph) {
        return (Condition) graph.nodesOfType(ConditionNode.class).iterator().next().component();
    }

    private CoordinateNode variable(final DecisionGraph graph) {
        return graph.nodesOfType(CoordinateNode.class).stream()
            .filter(node -> node.id().equals("coordinate:customer::age"))
            .findFirst().orElseThrow();
    }

    private ComputationContext context(final int age) {
        return new ComputationContext(
            new State(List.of(new InMemoryLocator("customer", Map.of("age", age)))),
            DecisionTables.using(List::of)
        );
    }

    private ComputationContext dynamicContext(final String move) {
        return new ComputationContext(
            new State(List.of(
                new InMemoryLocator("request", Map.of("move", move)),
                new InMemoryLocator("cells", Map.of("A1", "X", "A2", "O"))
            )),
            DecisionTables.using(List::of)
        );
    }
}
