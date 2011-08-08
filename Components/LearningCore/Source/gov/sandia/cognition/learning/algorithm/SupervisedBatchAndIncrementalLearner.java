/*
 * File:                SupervisedBatchAndIncrementalLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 06, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * Interface for a class that is a supervised learning algorithm that can be
 * used both batch and incremental contexts.
 *
 * @param   <InputType> The type of input data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also takes this as the input parameter.
 * @param   <OutputType> The type of output data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also produces this as its output.
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code LinearBinaryCategorizer}.
 * @author  Justin Basilico
 * @since   3.2.0
 */
public interface SupervisedBatchAndIncrementalLearner<InputType, OutputType, ResultType extends Evaluator<? super InputType, ? extends OutputType>>
    extends SupervisedIncrementalLearner<InputType, OutputType, ResultType>,
        SupervisedBatchLearner<InputType, OutputType, ResultType>,
        BatchAndIncrementalLearner<InputOutputPair<? extends InputType, OutputType>, ResultType>
{
}
