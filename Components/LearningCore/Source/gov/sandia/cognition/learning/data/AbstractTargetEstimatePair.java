/*
 * File:                AbstractTargetEstimatePair.java
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

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code TargetEstimatePair}. This is useful
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
public abstract class AbstractTargetEstimatePair<TargetType, EstimateType>
    extends AbstractCloneableSerializable
    implements TargetEstimatePair<TargetType, EstimateType>
{

    /**
     * Creates a new {@code AbstractTargetEstimatePair}.
     */
    public AbstractTargetEstimatePair()
    {
        super();
    }

    /**
     * Gets the target, which is the first element in the pair.
     *
     * @return
     *      The target.
     */
    @Override
    public TargetType getFirst()
    {
        return this.getTarget();
    }

    /**
     * Gets the estimate, which is the second element in the pair.
     *
     * @return
     *      The estimate.
     */
    @Override
    public EstimateType getSecond()
    {
        return this.getEstimate();
    }

    @Override
    public String toString()
    {
        return "(target=" + this.getTarget()
            + ", estimate=" + this.getEstimate() + ")";
    }
    
}
