/*
 * File:                Permanence.java
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
import gov.sandia.cognition.graph.GraphMetrics;
import gov.sandia.cognition.collection.IntArrayList;
import java.util.HashSet;
import java.util.Set;

@PublicationReference(author = "Tanmoy Chakraborty, Sriram Srinivasan, "
    + "Niloy Ganguly, Animesh Mukherjee, and Sanjukta Bhowmick",
    title = "On the permanence of vertices in network communities", year
    = 2014, type = PublicationType.Conference)
/**
 * This class computes the permanence-maximizing (approximation) algorithm as
 * described in the cited paper. This algorithm forms communities in an
 * agglomerative fashion, attempting to maximize permanence.
 *
 * @author jdwendt
 * @param <NodeNameType> The node name type in the graph
 */
public class Permanence<NodeNameType>
{

    /**
     * The discovered partitioning. Before and during computation, not
     * guaranteed to be anything good.
     */
    private final MutableNodePartitioning<NodeNameType> partitions;

    /**
     * The to-be partitioned graph
     */
    private final DirectedNodeEdgeGraph<NodeNameType> graph;

    /**
     * The maximum number of passes to do the iterative algorithm
     */
    private final int maxNumPasses;

    /**
     * The minimum performance gain to be considered over each iteration
     */
    private final double minPermanenceGain;

    /**
     * The resulting graph permanence
     */
    private double graphPermanence;

    /**
     * Initialize the class with the input graph and parameters
     *
     * @param graph The graph to be partitioned
     * @param maxNumPasses The maximum number of passes to perform
     * @param minPermanenceGain The minimum permanence gain to be considered for
     * each iteration
     */
    public Permanence(DirectedNodeEdgeGraph<NodeNameType> graph,
        int maxNumPasses,
        double minPermanenceGain)
    {
        this.partitions = new MutableNodePartitioning<>(graph);
        this.graph = graph;
        this.maxNumPasses = maxNumPasses;
        this.minPermanenceGain = minPermanenceGain;
        this.graphPermanence = -2;
    }

    /**
     * Performs the actual permanence-maximization computation and returns the
     * resulting partitioning.
     *
     * @return The resulting partitioning
     */
    public NodePartitioning<NodeNameType> solveCommunities()
    {
        int n = graph.getNumNodes();
        int iter = 0;
        double sum = 0;
        double oldSum = -1;
        GraphMetrics<NodeNameType> metrics = new GraphMetrics<>(graph);
        Set<Integer> curNeighborCommunities = new HashSet<>();
        while ((Math.abs(sum - oldSum) > minPermanenceGain) && (iter
            < maxNumPasses))
        {
            ++iter;
            oldSum = sum;
            sum = 0;
            IntArrayList order = IntArrayList.range(n);
            order.randomizeOrder();
            for (int ii = 0; ii < n; ++ii)
            {
                int i = order.get(ii);
                // Skip degree 1 nodes -- they add them to their siblings' communities at the end
                if (metrics.degree(i) == 1)
                {
                    continue;
                }
                int curPart = partitions.getPartitionById(i);
                double iPerm
                    = CommunityMetrics.computeOneNodePermanenceById(metrics,
                        partitions, i, graph);
                if (iPerm == 1)
                {
                    sum += iPerm;
                    continue;
                }

                double neighborsCurPerm = 0;
                curNeighborCommunities.clear();
                Set<Integer> neighbors = metrics.neighborIds(i);
                for (int neighbor : neighbors)
                {
                    // Ignore degree 1 neighbors
                    if (metrics.degree(neighbor) > 1)
                    {
                        neighborsCurPerm
                            += CommunityMetrics.computeOneNodePermanenceById(
                                metrics, partitions, neighbor, graph);
                        int part = partitions.getPartitionById(neighbor);
                        if (part != curPart)
                        {
                            curNeighborCommunities.add(part);
                        }
                    }
                }

                for (int neigComm : curNeighborCommunities)
                {
                    partitions.moveNodeById(i, neigComm);
                    double newPerm
                        = CommunityMetrics.computeOneNodePermanenceById(
                            metrics, partitions, i, graph);
                    double neighborsNewPerm = 0;
                    for (int neighbor : neighbors)
                    {
                        // Ignore degree 1 neighbors
                        if (metrics.degree(neighbor) > 1)
                        {
                            neighborsNewPerm
                                += CommunityMetrics.computeOneNodePermanenceById(
                                    metrics, partitions, neighbor, graph);
                        }
                    }

                    // Their code seems to do only the net increase, even though
                    // their paper indicates they want both to increase.  I
                    // believe it's a bug in the paper.
//                    if ((iPerm < newPerm) && (neighborsCurPerm
//                        < neighborsNewPerm))
                    if ((iPerm + neighborsCurPerm)
                        < (newPerm + neighborsNewPerm))
                    {
                        iPerm = newPerm;
                        // TODO: Why don't they update neighborsCurPerm in the paper?
                        // Assuming it's a bug in the paper, this next line does that
                        neighborsCurPerm = neighborsNewPerm;
                        curPart = neigComm;
                        // Speed-up for if I've now discovered the best community
                        if (iPerm == 1)
                        {
                            break;
                        }
                    }
                    else
                    {
                        partitions.moveNodeById(i, curPart);
                    }
                }

                sum += iPerm;
            }

            // Add all degree 1 nodes
            for (int i = 0; i < n; ++i)
            {
                if (metrics.degree(i) != 1)
                {
                    continue;
                }
                // It only has one neighbor, and this gets it
                NodeNameType neighbor = metrics.neighbors(i).iterator().next();
                int neighborCom = partitions.getPartition(neighbor);
                partitions.moveNodeById(i, neighborCom);
            }
        }

        partitions.removeEmptyPartitions();

        // Recompute whole-graph permanence as it will have changed by adding the degree 1 nodes
        graphPermanence
            = CommunityMetrics.computeGraphPermanance(graph, metrics, partitions);

        return partitions;
    }

    /**
     * Returns the permanence discovered by running solveCommunities. If called
     * before solveCommunities, this throws an exception.
     *
     * @return the permanence discovered by running solveCommunities
     */
    public double permanence()
    {
        if (graphPermanence < -1)
        {
            throw new RuntimeException("Permanence not yet computed");
        }
        return graphPermanence;
    }

}
