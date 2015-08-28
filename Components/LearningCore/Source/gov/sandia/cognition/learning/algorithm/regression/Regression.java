/*
 * File:                Regression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 23, 2012, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;

/**
 * A supervised learning algorithm that attempts to interpolate/extrapolate
 * inputs given a training set of input/output pairs.
 * @author Kevin R. Dixon
 * @since 3.3.3
 * @param   <InputType> The type of input data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also takes this as the input parameter.
 * @param   <OutputType> The type of output data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also produces this as its output.
 * @param   <EvaluatorType> The type of object created by the learning algorithm.
 *          For example, a {@code FeedforwardNeuralNetwork}.
 */
@PublicationReference(
    author="Wikipedia",
    title="Regression Analysis",
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Regression_analysis",
    year=2012
)
public interface Regression<InputType, OutputType, EvaluatorType extends Evaluator<? super InputType, ? extends OutputType>>
    extends SupervisedBatchLearner<InputType, OutputType, EvaluatorType>
{
}
