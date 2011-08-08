/*
 * File:                WeightedTargetEstimatePair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.Weighted;

/**
 * Extends {@code TargetEstimatePair} with an additional weight field.
 *
 * @param <TargetType>
 *      The type of the target (ground truth).
 * @param <EstimateType>
 *      The type of the estimate. Typically this is the same as TargetType,
 *      but it does not have to be.
 * @author  Justin Basilico
 * @since   3.0
 */
public class WeightedTargetEstimatePair<TargetType, EstimateType>
    extends TargetEstimatePair<TargetType, EstimateType>
    implements Weighted
{

    /** The weight. */
    private double weight;

    /**
     * Creates a new {@code WeightedTargetEstimatePair} with nulls for the
     * target and estimate and 1.0 for the weight.
     */
    public WeightedTargetEstimatePair()
    {
        this(null, null, 1.0);
    }

    /**
     * Creates a new {@code WeightedTargetEstimatePair} with the given target,
     * estimate, and weight.
     *
     * @param   target
     *      The target.
     * @param   estimate
     *      The estimate.
     * @param   weight
     *      The eight.
     */
    public WeightedTargetEstimatePair(
        final TargetType target,
        final EstimateType estimate,
        double weight)
    {
        super(target, estimate);

        this.setWeight(weight);
    }

    /**
     * Creates a new {@code WeightedTargetEstimatePair} as a shallow copy of
     * the given other object.
     *
     * @param   other
     *      The other object to get the target, estimate, and weight from.
     */
    public WeightedTargetEstimatePair(
        final WeightedTargetEstimatePair<? extends TargetType, ? extends EstimateType> other)
    {
        this(other.getTarget(), other.getEstimate(), other.getWeight());
    }
    
    public double getWeight()
    {
        return this.weight;
    }

    /**
     * Sets the weight.
     *
     * @param   weight
     *      The weight for the pair.
     */
    public void setWeight(
        final double weight)
    {
        this.weight = weight;
    }


}
