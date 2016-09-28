/*
 * File:                Louvain.java
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
import gov.sandia.cognition.graph.DirectedWeightedNodeEdgeGraph;
import gov.sandia.cognition.util.DoubleVector;
import gov.sandia.cognition.util.IntVector;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class performs community detection using the Louvain method. Each
 * instance should be used to run Louvain only one time.
 *
 * @param <NodeNameType> The class type for the graph's nodes
 * @author jdwendt
 */
@PublicationReference(author
    = "Vincent D Blondel, Jean-Loup Guillaume, Renaud Lambiotte, Etienne Lefebvre",
    title = "Fast unfolding of communities in large networks",
    type = PublicationType.Journal,
    year = 2008, publication
    = "Journal of Statistical Mechanics: Theory and Experiment")
public class Louvain<NodeNameType>
{

    /**
     * The weighted degree of each node in the graph at the current level of the
     * hierarchy.
     */
    private DoubleVector weightedNodeDegree;

    /**
     * The total weight of the graph at the current level of the hierarchy.
     */
    private double totalWeight;

    /**
     * Map from each node id to its assigned community id.
     */
    private IntVector nodeToCommunity;

    /**
     * Total weighted edges inside and outside a community.
     */
    private DoubleVector communityTotal;

    /**
     * Weighted edges inside a community.
     */
    private DoubleVector communityInternal;

    /**
     * Weighted self loops on a specific node.
     */
    private DoubleVector weightedSelfLoops;

    /**
     * A user-specified parameter that limits the number of passes that might be
     * done at each level in the hierarchy
     */
    private int maxNumPasses;

    /**
     * A user-specified parameter that specifies the minimum amount modularity
     * must increase if further levels should be done
     */
    private double minModularityGain;

    /**
     * Local map from the nodes of the graph to the internal integer id for the
     * node.
     */
    private Map<NodeNameType, Integer> nodeMap;

    /**
     * Weighted edges to that neighboring community (recalculated on the fly for
     * each node in the update loop -- see initNeighborCommunity)
     */
    private DoubleVector neighborCommWeight;

    /**
     * Neighbor communities' ids (recalculated on the fly for each node in the
     * update loop -- see initNeighborCommunity)
     */
    private IntVector neighborCommId;

    /**
     * Number of neighbors calculated on the fly in the update loop (see
     * initNeighborCommunity)
     */
    private int numNeighborCommunities;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This specifies
     * the index of the first neighbor in the neighbors list.
     */
    private IntVector neighborsFirstIdx;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This contains
     * the ids of all neighbors of all nodes in node-order. To figure out a
     * specific node's neighbors, look from indices neighborsFirstIdx.get(i) to
     * neighborsFirstIdx.get(i+1).
     */
    private IntVector neighbors;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This contains
     * the weights of all neighbors of all nodes in node-order. Follows the same
     * order as IntVector neighbors.
     */
    private DoubleVector wNeighbors;

    /**
     * The number of nodes in the input graph
     */
    private int numNodes;

    /**
     * The random number generator to be used
     */
    private Random generator;

    /**
     * Stores the results of the Louvain algorithm
     */
    private LouvainHierarchy<NodeNameType> results;

    /**
     * Initializes the internal Louvain datatypes that store the necessaries to
     * run Louvain on the input graph.
     *
     * @param graph The graph to execute community detection on
     */
    public Louvain(DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        this(graph, 100, 1e-4);
    }

    /**
     * Initializes the internal Louvain datatypes that store the necessaries to
     * run Louvain on the input graph.
     *
     * @param graph The graph to execute community detection on
     * @param maxNumPasses The maximum number of passes to iterate within each
     * level of the hierarchy
     * @param minModularityGain The minimum the amount modularity must improve
     * on each iteration to continue iterating within each level of the
     * hierarchy.
     */
    public Louvain(DirectedNodeEdgeGraph<NodeNameType> graph,
        int maxNumPasses,
        double minModularityGain)
    {
        if (minModularityGain < 0)
        {
            throw new IllegalArgumentException(
                "Minimum modularity gain must be postive.");
        }
        if (maxNumPasses <= 0)
        {
            throw new IllegalArgumentException(
                "Maximum number of passes per iteration must be postive");
        }
        // Copy the input parameters
        this.maxNumPasses = maxNumPasses;
        this.minModularityGain = minModularityGain;

        // Initialize various internal data for the communities/nodes
        this.numNodes = graph.numNodes();
        this.nodeToCommunity = new IntVector();
        this.weightedNodeDegree = new DoubleVector(numNodes);
        this.weightedSelfLoops = new DoubleVector(numNodes);
        this.neighborCommWeight = new DoubleVector(numNodes);
        this.neighborCommId = new IntVector(numNodes);
        this.nodeMap = new HashMap<>(numNodes);

        // Initialize the per-node values
        int neighborsSoFar = 0;
        this.numNeighborCommunities = 0;
        this.totalWeight = 0;
        Map<Integer, HashMap<Integer, Double>> edges = new HashMap<>(
            graph.numNodes());
        for (int i = 0; i < graph.numNodes(); ++i)
        {
            edges.put(i, new HashMap<>());
        }
        for (int i = 0; i < graph.numEdges(); ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            int l = edge.getFirst();
            int r = edge.getSecond();
            double w = 1.0;
            if (graph instanceof DirectedWeightedNodeEdgeGraph)
            {
                w = ((DirectedWeightedNodeEdgeGraph) graph).getEdgeWeight(i);
            }
            if (!edges.get(l).containsKey(r))
            {
                edges.get(l).put(r, 0.0);
            }
            edges.get(l).put(r, w + edges.get(l).get(r));
            if (!edges.get(r).containsKey(l))
            {
                edges.get(r).put(l, 0.0);
            }
            edges.get(r).put(l, w + edges.get(r).get(l));
        }
        for (int i = 0; i < graph.numNodes(); ++i)
        {
            // Save the vertex-to-id map for myself going forward
            this.nodeMap.put(graph.getNode(i), i);
            // All nodes start in their own community
            this.nodeToCommunity.add(i);
            // These are their "null" values
            this.neighborCommWeight.add(-1);
            this.neighborCommId.add(0);
            // Initialize all of these to zero ... they are computed in the per-edge for loop
            this.weightedNodeDegree.add(0);
            this.weightedSelfLoops.add(0);
            neighborsSoFar += edges.get(i).size();
        }
        // Initialize the per-edge values
        for (Map.Entry<Integer, HashMap<Integer, Double>> edgeMap
            : edges.entrySet())
        {
            int l = edgeMap.getKey();
            for (Map.Entry<Integer, Double> edge : edgeMap.getValue().entrySet())
            {
                int r = edge.getKey();
                double w = edge.getValue();
                // It's a self loop
                if (l == r)
                {
                    this.weightedSelfLoops.plusEquals(l, w);
                }
                this.weightedNodeDegree.plusEquals(l, w);
            }
        }
        this.communityTotal = new DoubleVector(numNodes);
        this.communityInternal = new DoubleVector(numNodes);
        // Now that weighted node degree and self loops are computed...
        for (int i = 0; i < numNodes; ++i)
        {
            // Update the total and internal edge weights for the current communities
            this.communityTotal.add(this.weightedNodeDegree.get(i));
            this.communityInternal.add(this.weightedSelfLoops.get(i));
            // And the total weight for the graph
            this.totalWeight += this.weightedNodeDegree.get(i);
        }
        YaleFormatWeightedNeighbors<NodeNameType> yale
            = new YaleFormatWeightedNeighbors<>(graph, false);
        this.neighbors = new IntVector(yale.getNeighbors());
        this.wNeighbors = new DoubleVector(yale.getNeighborsWeights());
        this.neighborsFirstIdx = new IntVector(yale.getNeighborsFirstIndex());

        generator = new Random();
        results = new LouvainHierarchy<>(nodeMap);
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
     * The input partition will serve as the initial partition for the graph
     * (replacing Louvain's default "each node to its own community"
     * partitioning). NOTE: This initial partitioning will be modified by
     * solveCommunities to find a "local best" community detection. Thus nodes
     * assigned to the same community by the input to this method may be
     * separated to different communities by the community detection algorithm.
     *
     * @param initPart The initial partitioning where the key is a node id and
     * the value is a community id. Not all nodes need be assigned.
     * @throws IllegalArgumentException if an input key does not exist in the
     * graph.
     */
    public void initialPartition(Map<NodeNameType, Integer> initPart)
    {
        for (Map.Entry<NodeNameType, Integer> community : initPart.entrySet())
        {
            // Get the node's local ID
            int nodeId = nodeMap.get(community.getKey());
            // If there is no such node, throw an exception
            if (nodeId == -1)
            {
                throw new IllegalArgumentException(
                    "There is no node in the graph at " + community.getKey());
            }
            // Get it's old community and remove it from that community
            int oldCommunity = nodeToCommunity.get(nodeId);
            initNeighborCommunity(nodeId);

            removeFromCommunity(nodeId, oldCommunity, neighborCommWeight.get(
                oldCommunity));

            // Now first see if the new community is even connected (not truly necessary...
            boolean found = false;
            for (int i = 0; i < numNeighborCommunities; ++i)
            {
                int bestCommunity = neighborCommId.get(i);
                if (bestCommunity == community.getValue())
                {
                    insertIntoCommunity(nodeId, bestCommunity,
                        neighborCommWeight.get(neighborCommId.get(i)));
                    found = true;
                    break;
                }
            }
            // because it's still added to that community even if it's not attached to it at present).
            if (!found)
            {
                insertIntoCommunity(nodeId, community.getValue(), 0);
            }
        }
    }

    /**
     * Helper that finds the connections between the input node ID and all
     * communities surrounding it. The results are stored in neighborCommWeight,
     * neighborCommId, and numNeighborCommunities.
     *
     * @param nodeId The node ID to update the neighborhood for
     */
    private void initNeighborCommunity(int nodeId)
    {
        // Clear out the last community values
        for (int i = 0; i < numNeighborCommunities; ++i)
        {
            neighborCommWeight.set(neighborCommId.get(i), -1);
        }
        numNeighborCommunities = 0;

        // Add its current assignment with no weight
        neighborCommId.set(0, nodeToCommunity.get(nodeId));
        neighborCommWeight.set(neighborCommId.get(0), 0);
        numNeighborCommunities = 1;

        // Now, go through all of its neighbors
        for (int i = neighborsFirstIdx.get(nodeId); i < neighborsFirstIdx.get(
            nodeId + 1); ++i)
        {
            // Get the neighbor's values
            int neighbor = neighbors.get(i);
            int neighborComm = nodeToCommunity.get(neighbor);
            double neighborW = wNeighbors.get(i);

            // If this isn't a self-loop
            // Self-loops are handled in insertIntoCommunity and removeFromCommunity
            if (neighbor != nodeId)
            {
                // If the neighbor's community hasn't been found yet, add it with default values
                if (neighborCommWeight.get(neighborComm) == -1)
                {
                    neighborCommId.set(numNeighborCommunities, neighborComm);
                    neighborCommWeight.set(neighborComm, 0);
                    ++numNeighborCommunities;
                }
                neighborCommWeight.plusEquals(neighborComm, neighborW);
            }
        }
    }

    /**
     * Returns the modularity of the most recent partitioning of the graph
     *
     * @return the modularity of the most recent partitioning of the graph
     */
    public double modularity()
    {
        double q = 0;
        double oneOverM2 = 1.0 / totalWeight;

        for (int i = 0; i < numNodes; ++i)
        {
            double cTot = communityTotal.get(i);
            if (cTot > 0)
            {
                q += communityInternal.get(i) * oneOverM2 - ((cTot * cTot)
                    * (oneOverM2 * oneOverM2));
            }
        }

        if (q >= 1.0 || q < -0.5)
        {
            throw new InternalError(
                "Modularity outside acceptable range [-0.5, 1): " + q);
        }
        return q;
    }

    /**
     * Helper that updates the internal datastructures for calculating the
     * modularity of the graph by inserting the input node ID's values into the
     * input community ID's partial-modularity values. NOTE: If you give this
     * method bad ids (say a node that doesn't belong to that community) this
     * will happily perform the same computations as normal and ruin the
     * internal representation. That's why this method is private!
     *
     * @param nodeId The ID of the node to insert from the community
     * @param communityId The ID of the community to insert the node from
     * @param dNodeComm The sum of the weight of the edges between the input
     * node and the community (I think).
     */
    private void insertIntoCommunity(int nodeId,
        int communityId,
        double dNodeComm)
    {
        communityTotal.plusEquals(communityId, weightedNodeDegree.get(nodeId));
        communityInternal.plusEquals(communityId, 2 * dNodeComm
            + weightedSelfLoops.get(nodeId));
        nodeToCommunity.set(nodeId, communityId);
    }

    /**
     * Helper that updates the internal datastructures for calculating the
     * modularity of the graph by removing the input node ID's values from the
     * input community ID's partial-modularity values. NOTE: If you give this
     * method bad ids (say a node that doesn't belong to that community) this
     * will happily perform the same computations as normal and ruin the
     * internal representation. That's why this method is private!
     *
     * @param nodeId The ID of the node to remove from the community
     * @param communityId The ID of the community to remove the node from
     * @param dNodeComm The sum of the weight of the edges between the input
     * node and the community (I think).
     */
    private void removeFromCommunity(int nodeId,
        int communityId,
        double dNodeComm)
    {
        communityTotal.plusEquals(communityId, -weightedNodeDegree.get(nodeId));
        communityInternal.plusEquals(communityId, -(2 * dNodeComm
            + weightedSelfLoops.get(nodeId)));
        nodeToCommunity.set(nodeId, -1);
    }

    /**
     * Private helper that calculates the improvement of the modularity (can be
     * negative) by adding a node with the input values into the input
     * community.
     *
     * @param communityId The community the node will be added into
     * @param dNodeComm (I think) the sum of the weight of the edges between the
     * to-be-added node and the community.
     * @param wDegree The weighted degree of the node.
     * @return The improvement of the modularity (can be negative) by adding a
     * node with the input values into the input community.
     */
    private double modularityGain(int communityId,
        double dNodeComm,
        double wDegree)
    {
        return dNodeComm - communityTotal.get(communityId) * wDegree
            / totalWeight;
    }

    /**
     * Private helper that iterates while adding nodes to their best neighboring
     * community until the no changes are made on an iteration (or other timeout
     * thresholds are reached).
     *
     * @return True if changes occurred in this iteration
     */
    private boolean computeOneLevel()
    {
        boolean improved = false;
        int numMoves;
        int numPasses = 0;
        double newModularity = modularity();
        double curModularity;

        // Create random-order list
        int n = nodeToCommunity.size();
        IntVector randomOrder = new IntVector(n);
        for (int i = 0; i < n; ++i)
        {
            randomOrder.add(i);
        }
        for (int i = 0; i < n; ++i)
        {
            int randomPositionInRemainingList = generator.nextInt(n - i) + i;
            randomOrder.swap(i, randomPositionInRemainingList);
        }

        // iterate while modularity improves more than epsilon (can be 0), or numPasses
        do
        {
            curModularity = newModularity;
            numMoves = 0;
            ++numPasses;

            // for each node, remove it from current community and insert into best community
            for (int i = 0; i < n; ++i)
            {
                int node = randomOrder.get(i);
                int curCommunity = nodeToCommunity.get(node);
                double weightedDegree = weightedNodeDegree.get(node);

                // Compute all neighboring communities of current node
                initNeighborCommunity(node);
                // Remove from current community
                removeFromCommunity(node, curCommunity, neighborCommWeight.get(
                    curCommunity));

                if (this.communityInternal.get(curCommunity)
                    - this.communityTotal.get(curCommunity) > 1e-6)
                {
                    throw new InternalError("Removed node " + node
                        + " from community " + curCommunity
                        + " and somehow community weights got thrown off");
                }
                // Compute nearest community for node
                int bestCommunity = curCommunity;
                // NOTE: This fixes a bug that exists in the Louvain code from the authors.
                // Specifically, there are some cases where assigning this 0 here (as they
                // do) fouls up the communityInternal temporary variable, and messes up
                // modularity computations.
                double bestNumLinks = neighborCommWeight.get(curCommunity);
                double bestIncrease = 0;
                for (int j = 0; j < numNeighborCommunities; ++j)
                {
                    int neighId = neighborCommId.get(j);
                    double neighComW = neighborCommWeight.get(neighId);
                    double increase = modularityGain(neighId, neighComW,
                        weightedDegree);
                    if (increase > bestIncrease)
                    {
                        bestCommunity = neighId;
                        bestNumLinks = neighComW;
                        bestIncrease = increase;
                    }
                }

                // Insert node into best community
                insertIntoCommunity(node, bestCommunity, bestNumLinks);
                if (this.communityInternal.get(bestCommunity)
                    - this.communityTotal.get(bestCommunity) > 1e-6)
                {
                    throw new InternalError("Removed node " + node
                        + " from community " + curCommunity
                        + " and somehow community weights got thrown off");
                }

                numMoves += (bestCommunity == curCommunity) ? 0 : 1;
            }

            newModularity = modularity();
            if (numMoves > 0)
            {
                improved = true;
            }
        }
        while ((numMoves > 0) && ((newModularity - curModularity)
            > minModularityGain) && (numPasses < maxNumPasses));

        return improved;
    }

    /**
     * Fixes the numbering of the communities in a level so that they go from 0
     * ... numCommunities.
     */
    private void renumberCommunitesInLevel()
    {
        Map<Integer, Integer> commIds = new HashMap<>();
        int renumber = 0;
        for (int i = 0; i < nodeToCommunity.size(); ++i)
        {
            int j = nodeToCommunity.get(i);
            if (j == -1)
            {
                throw new InternalError(
                    "Node to community map corrupted: -1 at position " + i
                    + " out of " + nodeToCommunity.size());
            }
            if (!commIds.keySet().contains(j))
            {
                commIds.put(j, renumber);
                ++renumber;
            }
            nodeToCommunity.set(i, commIds.get(j));
        }
    }

    /**
     * This creates the new internal values for the next level. This is
     * performed by merging all nodes within a community at the current level
     * into a new "node" that has a self loop with the same weight as all edges
     * that went between the old nodes at this level, and outward links to all
     * other "nodes" with the same weight as all old edges to nodes in the
     * corresponding "node"/community.
     */
    private void updateForNextLevel()
    {
        double curMod = modularity();
        // How many communities are there in the current level?
        int numCommunities = 0;
        for (int i = 0; i < nodeToCommunity.size(); ++i)
        {
            numCommunities = Math.max(numCommunities, nodeToCommunity.get(i));
        }
        // Because it includes the 0 index, it can go one past the max value
        ++numCommunities;
        // Create the community-based graph
        Map<Integer, Set<Integer>> edges = new HashMap<>(numCommunities);
        for (int i = 0; i < numCommunities; ++i)
        {
            edges.put(i, new HashSet<Integer>());
        }
        for (int i = 0; i < numNodes; ++i)
        {
            int src = nodeToCommunity.get(i);
            for (int j = neighborsFirstIdx.get(i); j < neighborsFirstIdx.get(i
                + 1); ++j)
            {
                edges.get(src).add(nodeToCommunity.get(neighbors.get(j)));
            }
        }
        // Convert it to the denser structure
        // First the firstIdxs
        IntVector commFirstIdx = new IntVector(numCommunities + 1);
        int totalEdges = 0;
        commFirstIdx.add(totalEdges);
        for (int i = 0; i < numCommunities; ++i)
        {
            totalEdges += edges.get(i).size();
            commFirstIdx.add(totalEdges);
        }
        IntVector commNeighbors = new IntVector(totalEdges);
        DoubleVector commNeighWeights = new DoubleVector(totalEdges);
        // Initialize all to -1 for edges, 0 for weights
        for (int i = 0; i < totalEdges; ++i)
        {
            commNeighbors.add(-1);
            commNeighWeights.add(0);
        }
        // Go through and add each edge and increase weight (even if it's already there)
        for (int i = 0; i < numNodes; ++i)
        {
            int src = nodeToCommunity.get(i);
            for (int j = neighborsFirstIdx.get(i); j < neighborsFirstIdx.get(i
                + 1); ++j)
            {
                int dst = nodeToCommunity.get(neighbors.get(j));
                int idx = -1;
                for (int k = commFirstIdx.get(src); k
                    < commFirstIdx.get(src + 1); ++k)
                {
                    if (commNeighbors.get(k) == dst)
                    {
                        idx = k;
                        break;
                    }
                    else if (commNeighbors.get(k) == -1)
                    {
                        commNeighbors.set(k, dst);
                        idx = k;
                        break;
                    }
                }
                if (idx == -1)
                {
                    throw new InternalError(
                        "Unable to find the new edge's location or an empty place for an edge");
                }
                commNeighWeights.plusEquals(idx, wNeighbors.get(j));
            }
        }
        // Just make sure that it's all the way full (no -1 edges)
        for (int i = 0; i < totalEdges; ++i)
        {
            if (commNeighbors.get(i) == -1)
            {
                throw new InternalError("An edge wasn't set.");
            }
        }

        // I now have the new dense representation of the graph at this level
        // Now create the new per-node values (each community will become a node)
        // This is largely the same as what was done in the constructor
        this.numNodes = numCommunities;
        this.nodeToCommunity.clear();
        this.nodeToCommunity.reserve(numNodes);
        this.weightedNodeDegree.clear();
        this.weightedNodeDegree.reserve(numNodes);
        this.weightedSelfLoops.clear();
        this.weightedSelfLoops.reserve(numNodes);
        this.neighborCommWeight.clear();
        this.neighborCommWeight.reserve(numNodes);
        this.neighborCommId.clear();
        this.neighborCommId.reserve(numNodes);
        this.numNeighborCommunities = 0;
        this.totalWeight = 0;
        for (int i = 0; i < numNodes; ++i)
        {
            // All nodes start in their own community
            this.nodeToCommunity.add(i);
            // These are their "null" values
            this.neighborCommWeight.add(-1);
            this.neighborCommId.add(0);
            // Initialize all to zero .. they are computed in the per-edge for loop
            this.weightedNodeDegree.add(0);
            this.weightedSelfLoops.add(0);
        }
        // Now the per-edge values
        for (int i = 0; i < numNodes; ++i)
        {
            for (int j = commFirstIdx.get(i); j < commFirstIdx.get(i + 1); ++j)
            {
                int l = i;
                int r = commNeighbors.get(j);
                double w = commNeighWeights.get(j);
                // It's a self-loop
                if (l == r)
                {
                    this.weightedSelfLoops.plusEquals(l, w);
                    this.weightedNodeDegree.plusEquals(l, w);
                }
                else
                {
                    // NOTE: This isn't an edge list, it's a neighbors list.
                    // That means that both nodes have each other in their
                    // neighbors list, so you only need to add the weight to one
                    // side of each edge.
                    this.weightedNodeDegree.plusEquals(l, w);
                    //this.weightedNodeDegree.plusEquals(r, w);
                }
            }
        }
        this.communityTotal.clear();
        this.communityTotal.reserve(numNodes);
        this.communityInternal.clear();
        this.communityInternal.reserve(numNodes);
        for (int i = 0; i < numNodes; ++i)
        {
            // Update the total and internal edge weights for the current communities
            this.communityTotal.add(this.weightedNodeDegree.get(i));
            this.communityInternal.add(this.weightedSelfLoops.get(i));
            // And the total weight for the graph
            this.totalWeight += this.weightedNodeDegree.get(i);
        }

        // Now blow away the old neighbors group
        this.neighborsFirstIdx = commFirstIdx;
        this.neighbors = commNeighbors;
        this.wNeighbors = commNeighWeights;
        if (Math.abs(curMod - modularity()) > 1e-6)
        {
            throw new InternalError("Modularity should remain the same after "
                + "this update: " + curMod + " != " + modularity());
        }
    }

    /**
     * Solves for community detection of the graph passed in during the
     * constructor. If this was called previously, the same result will be
     * returned herein. If you want a new random run, create a new instance with
     * the same graph.
     *
     * @return The hierarchy of the community structure for all nodes.
     */
    public LouvainHierarchy<NodeNameType> solveCommunities()
    {
        // Only solve it once per instance of the class
        if (results.numLevels() != 0)
        {
            return results;
        }
        while (true)
        {
            boolean improved = computeOneLevel();
            renumberCommunitesInLevel();
            if (!improved)
            {
                // This is necessary for if the user seeded the graph with the optimal modularity
                if (results.numLevels() == 0)
                {
                    results.addLevel(nodeToCommunity, modularity());
                }
                return results;
            }
            results.addLevel(nodeToCommunity, modularity());
            updateForNextLevel();
        }
    }

    /**
     * The return type from running Louvain. Can be queried for the community ID
     * for a node either for the top-level or for any level in the hierarchy.
     *
     * @param <NodeNameType> The class-type of the nodes in the graph this
     * references.
     */
    public static class LouvainHierarchy<NodeNameType>
        implements NodePartitioning<NodeNameType>
    {

        /**
         * This vector stores all community assignments at all levels for the
         * graph. Stored as [level0:[level1_community_id_for_node_0 ...
         * level1_community_id_for_node_n-1],
         * level1:[level2_community_for_level1_comm_0 ...
         * level2_community_for_level1_comm_m], ...].
         */
        private final IntVector communities;

        /**
         * This has numLevels + 1 indices into the communities vector. The ith
         * entry is the index of the first position in that level. NOTE: This
         * (combined with communities) is a lot like the Yale format for densely
         * storing sparse matrices.
         */
        private final IntVector levelIndex;

        /**
         * The map from nodes to their local integer ids.
         */
        private final Map<NodeNameType, Integer> nodeMap;

        /**
         * The modularity at each level in the map.
         */
        private final DoubleVector modularities;

        private List<Set<NodeNameType>> topCommunities;

        /**
         * Initializes this for the nodes it will handle. After this method, the
         * data is largely empty and is completely invalid.
         *
         * @param nodeMap The map from node value to local id.
         */
        LouvainHierarchy(Map<NodeNameType, Integer> nodeMap)
        {
            communities = new IntVector();
            levelIndex = new IntVector(10);
            modularities = new DoubleVector(10);
            levelIndex.add(communities.size());
            this.nodeMap = Collections.unmodifiableMap(nodeMap);
            topCommunities = null;
        }

        /**
         * Adds all community ids assigned in the current level. The ordering of
         * level must follow the local-id value for the nodes/communities in the
         * previous level.
         *
         * @param level The level to add
         * @param modularity The modularity after this level was run.
         */
        void addLevel(IntVector level,
            double modularity)
        {
            for (int i = 0; i < level.size(); ++i)
            {
                communities.add(level.get(i));
            }
            levelIndex.add(communities.size());
            modularities.add(modularity);
        }

        /**
         * The number of levels in the hierarchy.
         *
         * @return the number of levels in the hierarchy
         */
        public int numLevels()
        {
            return levelIndex.size() - 1;
        }

        /**
         * Returns the modularity at the input level of the hierarchy.
         *
         * @param level The level to check on
         * @return the modularity at the input level of the hierarchy.
         * @throws IllegalArgumentException if the input level isn't in this
         * datastructure.
         */
        public double getModularity(int level)
        {
            if (level > numLevels() || level < 0)
            {
                throw new IllegalArgumentException(
                    "Unable to return modularity at level " + level
                    + " as levels are only defined in [0 ... " + numLevels()
                    + ")");
            }

            return modularities.get(level);
        }

        /**
         * Returns the number of communities at the input level of the
         * hierarchy.
         *
         * @param level The level to check on
         * @return the number of communities at the input level of the
         * hierarchy.
         * @throws IllegalArgumentException if the input level isn't in this
         * datastructure.
         */
        public int getNumCommunitiesAtLevel(int level)
        {
            if (level > numLevels() || level < 0)
            {
                throw new IllegalArgumentException(
                    "Unable to find number of communities at level " + level
                    + " as levels are only defined in [0 ... " + numLevels()
                    + ")");
            }

            // This works because the next level has an entry for each community in this level
            if (level < (numLevels() - 1))
            {
                return levelIndex.get(level + 2) - levelIndex.get(level + 1);
            }

            // This only works because the communities are numbered 0...max;
            int max = 0;
            for (int i = levelIndex.get(level); i < levelIndex.get(level + 1);
                ++i)
            {
                max = Math.max(communities.get(i), max);
            }

            return max + 1;
        }

        /**
         * Returns the community ID for the input node at the input level
         *
         * @param n The node to search for
         * @param level The level to search on
         * @return The community ID at that level
         * @throws IllegalArgumentException if the input node isn't in the
         * graph, or if the level isn't valid
         */
        public int getCommunityForNodeAtLevel(NodeNameType n,
            int level)
        {
            Integer nodeId = nodeMap.get(n);
            if (nodeId == null)
            {
                throw new IllegalArgumentException("Input node (" + n.toString()
                    + ") is not in this community graph.");
            }

            return getCommunityForNodeAtLevelById(nodeId, level);
        }

        public int getCommunityForNodeAtLevelById(int nodeId,
            int level)
        {
            if (level > numLevels() || level < 0)
            {
                throw new IllegalArgumentException(
                    "Unable to find communities at level " + level
                    + " as levels are only defined in [0 ... " + numLevels()
                    + ")");
            }

            // March up the hierarchy until you find the community at the right level
            // Level 0
            int commId = communities.get(nodeId);
            // Level 1..level
            for (int i = 1; i <= level; ++i)
            {
                commId = communities.get(levelIndex.get(i) + commId);
            }

            return commId;
        }

        @Override
        public int getPartition(NodeNameType n)
        {
            return getCommunityForNodeAtLevel(n, numLevels() - 1);
        }

        @Override
        public int getPartitionById(int nodeId)
        {
            return getCommunityForNodeAtLevelById(nodeId, numLevels() - 1);
        }

        @Override
        public int numPartitions()
        {
            return getNumCommunitiesAtLevel(numLevels() - 1);
        }

        @Override
        public Set<NodeNameType> getPartitionMembers(int i)
        {
            if (topCommunities == null)
            {
                topCommunities = new ArrayList<>(numPartitions());
                for (int j = 0; j < numPartitions(); ++j)
                {
                    topCommunities.add(new HashSet<>());
                }
                for (NodeNameType member : getAllMembers())
                {
                    topCommunities.get(getPartition(member)).add(member);
                }
            }

            return Collections.unmodifiableSet(topCommunities.get(i));
        }

        @Override
        public Set<NodeNameType> getAllMembers()
        {
            return Collections.unmodifiableSet(nodeMap.keySet());
        }

        @Override
        public Double getModularity()
        {
            return getModularity(numLevels() - 1);
        }

    }

}
