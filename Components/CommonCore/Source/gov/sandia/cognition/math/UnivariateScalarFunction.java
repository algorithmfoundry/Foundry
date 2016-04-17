/*
 * File:                ScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Simple interface that describes a function that maps the reals to the reals,
 * has a Double to Double and double to double.
 * 
 * It is recommended that a {@link UnivariateScalarFunction} implement
 * {@link CloneableSerializable}, though it is not required.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-26",
    changesNeeded=false,
    comments="Looks good."
)
public interface UnivariateScalarFunction
    extends Evaluator<Double, Double>,
        ScalarFunction<Double>
{
    
    /**
     * Produces a double output for the given double input
     * @param input 
     * Input to the Evaluator
     * @return 
     * output at the given input
     */
    double evaluate(
        final double input);

    @Override
    default Double evaluate(
        final Double input)
    {
        return this.evaluate((double) input);
    }
    
    @Override
    default double evaluateAsDouble(
        final Double input)
    {
        return this.evaluate((double) input);
    }

}
