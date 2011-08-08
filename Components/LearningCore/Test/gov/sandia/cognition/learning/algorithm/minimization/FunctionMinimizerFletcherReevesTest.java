/*
 * File:                FunctionMinimizerFletcherReevesTest.java
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

import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerFletcherReeves;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class FunctionMinimizerFletcherReevesTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerFletcherReevesTest
    extends FunctionMinimizerTestHarness<DifferentiableEvaluator<? super Vector,Double,Vector>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerFletcherReevesTest
     * @param testName name of this test
     */
    public FunctionMinimizerFletcherReevesTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public FunctionMinimizerFletcherReeves createInstance()
    {
        return new FunctionMinimizerFletcherReeves();
    }


}
