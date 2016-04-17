/*
 * File:                DifferentiableUnivariateScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * A differentiable univariate scalar function
 * @author Kevin R. Dixon
 * @since 2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-12",
    changesNeeded=false,
    comments={
        "Added differentiate(double) method.",
        "Created AbstractDifferentiableUnivariateScalarFunction as a partial implementation.",
        "Otherwise, interface is fine."
    }
)
public interface DifferentiableUnivariateScalarFunction
    extends UnivariateScalarFunction,
    DifferentiableEvaluator<Double, Double, Double>
{

    /**
     * Differentiates the output of the function about the given input
     * @param input
     * Input about which to compute the derivative of the function output
     * @return
     * Derivative of the output with respect to the input
     */
    double differentiate(
        double input );

    @Override
    default Double differentiate(
        final Double input)
    {
        return this.differentiate(input.doubleValue());
    }
    
}
