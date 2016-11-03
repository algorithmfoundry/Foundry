/*
 * File:                CommunityMetrics.java
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
import gov.sandia.cognition.graph.GraphMetrics;
import gov.sandia.cognition.util.Pair;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class stores several static methods for computing metrics specific to a
 * graph and a set of communities. Unlike the GraphMetrics class, this does not
 * preserve values for the community structure nor the graph internally -- each
 * method is a separate call.
 *
 * @author jdwendt
 */
public class CommunityMetrics
{

    /**
     * Computes the modularity of the input graph into the input set of
     * communities. NOTE: Modularity is intended for exclusive community
     * detection (every node is in exactly one community), but this code does
     * not check that the communities are constituted correctly. Therefore, if
     * you insert garbage, be prepared for bad results!
     *
     * @param <NodeNameType>
     * @param graph The graph that has been partitioned
     * @param communities The partitions created on the input graph
     * @return The modularity score
     */
    public static <NodeNameType> double computeModularity(
        DirectedNodeEdgeGraph<NodeNameType> graph,
        Set<Set<NodeNameType>> communities)
    {
        return computeModularity(communities, new GraphMetrics<>(graph));
    }

    /**
     * Computes the modularity of the graph (whose metrics were passed in) into
     * the input set of communities. NOTE: Modularity is intended for exclusive
     * community detection (every node is in exactly one community), but this
     * code does not check that the communities are constituted correctly.
     * Therefore, if you insert garbage, be prepared for bad results!
     *
     * @param <NodeNameType>
     * @param communities The partitions created on the input graph
     * @return The modularity score
     * @param graphMetrics The complete set of metrics for the partitioned graph
     */
    public static <NodeNameType> double computeModularity(
        Set<Set<NodeNameType>> communities,
        GraphMetrics<NodeNameType> graphMetrics)
    {
        double modularity = 0.0;
        for (Set<NodeNameType> community : communities)
        {
            modularity += modularityPartForCommunity(community, graphMetrics);
        }

        return modularity / (2.0 * graphMetrics.numEdges());
    }

    /**
     * Computes the modularity of the input graph into the input set of
     * communities. NOTE: Modularity is intended for exclusive community
     * detection (every node is in exactly one community), but this code does
     * not check that the communities are constituted correctly. Therefore, if
     * you insert garbage, be prepared for bad results!
     *
     * @param <NodeNameType>
     * @param graph The graph that has been partitioned
     * @param communities The partitions created on the input graph
     * @return The modularity score
     */
    public static <NodeNameType> double computeModularity(
        DirectedNodeEdgeGraph<NodeNameType> graph,
        NodePartitioning<NodeNameType> communities)
    {
        return computeModularity(communities, new GraphMetrics<>(graph));
    }

    @PublicationReference(author = "Wikipedia", title = "Modularity (networks)",
        type = PublicationType.WebPage, year = 2016, url
        = "https://en.wikipedia.org/wiki/Modularity_(networks)")
    /**
     * Computes the modularity of the graph (whose metrics are passed in) into
     * the input set of communities. NOTE: Modularity is intended for exclusive
     * community detection (every node is in exactly one community), but this
     * code does not check that the communities are constituted correctly.
     * Therefore, if you insert garbage, be prepared for bad results!
     *
     * @param <NodeNameType>
     * @param communities The partitions created on the input graph
     * @return The modularity score
     * @param graphMetrics The complete set of metrics for the partitioned graph
     */
    public static <NodeNameType> double computeModularity(
        NodePartitioning<NodeNameType> communities,
        GraphMetrics<NodeNameType> graphMetrics)
    {
        Double internalMod = communities.getModularity();
        if (internalMod != null)
        {
            return internalMod;
        }
        double modularity = 0.0;
        for (int i = 0; i < communities.getNumPartitions(); ++i)
        {
            modularity += modularityPartForCommunity(
                communities.getPartitionMembers(i), graphMetrics);
        }

        return modularity / (2.0 * graphMetrics.numEdges());
    }

    /**
     * Helper that performs the meat of the modularity computation. This allows
     * the above methods to all work given different input types but need not
     * reimplement too much work.
     *
     * @param <NodeNameType>
     * @param community The community to compute part of the modularity score
     * @param graphMetrics The metrics for the graph that contains the input
     * community
     * @return the non-normalized modularity for this community in part of the
     * graph
     */
    private static <NodeNameType> double modularityPartForCommunity(
        Set<NodeNameType> community,
        GraphMetrics<NodeNameType> graphMetrics)
    {
        double oneOverTwoM = 1.0 / (2.0 * graphMetrics.numEdges());
        double modularityPart = 0.0;
        for (NodeNameType nodei : community)
        {
            Set<NodeNameType> neighbors = graphMetrics.neighbors(nodei);
            int degi = graphMetrics.degree(nodei);
            for (NodeNameType nodej : community)
            {
                if (neighbors.contains(nodej))
                {
                    modularityPart += 1;
                }
                modularityPart -= (graphMetrics.degree(nodej) * degi)
                    * oneOverTwoM;
            }
        }

        return modularityPart;
    }

    @PublicationReference(author = "Wikipedia", title = "Conductance (graph)",
        type = PublicationType.WebPage, year = 2016, url
        = "https://en.wikipedia.org/wiki/Conductance_(graph)")
    /**
     * Helper that computes the conductance resulting from the cut between the
     * input community and the rest of the graph. Note this supports weighted or
     * unweighted graphs.
     *
     * @param <NodeNameType>
     * @param graph The graph that is being cut by separating the input
     * community
     * @param community The set of nodes that are proposed to be their own
     * community
     * @return the conductance resulting by the cut of this community from the
     * graph
     */
    public static <NodeNameType> double computeConductance(
        DirectedNodeEdgeGraph<NodeNameType> graph,
        Set<NodeNameType> community)
    {
        double edgesCut = 0;
        double edgesInside = 0;
        double edgesOutside = 0;
        boolean isWeighted = (graph instanceof DirectedWeightedNodeEdgeGraph);
        for (int i = 0; i < graph.getNumEdges(); ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            double w = 1.0;
            if (isWeighted)
            {
                w = ((DirectedWeightedNodeEdgeGraph) graph).getEdgeWeight(i);
            }
            boolean inside_i
                = community.contains(graph.getNode(edge.getFirst()));
            boolean inside_j = community.contains(
                graph.getNode(edge.getSecond()));
            if (inside_i != inside_j)
            {
                edgesCut += w;
            }
            if (inside_i)
            {
                edgesInside += w;
            }
            else
            {
                edgesOutside += w;
            }
            if (inside_j)
            {
                edgesInside += w;
            }
            else
            {
                edgesOutside += w;
            }
        }
        return (edgesCut) / Math.min(edgesInside, edgesOutside);
    }

    /**
     * This computes the clustering coefficient for a subgraph of a graph. The
     * permanence paper makes it seem they will use this, but it turns out what
     * they call internal clustering coefficient is actually internal edge
     * density. Method preserved for in case its needed in the future.
     *
     * @param <NodeNameType>
     * @param metrics The graph metrics object to be used
     * @param nodesInSubgraph The set of nodes that comprise the subgraph
     * @return The clustering coefficient for the graph created by the input
     * subgraph
     */
    private static <NodeNameType> double getSubgraphClusteringCoefficient(
        GraphMetrics<NodeNameType> metrics,
        Set<NodeNameType> nodesInSubgraph)
    {
        int totalNumInternalWedges = 0;
        int totalNumInternalTriangles = 0;
        for (NodeNameType node : nodesInSubgraph)
        {
            int numInternalNeighbors = 0;
            for (NodeNameType neighbor : metrics.neighbors(node))
            {
                numInternalNeighbors += (nodesInSubgraph.contains(neighbor)) ? 1
                    : 0;
            }
            totalNumInternalWedges += numInternalNeighbors
                * (numInternalNeighbors - 1);
            int numInternalTris = 0;
            for (Pair<NodeNameType, NodeNameType> others
                : metrics.getNodeTriangleEndpoints(node))
            {
                numInternalTris += (nodesInSubgraph.contains(others.getFirst())
                    && nodesInSubgraph.contains(others.getSecond())) ? 1 : 0;
            }
            totalNumInternalTriangles += numInternalTris;
        }
        if (totalNumInternalWedges <= 0)
        {
            return 0;
        }
        return ((double) totalNumInternalTriangles)
            / ((double) totalNumInternalWedges);
    }

    /**
     * Returns the edge density of the neighbors of node internal to subgraph.
     *
     * @param <NodeNameType>
     * @param metrics The graph metrics object to be used herein
     * @param nodesInSubgraph The subset of nodes that are within the input
     * community
     * @param node The node (must be within the community) whose edge density is
     * desired
     * @return The ratio of the existing edges and the total number of possible
     * edges among the internal neighbors of node.
     */
    private static <NodeNameType> double getInternalEdgeDensity(
        GraphMetrics<NodeNameType> metrics,
        Set<Integer> nodesInSubgraph,
        Integer nodeId)
    {
        int numInternalNeighbors = 0;
        // The following is identical to this, but faster
        // Iterate through the smaller and do O(1) contains on larger
        // for (NodeNameType neighbor : metrics.neighbors(node))
        // {
        //     numInternalNeighbors += (nodesInSubgraph.contains(neighbor)) ? 1 : 0;
        // }
        Set<Integer> smaller = metrics.neighborIds(nodeId);
        Set<Integer> larger = nodesInSubgraph;
        if (smaller.size() > larger.size())
        {
            Set<Integer> tmp = smaller;
            smaller = larger;
            larger = tmp;
        }
        for (int s : smaller)
        {
            numInternalNeighbors += larger.contains(s) ? 1 : 0;
        }

        int numInternalTriangles = 0;
        for (Pair<Integer, Integer> others
            : metrics.getNodeTriangleEndpointIds(nodeId))
        {
            numInternalTriangles += (nodesInSubgraph.contains(others.getFirst())
                && nodesInSubgraph.contains(others.getSecond())) ? 1 : 0;
        }

        // If there are no internal triangles possible, this returns 0
        if (numInternalNeighbors < 2)
        {
            return 0;
        }
        return numInternalTriangles / (((numInternalNeighbors - 1)
            * numInternalNeighbors) / 2.0);
    }

    /**
     * Temporary variable used by getInternalToMaxExternalRatio. As that method
     * is called many times when maximizing permanence, initializing an array
     * over-and-over was getting expensive.
     */
    private static int[] connections = null;

    /**
     * Returns the ratio of internal neighbors of node to the maximum number
     * node's neighbors in any other community
     *
     * @param <NodeNameType>
     * @param metrics The graph metrics to be used herein
     * @param partitions The partitioning of the graph
     * @param node The node whose ratio is desired
     * @return the ratio of internal neighbors to the maximum number of
     * neighbors in any other one community
     */
    private static <NodeNameType> double getInternalToMaxExternalRatio(
        GraphMetrics<NodeNameType> metrics,
        NodePartitioning<NodeNameType> partitions,
        int nodeId)
    {
        int nodesPartition = partitions.getPartitionById(nodeId);
        int numPartitions = partitions.getNumPartitions();
        if (connections == null || connections.length < numPartitions)
        {
            connections = new int[numPartitions];
        }
        Arrays.fill(connections, 0);
        int maxExternal = 1;
        for (int neighbor : metrics.neighborIds(nodeId))
        {
            int partId = partitions.getPartitionById(neighbor);
            ++connections[partId];
            if (partId != nodesPartition)
            {
                maxExternal = Math.max(maxExternal, connections[partId]);
            }
        }

        return ((double) connections[nodesPartition]) / ((double) maxExternal);
    }

    @PublicationReference(author = "Tanmoy Chakraborty, Sriram Srinivasan, "
        + "Niloy Ganguly, Animesh Mukherjee, and Sanjukta Bhowmick",
        title = "On the permanence of vertices in network communities", year
        = 2014, type = PublicationType.Conference)
    /**
     * Computes the permanence for one node in the graph.
     *
     * @param <NodeNameType>
     * @param metrics The graph metrics object to be used herein
     * @param partitions The partitioning on the graph
     * @param node The node whose permanence is requested
     * @param graph The graph being partitioned
     * @return the permanence for the input node
     */
    public static <NodeNameType> double computeOneNodePermanence(
        GraphMetrics<NodeNameType> metrics,
        NodePartitioning<NodeNameType> partitions,
        NodeNameType node,
        DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        return computeOneNodePermanenceById(metrics, partitions,
            graph.getNodeId(node), graph);
    }

    /**
     * Computes the permanence for one node in the graph.
     *
     * @param <NodeNameType>
     * @param metrics The graph metrics object to be used herein
     * @param partitions The partitioning on the graph
     * @param nodeId The node whose permanence is requested
     * @param graph The graph being partitioned
     * @return the permanence for the input node
     */
    public static <NodeNameType> double computeOneNodePermanenceById(
        GraphMetrics<NodeNameType> metrics,
        NodePartitioning<NodeNameType> partitions,
        int nodeId,
        DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        int degree = Math.max(1, metrics.degree(nodeId));
        return (getInternalToMaxExternalRatio(metrics, partitions, nodeId)
            / degree) - (1 - getInternalEdgeDensity(metrics,
                getPartitionIds(partitions, nodeId, graph), nodeId));
    }

    /**
     * Helper that gets the ids of the nodes within the input node's
     * partitioning. If an instance of MutableNodePartitioning is passed in, the
     * faster method that implementation provides is used.
     *
     * @param <NodeNameType>
     * @param partitioning The partitioning to use
     * @param node The whose partition members is wanted
     * @param graph The graph that's partitioned
     * @return the ids of the nodes in the same partition as the input node
     */
    private static <NodeNameType> Set<Integer> getPartitionIds(
        NodePartitioning<NodeNameType> partitioning,
        int nodeId,
        DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        int partition = partitioning.getPartitionById(nodeId);
        if (partitioning instanceof MutableNodePartitioning)
        {
            Set<Integer> tmp
                = ((MutableNodePartitioning<NodeNameType>) partitioning).getPartitionMemberIds(
                    partition);
            return tmp;
        }
        Set<NodeNameType> nodes = partitioning.getPartitionMembers(partition);
        Set<Integer> ret = new HashSet<>();
        for (NodeNameType n : nodes)
        {
            ret.add(graph.getNodeId(n));
        }

        return ret;
    }

    /**
     * Computes the average permanence for the partitioning of the entire graph
     *
     * @param <NodeNameType>
     * @param graph The graph to partition
     * @param metrics The metrics on that graph
     * @param partitions The partitioning of the graph
     * @return the average permanence for the partitioning
     */
    public static <NodeNameType> double computeGraphPermanance(
        DirectedNodeEdgeGraph<NodeNameType> graph,
        GraphMetrics<NodeNameType> metrics,
        NodePartitioning<NodeNameType> partitions)
    {
        double sum = 0;
        for (int i = 0; i < graph.getNumNodes(); ++i)
        {
            sum += computeOneNodePermanenceById(metrics, partitions, i, graph);
        }

        return sum / metrics.numNodes();
    }

}
