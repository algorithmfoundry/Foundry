/*
 * File:                TargetEstimatePair.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A Pair that encapsulates a target-estimate Pair.  This is useful when
 * capturing a target (ground-truth) and an estimate of that Pair
 *
 * @param <TargetType> Class of the target (ground-truth)
 * @param <EstimateType> Class of the estimate
 * @author Kevin R. Dixon
 * @since 2.0
 */
public class TargetEstimatePair<TargetType, EstimateType>
    extends AbstractCloneableSerializable
    implements Pair<TargetType, EstimateType>
{

    /**
     * Target (ground-truth) value
     */
    private TargetType target;

    /**
     * Estimate of the target value
     */
    private EstimateType estimate;

    /** 
     * Creates a new instance of TargetEstimatePair 
     */
    public TargetEstimatePair()
    {
        this( null, null );
    }

    /**
     * Creates a new instance of TargetEstimatePair 
     * @param target
     * Target (ground-truth) value
     * @param estimate
     * Estimate of the target value
     */
    public TargetEstimatePair(
        TargetType target,
        EstimateType estimate )
    {
        super();

        this.setTarget( target );
        this.setEstimate( estimate );
    }

    /**
     * Copy Constructor
     * @param other TargetEstimatePair to copy
     */
    public TargetEstimatePair(
        TargetEstimatePair<? extends TargetType, ? extends EstimateType> other )
    {
        this( other.getTarget(), other.getEstimate() );
    }

    /**
     * Merges together two Collections into a single target-estimate pair
     * Collection.
     * 
     * @param   <TargetType> The target type.
     * @param   <EstimateType> The estimate type.
     * @param targets Collection of targets, must be same size as estimates
     * @param estimates Collection of estimates, must be same size as targets
     * @return ArrayList of TargetEstimatePairs, will be same size as both
     * targets and estimates
     */
    public static <TargetType, EstimateType> 
    ArrayList<TargetEstimatePair<TargetType, EstimateType>> mergeCollections(
        Collection<? extends TargetType> targets,
        Collection<? extends EstimateType> estimates )
    {
        if (targets.size() != estimates.size())
        {
            throw new IllegalArgumentException(
                "Targets and estimates must be the same size!" );
        }

        int num = targets.size();

        ArrayList<TargetEstimatePair<TargetType, EstimateType>> retval =
            new ArrayList<TargetEstimatePair<TargetType, EstimateType>>( num );
        Iterator<? extends TargetType> ti = targets.iterator();
        Iterator<? extends EstimateType> ei = estimates.iterator();
        for (int n = 0; n < num; n++)
        {
            retval.add( new TargetEstimatePair<TargetType, EstimateType>( 
                ti.next(), ei.next() ) );
        }

        return retval;
    }

    @Override
    public TargetEstimatePair clone()
    {
        @SuppressWarnings("unchecked")
        TargetEstimatePair<TargetType,EstimateType> clone =
            (TargetEstimatePair<TargetType,EstimateType>) super.clone();
        clone.setTarget( ObjectUtil.cloneSmart(this.getTarget()) );
        clone.setEstimate( ObjectUtil.cloneSmart( this.getEstimate() ) );
        return clone;
    }

    /**
     * Gets the target
     * @return Target
     */
    public TargetType getFirst()
    {
        return this.getTarget();
    }

    /**
     * Gets the estimate
     * @return Estimate
     */
    public EstimateType getSecond()
    {
        return this.getEstimate();
    }

    /**
     * Getter for target
     * @return
     * Target (ground-truth) value
     */
    public TargetType getTarget()
    {
        return this.target;
    }

    /**
     * Setter for target
     * @param target
     * Target (ground-truth) value
     */
    public void setTarget(
        TargetType target )
    {
        this.target = target;
    }

    /**
     * Getter for estimate
     * @return
     * Estimate of the target value
     */
    public EstimateType getEstimate()
    {
        return this.estimate;
    }

    /**
     * Setter for estimate
     * @param estimate
     * Estimate of the target value
     */
    public void setEstimate(
        EstimateType estimate )
    {
        this.estimate = estimate;
    }

}
