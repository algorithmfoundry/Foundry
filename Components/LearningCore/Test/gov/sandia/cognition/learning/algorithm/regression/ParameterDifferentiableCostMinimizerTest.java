/*
 * File:                ParameterDifferentiableCostMinimizerTest.java
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
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;

/**
 * JUnit tests for class ParameterDifferentiableCostMinimizerTest
 * @author Kevin R. Dixon
 */
public class ParameterDifferentiableCostMinimizerTest
    extends MinimizerBasedParameterCostMinimizerTestHarness
{

    /**
     * Entry point for JUnit tests for class ParameterDifferentiableCostMinimizerTest
     * @param testName name of this test
     */
    public ParameterDifferentiableCostMinimizerTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public ParameterDifferentiableCostMinimizer createInstance(
        GradientDescendable objectToOptimize )
    {
        ParameterDifferentiableCostMinimizer mini = 
            new ParameterDifferentiableCostMinimizer();
        mini.setCostFunction( new SumSquaredErrorCostFunction() );
        mini.setObjectToOptimize( objectToOptimize );
        return mini;
    }
    
    public void testSetMaxIterations()
    {
        System.out.println( "setMaxIterations" );
        ParameterDifferentiableCostMinimizer instance = 
            new ParameterDifferentiableCostMinimizer();
        
        int maxIterations = 9;
        instance.setMaxIterations(maxIterations);
        assertEquals(maxIterations, instance.getMaxIterations());
        
        
        maxIterations = 90;
        instance.setMaxIterations(maxIterations);
        assertEquals(maxIterations, instance.getMaxIterations());
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ParameterDifferentiableCostMinimizer instance =
            new ParameterDifferentiableCostMinimizer();
        // Should be clones here!
        assertNotNull( instance.getAlgorithm() );
        assertNotSame( ParameterDifferentiableCostMinimizer.DEFAULT_FUNCTION_MINIMIZER,
            instance.getAlgorithm() );
        assertTrue( ParameterDifferentiableCostMinimizer.DEFAULT_FUNCTION_MINIMIZER.getClass().isInstance(
            instance.getAlgorithm() ) );

        assertSame( ParameterDifferentiableCostMinimizer.DEFAULT_COST_FUNCTION,
            instance.getCostFunction() );
    }
    

}
