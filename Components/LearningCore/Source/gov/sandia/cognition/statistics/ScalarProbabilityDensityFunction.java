/*
 * File:                ScalarProbabilityDensityFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.UnivariateScalarFunction;

/**
 * A PDF that takes doubles as input.  Most PDFs will inherit from this
 * interface.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ScalarProbabilityDensityFunction 
    extends SmoothScalarDistribution,
    ProbabilityDensityFunction<Double>,
    UnivariateScalarFunction
{

    public ScalarProbabilityDensityFunction getProbabilityFunction();

    /**
     * Evaluate the natural logarithm of the distribution function.
     * This is sometimes more efficient than evaluating the distribution
     * function itself, and when evaluating the product of many independent
     * or exchangeable samples.
     *
     * @param input
     *      The input value.
     * @return
     *      The natural logarithm of the distribution function.
     */
    public double logEvaluate(
        final double input);

}
