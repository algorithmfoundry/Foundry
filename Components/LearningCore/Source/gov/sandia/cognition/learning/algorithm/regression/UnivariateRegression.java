/*
 * File:                UnivariateRegression.java
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
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;

/**
 * A type of Regression algorithm that has a single dependent (output) variable
 * that we are trying to predict.  This formulation allows for single
 * independent input variable (simple regression) or multiple input variables
 * (multiple regression) onto a single dependent (output) variable.
 * @author Kevin R. Dixon
 * @since 3.3.3
 * @param   <InputType> The type of input data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also takes this as the input parameter.
 * @param   <EvaluatorType> The type of object created by the learning algorithm.
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Simple linear regression",
            type=PublicationType.WebPage,
            year=2012,
            url="http://en.wikipedia.org/wiki/Simple_regression"
        )
        ,
        @PublicationReference(
            author="StatSoft",
            title="Multiple Regression",
            type=PublicationType.WebPage,
            url="http://www.statsoft.com/textbook/multiple-regression/",
            year=2012
        )
    }
)
public interface UnivariateRegression<InputType, EvaluatorType extends Evaluator<? super InputType, ? extends Double>>
    extends Regression<InputType,Double,EvaluatorType>
{
}
