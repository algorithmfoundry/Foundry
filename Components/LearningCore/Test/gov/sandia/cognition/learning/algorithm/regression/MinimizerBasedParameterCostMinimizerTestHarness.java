/*
 * File:                MinimizerBasedParameterCostMinimizerTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 9, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedLearnerTestHarness;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Unit tests for MinimizerBasedParameterCostMinimizerTestHarness.
 *
 * @author krdixon
 */
public abstract class MinimizerBasedParameterCostMinimizerTestHarness
    extends SupervisedLearnerTestHarness
{

    /**
     * Tests for class MinimizerBasedParameterCostMinimizerTestHarness.
     * @param testName Name of the test.
     */
    public MinimizerBasedParameterCostMinimizerTestHarness(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MinimizerBasedParameterCostMinimizerTestHarness.
     */
    public abstract void testConstructors();

    @Override
    public abstract AbstractMinimizerBasedParameterCostMinimizer<? super GradientDescendable,?> createInstance(
        GradientDescendable objectToOptimize);

    /**
     * Test of clone method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    public void testClone()
    {
        System.out.println("clone");

        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance =
            this.createInstance( new FunctionDiffyANN(3,2,1) );
        AbstractMinimizerBasedParameterCostMinimizer<?,?> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getObjectToOptimize() );
        assertNotSame( instance.getObjectToOptimize(), clone.getObjectToOptimize() );
        assertNotNull( clone.getCostFunction() );
        assertNotSame( instance.getCostFunction(), clone.getCostFunction() );
    }

    /**
     * Test of createInternalFunction method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    public void testCreateInternalFunction()
    {
        System.out.println("createInternalFunction");
        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance =
            this.createInstance(new FunctionDiffyANN(3,2,1) );
        Evaluator<?,?> result = instance.createInternalFunction();
        assertNotNull( result );
        Evaluator<?,?> clone = ObjectUtil.cloneSmart(result);
        assertNotNull( clone );
        assertNotSame( result, clone );
    }

    /**
     * Test of getObjectToOptimize method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    public void testGetObjectToOptimize()
    {
        System.out.println("getObjectToOptimize");
        FunctionDiffyANN f = new FunctionDiffyANN(3,2,1);

        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance =
            this.createInstance(f);
        assertSame( f, instance.getObjectToOptimize() );
    }

    /**
     * Test of setObjectToOptimize method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    @SuppressWarnings("unchecked")
    public void testSetObjectToOptimize()
    {
        System.out.println("setObjectToOptimize");
        FunctionDiffyANN f = new FunctionDiffyANN(3,2,1);

        AbstractMinimizerBasedParameterCostMinimizer<? super GradientDescendable,?> instance =
            this.createInstance(f);
        assertSame( f, instance.getObjectToOptimize() );

        FunctionDiffyANN f2 = new FunctionDiffyANN(1,2,3);
        instance.setObjectToOptimize(f2);
        assertSame( f2, instance.getObjectToOptimize() );

    }

    /**
     * Test of getResult method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    public void testGetResult()
    {
        System.out.println("getResult");

        FunctionDiffyANN f = new FunctionDiffyANN(3,2,1);
        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance = this.createInstance(f);
        assertNull( instance.getResult() );
    }

    /**
     * Test of setResult method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    @SuppressWarnings("unchecked")
    public void testSetResult()
    {
        System.out.println("setResult");
        FunctionDiffyANN f = new FunctionDiffyANN(3,2,1);
        AbstractMinimizerBasedParameterCostMinimizer<? super GradientDescendable,?> instance = this.createInstance(f);
        instance.setResult( f );
        assertSame( instance.getResult(), f );
    }

    /**
     * Test of getCostFunction method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    public void testGetCostFunction()
    {
        System.out.println("getCostFunction");
        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance =
            this.createInstance(new FunctionDiffyANN(3,2,1) );
        assertNotNull( instance.getCostFunction() );
    }

    /**
     * Test of setCostFunction method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    @SuppressWarnings("unchecked")
    public void testSetCostFunction()
    {
        System.out.println("setCostFunction");
        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance =
            this.createInstance(new FunctionDiffyANN(3,2,1) );
        assertNotNull( instance.getCostFunction() );

        SumSquaredErrorCostFunction sse = new SumSquaredErrorCostFunction();
        instance.setCostFunction( sse );
        assertSame( sse, instance.getCostFunction() );

    }

    /**
     * Test of getPerformance method, of class AbstractMinimizerBasedParameterCostMinimizer.
     */
    public void testGetPerformance()
    {
        System.out.println("getPerformance");
        AbstractMinimizerBasedParameterCostMinimizer<?,?> instance =
            this.createInstance(new FunctionDiffyANN(3,2,1) );
        @SuppressWarnings("unchecked")
        NamedValue<Double> result = instance.getPerformance();
        assertNotNull( result );
        System.out.println( "Performance: " + result.getName() + " = " + result.getValue() );
    }

}
