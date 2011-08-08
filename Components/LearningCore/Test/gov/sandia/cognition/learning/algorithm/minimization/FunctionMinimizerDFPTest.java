/*
 * File:                FunctionMinimizerDFPTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 21, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerDFP;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerQuasiNewton;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class FunctionMinimizerDFPTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerDFPTest
    extends FunctionMinimizerTestHarness<DifferentiableEvaluator<? super Vector,Double,Vector>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerDFPTest
     * @param testName name of this test
     */
    public FunctionMinimizerDFPTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public FunctionMinimizerDFP createInstance()
    {
        return new FunctionMinimizerDFP(
            FunctionMinimizerQuasiNewton.DEFAULT_LINE_MINIMIZER, null,
            this.TOLERANCE/100.0, this.MAX_ITERATIONS );
    }

}
