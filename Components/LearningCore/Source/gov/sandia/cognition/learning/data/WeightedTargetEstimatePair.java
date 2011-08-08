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
public interface WeightedTargetEstimatePair<TargetType, EstimateType>
    extends TargetEstimatePair<TargetType, EstimateType>, Weighted
{
}
