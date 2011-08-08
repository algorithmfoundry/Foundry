/*
 * File:                ParameterDerivativeFreeCostMinimizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 4, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;

/**
 * JUnit tests for class ParameterDerivativeFreeCostMinimizerTest
 * @author Kevin R. Dixon
 */
public class ParameterDerivativeFreeCostMinimizerTest
    extends MinimizerBasedParameterCostMinimizerTestHarness
{

    /**
     * Entry point for JUnit tests for class ParameterDerivativeFreeCostMinimizerTest
     * @param testName name of this test
     */
    public ParameterDerivativeFreeCostMinimizerTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public ParameterDerivativeFreeCostMinimizer createInstance(
        GradientDescendable objectToOptimize )
    {
        ParameterDerivativeFreeCostMinimizer mini =
            new ParameterDerivativeFreeCostMinimizer();
        mini.setObjectToOptimize( objectToOptimize );
        return mini;
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ParameterDerivativeFreeCostMinimizer instance =
            new ParameterDerivativeFreeCostMinimizer();
        // Should be clones here!
        assertNotNull( instance.getAlgorithm() );
        assertNotSame( ParameterDerivativeFreeCostMinimizer.DEFAULT_FUNCTION_MINIMIZER,
            instance.getAlgorithm() );
        assertTrue( ParameterDerivativeFreeCostMinimizer.DEFAULT_FUNCTION_MINIMIZER.getClass().isInstance(
            instance.getAlgorithm() ) );

        assertSame( ParameterDerivativeFreeCostMinimizer.DEFAULT_COST_FUNCTION,
            instance.getCostFunction() );
    }

}
