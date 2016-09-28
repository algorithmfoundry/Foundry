/*
 * File:                EnergyFunctionSolver.java
 * Authors:             Tu-Thach Quach
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

/**
 * Interface for any belief propagation implementation.
 *
 * @author tong
 * @param <LabelType> The type for node's possible labels. Only used as an
 * enumeration.
 */
public interface EnergyFunctionSolver<LabelType>
{

    /**
     * Initializes internal state and stores the energy function for future
     * solutions. The energy function can still change its unary and pairwise
     * method outputs after this is called, but the number of nodes, edges, etc.
     * must stay constant.
     *
     * @param f The energy function to initialize against
     */
    void init(EnergyFunction<LabelType> f);

    /**
     * Solves for the energy function passed in during initialize, e.g., compute
     * the beliefs.
     *
     * @return true if converged, false otherwise.
     */
    boolean solve();

    /**
     * Gets the belief for node i with the specified label. This can only be
     * called after solve().
     *
     * @param i The index of the node whose belief associated with the input
     * label is wanted
     * @param label The label for the node whose belief is desired
     * @return The belief for the input node/label pair
     */
    double getBelief(int i,
        int label);

}
