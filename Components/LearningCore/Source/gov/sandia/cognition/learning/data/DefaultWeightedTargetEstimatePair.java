/*
 * File:                DefaultWeightedTargetEstimatePair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 22, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.data;

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
public class DefaultWeightedTargetEstimatePair<TargetType, EstimateType>
    extends DefaultTargetEstimatePair<TargetType, EstimateType>
    implements WeightedTargetEstimatePair<TargetType, EstimateType>
{

    /** The default weight is {@value}. */
    public static final double DEFAULT_WEIGHT = 1.0;

    /** The weight. */
    protected double weight;

    /**
     * Creates a new {@code WeightedTargetEstimatePair} with nulls for the
     * target and estimate and 1.0 for the weight.
     */
    public DefaultWeightedTargetEstimatePair()
    {
        this(null, null, DEFAULT_WEIGHT);
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
    public DefaultWeightedTargetEstimatePair(
        final TargetType target,
        final EstimateType estimate,
        final double weight)
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
    public DefaultWeightedTargetEstimatePair(
        final DefaultWeightedTargetEstimatePair<? extends TargetType, ? extends EstimateType> other)
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


    /**
     * Convenience method for creating a new {@code DefaultWeightedTargetEstimatePair}.
     *
     * @param   <TargetType>
     *      The target (ground-truth) type.
     * @param   <EstimateType>
     *      The estimate (prediction) type. Usually the same as TargetType.
     * @return
     *      A new, empty pair with a default weight of 1.0.
     */
    public static <TargetType, EstimateType> DefaultWeightedTargetEstimatePair<TargetType, EstimateType> create()
    {
        return create(null, null);
    }
    
    /**
     * Convenience method for creating a new {@code DefaultWeightedTargetEstimatePair}.
     *
     * @param   <TargetType>
     *      The target (ground-truth) type.
     * @param   <EstimateType>
     *      The estimate (prediction) type. Usually the same as TargetType.
     * @param   target
     *      The target (ground-truth) value.
     * @param   estimate
     *      The estimate (prediction) value.
     * @return
     *      A new target-estimate pair with a default weight of 1.0.
     */
    public static <TargetType, EstimateType> DefaultWeightedTargetEstimatePair<TargetType, EstimateType> create(
        final TargetType target,
        final EstimateType estimate)
    {
        return create(target, estimate, DEFAULT_WEIGHT);
    }

    /**
     * Convenience method for creating a new {@code DefaultWeightedTargetEstimatePair}.
     *
     * @param   <TargetType>
     *      The target (ground-truth) type.
     * @param   <EstimateType>
     *      The estimate (prediction) type. Usually the same as TargetType.
     * @param   target
     *      The target (ground-truth) value.
     * @param   estimate
     *      The estimate (prediction) value.
     * @param   weight
     *      The weight for the pair.
     * @return
     *      A new target-estimate pair with the given weight.
     */
    public static <TargetType, EstimateType> DefaultWeightedTargetEstimatePair<TargetType, EstimateType> create(
        final TargetType target,
        final EstimateType estimate,
        final double weight)
    {
        return new DefaultWeightedTargetEstimatePair<TargetType, EstimateType>(
            target, estimate, weight);
    }
}
