/*
 * File:                NodePartitioning.java
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

import java.util.Set;

/**
 * Interface for all graph partitioning classes
 *
 * @author jdwendt
 * @param <NodeNameType> The type used to represent nodes
 */
public interface NodePartitioning<NodeNameType>
{

    /**
     * Return the number of partitions in this partitioning
     *
     * @return the number of partitions in this partitioning
     */
    public int getNumPartitions();

    /**
     * Returns the members of the ith partition
     *
     * @param i the 0-based index into the partitioning
     * @return the members of the ith partition
     */
    public Set<NodeNameType> getPartitionMembers(int i);

    /**
     * Returns all nodes stored within this partitioning
     *
     * @return all the nodes stored within this partitioning
     */
    public Set<NodeNameType> getAllMembers();

    /**
     * Returns the partition id for the input node
     *
     * @param node The node whose partition is desired
     * @return the partition id for the input node
     */
    public int getPartition(NodeNameType node);

    /**
     * Returns the partition id for the input node
     *
     * @param nodeId The node's ID (from the graph) whose partition is desired
     * @return the partition id for the input node
     */
    public int getPartitionById(int nodeId);

    /**
     * Returns the modularity for this community specification. If not
     * supported, return null
     *
     * @return the modularity for this community specification. If not
     * supported, return null
     */
    public Double getModularity();

    /**
     * A helper pretty print method so that all partitionings can have the same
     * look-and-feel.
     *
     * @param <NodeNameType> The type for each node
     * @param part The partition to print
     */
    public static <NodeNameType> void printPartitioning(
        NodePartitioning<NodeNameType> part)
    {
        for (int i = 0; i < part.getNumPartitions(); ++i)
        {
            System.out.println("Partition " + i);
            for (NodeNameType node : part.getPartitionMembers(i))
            {
                System.out.println("  " + node);
            }
        }
    }

}
