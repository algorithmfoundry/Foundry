/*
 * File:                EnergyFunction.java
 * Authors:             Jeremy Wendt
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

import gov.sandia.cognition.util.Pair;
import java.util.Collection;

/**
 * This interface defines the methods necessary for an object to be passed into
 * an iterative message-passing data inferencing solver (such as belief
 * propagation).
 *
 * @author jdwendt
 *
 * @param <LabelType> This type allows for the labels to be any object type or
 * enum
 */
public interface EnergyFunction<LabelType>
{

    /**
     * Returns the set of labels that this message passer supports
     *
     * @param nodeId The node whose labels is wanted
     * @return the set of labels that this message passer supports
     */
    public Collection<LabelType> getPossibleLabels(int nodeId);

    /**
     * Returns the number of paths that messages should be passed down. In
     * conjunction with the {@link #getEdge(int)} method, all paths can be
     * retrieved.
     *
     * @return the number of paths that messages can be passed through
     */
    public int numEdges();

    /**
     * Returns the number of nodes in the energy function.
     *
     * @return the number of nodes in the energy function
     */
    public int numNodes();

    /**
     * Returns the indices for the two endpoints in the specified path. The path
     * ID should be 0-indexed and all paths from 0 through ( {@link #numEdges()}
     * - 1) should return a non-null value. All path IDs outside that range
     * should throw an Exception.
     *
     * @param i The path ID (should be in [0 ... {@link #numEdges()}) ).
     * @return The two endpoints' IDs (to be used for {@link #getUnaryCost(int)}
     * and {@link #getPairwisePotential(int, int, Object, Object)})
     */
    public Pair<Integer, Integer> getEdge(int i);

    /**
     * Returns the unary cost for node i having the input label. i must be a
     * valid node id returned from {@link #getEdge(int)}. If not, an exception
     * should be thrown.
     *
     * @param i The index of the node whose unary cost should be returned
     * @param label The label that could be applied to the node
     * @return The unary cost of node i being assigned the input label
     */
    public double getUnaryPotential(int i,
        LabelType label);

    /**
     * Returns the pairwise cost of node i being assigned label_i and node j
     * being assigned label_j. NOTE: i and j must be the two endpoints returned
     * by {@link #getEdge(int)} in the ordere specified by the returned pair (i
     * = getFirst, j = getSecond). If not, an exception should be thrown.
     *
     * @param edgeId The id for the edge being requested
     * @param ilabel The label being (possibly) assigned to node i
     * @param jlabel The label being (possibly) assigned to node j
     * @return the pairwise cost of node i being assigned label_i and node j
     * being assigned label_j
     */
    public double getPairwisePotential(int edgeId,
        LabelType ilabel,
        LabelType jlabel);

    /**
     * This is the -log(getUnaryPotential(.)).
     *
     * @param i
     * @param label
     * @return
     */
    public double getUnaryCost(int i,
        LabelType label);

    /**
     * This is the -log(getPairwisePotential(.)).
     *
     * @param edgeId
     * @param ilabel
     * @param jlabel
     * @return
     */
    public double getPairwiseCost(int edgeId,
        LabelType ilabel,
        LabelType jlabel);

}
