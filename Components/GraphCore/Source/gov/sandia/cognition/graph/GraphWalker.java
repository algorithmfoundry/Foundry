/*
 * File:                GraphWalker.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.graph;

import gov.sandia.cognition.util.DoubleVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides the core functionality for any random, deterministic, or
 * probabilistic graph walking code as long as only one edge can be traversed
 * from any given node (that is a depth-first or breadth-first can't be
 * implemented by this as it doesn't permit a queue of nodes to visit, but only
 * a single element "next node to visit").
 *
 * @author jdwendt
 * @param <NodeNameType> The datatype for the graph's nodes
 */
public class GraphWalker<NodeNameType>
{

    /**
     * The graph that will be traversed by this walker
     */
    private final DirectedNodeEdgeGraph<NodeNameType> graph;

    /**
     * The metrics that will be used by this for any walk-algorithm heuristics
     */
    private final GraphMetrics<NodeNameType> metrics;

    /**
     * Stores the id of current node being visited
     */
    private int curNodeId;

    /**
     * Stores the id of the last node visited
     */
    private int lastNodeId;

    /**
     * Passed in by the calling code -- Selects the next node to be visited
     * based on the current node, last node, and graph metrics
     */
    private final NextNodeSelector<NodeNameType> selector;

    /**
     * This interface defines the method needed by the GraphWalker class to
     * implement all features of graph walking _except_ the algorithm used to
     * select the next node to visit
     *
     * @param <NodeNameType> The datatype for the graph's node's names
     */
    public interface NextNodeSelector<NodeNameType>
    {

        /**
         * Given the current state of the system, this method returns the id of
         * the next node to visit
         *
         * @param lastNodeId The last node visited
         * @param curNodeId The current node being visited
         * @param metrics The graph's metrics
         * @return The id (NOT THE NAME) of the next node to vist
         */
        public int getNextNode(int lastNodeId,
            int curNodeId,
            GraphMetrics<NodeNameType> metrics);

    };

    /**
     * Initializes a new graph walker with as-yet empty metrics
     *
     * @param graph The graph to walk
     * @param selector The algorithm to use to select the next node in a walk
     */
    public GraphWalker(DirectedNodeEdgeGraph<NodeNameType> graph,
        NextNodeSelector<NodeNameType> selector)
    {
        this(graph, selector, new GraphMetrics<>(graph));
    }

    /**
     * Initializes a new graph walker with the input values
     *
     * @param graph The graph to walk
     * @param selector The algorithm to use to select the next node in a walk
     * @param metrics The metrics to use
     */
    public GraphWalker(DirectedNodeEdgeGraph<NodeNameType> graph,
        NextNodeSelector<NodeNameType> selector,
        GraphMetrics<NodeNameType> metrics)
    {
        this.graph = graph;
        this.metrics = metrics;
        this.selector = selector;
        this.curNodeId = -1;
        this.lastNodeId = -1;
    }

    /**
     * Private helper that takes a single step as directed by the selector and
     * updates the proper internal variables.
     *
     * @return the id of the new current node
     */
    private int step()
    {
        int next = selector.getNextNode(lastNodeId, curNodeId, metrics);
        lastNodeId = curNodeId;
        curNodeId = next;

        return curNodeId;
    }

    /**
     * Sets the starting node (by id) for the next step (or series of steps)
     *
     * @param nodeId The node to start at
     */
    public void setStartNode(int nodeId)
    {
        curNodeId = nodeId;
        lastNodeId = -1;
    }

    /**
     * Sets the start node (by name) for the next step (or series of steps)
     *
     * @param node The node to start at
     */
    public void setStartNode(NodeNameType node)
    {
        curNodeId = graph.getNodeId(node);
        lastNodeId = -1;
    }

    /**
     * Returns the path traversed across numSteps steps of the walk from the
     * current start node.
     *
     * @param numSteps The number of steps to take
     * @return A list of numSteps node names (not necessarily distinct)
     */
    public List<NodeNameType> getPath(int numSteps)
    {
        List<NodeNameType> path = new ArrayList<>(numSteps);
        for (int i = 0; i < numSteps; ++i)
        {
            path.add(graph.getNode(step()));
        }

        return path;
    }

    /**
     * Returns the path traversed across numSteps steps of the walk from
     * startNode
     *
     * @param startNode The node to start from
     * @param numSteps The number of steps to take
     * @return A list of numSteps node names (not necessarily distinct)
     */
    public List<NodeNameType> getPath(NodeNameType startNode,
        int numSteps)
    {
        setStartNode(startNode);
        return getPath(numSteps);
    }

    /**
     * Returns the last node reached on a walk of numSteps steps starting from
     * current node
     *
     * @param numSteps The number of steps to take
     * @return The node reached at the end of the walk
     */
    public NodeNameType getEndNode(int numSteps)
    {
        List<NodeNameType> path = getPath(numSteps);
        if (path.isEmpty())
        {
            return null;
        }
        return path.get(path.size() - 1);
    }

    /**
     * Returns the last node reached on a walk of numSteps steps starting from
     * startNode
     *
     * @param startNode The node to start from
     * @param numSteps The number of steps to take
     * @return the node reached at the end of the walk
     */
    public NodeNameType getEndNode(NodeNameType startNode,
        int numSteps)
    {
        setStartNode(startNode);
        return getEndNode(numSteps);
    }

    /**
     * After running numTries walk of numSteps starting from startNode, this
     * returns a map where the keys are all nodes reached at the end and the
     * values are the number of times those nodes were reached. The sum of all
     * keys is numTries.
     *
     * @param startNode The node to start from on every path
     * @param numSteps The number of steps to take on any path
     * @param numTries The number of paths to run
     * @return The end nodes (keys) with the number of times each was reached at
     * the end (values)
     */
    public Map<NodeNameType, Integer> getEndNodes(NodeNameType startNode,
        int numSteps,
        int numTries)
    {
        Map<NodeNameType, Integer> endNodes = new HashMap<>(numTries);
        for (int i = 0; i < numTries; ++i)
        {
            NodeNameType end = getEndNode(startNode, numSteps);
            if (!endNodes.containsKey(end))
            {
                endNodes.put(end, 0);
            }
            endNodes.put(end, endNodes.get(end) + 1);
        }

        return endNodes;
    }

    /**
     * This class implements the most simple walker which randomly selects from
     * the available edges. If directed, it follows only those edges that
     * originate at the current node. If no edges are available, the walker
     * remains in the current node on each "step".
     *
     * Note that the current implementation does not give repeated edges
     * increased weight in either the directed or undirected case.
     *
     * @param <NodeNameType> The datatype used for node names in the graph being
     * walked
     */
    public static class RandomWalker<NodeNameType>
        implements NextNodeSelector<NodeNameType>
    {

        /**
         * If the walk should be directed or not
         */
        private final boolean directed;

        /**
         * Creates a random walker with directedness specified
         *
         * @param directed If true, this follows only edges which originate at
         * the current node
         */
        public RandomWalker(boolean directed)
        {
            this.directed = directed;
        }

        /**
         * Returns the id of the next node to visit based on current state
         *
         * @param lastNodeId The last node visited
         * @param curNodeId The node presently visiting
         * @param metrics The metrics for the graph being walked
         * @return the id of the next node to visit
         */
        @Override
        public int getNextNode(int lastNodeId,
            int curNodeId,
            GraphMetrics<NodeNameType> metrics)
        {
            Set<Integer> possibles;
            if (directed)
            {
                possibles = metrics.successorIds(curNodeId);
            }
            else
            {
                possibles = metrics.neighborIds(curNodeId);
            }
            int numChoices = possibles.size();
            // Can't go anywhere
            if (numChoices == 0)
            {
                return curNodeId;
            }
            int which = (int) (Math.random() * numChoices);
            // If Math.random returns 1.0, then it selects an impossible index
            if (which == numChoices)
            {
                --which;
            }
            int cnt = 0;
            for (Integer possible : possibles)
            {
                if (cnt == which)
                {
                    return possible;
                }
                ++cnt;
            }
            throw new RuntimeException("After running through " + cnt
                + " choices, choice " + which + " was never found");
        }

    };

    /**
     * Helper method that returns the index of the probabilistically selected
     * input weight. Note that the input weights need not be probabilities (sum
     * to 1) as this will normalize the selection properly (without changing the
     * input values)
     *
     * @param weights The relative weights of each choice
     * @return The index of the probabilistically chosen input weight
     */
    public static int probablisticSelect(DoubleVector weights)
    {
        double sum = 0;
        for (int i = 0; i < weights.size(); ++i)
        {
            sum += weights.get(i);
        }
        double random = Math.random() * sum;
        sum = 0;
        for (int i = 0; i < weights.size(); ++i)
        {
            sum += weights.get(i);
            if (random <= sum)
            {
                return i;
            }
        }

        throw new RuntimeException("It should be impossible that random ("
            + random + ") is strictly greater than sum (" + sum + ")");
    }

}
