/*
 * File:                LineMinimizerDerivativeBasedTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeBased;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;

/**
 * JUnit tests for class LineMinimizerDerivativeBasedTest
 * @author Kevin R. Dixon
 */
public class LineMinimizerDerivativeBasedTest
    extends LineMinimizerTestHarness<DifferentiableUnivariateScalarFunction>
{

    /**
     * Entry point for JUnit tests for class LineMinimizerDerivativeBasedTest
     * @param testName name of this test
     */
    public LineMinimizerDerivativeBasedTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public LineMinimizerDerivativeBased createInstance()
    {
        LineMinimizerDerivativeBased mini = new LineMinimizerDerivativeBased();
        mini.setMinFunctionValue( -1.0 );
        mini.setTolerance( 1e-2 );
        return mini;
    }

}
