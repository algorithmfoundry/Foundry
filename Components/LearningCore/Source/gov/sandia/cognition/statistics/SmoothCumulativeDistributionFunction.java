/*
 * File:                SmoothCumulativeDistributionFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 15, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.ClosedFormDifferentiableEvaluator;
import gov.sandia.cognition.math.UnivariateScalarFunction;

/**
 * This defines a CDF that has an associated derivative, which is its PDF.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface SmoothCumulativeDistributionFunction
    extends ClosedFormCumulativeDistributionFunction<Double>,
    UnivariateScalarFunction,
    ClosedFormDifferentiableEvaluator<Double,Double,Double>
{

    public ScalarProbabilityDensityFunction getDerivative();

}
