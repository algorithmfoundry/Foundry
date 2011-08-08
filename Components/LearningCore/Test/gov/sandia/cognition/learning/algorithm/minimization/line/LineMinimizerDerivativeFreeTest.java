/*
 * File:                LineMinimizerDerivativeFreeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.evaluator.Evaluator;

/**
 * JUnit tests for class LineMinimizerDerivativeFreeTest
 * @author Kevin R. Dixon
 */
public class LineMinimizerDerivativeFreeTest
    extends LineMinimizerTestHarness<Evaluator<Double,Double>>
{

    /**
     * Entry point for JUnit tests for class LineMinimizerDerivativeFreeTest
     * @param testName name of this test
     */
    public LineMinimizerDerivativeFreeTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public LineMinimizerDerivativeFree createInstance()
    {
        LineMinimizerDerivativeFree mini = new LineMinimizerDerivativeFree();
        mini.setTolerance( 1e-2 );
        return mini;
    }

}
