/*
 * File:                MultivariateRegression.java
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
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * A regression algorithm that maps one or more independent (input) variables
 * onto multiple output variables.
 * @author Kevin R. Dixon
 * @since 3.3.3
 * @param   <InputType> The type of input data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also takes this as the input parameter.
 * @param   <EvaluatorType> The type of object created by the learning algorithm.
 */
@PublicationReference(
    author="Wikipedia",
    title="General linear model",
    type=PublicationType.WebPage,
    year=2012,
    url="http://en.wikipedia.org/wiki/Multivariate_regression",
    notes={
        "The only article on Multivariate Regression I could find only talks about the linear case.",
        "This interface, however, can deal with the nonlinear case as well."
    }
)
public interface MultivariateRegression<InputType, EvaluatorType extends Evaluator<? super InputType, ? extends Vectorizable>>
    extends Regression<InputType,Vectorizable,EvaluatorType>
{    
}
