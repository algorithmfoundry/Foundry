/*
 * File:                NodeNameAwareEnergyFunction.java
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

import java.util.Map;

/**
 * Helpful interface that adds per-node labeling and getting results from energy
 * functions using the graph's node label type.
 *
 * @author jdwendt
 *
 * @param <LabelType>
 * @param <NodeNameType>
 */
public interface NodeNameAwareEnergyFunction<LabelType, NodeNameType>
    extends EnergyFunction<LabelType>
{
    /**
     * Set the label for the input node
     * 
     * @param node
     * @param label
     */
    public void setLabel(NodeNameType node,
        LabelType label);

    /**
     * Get the probabilities for each label for the input node as produced by
     * the input belief propagation run.
     * 
     * @param node
     * @param bp
     * @return
     */
    public Map<LabelType, Double> getBeliefs(NodeNameType node,
        EnergyFunctionSolver<LabelType> bp);

}
