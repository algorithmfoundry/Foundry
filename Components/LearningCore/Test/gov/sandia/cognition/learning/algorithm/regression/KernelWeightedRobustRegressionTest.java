/*
 * File:                KernelWeightedRobustRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.cost.MeanSquaredErrorCostFunction;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.RadialBasisKernel;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminant;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.util.ObjectUtil;

import java.util.LinkedList;

import junit.framework.TestCase;

/**
 * JUnit tests for class KernelWeightedRobustRegressionTest
 * @author Kevin R. Dixon
 */
public class KernelWeightedRobustRegressionTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class KernelWeightedRobustRegressionTest
     * @param testName name of this test
     */
    public KernelWeightedRobustRegressionTest(
        String testName )
    {
        super( testName );
    }

    public KernelWeightedRobustRegression<Vector, Vector> createInstance()
    {
        return new KernelWeightedRobustRegression<Vector, Vector>(
            new MultivariateLinearRegression(), new RadialBasisKernel() );
    }

    /**
     * Creates an uncorrupted dataset
     * @return
     */
    public LinkedList<InputOutputPair<Vector, Vector>> createDataset1()
    {

        LinkedList<InputOutputPair<Vector, Vector>> d =
            new LinkedList<InputOutputPair<Vector, Vector>>();
        for (int i = 1; i < 4; i++)
        {
            d.add( new DefaultInputOutputPair<Vector, Vector>( VectorFactory.getDefault().copyValues( i ), VectorFactory.getDefault().copyValues( 2 * i ) ) );
        }

        return d;
    }

    /**
     * Creates a dataset with an outlier
     * @return
     */
    public LinkedList<InputOutputPair<Vector, Vector>> createDataset2()
    {

        LinkedList<InputOutputPair<Vector, Vector>> d =
            new LinkedList<InputOutputPair<Vector, Vector>>();
        ScalarBasisSet<Double> polynomials = new ScalarBasisSet<Double>(
            PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ) );
        VectorFunctionLinearDiscriminant<Double> f =
            new VectorFunctionLinearDiscriminant<Double>( polynomials,
                new LinearDiscriminant( VectorFactory.getDefault().copyValues(-5.0, 2.0, 0.0 ) ) );
        for (double i = 1; i <= 10; i++)
        {
            d.add(new DefaultInputOutputPair<Vector, Vector>( polynomials.evaluate(i), VectorFactory.getDefault().copyValues(f.evaluate(i) ) ) );
        }

        double j = 2.5;
        d.add(new DefaultInputOutputPair<Vector, Vector>( polynomials.evaluate(j), VectorFactory.getDefault().copyValues(30+f.evaluate(j) )) );
        return d;
    }

    /**
     * Test of getKernelWeightingFunction method, of class KernelWeightedRobustRegression.
     */
    public void testGetKernelWeightingFunction()
    {
        System.out.println( "getKernelWeightingFunction" );
        KernelWeightedRobustRegression<Vector, Vector> instance = this.createInstance();
        assertNotNull( instance.getKernelWeightingFunction() );
    }

    /**
     * Test of setKernelWeightingFunction method, of class KernelWeightedRobustRegression.
     */
    public void testSetKernelWeightingFunction()
    {
        System.out.println( "setKernelWeightingFunction" );
        KernelWeightedRobustRegression<Vector, Vector> instance = this.createInstance();
        Kernel<? super Vector> kernel = instance.getKernelWeightingFunction();
        assertNotNull( kernel );

        instance.setKernelWeightingFunction( null );
        assertNull( instance.getKernelWeightingFunction() );

        instance.setKernelWeightingFunction( kernel );
        assertSame( kernel, instance.getKernelWeightingFunction() );

    }

    /**
     * Test of getTolerance method, of class KernelWeightedRobustRegression.
     */
    public void testGetTolerance()
    {
        System.out.println( "getTolerance" );
        KernelWeightedRobustRegression<Vector, Vector> instance = this.createInstance();
        assertTrue( instance.getTolerance() > 0.0 );
    }

    /**
     * Test of setTolerance method, of class KernelWeightedRobustRegression.
     */
    public void testSetTolerance()
    {
        System.out.println( "setTolerance" );
        double tolerance = Math.random();
        KernelWeightedRobustRegression<Vector, Vector> instance = this.createInstance();
        assertTrue( instance.getTolerance() > 0.0 );

        instance.setTolerance( tolerance );
        assertEquals( tolerance, instance.getTolerance() );

        try
        {
            instance.setTolerance( 0.0 );
            fail( "Tolerance must be > 0.0" );

        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getIterationLearner method, of class KernelWeightedRobustRegression.
     */
    public void testGetIterationLearner()
    {
        System.out.println( "getIterationLearner" );
        KernelWeightedRobustRegression<Vector, Vector> instance = this.createInstance();
        SupervisedBatchLearner<Vector,Vector, ?> learner = instance.getIterationLearner();
        assertNotNull( learner );

    }

    /**
     * Test of setIterationLearner method, of class KernelWeightedRobustRegression.
     */
    public void testSetIterationLearner()
    {
        System.out.println( "setIterationLearner" );
        KernelWeightedRobustRegression<Vector, Vector> instance = this.createInstance();
        SupervisedBatchLearner<Vector,Vector, ?> learner = instance.getIterationLearner();
        assertNotNull( learner );

        instance.setIterationLearner( null );
        assertNull( instance.getIterationLearner() );

        instance.setIterationLearner( learner );
        assertSame( learner, instance.getIterationLearner() );
    }

    public void testLearning1()
    {
        System.out.println( "learn1" );

        LinkedList<InputOutputPair<Vector, Vector>> d1 = this.createDataset1();

        KernelWeightedRobustRegression<Vector, Vector> r1 = this.createInstance();

        VectorFunction f2 = (VectorFunction) r1.learn( d1 );
        System.out.println( "Learner:\n" + ObjectUtil.inspectFieldValues( r1 ) );

        // Since there are no outliers, I should have only iterated once
        assertEquals( 1, r1.getIteration() );

        // I should have no mean-squared error, either.
        MeanSquaredErrorCostFunction cost = new MeanSquaredErrorCostFunction( d1 );
        assertEquals( 0.0, cost.evaluate( f2 ), 1e-5 );

    }

    public void testLearning2()
    {
        System.out.println( "learn2" );

        LinkedList<InputOutputPair<Vector, Vector>> d1 = this.createDataset2();


        KernelWeightedRobustRegression<Vector, Vector> r1 = this.createInstance();

        r1.setKernelWeightingFunction( new RadialBasisKernel( 10.0 ) );

        VectorFunction f2 = (VectorFunction) r1.learn( d1 );
        System.out.println( "Learner:\n" + ObjectUtil.inspectFieldValues( r1 ) );

        // Since we have an outlier, it will take a few iterations to arrive
        // at a stable solution
        assertTrue( 1 < r1.getIteration() );

        // I should have no mean-squared error, either.
        MeanSquaredErrorCostFunction cost = new MeanSquaredErrorCostFunction( d1 );
        double outlier = d1.getLast().getOutput().getElement( 0 );
        double expected = outlier * outlier / d1.size();
        assertEquals( expected, cost.evaluate( f2 ), outlier / d1.size() );
    }

}
