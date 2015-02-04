/*
 * File:                VectorThresholdMaximumGainLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 23, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.algorithm.DimensionFilterableLearner;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * An interface class for decider learners that produce a threshold function
 * on a vector element based on maximizing some gain value.
 *
 * @param   <OutputType>
 *      The output type of the learner.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface VectorThresholdLearner<OutputType>
    extends
    DeciderLearner<Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer>,
        DimensionFilterableLearner
{
}
