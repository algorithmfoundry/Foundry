/*
 * File:                DefaultTargetEstimatePair.java
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

import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A default implementation of the {@code TargetEstimatePair}. This is useful
 * when keeping track of both a target (ground-truth) and an estimate
 * (prediction), for example when estimating the performance of a function.
 *
 * @param   <TargetType>
 *      The type of the target (ground-truth).
 * @param   <EstimateType>
 *      The type of the estimate (prediction).
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.1
 */
public class DefaultTargetEstimatePair<TargetType, EstimateType>
    extends AbstractTargetEstimatePair<TargetType, EstimateType>
{

    /** Target (ground-truth) value. */
    protected TargetType target;

    /** Estimate (prediction) of the target value. */
    protected EstimateType estimate;

    /**
     * Creates a new instance of {@code TargetEstimatePair}, with null target
     * and estimate values.
     */
    public DefaultTargetEstimatePair()
    {
        this(null, null);
    }

    /**
     * Creates a new instance of {@code TargetEstimatePair} with the given
     * target and estimate values.
     *
     * @param   target
     *      Target (ground-truth) value.
     * @param   estimate
     *      Estimate (prediction) of the target value.
     */
    public DefaultTargetEstimatePair(
        final TargetType target,
        final EstimateType estimate)
    {
        super();

        this.setTarget(target);
        this.setEstimate(estimate);
    }

    /**
     * Creates a shallow copy of another target-estimate pair.
     *
     * @param other 
     *      TargetEstimatePair to shallow copy.
     */
    public DefaultTargetEstimatePair(
        final Pair<? extends TargetType, ? extends EstimateType> other)
    {
        this(other.getFirst(), other.getSecond());
    }

    /**
     * Merges together two Collections into a single target-estimate pair
     * Collection.
     *
     * @param   <TargetType> The target type.
     * @param   <EstimateType> The estimate type.
     * @param   targets
     *      Collection of targets, must be same size as estimates
     * @param   estimates
     *      Collection of estimates, must be same size as targets
     * @return
     *      A new ArrayList of TargetEstimatePairs, will be same size as both
     *      targets and estimates.
     */
    public static <TargetType, EstimateType> ArrayList<DefaultTargetEstimatePair<TargetType, EstimateType>> mergeCollections(
        final Collection<? extends TargetType> targets,
        final Collection<? extends EstimateType> estimates)
    {
        if (targets.size() != estimates.size())
        {
            throw new IllegalArgumentException(
                "Targets and estimates must be the same size.");
        }

        int count = targets.size();

        ArrayList<DefaultTargetEstimatePair<TargetType, EstimateType>> result =
            new ArrayList<DefaultTargetEstimatePair<TargetType, EstimateType>>(count);
        Iterator<? extends TargetType> ti = targets.iterator();
        Iterator<? extends EstimateType> ei = estimates.iterator();
        for (int n = 0; n < count; n++)
        {
            result.add(new DefaultTargetEstimatePair<TargetType, EstimateType>(
                ti.next(), ei.next()));
        }

        return result;
    }

    @Override
    public DefaultTargetEstimatePair<TargetType, EstimateType> clone()
    {
        @SuppressWarnings("unchecked")
        DefaultTargetEstimatePair<TargetType, EstimateType> clone =
            (DefaultTargetEstimatePair<TargetType, EstimateType>) super.clone();
        clone.setTarget(ObjectUtil.cloneSmart(this.getTarget()));
        clone.setEstimate(ObjectUtil.cloneSmart(this.getEstimate()));
        return clone;
    }

    /**
     * Gets the target, which is the ground-truth or actual value.
     *
     * @return
     *      The target (ground-truth) value.
     */
    public TargetType getTarget()
    {
        return this.target;
    }


    /**
     * Sets the target, which is the ground-truth or actual value.
     *
     * @param   target
     *      The target (ground-truth) value.
     */
    public void setTarget(
        TargetType target)
    {
        this.target = target;
    }

    /**
     * Gets the estimate, which is the prediction or guess.
     *
     * @return
     *      The target (predicted) value.
     */
    public EstimateType getEstimate()
    {
        return this.estimate;
    }

    /**
     * Sets the estimate, which is the prediction or guess.
     *
     * @param   estimate
     *      The target (predicted) value.
     */
    public void setEstimate(
        EstimateType estimate)
    {
        this.estimate = estimate;
    }

    /**
     * Convenience method for creating a new {@code DefaultTargetEstimatePair}.
     *
     * @param   <TargetType>
     *      The target (ground-truth) type.
     * @param   <EstimateType>
     *      The estimate (prediction) type. Usually the same as TargetType.
     * @return
     *      A new, empty pair.
     */
    public static <TargetType, EstimateType> DefaultTargetEstimatePair<TargetType, EstimateType> create()
    {
        return new DefaultTargetEstimatePair<TargetType, EstimateType>();
    }

    /**
     * Convenience method for creating a new {@code DefaultTargetEstimatePair}.
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
     *      A new target-estimate pair.
     */
    public static <TargetType, EstimateType> DefaultTargetEstimatePair<TargetType, EstimateType> create(
        final TargetType target,
        final EstimateType estimate)
    {
        return new DefaultTargetEstimatePair<TargetType, EstimateType>(
            target, estimate);
    }
    
}
