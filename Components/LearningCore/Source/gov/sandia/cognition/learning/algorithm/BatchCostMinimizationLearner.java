/*
 * File:                BatchCostMinimizationLearner.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.function.cost.CostFunction;

/**
 * The {@code BatchCostMinimizationLearner} interface defines the functionality
 * of a cost-minimization learning algorithm should follow.
 * (These algorithms typically fall into the categories of "supervised" and 
 * "reinforcement" learning algorithms, but I don't like anthropomorphic terms.)
 * A {@code BatchLearner} takes two generics to parameterize it: 
 * {@code LearnableType} is the class of thing we're going to minimize and the 
 * second parameter {@code CostFunctionType} is a class of {@code CostFunction}
 * that can evaluate the {@code LearnableType}.
 * 
 * @param   <CostParametersType> The type of parameters that the cost function 
 *          takes. For example, a Collection of InputOutputPairs.
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code FeedforwardNeuralNetwork}.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-22",
            changesNeeded=false,
            comments={
                "Moved previous code review to annotation.",
                "Interface looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-03",
            changesNeeded=false,
            comments={
                "Added some missing documentation.",
                "Interface looks fine."
            }
        )
    }
)
public interface BatchCostMinimizationLearner<CostParametersType, ResultType>
    extends BatchLearner<CostParametersType, ResultType>
{

    /**
     * Invokes the minimization (learning) call using the given cost function
     * parameters.
     *
     * @param   minimizationParameters The parameters for the cost function to
     *          minimize against.
     * @return  The object learned by the learning algorithm.
     */
    ResultType learn(
        CostParametersType minimizationParameters);

    /**
     * Gets the cost function that the learner is minimizing.
     *
     * @return  The CostFunction that the learner's algorithm is minimizing.
     */
    CostFunction<? super ResultType, ? super CostParametersType> 
        getCostFunction();

}
