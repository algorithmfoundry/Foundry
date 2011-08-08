/*
 * File:                AbstractAnytimeSupervisedBatchLearner.java
 * Authors:             Kevin R. Dixon
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

import gov.sandia.cognition.algorithm.AnytimeAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Collection;

/**
 * The {@code AbstractAnytimeSupervisedBatchLearner} abstract class extends
 * the {@code AbstractAnytimeBatchLearner} to implement the 
 * {@code SupervisedBatchLearner} interface. It does this to simplify the use
 * of the standard generics that a batch supervised learning algorithm uses.
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
 * @see     BatchLearner
 * @see     AnytimeAlgorithm
 * @see     SupervisedBatchLearner
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Reformatted the rather long class-generic parameterization.",
        "Code looks fine."
    }
)
public abstract class AbstractAnytimeSupervisedBatchLearner
        <InputType, OutputType, ResultType extends Evaluator<? super InputType, ? extends OutputType>>
    extends AbstractAnytimeBatchLearner
        <Collection<? extends InputOutputPair<? extends InputType, OutputType>>, ResultType>
    implements SupervisedBatchLearner<InputType, OutputType, ResultType>
{

    /** 
     * Creates a new instance of {@code AbstractAnytimeSupervisedBatchLearner}.
     * 
     * @param maxIterations 
     *      Maximum number of iterations before stopping. Must be greater than 
     *      zero.
     */
    public AbstractAnytimeSupervisedBatchLearner(
        final int maxIterations)
    {
        super(maxIterations);
    }

}
