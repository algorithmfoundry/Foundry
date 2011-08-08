/*
 * File:                FunctionMinimizerLiuStoreyTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerLiuStorey;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class FunctionMinimizerLiuStoreyTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerLiuStoreyTest
    extends FunctionMinimizerTestHarness<DifferentiableEvaluator<? super Vector,Double,Vector>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerLiuStoreyTest
     * @param testName name of this test
     */
    public FunctionMinimizerLiuStoreyTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public FunctionMinimizer<Vector, Double, DifferentiableEvaluator<? super Vector, Double, Vector>> createInstance()
    {
        return new FunctionMinimizerLiuStorey();
    }

}
