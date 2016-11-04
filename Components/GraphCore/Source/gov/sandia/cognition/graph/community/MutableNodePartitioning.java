/*
 * File:                MutableNodePartitioning.java
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

import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.collection.IntArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This package-private class gives a default implementation to the
 * NodePartitioning interface that is mutable (in a package-private manner).
 *
 * @author jdwendt
 */
class MutableNodePartitioning<NodeNameType>
    implements NodePartitioning<NodeNameType>
{

    /**
     * Stores the partition assignment for each node (by id)
     */
    private final IntArrayList nodeToPartitionAssignments;

    /**
     * The partitions stored herein (nodes stored by id)
     */
    private List<Set<Integer>> partitions;

    /**
     * The graph that has been partitioned. Required so that id-to-name can be
     * performed when needed. Note that this is not a deep copy, so if you alter
     * the graph after passing it in here, there will be problems.
     */
    private final DirectedNodeEdgeGraph<NodeNameType> partitionedGraph;

    /**
     * Initializes this with each node assigned to its own separate community.
     * Note that this implicitly makes a copy of the node list (by assigning
     * each a partition) and stores a reference to the input graph. Therefore,
     * if you alter the graph after passing it in here, strange things will
     * occur.
     *
     * @param graph The graph that will be partitioned
     */
    MutableNodePartitioning(DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        partitionedGraph = graph;
        int n = graph.getNumNodes();
        partitions = new ArrayList<>(n);
        nodeToPartitionAssignments = new IntArrayList(n);

        for (int i = 0; i < n; ++i)
        {
            partitions.add(new HashSet<>());
            partitions.get(i).add(i);
            nodeToPartitionAssignments.add(i);
        }
    }

    /**
     * Removes empty partitions from the partitions list. Partitions empty by
     * having all nodes moved out of them. However, they are not immediately
     * removed as sometimes those nodes may move back in (depending on community
     * detection algorithm). Also, doing the removal in bulk is less
     * computationally expensive.
     *
     * Note that the remaining partitions will be reassigned to ids [0 ... k).
     */
    void removeEmptyPartitions()
    {
        List<Set<Integer>> updatedPartitions = new ArrayList<>();
        int[] partIdMap = new int[getNumPartitions()];
        int cnt = 0;
        for (Set<Integer> partition : partitions)
        {
            if (!partition.isEmpty())
            {
                partIdMap[cnt] = updatedPartitions.size();
                updatedPartitions.add(partition);
            }
            else
            {
                partIdMap[cnt] = -1;
            }
            ++cnt;
        }
        partitions = updatedPartitions;
        for (int i = 0; i < nodeToPartitionAssignments.size(); ++i)
        {
            int newPartitionId = partIdMap[nodeToPartitionAssignments.get(i)];
            if (newPartitionId == -1)
            {
                throw new RuntimeException("This should be impossible, but "
                    + "node " + i + "'s old partition assignment was found to "
                    + "be empty");
            }
            nodeToPartitionAssignments.set(i, newPartitionId);
        }
    }

    /**
     * Moves the node specified by the input id to a new partition and removes
     * it from its old partition. Note that if that node was (somehow) not in a
     * partition before, this will fail.
     *
     * @param nodeId The node to move
     * @param newPartitionId The new partition to move to. Must be in [0 ...
     * numPartitions).
     */
    void moveNodeById(int nodeId,
        int newPartitionId)
    {
        if (newPartitionId < 0 || newPartitionId >= getNumPartitions())
        {
            throw new ArrayIndexOutOfBoundsException("Input partition id ("
                + newPartitionId + ") outside of expected bounds[0, "
                + getNumPartitions() + ")");
        }
        if (nodeId < 0 || nodeId >= partitionedGraph.getNumNodes())
        {
            throw new ArrayIndexOutOfBoundsException("Input node id ("
                + nodeId + ") outside of expected bounds[0, "
                + partitionedGraph.getNumNodes() + ")");
        }
        partitions.get(nodeToPartitionAssignments.get(nodeId)).remove(nodeId);
        partitions.get(newPartitionId).add(nodeId);
        nodeToPartitionAssignments.set(nodeId, newPartitionId);
    }

    /**
     * Moves the input node to a new partition and removes it from its old
     * partition. Note that if that node was (somehow) not in a partition
     * before, this will fail.
     *
     * @param node The node to move
     * @param newPartitionId The new partition to move to. Must be in [0 ...
     * numPartitions).
     */
    void moveNode(NodeNameType node,
        int newPartitionId)
    {
        moveNodeById(partitionedGraph.getNodeId(node), newPartitionId);
    }

    /**
     * @see NodePartitioning#getNumPartitions()
     */
    @Override
    public int getNumPartitions()
    {
        return partitions.size();
    }

    /**
     * Private helper that translates the input node ids to node names and
     * returns the set of the names
     *
     * @param ids The node ids needing to be translated
     * @return The nodes that were translated from the ids
     */
    private Set<NodeNameType> translateIds(Set<Integer> ids)
    {
        Set<NodeNameType> ret = new HashSet<>(ids.size());
        for (int id : ids)
        {
            ret.add(partitionedGraph.getNode(id));
        }

        return ret;
    }

    /**
     * @see NodePartitioning#getPartitionMembers(int)
     */
    @Override
    public Set<NodeNameType> getPartitionMembers(int i)
    {
        return translateIds(partitions.get(i));
    }

    /**
     * This method that is specific to this implementation is faster than
     * getPartitionMembers(int), and so is used when speed is critical.
     *
     * @param i The partition index
     * @return The ids stored in that partition
     */
    public Set<Integer> getPartitionMemberIds(int i)
    {
        return partitions.get(i);
    }

    /**
     * @see NodePartitioning#getAllMembers()
     */
    @Override
    public Set<NodeNameType> getAllMembers()
    {
        return new HashSet<>(partitionedGraph.getNodes());
    }

    /**
     * @see NodePartitioning#getPartition(java.lang.Object)
     */
    @Override
    public int getPartition(NodeNameType node)
    {
        return nodeToPartitionAssignments.get(partitionedGraph.getNodeId(node));
    }

    /**
     * @see NodePartitioning#getPartitionById(int)
     */
    @Override
    public int getPartitionById(int nodeId)
    {
        return nodeToPartitionAssignments.get(nodeId);
    }

    /**
     * @see NodePartitioning#getModularity()
     */
    @Override
    public Double getModularity()
    {
        return null;
    }

}
