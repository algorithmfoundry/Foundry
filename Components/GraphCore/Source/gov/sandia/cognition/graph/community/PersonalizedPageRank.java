/*
 * File:                PersonalizedPageRank.java
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

package gov.sandia.cognition.graph.community;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.util.DefaultKeyValuePair;
import gov.sandia.cognition.util.DoubleVector;
import gov.sandia.cognition.util.IntVector;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * This class can compute PersonalizedPageRank for the input graph and a
 * specified node, and also can determine a community for any specified node.
 * The code permits multiple queries against the same graph, storing the
 * necessary speed-up objects as part of the instance.
 *
 * @author jdwendt
 * @param <NodeNameType> The graph's node name type
 */
@PublicationReference(type = PublicationType.WebPage, title
    = "Personalized PageRank code", author = "dgleich", year = 2016, url
    = "https://gist.github.com/dgleich/6201856")
public class PersonalizedPageRank<NodeNameType>
{

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This contains
     * the ids of all neighbors of all nodes in node-order. To figure out a
     * specific node's neighbors, look from indices neighborsFirstIdx.get(i) to
     * neighborsFirstIdx.get(i+1).
     */
    private final IntVector neighbors;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This specifies
     * the index of the first neighbor in the neighbors list.
     */
    private final IntVector neighborsFirstIdx;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This contains
     * the weights of all neighbors of all nodes in node-order. Follows the same
     * order as IntVector neighbors.
     */
    private final DoubleVector neighborsWeights;

    /**
     * Stores the weighted degree of each node in the graph
     */
    private final DoubleVector nodeWeightedDegree;

    /**
     * Stores a copy of the graph for some of the translation capabilities, etc.
     */
    private final DirectedNodeEdgeGraph<NodeNameType> graph;

    /**
     * Stores the total weight of the edges in the graph times two (as each edge
     * is counted for both directions)
     */
    private final double gVol;

    /**
     * The random number generator for this instance
     */
    private Random generator;

    /**
     * The tolerance for spreading PPR further
     */
    private double pprTolerance;

    /**
     * Initializes all of the internal data-structures for the input graph. Note
     * that if the input graph is altered after passing it to this class, the
     * results for this instance can become unstable.
     *
     * @param graph The graph to compute personalized page rank for
     */
    public PersonalizedPageRank(DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        this(graph, 0.01);
    }

    /**
     * Initializes all of the internal data-structures for the input graph. Note
     * that if the input graph is altered after passing it to this class, the
     * results for this instance can become unstable.
     *
     * @param graph The graph to compute personalized page rank for
     * @param tolerance The tolerance for further spreading PPR. Should be
     * fairly small 0.01 or smaller. The closer to 0, the further it will
     * spread. Setting to 0 could lead to never quite converging (so probably
     * don't do that).
     */
    public PersonalizedPageRank(DirectedNodeEdgeGraph<NodeNameType> graph,
        double tolerance)
    {
        this.pprTolerance = tolerance;
        YaleFormatWeightedNeighbors<NodeNameType> neigh
            = new YaleFormatWeightedNeighbors<>(graph, true);
        this.neighbors = neigh.getNeighbors();
        this.neighborsFirstIdx = neigh.getNeighborsFirstIndex();
        this.neighborsWeights = neigh.getNeighborsWeights();
        this.graph = graph;
        this.nodeWeightedDegree = new DoubleVector(graph.numNodes());
        double tmpgVol = 0;
        for (int i = 0; i < graph.numNodes(); ++i)
        {
            this.nodeWeightedDegree.add(0.0);
            for (int j = this.neighborsFirstIdx.get(i); j
                < this.neighborsFirstIdx.get(i + 1); ++j)
            {
                this.nodeWeightedDegree.plusEquals(i, this.neighborsWeights.get(
                    j));
                tmpgVol += this.neighborsWeights.get(j);
            }
        }
        this.gVol = tmpgVol;
        this.generator = new Random();
    }

    /**
     * Set the tolerance to a new value. Should be small (0, 0.01]. The closer
     * to 0, the further it will spread. Setting to 0 could lead to never quite
     * converging (so probably don't do that).
     *
     * @param tolerance The tolerance for further spreading PPR.
     */
    public void setTolerance(double tolerance)
    {
        pprTolerance = tolerance;
    }

    /**
     * Initialize the random number generator with the input seed.
     *
     * @param seed The seed for the random number generator
     */
    public void setRandomSet(long seed)
    {
        generator = new Random(seed);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph)
     *
     * @param node The node to use as seed
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodes(NodeNameType node)
    {
        return getScoresForAllNodesById(graph.getNodeId(node));
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph)
     *
     * @param nodeIdx The node index to use as seed
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesById(int nodeIdx)
    {
        return getScoresForAllNodesByIds(Collections.singletonList(nodeIdx),
            false);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seeds
     *
     * @param nodes The nodes to use as seed
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodes(List<NodeNameType> nodes)
    {
        return getScoresForAllNodesByIds(convertToIds(nodes), false);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph)
     *
     * @param node The node to use as seed
     * @param randomized If true, the order nodes are treated within the
     * algorithm will be randomized so that you can get an average result across
     * multiple runs or just realize that any single run is not the completely
     * true answer.
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodes(NodeNameType node,
        boolean randomized)
    {
        return getScoresForAllNodesById(graph.getNodeId(node), randomized);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph)
     *
     * @param nodeIdx The node index to use as seed
     * @param randomized If true, the order nodes are treated within the
     * algorithm will be randomized so that you can get an average result across
     * multiple runs or just realize that any single run is not the completely
     * true answer.
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesById(int nodeIdx,
        boolean randomized)
    {
        return getScoresForAllNodesByIds(Collections.singletonList(nodeIdx),
            randomized);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seeds
     *
     * @param nodes The nodes to use as seed
     * @param randomized If true, the order nodes are treated within the
     * algorithm will be randomized so that you can get an average result across
     * multiple runs or just realize that any single run is not the completely
     * true answer.
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodes(List<NodeNameType> nodes,
        boolean randomized)
    {
        return getScoresForAllNodesByIds(convertToIds(nodes), randomized);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seed indices
     *
     * @param nodeIdxs The node indices to use as seed
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesByIds(List<Integer> nodeIdxs)
    {
        return getScoresForAllNodesByIds(nodeIdxs, false);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seed indices
     *
     * @param nodeIdxs The node indices to use as seed
     * @param randomized If true, the order nodes are treated within the
     * algorithm will be randomized so that you can get an average result across
     * multiple runs or just realize that any single run is not the completely
     * true answer.
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesByIds(List<Integer> nodeIdxs,
        boolean randomized)
    {
        final double ALPHA = 0.99;
        final double TOL = pprTolerance;

        Queue<Integer> fifo = new LinkedList<>();
        DoubleVector residual = new DoubleVector(graph.numNodes());
        DoubleVector x = new DoubleVector(graph.numNodes());
        for (int i = 0; i < graph.numNodes(); ++i)
        {
            residual.add(0.0);
            x.add(0.0);
        }
        double init = 1.0 / nodeIdxs.size();
        for (int n : nodeIdxs)
        {
            residual.set(n, init);
            fifo.add(n);
        }

        while (!fifo.isEmpty())
        {
            int v = fifo.remove();
            x.plusEquals(v, (1 - ALPHA) * residual.get(v));
            double mass = ALPHA * residual.get(v) / (2 * nodeWeightedDegree.get(
                v));
            IntVector range = IntVector.range(neighborsFirstIdx.get(v),
                neighborsFirstIdx.get(v + 1));
            if (randomized)
            {
                range.randomizeOrder(generator);
            }
            for (int m = 0; m < range.size(); ++m)
            {
                int i = range.get(m);
                int u = neighbors.get(i);
                if (u == v)
                {
                    throw new RuntimeException(
                        "This line should be unreachable.");
                }
                // The first part of the if insures u is not already in the queue
                if ((residual.get(u) < nodeWeightedDegree.get(u) * TOL)
                    && (residual.get(u) + mass * neighborsWeights.get(i)
                    >= nodeWeightedDegree.get(u) * TOL))
                {
                    fifo.add(u);
                }
                residual.plusEquals(u, mass * neighborsWeights.get(i));
            }
            residual.set(v, mass * nodeWeightedDegree.get(v));
            if (residual.get(v) >= nodeWeightedDegree.get(v) * TOL)
            {
                fifo.add(v);
            }
        }

        return x;
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seed. This implementation uses the random version and averages over
     * numRuns random runs.
     *
     * @param node The seed node to consider
     * @param numRuns The number of runs to perform
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesMultirun(NodeNameType node,
        int numRuns)
    {
        return getScoresForAllNodesByIdMultirun(graph.getNodeId(node), numRuns);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seed index. This implementation uses the random version and
     * averages over numRuns random runs.
     *
     * @param nodes The seed nodes
     * @param numRuns The number of runs to perform
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesMultirun(List<NodeNameType> nodes,
        int numRuns)
    {
        return getScoresForAllNodesByIdMultirun(convertToIds(nodes), numRuns);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seed index. This implementation uses the random version and
     * averages over numRuns random runs.
     *
     * @param nodeIdx The seed node's id
     * @param numRuns The number of runs to perform
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesByIdMultirun(int nodeIdx,
        int numRuns)
    {
        return getScoresForAllNodesByIdMultirun(Collections.singletonList(
            nodeIdx), numRuns);
    }

    /**
     * Returns the vector of all scores for all nodes in the graph (order
     * determined by node order as stored in the graph) as specified for the
     * input seed index. This implementation uses the random version and
     * averages over numRuns random runs.
     *
     * @param nodeIdxs The seed nodes' id
     * @param numRuns The number of runs to perform
     * @return the vector of all scores for all nodes in the graph
     */
    public DoubleVector getScoresForAllNodesByIdMultirun(List<Integer> nodeIdxs,
        int numRuns)
    {
        DoubleVector x = DoubleVector.zeros(graph.numNodes());
        for (int i = 0; i < numRuns; ++i)
        {
            DoubleVector tmp = getScoresForAllNodesByIds(nodeIdxs, true);
            for (int j = 0; j < tmp.size(); ++j)
            {
                x.plusEquals(j, tmp.get(j));
            }
        }
        double scalar = 1.0 / numRuns;
        for (int j = 0; j < x.size(); ++j)
        {
            x.set(j, x.get(j) * scalar);
        }

        return x;
    }

    /**
     * Computes the best community for the input node id by personalized page
     * rank scores and conductance of cut. Note both the PPR computation and the
     * community-via-conducntance computation contain order-dependence.
     * Therefore, this code always uses a random ordering. For PPR, it returns
     * the average PPR across all of the runs. For the community determination,
     * it returns the community with the best conductance based as found by
     * following edges in different orders.
     *
     * @param node The node whose community is desired
     * @param numRunsPpr The number of randomized runs to perform for the PPR
     * computation
     * @param numRunsCut The number of randomized runs to perform for community
     * determination
     * @return
     */
    public Set<Integer> getCommunityForNode(NodeNameType node,
        int numRunsPpr,
        int numRunsCut)
    {
        return getCommunityForNodeById(graph.getNodeId(node), numRunsPpr,
            numRunsCut);
    }

    /**
     * Computes the best community for the input node id by personalized page
     * rank scores and conductance of cut. Note both the PPR computation and the
     * community-via-conducntance computation contain order-dependence.
     * Therefore, this code always uses a random ordering. For PPR, it returns
     * the average PPR across all of the runs. For the community determination,
     * it returns the community with the best conductance based as found by
     * following edges in different orders.
     *
     * @param nodeIdx The node id whose community is desired
     * @param numRunsPpr The number of randomized runs to perform for the PPR
     * computation
     * @param numRunsCut The number of randomized runs to perform for community
     * determination
     * @return
     */
    public Set<Integer> getCommunityForNodeById(int nodeIdx,
        int numRunsPpr,
        int numRunsCut)
    {
        return getCommunityForNodesById(Collections.singletonList(nodeIdx),
            numRunsPpr, numRunsCut);
    }

    /**
     * Computes the best community for the input node id by personalized page
     * rank scores and conductance of cut. Note both the PPR computation and the
     * community-via-conducntance computation contain order-dependence.
     * Therefore, this code always uses a random ordering. For PPR, it returns
     * the average PPR across all of the runs. For the community determination,
     * it returns the community with the best conductance based as found by
     * following edges in different orders.
     *
     * @param nodes The node(s) whose community is desired
     * @param numRunsPpr The number of randomized runs to perform for the PPR
     * computation
     * @param numRunsCut The number of randomized runs to perform for community
     * determination
     * @return
     */
    public Set<Integer> getCommunityForNodes(List<NodeNameType> nodes,
        int numRunsPpr,
        int numRunsCut)
    {
        return getCommunityForNodesById(convertToIds(nodes), numRunsPpr,
            numRunsCut);
    }

    /**
     * Private helper that converts a list of node names to a list of node ids
     *
     * @param nodes The node names to convert
     * @return the node ids
     */
    private List<Integer> convertToIds(List<NodeNameType> nodes)
    {
        List<Integer> nodeIds = new ArrayList<>(nodes.size());
        for (NodeNameType node : nodes)
        {
            nodeIds.add(graph.getNodeId(node));
        }
        return nodeIds;
    }

    /**
     * Computes the best community for the input node id by personalized page
     * rank scores and conductance of cut. Note both the PPR computation and the
     * community-via-conducntance computation contain order-dependence.
     * Therefore, this code always uses a random ordering. For PPR, it returns
     * the average PPR across all of the runs. For the community determination,
     * it returns the community with the best conductance based as found by
     * following edges in different orders.
     *
     * @param nodeIdxs The node id(s) whose community is desired
     * @param numRunsPpr The number of randomized runs to perform for the PPR
     * computation
     * @param numRunsCut The number of randomized runs to perform for community
     * determination
     * @return
     */
    public Set<Integer> getCommunityForNodesById(List<Integer> nodeIdxs,
        int numRunsPpr,
        int numRunsCut)
    {
        DoubleVector x = getScoresForAllNodesByIdMultirun(nodeIdxs, numRunsPpr);
        for (int i = 0; i < graph.numNodes(); ++i)
        {
            x.set(i, x.get(i) / nodeWeightedDegree.get(i));
        }

        List<Pair<Integer, Double>> sorted = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); ++i)
        {
            // Don't add unvisited nodes
            if (x.get(i) == 0)
            {
                continue;
            }
            sorted.add(new DefaultKeyValuePair<>(i, x.get(i)));
        }
        Collections.sort(sorted, new Comparator<Pair<Integer, Double>>()
        {

            @Override
            public int compare(Pair<Integer, Double> o1,
                Pair<Integer, Double> o2)
            {
                return Double.compare(o2.getSecond(), o1.getSecond());
            }

        });
        double bestCond = Double.MAX_VALUE;
        Set<Integer> bestSet = null;
        for (int r = 0; r < numRunsCut; ++r)
        {
            double volS = 0.0;
            double cutS = 0.0;
            Set<Integer> setToAddTo = new HashSet<>(x.size());
            for (int i = 0; i < sorted.size(); ++i)
            {
                int idx = sorted.get(i).getFirst();
                volS += nodeWeightedDegree.get(idx);
                IntVector loop = IntVector.range(neighborsFirstIdx.get(idx),
                    neighborsFirstIdx.get(idx + 1));
                loop.randomizeOrder(generator);
                for (int s = 0; s < loop.size(); ++s)
                {
                    int j = loop.get(s);
                    if (setToAddTo.contains(neighbors.get(j)))
                    {
                        cutS -= neighborsWeights.get(j);
                    }
                    else
                    {
                        cutS += neighborsWeights.get(j);
                    }
                }
                setToAddTo.add(idx);
                double denom = Math.min(volS, gVol - volS);
                double cond = cutS / denom;
                // It can be practically 0, and that leads to ill-defined results
                if (Math.abs(denom) < 1e-10)
                {
                    cond = Double.MAX_VALUE;
                }
                if (cond < bestCond)
                {
                    bestCond = cond;
                    // Make a copy of this set
                    bestSet = new HashSet<>(setToAddTo);
                }
            }
        }
        return bestSet;
    }

}
