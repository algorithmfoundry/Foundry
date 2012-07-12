/*
 * File:                KernelOnlineRegressionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: KernelBasedIterativeRegression
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelBasedIterativeRegressionTest
    extends TestCase
{

    public KernelBasedIterativeRegressionTest(
        String testName )
    {
        super( testName );
    }

    public void testConstants()
    {
        assertEquals( 100, KernelBasedIterativeRegression.DEFAULT_MAX_ITERATIONS );
        assertEquals( 10.0, KernelBasedIterativeRegression.DEFAULT_MIN_SENSITIVITY );
    }

    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        KernelBasedIterativeRegression<?> instance = new KernelBasedIterativeRegression<String>();

        KernelBasedIterativeRegression<?> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }


    public void testConstructors()
    {
        KernelBasedIterativeRegression<Vector> instance = new KernelBasedIterativeRegression<Vector>();
        assertNull( instance.getKernel() );
        assertEquals( KernelBasedIterativeRegression.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
        assertEquals( KernelBasedIterativeRegression.DEFAULT_MIN_SENSITIVITY, instance.getMinSensitivity() );

        PolynomialKernel kernel = new PolynomialKernel( 4, 7.0 );
        instance = new KernelBasedIterativeRegression<Vector>( kernel );
        assertSame( kernel, instance.getKernel() );
        assertEquals( KernelBasedIterativeRegression.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
        assertEquals( KernelBasedIterativeRegression.DEFAULT_MIN_SENSITIVITY, instance.getMinSensitivity() );


        double minSensitivity = Math.random();
        instance = new KernelBasedIterativeRegression<Vector>( kernel, minSensitivity );
        assertSame( kernel, instance.getKernel() );
        assertEquals( KernelBasedIterativeRegression.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
        assertEquals( minSensitivity, instance.getMinSensitivity() );

        int maxIterations = KernelBasedIterativeRegression.DEFAULT_MAX_ITERATIONS + 10;
        instance = new KernelBasedIterativeRegression<Vector>( kernel, minSensitivity, maxIterations );
        assertSame( kernel, instance.getKernel() );
        assertEquals( maxIterations, instance.getMaxIterations() );
        assertEquals( minSensitivity, instance.getMinSensitivity() );
    }

    public void testLearn()
    {
        double epsilon = 0.0001;
        Kernel<? super Vector> kernel = LinearKernel.getInstance();
        double minSensitivity = 0.2;
        KernelBasedIterativeRegression<Vector> instance =
            new KernelBasedIterativeRegression<Vector>( kernel, minSensitivity );

        // This is the function f(x) = 2*x - 2 plus some noise that is at most
        // 0.1 off.
        double[][] values = new double[][]{
            new double[]{0.00, -2.00},
            new double[]{2.00, 2.00},
            new double[]{3.00, 4.10},
            new double[]{3.50, 5.00},
            new double[]{4.00, 5.90},
            new double[]{6.00, 10.10},
            new double[]{8.00, 13.90},
            new double[]{9.00, 16.00}};

        ArrayList<InputOutputPair<Vector, Double>> data =
            new ArrayList<InputOutputPair<Vector, Double>>();
        VectorFactory<?> factory = VectorFactory.getDefault();

        for (int i = 0; i < values.length; i++)
        {
            double input = values[i][0];
            double output = values[i][1];
            data.add( new DefaultInputOutputPair<Vector, Double>(
                factory.copyValues( input ), output ) );
        }

        Evaluator<? super Vector,Double> learned = instance.learn( data );
        assertEquals( 0, instance.getErrorCount() );
        assertEquals( learned, instance.getResult() );

        for (InputOutputPair<Vector, Double> example : data)
        {
            double actual = example.getOutput();
            double predicted = learned.evaluate( example.getInput() );
            assertEquals( actual, predicted, minSensitivity + epsilon );
        }

        instance.setMaxIterations( instance.getIteration() / 2 );
        learned = instance.learn( data );
        assertTrue( instance.getErrorCount() > 0 );

        data = new ArrayList<InputOutputPair<Vector, Double>>();
        learned = instance.learn( data );
        assertNull( learned );

        data.add( new DefaultInputOutputPair<Vector, Double>( new Vector2( 4.0, 7.0 ), 4.7 ) );
        learned = instance.learn( data );
        assertEquals( 4.7, learned.evaluate( new Vector2( 4.0, 7.0 ) ) );

        learned = instance.learn( null );
        assertNull( learned );
    }

    /**
     * Test of getKernel method, of class gov.sandia.cognition.learning.regression.KernelOnlineRegression.
     */
    public void testGetKernel()
    {
        this.testSetKernel();
    }

    /**
     * Test of setKernel method, of class gov.sandia.cognition.learning.regression.KernelOnlineRegression.
     */
    public void testSetKernel()
    {
        KernelBasedIterativeRegression<Vector> instance = new KernelBasedIterativeRegression<Vector>();
        assertNull( instance.getKernel() );

        Kernel<? super Vector> kernel = LinearKernel.getInstance();
        instance.setKernel( kernel );
        assertSame( kernel, instance.getKernel() );

        kernel = new PolynomialKernel( 4, 7.0 );
        instance.setKernel( kernel );
        assertSame( kernel, instance.getKernel() );

        instance.setKernel( null );
        assertNull( instance.getKernel() );
    }

    /**
     * Test of getResult method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetResult()
    {
    // Tested by learn.
    }

    /**
     * Test of getErrorCount method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetErrorCount()
    {
    // Tested by learn.
    }

    /**
     * Test of getMinSensitivity method, of class gov.sandia.cognition.learning.regression.KernelOnlineRegression.
     */
    public void testGetMinSensitivity()
    {
        this.testSetMinSensitivity();
    }

    /**
     * Test of setMinSensitivity method, of class gov.sandia.cognition.learning.regression.KernelOnlineRegression.
     */
    public void testSetMinSensitivity()
    {
        KernelBasedIterativeRegression<Vector> instance = new KernelBasedIterativeRegression<Vector>();
        assertEquals( KernelBasedIterativeRegression.DEFAULT_MIN_SENSITIVITY, instance.getMinSensitivity() );

        double minSensitivity = 0.5;
        instance.setMinSensitivity( minSensitivity );
        assertEquals( minSensitivity, instance.getMinSensitivity() );

        minSensitivity = 100000.0;
        instance.setMinSensitivity( minSensitivity );
        assertEquals( minSensitivity, instance.getMinSensitivity() );


        minSensitivity = 0.0;
        instance.setMinSensitivity( minSensitivity );
        assertEquals( minSensitivity, instance.getMinSensitivity() );

        boolean exceptionThrown = false;
        try
        {
            instance.setMinSensitivity( -1.0 );
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue( exceptionThrown );
        }
    }

    /**
     * Get performance
     */
    public void testGetPerformance()
    {
        System.out.println( "getPerformance()" );

        KernelBasedIterativeRegression<Vector> instance = new KernelBasedIterativeRegression<Vector>();
        NamedValue<Integer> value = instance.getPerformance();
        assertNotNull( value );
        System.out.println( "Performance: " + value.getName() + " = " + value.getValue() );

    }

}
