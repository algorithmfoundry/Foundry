/*
 * File:                DistributionEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import java.util.Collection;

/**
 * A BatchLearner that estimates a Distribution.
 * @param <ObservationType> Type of observations from the Distribution
 * @param <DistributionType> Type of Distribution estimated from the learner.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface DistributionEstimator<ObservationType,DistributionType extends Distribution<? extends ObservationType>>
    extends BatchLearner<Collection<? extends ObservationType>,DistributionType>
{
}
