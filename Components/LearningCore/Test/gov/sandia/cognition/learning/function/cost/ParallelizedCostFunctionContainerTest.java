/*
 * File:                ParallelizedCostFunctionContainerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class ParallelizedCostFunctionContainerTest
 * @author Kevin R. Dixon
 */
public class ParallelizedCostFunctionContainerTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class ParallelizedCostFunctionContainerTest
     * @param testName name of this test
     */
    public ParallelizedCostFunctionContainerTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Random
     */
    public Random RANDOM = new Random( 0 );

    /**
     * Test of getCostFunction method, of class ParallelizedCostFunctionContainer.
     */
    public void testGetCostFunction()
    {
        System.out.println( "getCostFunction" );
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        assertNull( instance.getCostFunction() );
        SumSquaredErrorCostFunction costFunction = new SumSquaredErrorCostFunction();
        instance.setCostFunction( costFunction );
        assertSame( costFunction, instance.getCostFunction() );
        
        instance = new ParallelizedCostFunctionContainer( costFunction );
        assertSame( costFunction, instance.getCostFunction() );
    }

    /**
     * Test of setCostFunction method, of class ParallelizedCostFunctionContainer.
     */
    public void testSetCostFunction()
    {
        System.out.println( "setCostFunction" );
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        assertNull( instance.getCostFunction() );
        SumSquaredErrorCostFunction costFunction = new SumSquaredErrorCostFunction();
        instance.setCostFunction( costFunction );
        assertSame( costFunction, instance.getCostFunction() );
    }

    /**
     * Test of createPartitions method, of class ParallelizedCostFunctionContainer.
     */
    public void testCreatePartitions()
    {
        System.out.println( "createPartitions" );
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        try
        {
            instance.createPartitions();
            fail( "Should have thrown null-pointer exception since we have not set the cost function!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of createThreadPool method, of class ParallelizedCostFunctionContainer.
     */
    public void testCreateThreadPool()
    {
        System.out.println( "createThreadPool" );
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        instance.createThreadPool();
    }

    /**
     * Test of clone method, of class ParallelizedCostFunctionContainer.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        instance.setCostFunction( new SumSquaredErrorCostFunction() );
        ParallelizedCostFunctionContainer clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getCostFunction(), clone.getCostFunction() );
        assertNotSame( instance.getThreadPool(), clone.getThreadPool() );
        assertEquals( instance.getNumThreads(), clone.getNumThreads() );
    }

    /**
     * Test of evaluate method, of class ParallelizedCostFunctionContainer.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        
        int num = 100;
        int dim = 3;
        double r = 1.0;
        
        MatrixMultiplyVectorFunction f = new MatrixMultiplyVectorFunction(
            MatrixFactory.getDefault().createUniformRandom( dim, dim, -r, r, RANDOM ) );
            
            new LinearVectorFunction( RANDOM.nextDouble() );
            
        ArrayList<InputOutputPair<Vector,Vector>> data =
            new ArrayList<InputOutputPair<Vector, Vector>>( num );
        for( int n = 0; n < num; n++ )
        {
            data.add( new DefaultInputOutputPair<Vector, Vector>(
                VectorFactory.getDefault().createUniformRandom( dim, -r, r, RANDOM ),
                VectorFactory.getDefault().createUniformRandom( dim, -r, r, RANDOM ) ) );
        }
        
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        SumSquaredErrorCostFunction costFunction = new SumSquaredErrorCostFunction();
        instance.setCostFunction( costFunction );
        instance.setThreadPool( ParallelUtil.createThreadPool( 10 ) );
        instance.setCostParameters( data );
        
        Double result = instance.evaluate( f );
        
        costFunction.setCostParameters( data );
        assertEquals( costFunction.evaluate( f ), result, 1e-5 );

    }

    /**
     * Test of evaluatePerformance method, of class ParallelizedCostFunctionContainer.
     */
    public void testEvaluatePerformance()
    {
        System.out.println( "evaluatePerformance" );
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        instance.setCostFunction( new SumSquaredErrorCostFunction() );
        
        int dim = 3;
        double r = 1.0;
        int num = 10;
        ArrayList<TargetEstimatePair<Vector,Vector>> data =
            new ArrayList<TargetEstimatePair<Vector, Vector>>( num );
        for( int n = 0; n < num; n++ )
        {
            data.add( new TargetEstimatePair<Vector, Vector>(
                VectorFactory.getDefault().createUniformRandom( dim, -r, r, RANDOM ),
                VectorFactory.getDefault().createUniformRandom( dim, -r, r, RANDOM ) ) );
        }        
        Double result = instance.evaluatePerformance( data );
        assertEquals( instance.getCostFunction().evaluatePerformance( data ), result );

    }

    /**
     * Test of computeParameterGradient method, of class ParallelizedCostFunctionContainer.
     */
    public void testComputeParameterGradient()
    {
        System.out.println( "computeParameterGradient" );
        
        int num = 100;
        int dim = 3;
        double r = 1.0;
        
        MatrixMultiplyVectorFunction f = new MatrixMultiplyVectorFunction(
            MatrixFactory.getDefault().createUniformRandom( dim, dim, -r, r, RANDOM ) );
            
            new LinearVectorFunction( RANDOM.nextDouble() );
            
        ArrayList<InputOutputPair<Vector,Vector>> data =
            new ArrayList<InputOutputPair<Vector, Vector>>( num );
        for( int n = 0; n < num; n++ )
        {
            data.add( new DefaultInputOutputPair<Vector, Vector>(
                VectorFactory.getDefault().createUniformRandom( dim, -r, r, RANDOM ),
                VectorFactory.getDefault().createUniformRandom( dim, -r, r, RANDOM ) ) );
        }
        
        ParallelizedCostFunctionContainer instance = new ParallelizedCostFunctionContainer();
        SumSquaredErrorCostFunction costFunction = new SumSquaredErrorCostFunction();
        instance.setCostFunction( costFunction );
        instance.setCostParameters( data );

        instance.setThreadPool( ParallelUtil.createThreadPool( 1 ));
        Vector result = instance.computeParameterGradient( f );
        instance.getCostFunction().setCostParameters( data );
        Vector expected = instance.getCostFunction().computeParameterGradient( f );
        if( !result.equals( expected, 1e-5 ) )
        {
            assertEquals( expected, result );
        }
        
    }

}
