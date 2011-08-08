/*
 * File:                FunctionMinimizerBFGSTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright November 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class FunctionMinimizerBFGSTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerBFGSTest
    extends FunctionMinimizerTestHarness<DifferentiableEvaluator<? super Vector,Double,Vector>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerBFGSTest
     * @param testName name of this test
     */
    public FunctionMinimizerBFGSTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public FunctionMinimizerBFGS createInstance()
    {
        return new FunctionMinimizerBFGS(
            FunctionMinimizerQuasiNewton.DEFAULT_LINE_MINIMIZER, null,
            this.TOLERANCE, this.MAX_ITERATIONS );
    }

}
