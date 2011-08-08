/*
 * File:                SupervisedBatchLearner.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Collection;

/**
 * The {@code BatchSupervisedLearner} interface is an extension of the 
 * {@code BatchLearner} interface that contains the typical generic definition
 * conventions for a batch, supervised learning algorithm. That is, it takes
 * a collection of input-output pairs and learns an evaluator that can take the
 * the input value type and return the output value type. The interface does
 * not define any extra functionality, it just provides a convenience for the
 * large generic parameter definition.
 * 
 * @param   <InputType> The type of input data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also takes this as the input parameter.
 * @param   <OutputType> The type of output data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also produces this as its output.
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code FeedforwardNeuralNetwork}.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Interface looks fine."
)
public interface SupervisedBatchLearner
        <InputType, OutputType, 
         ResultType extends Evaluator<? super InputType, ? extends OutputType>>
    extends BatchLearner
        <Collection<? extends InputOutputPair<? extends InputType, OutputType>>,
         ResultType>
{
}
