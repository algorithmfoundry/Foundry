/*
 * File:                EdgeMergingEnergyFunction.java
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

package gov.sandia.cognition.graph.inference;

import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Our implementation of belief propagation requires that there be at most one
 * edge between any pair of nodes. This class wraps any graph (with any number
 * of edges between pairs of nodes) and merges any edges between any two nodes
 * into one BP-visible edge while preserving the correct results. This wrapper
 * makes the edges undirected (changing the direction stored in this class from
 * that stored in the parent class), so don't use with
 * SumProductPairwiseBayesNet.
 *
 * @author jdwendt
 * @param <LabelType>
 * @param <NodeNameType>
 */
public class EdgeMergingEnergyFunction<LabelType, NodeNameType>
    implements NodeNameAwareEnergyFunction<LabelType, NodeNameType>
{

    /**
     * A helper class for storing the (possibly) multiple parallel edges between
     * two nodes in the input graph.
     *
     * @author jdwendt
     *
     */
    private static class UniqueEdge
    {

        /**
         * The edge's lower vertex index (note: This will change the direction
         * of an edge to store the lower index here)
         */
        private final int idxi;

        /**
         * The edge's higher vertex index (note: This will change the direction
         * of an edge to store the higher index here)
         */
        private final int idxj;

        /**
         * Initializes this edge with appropriate values
         *
         * @param src The edge's source vertex index
         * @param dst The edge's destination vertex index
         */
        public UniqueEdge(int src,
            int dst)
        {
            // Note that as BP runs messages in both directions across an edge,
            // this can ignore node order on the edge
            if (src < dst)
            {
                this.idxi = src;
                this.idxj = dst;
            }
            else
            {
                this.idxi = dst;
                this.idxj = src;
            }
        }

        /**
         * This method returns true if the input src/dst indices were flipped
         * for storage in this class. This is necessary information for belief
         * propagation as flipping the indices requires flipping the potential
         * matrix values. This class can't store that internally as it would
         * affect the equals and hash methods and those need to find edges equal
         * if they have the same indices regardless of whether they were
         * flipped.
         *
         * NOTE: This method will return false if you hand it a src and dst that
         * don't match the internal indices at all. While it may be more robust
         * to throw an exception, this is a private, internal class that should
         * only be used herein in specific ways.
         *
         * @param src The original source index for the edge
         * @param dst The original destination index for the edge
         * @return true if the edge indices were flipped internal to this class,
         * else false.
         */
        public boolean wasFlipped(int src,
            int dst)
        {
            return (src == this.idxj) && (dst == this.idxi);
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof UniqueEdge))
            {
                return false;
            }
            UniqueEdge e = (UniqueEdge) o;
            if (idxi != e.idxi)
            {
                return false;
            }
            else if (idxj != e.idxj)
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int ret = 7;
            ret += ret * 5 + idxi;
            ret += ret * 5 + idxj;

            return ret;
        }

    };

    /**
     * Internal helper class that stores values for graph edges of the original
     * graph. These values are used when computing things like the pairwise
     * potential.
     */
    private static class EdgeIndex
    {

        /**
         * The index of this edge in the underlying graph
         */
        public int internalIdx;

        /**
         * True if the edge was flipped to match default edge-node ordering
         */
        public boolean wasFlipped;

        /**
         * Stores the specified values in this data type object
         *
         * @param internalIdx The index of this edge in the underlying graph
         * @param wasFlipped True if the edge was flipped to match default
         * edge-node ordering
         */
        public EdgeIndex(int internalIdx,
            boolean wasFlipped)
        {
            this.internalIdx = internalIdx;
            this.wasFlipped = wasFlipped;
        }

    }

    /**
     * Private helper class that stores the map from canonically ordered edges
     * to the (possibly multiple) edges. That is, this is a one-to-many map
     * where the outside world sees each edge only once, but the graph being
     * wrapped may have multiple edges between any pair of nodes.
     */
    private static class UniqueEdgeMap
    {

        /**
         * This is the editable version of the data. Once initialized fully,
         * call convert to switch to the faster-to-run-through-and-look-up list.
         */
        private Map<UniqueEdge, List<EdgeIndex>> editableEdgeMap;

        /**
         * This is the faster, read-only version of the edges
         */
        private List<Pair<UniqueEdge, List<EdgeIndex>>> listEdgeMap;

        /**
         * Initialize an empty edge map
         */
        public UniqueEdgeMap()
        {
            editableEdgeMap = new HashMap<>();
            listEdgeMap = null;
        }

        /**
         * Add edges to the map. This may or may not add new visible edges
         * (depending on if the input edge is a repeat of an already externally
         * visible edge). This method should not be called after convert is
         * called.
         *
         * @param src The source node of the edge
         * @param dst The destination node of the edge
         * @param wrappedIdx The edge's index in the wrapped graph
         * @throws RuntimeException if this method called after convert called
         */
        public void addEdge(int src,
            int dst,
            int wrappedIdx)
        {
            if (editableEdgeMap == null)
            {
                throw new RuntimeException(
                    "Can't addEdges once converted to list");
            }
            UniqueEdge e = new UniqueEdge(src, dst);
            if (!editableEdgeMap.containsKey(e))
            {
                editableEdgeMap.put(e, new ArrayList<>());
            }
            editableEdgeMap.get(e).add(new EdgeIndex(wrappedIdx, e.wasFlipped(
                src, dst)));
        }

        /**
         * Converts from the editable edge map to the dense-memory edge list
         */
        public void convert()
        {
            listEdgeMap = new ArrayList<>(editableEdgeMap.size());
            for (Map.Entry<UniqueEdge, List<EdgeIndex>> e
                : editableEdgeMap.entrySet())
            {
                listEdgeMap.add(new DefaultPair<>(e.getKey(), e.getValue()));
            }
            editableEdgeMap.clear();
            editableEdgeMap = null;
        }

        /**
         * Returns the number of externally visible edges stored herein
         *
         * @return the number of externally visible edges stored herein
         */
        public int size()
        {
            if (editableEdgeMap != null)
            {
                return editableEdgeMap.size();
            }
            else
            {
                return listEdgeMap.size();
            }
        }

        /**
         * Returns the externally visible edge stored at index i (may contain
         * multiple wrapped-graph edges).
         *
         * @param i The edge index
         * @return the externally visible edge stored at index i
         */
        public Pair<UniqueEdge, List<EdgeIndex>> getEdge(int i)
        {
            if (listEdgeMap == null)
            {
                throw new RuntimeException(
                    "Edge map not finalized before this call");
            }
            return listEdgeMap.get(i);
        }

        /**
         * Returns the source/dest pair for the edge stored at index i
         *
         * @param i The edge index
         * @return the source/dest pair for the edge stored at index i
         */
        public Pair<Integer, Integer> getEdgePair(int i)
        {
            UniqueEdge e = getEdge(i).getFirst();
            return new DefaultPair<>(e.idxi, e.idxj);
        }

    };

    /**
     * The energy function being wrapped by this
     */
    private final NodeNameAwareEnergyFunction<LabelType, NodeNameType> wrapped;

    /**
     * The externally visible edges generated by removing repeated edges in
     * wrapped
     */
    private final UniqueEdgeMap edgeMap;

    /**
     * Initializes this edge merging energy function
     *
     * @param wrapMe The energy function to wrap
     */
    public EdgeMergingEnergyFunction(
        NodeNameAwareEnergyFunction<LabelType, NodeNameType> wrapMe)
    {
        this.wrapped = wrapMe;
        this.edgeMap = new UniqueEdgeMap();
        for (int i = 0; i < wrapMe.numEdges(); ++i)
        {
            Pair<Integer, Integer> edge = wrapMe.getEdge(i);
            edgeMap.addEdge(edge.getFirst(), edge.getSecond(), i);
        }
        edgeMap.convert();
    }

    /**
     * @see NodeNameAwareEnergyFunction#setLabel(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void setLabel(NodeNameType node,
        LabelType label)
    {
        wrapped.setLabel(node, label);
    }

    /**
     * @see NodeNameAwareEnergyFunction#getBeliefs(java.lang.Object,
     * gov.sandia.cognition.graph.inference.EnergyFunctionSolver)
     */
    @Override
    public Map<LabelType, Double> getBeliefs(NodeNameType node,
        EnergyFunctionSolver<LabelType> bp)
    {
        return wrapped.getBeliefs(node, bp);
    }

    /**
     * @see NodeNameAwareEnergyFunction#getPossibleLabels(int)
     */
    @Override
    public Collection<LabelType> getPossibleLabels(int nodeId)
    {
        return wrapped.getPossibleLabels(nodeId);
    }

    /**
     * @see NodeNameAwareEnergyFunction#numEdges()
     */
    @Override
    public int numEdges()
    {
        return edgeMap.size();
    }

    /**
     * @see NodeNameAwareEnergyFunction#numNodes()
     */
    @Override
    public int numNodes()
    {
        return wrapped.numNodes();
    }

    /**
     * @see NodeNameAwareEnergyFunction#getEdge(int)
     */
    @Override
    public Pair<Integer, Integer> getEdge(int i)
    {
        return edgeMap.getEdgePair(i);
    }

    /**
     * @see NodeNameAwareEnergyFunction#getUnaryPotential(int, java.lang.Object)
     */
    @Override
    public double getUnaryPotential(int i,
        LabelType label)
    {
        return wrapped.getUnaryPotential(i, label);
    }

    /**
     * @see NodeNameAwareEnergyFunction#getPairwisePotential(int,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public double getPairwisePotential(int edgeId,
        LabelType ilabel,
        LabelType jlabel)
    {
        Pair<UniqueEdge, List<EdgeIndex>> edge = edgeMap.getEdge(edgeId);
        double ret = 1.0;
        for (EdgeIndex ei : edge.getSecond())
        {
            if (ei.wasFlipped)
            {
                ret *= wrapped.getPairwisePotential(ei.internalIdx, jlabel,
                    ilabel);
            }
            else
            {
                ret *= wrapped.getPairwisePotential(ei.internalIdx, ilabel,
                    jlabel);
            }
        }
        return ret;
    }

    /**
     * @see NodeNameAwareEnergyFunction#getUnaryCost(int, java.lang.Object)
     */
    @Override
    public double getUnaryCost(int i,
        LabelType label)
    {
        return wrapped.getUnaryCost(i, label);
    }

    /**
     * @see NodeNameAwareEnergyFunction#getPairwiseCost(int, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public double getPairwiseCost(int edgeId,
        LabelType ilabel,
        LabelType jlabel)
    {
        return -Math.log(getPairwisePotential(edgeId, ilabel, jlabel));
    }

}
