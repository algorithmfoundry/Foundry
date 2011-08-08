/*
 * File:                LocallyWeightedFunctionTest.java
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
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.RadialBasisKernel;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class LocallyWeightedFunctionTest
 * @author Kevin R. Dixon
 */
public class LocallyWeightedFunctionTest
    extends TestCase
{

    /** The random number generator for the tests. */
    private Random random = new Random();

    /**
     * Entry point for JUnit tests for class LocallyWeightedFunctionTest
     * @param testName name of this test
     */
    public LocallyWeightedFunctionTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Number of dimensions in the learner
     */
    public static final int NUM_DIM = 2;

    /**
     * 
     * @return
     */
    public LocallyWeightedFunction.Learner<Vector, Vector> createLearnerInstance()
    {
        DecoupledVectorLinearRegression learner = new DecoupledVectorLinearRegression(
            NUM_DIM, PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ) );

        return new LocallyWeightedFunction.Learner<Vector, Vector>(
            new RadialBasisKernel(), learner );


    }

    /**
     * 
     * @return
     */
    public LocallyWeightedFunction<Vector, Vector> createFunctionInstance()
    {
        LocallyWeightedFunction.Learner<Vector, Vector> learner = this.createLearnerInstance();

        int num = 100;
        ArrayList<InputOutputPair<Vector, Vector>> rawData =
            new ArrayList<InputOutputPair<Vector, Vector>>( num );
        double r = 1.0;
        for (int n = 0; n < num; n++)
        {
            Vector x = VectorFactory.getDefault().createUniformRandom( NUM_DIM, -r, r, random );
            Vector y = VectorFactory.getDefault().createUniformRandom( NUM_DIM, -r, r, random );
            rawData.add( new DefaultWeightedInputOutputPair<Vector, Vector>( x, y, Math.random() ) );
        }
        return learner.learn( rawData );
    }

    /**
     * Test of getKernel method, of class LocallyWeightedFunction.
     */
    public void testGetKernel()
    {
        System.out.println( "getKernel" );
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();
        Kernel<? super Vector> kernel = instance.getKernel();
        assertNotNull( kernel );

    }

    /**
     * Test of setKernel method, of class LocallyWeightedFunction.
     */
    public void testSetKernel()
    {
        System.out.println( "setKernel" );
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();
        Kernel<? super Vector> kernel = instance.getKernel();
        assertNotNull( kernel );

        instance.setKernel( null );
        assertNull( instance.getKernel() );

        instance.setKernel( kernel );
        assertSame( kernel, instance.getKernel() );
    }

    /**
     * Test of getLearner method, of class LocallyWeightedFunction.
     */
    public void testGetLearner()
    {
        System.out.println( "getLearner" );
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();
        SupervisedBatchLearner<Vector,Vector,?> learner = instance.getLearner();
        assertNotNull( learner );
    }

    /**
     * Test of setLearner method, of class LocallyWeightedFunction.
     */
    public void testSetLearner()
    {
        System.out.println( "setLearner" );
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();
        SupervisedBatchLearner<Vector,Vector,?> learner = instance.getLearner();
        assertNotNull( learner );

        instance.setLearner( null );
        assertNull( instance.getLearner() );

        instance.setLearner( learner );
        assertSame( learner, instance.getLearner() );
    }

    /**
     * Test of evaluate method, of class LocallyWeightedFunction.
     */
    public void testEvaluate()
    {
        System.out.println( "Function.evaluate" );
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();

        assertNull( instance.getLocalApproximator() );
        double r = 1.0;
        Vector input = VectorFactory.getDefault().createUniformRandom( NUM_DIM, -r, r, random );
        Vector output = instance.evaluate( input );

        assertNotNull( instance.getLocalApproximator() );
        Vector localOutput = instance.getLocalApproximator().evaluate( input );
        assertEquals( localOutput, output );
    }

    /**
     * Test of getLocalApproximator method, of class LocallyWeightedFunction.
     */
    public void testGetLocalApproximator()
    {
        System.out.println( "Function.getLocalApproximator" );
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();
        assertNull( instance.getLocalApproximator() );
    }

    /**
     * Test of setLocalApproximator method, of class LocallyWeightedFunction.
     */
    public void testSetLocalApproximator()
    {
        System.out.println( "Function.setLocalApproximator" );

        double r = 1.0;
        LocallyWeightedFunction<Vector, Vector> instance = this.createFunctionInstance();
        Vector input = VectorFactory.getDefault().createUniformRandom( NUM_DIM, -r, r, random );
        Vector output = instance.evaluate( input );

        Evaluator<? super Vector, ? extends Vector> f = instance.getLocalApproximator();

        assertNotNull( f );

        Vector input2 = input.scale( Math.random() );
        Vector output2 = instance.evaluate( input2 );

        Evaluator<? super Vector, ? extends Vector> f2 = instance.getLocalApproximator();
        assertNotNull( f2 );
        assertNotSame( f2, f );

        Vector outputAgain = instance.evaluate( input );
        assertEquals( output, outputAgain );
        assertNotSame( f, instance.getLocalApproximator() );
    }
    
    public void testKnownResult()
    {
        System.out.println( "testKnownResult" );
        
        LinkedList<InputOutputPair<Double,Double>> data =
            new LinkedList<InputOutputPair<Double,Double>>();
        data.add( new DefaultInputOutputPair<Double, Double>( 1.0, 0.0 ) );
        data.add( new DefaultInputOutputPair<Double, Double>( 2.0, 1.0 ) );
        data.add( new DefaultInputOutputPair<Double, Double>( 3.0, 4.0 ) );
        
        LocallyWeightedFunction.Learner<Double,Double> learner =
            new LocallyWeightedFunction.Learner<Double,Double>(
                new RadialBasisScalarKernel(),
                new LinearRegression<Double>( PolynomialFunction.createPolynomials(0.0,1.0) ) );
        
        LocallyWeightedFunction<Double,Double> f = learner.learn(data);
        
        double y1 = f.evaluate(data.get(0).getInput());
        System.out.println( "Y1: " + y1 + ", approximator: " + f.getLocalApproximator() );

        double y2 = f.evaluate(data.get(1).getInput());
        System.out.println( "Y2: " + y2 + ", approximator: " + f.getLocalApproximator() );
        
        double y3 = f.evaluate(data.get(2).getInput());
        System.out.println( "Y3: " + y3 + ", approximator: " + f.getLocalApproximator() );
        
        // I computed these values BY HAND in octave (ugh)
        final double EPS = 1e-5;
        assertEquals( -0.0300881836, y1, EPS );
        assertEquals(  1.4238831152, y2, EPS );
        assertEquals(  3.9699118164, y3, EPS );
    }

    public static class RadialBasisScalarKernel
        implements Kernel<Double>
    {

        public final RadialBasisKernel rbf = new RadialBasisKernel(1.0);
        
        public double evaluate(
            Double x,
            Double y )
        {
            Vector v1 = VectorFactory.getDefault().copyValues(x);
            Vector v2 = VectorFactory.getDefault().copyValues(y);
            double weight = rbf.evaluate(v1, v2);
            System.out.println( "X: " + x + ", y: " + y + " weight: " + weight );
            return weight;
        }

        @Override
        public RadialBasisScalarKernel clone()
        {
            return new RadialBasisScalarKernel();
        }
    }
    
}
