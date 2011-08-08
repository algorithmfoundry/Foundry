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

import gov.sandia.cognition.util.Pair;

/**
 * A Pair that encapsulates a target-estimate Pair.  This is useful when
 * capturing a target (ground-truth) and an estimate of that Pair
 *
 * @param   <TargetType>
 *      The type of the target (ground-truth).
 * @param   <EstimateType>
 *      The type of the estimate (prediction). Typically this is the same as
 *      TargetType, but it does not have to be.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.0
 */
public interface TargetEstimatePair<TargetType, EstimateType>
    extends Pair<TargetType, EstimateType>
{

    /**
     * Gets the target, which is the ground-truth or actual value.
     *
     * @return
     *      The target (ground-truth) value.
     */
    public TargetType getTarget();

    /**
     * Gets the estimate, which is the prediction or guess.
     *
     * @return
     *      The target (predicted) value.
     */
    public EstimateType getEstimate();

}
