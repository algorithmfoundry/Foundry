/*
 * File:                PolynomialFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 4, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class PolynomialFunctionTest extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random(1234567);
    
    /**
     * 
     * @param testName
     */
    public PolynomialFunctionTest( String testName )
    {
        super( testName );
    }

    /**
     * 
     * @return
     */
    protected PolynomialFunction createInstance()
    {
        double exponent = (int) (4.0 * random.nextDouble() - 2.0);
        return new PolynomialFunction( exponent );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        double exponent = random.nextDouble();
        PolynomialFunction instance = new PolynomialFunction( exponent );
        PolynomialFunction clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getExponent(), clone.getExponent() );

    }

    /**
     * Test of convertToVector method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );

        double exponent = random.nextDouble();
        PolynomialFunction instance = new PolynomialFunction( exponent );
        Vector params = instance.convertToVector();
        assertEquals( 1, params.getDimensionality() );
        assertEquals( exponent, params.getElement( 0 ) );

    }

    /**
     * Test of convertFromVector method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testConvertFromVector()
    {

        System.out.println( "convertFromVector" );

        double exponent = random.nextDouble();
        PolynomialFunction instance = new PolynomialFunction( exponent );
        assertEquals( exponent, instance.getExponent() );
        Vector params = instance.convertToVector();
        assertEquals( 1, params.getDimensionality() );
        assertEquals( exponent, params.getElement( 0 ) );

        Vector p2 = params.clone();
        assertNotSame( p2, params );
        assertEquals( p2, params );

        double e2 = exponent + 1;
        p2.setElement( 0, e2 );
        instance.convertFromVector( p2 );
        assertEquals( e2, instance.getExponent() );

    }

    /**
     * Test of differentiate method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testDifferentiate()
    {
        System.out.println( "differentiate" );

        for (int i = 0; i < 10; i++)
        {
            double e1 = random.nextDouble() * 10 + 1;
            PolynomialFunction p1 = new PolynomialFunction( e1 );

            final double EPS = 1e-5;

            System.out.println( p1.toString() );

            double x1 = random.nextDouble();
            double d1 = e1 * Math.pow( x1, e1 - 1.0 );
            assertEquals( d1, p1.differentiate( x1 ), EPS );
        }
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );

        final double EPS = 1e-5;
        for (int i = 0; i < 10; i++)
        {
            PolynomialFunction p1 = new PolynomialFunction( 0.0 );
            assertEquals( 1.0, p1.evaluate( random.nextDouble() ) );

            PolynomialFunction p2 = new PolynomialFunction( 1.0 );
            double x2 = random.nextDouble();
            assertEquals( x2, p2.evaluate( x2 ) );

            PolynomialFunction p3 = new PolynomialFunction( 10 * random.nextDouble() - 5 );
            double x3 = 10 * random.nextDouble();
            assertEquals( Math.pow( x3, p3.getExponent() ), p3.evaluate( x3 ), EPS );
        }

    }

    /**
     * Test of computeParameterGradient method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testComputeParameterGradient()
    {
        System.out.println( "computeParameterGradient" );

        PolynomialFunction p2 = new PolynomialFunction( random.nextDouble() );
        double x = random.nextDouble() * 20 - 10;
        Vector g = VectorFactory.getDefault().createVector( 1 );
        double v = Math.log( x ) * p2.evaluate( x );
        g.setElement( 0, v );

        final double EPS = 1e-5;
        Vector ghat = p2.computeParameterGradient( x );
        assertEquals( g.getDimensionality(), ghat.getDimensionality() );
        assertTrue( g.equals( p2.computeParameterGradient( x ), EPS ) );

    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.Regression.
     */
    public void testRegressionLearn()
    {
        System.out.println( "static Regression.learn" );


        Collection<InputOutputPair<Double, Double>> d1 =
            new LinkedList<InputOutputPair<Double, Double>>();
        int o1 = 5;
        VectorFunctionLinearDiscriminant<Double> t1 = new VectorFunctionLinearDiscriminant<Double>(
            new ScalarBasisSet<Double>( PolynomialFunction.createPolynomials( 1.0, 2.0, 3.0, 4.0, 5.0 ) ),
            new LinearDiscriminantWithBias( VectorFactory.getDefault().createUniformRandom( o1, -10, 10, random ), random.nextGaussian() ) );
        double r = 5;
        for (int n = 0; n < 10; n++)
        {
            double x = random.nextDouble() * 2 * r - r;
            double y = t1.evaluate( x );
            d1.add( new DefaultInputOutputPair<Double, Double>( x, y ) );
        }

        VectorFunctionLinearDiscriminant<Double> e1 =
            PolynomialFunction.Regression.learn( o1, d1 );

        System.out.println( "Target:     " + t1.convertToVector() );
        System.out.println( "Regression: " + e1.convertToVector() );

        double EPS = 1e-5;
        assertTrue( t1.convertToVector().equals( e1.convertToVector(), EPS ) );

    }

    /**
     * Test of getExponent method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testGetExponent()
    {
        System.out.println( "getExponent" );

        double exponent = random.nextDouble();
        PolynomialFunction instance = new PolynomialFunction( exponent );
        assertEquals( exponent, instance.getExponent() );

    }

    /**
     * Test of setExponent method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testSetExponent()
    {
        System.out.println( "setExponent" );

        double exponent = random.nextDouble();
        PolynomialFunction instance = new PolynomialFunction( exponent );
        assertEquals( exponent, instance.getExponent() );

        double e2 = exponent + random.nextDouble();
        instance.setExponent( e2 );
        assertEquals( e2, instance.getExponent() );
    }

    /**
     * Test of createPolynomials method, of class gov.sandia.cognition.learning.util.function.PolynomialFunction.
     */
    public void testCreatePolynomials()
    {
        System.out.println( "createPolynomials" );

        int num = 10;
        double[] polynomialExponents = new double[num];
        for (int i = 0; i < num; i++)
        {
            polynomialExponents[i] = random.nextDouble();
        }
        ArrayList<PolynomialFunction> result = PolynomialFunction.createPolynomials( polynomialExponents );

        assertEquals( num, result.size() );
        for (int i = 0; i < num; i++)
        {
            assertEquals( polynomialExponents[i], result.get( i ).getExponent() );
        }

    }

    /**
     * 
     * @return
     */
    protected PolynomialFunction createFunction()
    {
        return this.createInstance();
    }

    /**
     * 
     */
    public void testEvaluateScalar()
    {
        System.out.println( "evaluateScalar" );
        this.testEvaluate();
    }

    /**
     * Test of differentiate method, of class gov.sandia.isrc.learning.util.function.ElementWiseDifferentiableFunction.
     */
    public void testDifferentiateScalar()
    {

        System.out.println( "differentiate (Scalar)" );

        // Just perform a Reimann sum integral over part of the space using
        // the mid-point rule
        for (int n = 0; n < 10; n++)
        {
            double min = random.nextDouble() * 4.0;
            double max = min + random.nextDouble() * 4.0;
            double step = 1e-3;
            double sum = 0.0;
            PolynomialFunction f = this.createFunction();
            int num = 0;
            for (double x = min; x < max; x += step)
            {
                sum += f.differentiate( x + (0.5 * step) );
                num++;
            }
            double estimated = sum * step;

            double fmin = f.evaluate( min );
            double fmax = f.evaluate( max );
            double expected = fmax - fmin;
            System.out.printf( "f(%f) = %f, f(%f) = %f, difference = %f, estimate = %f\n", min, fmin, max, fmax, expected, estimated );

            assertEquals( expected, estimated, 1 / Math.log( num ) );

        }

    }

    /**
     * Test of differentiate method, of class gov.sandia.isrc.learning.util.function.ElementWiseDifferentiableFunction.
     */
    public void testQuadtraticFormula()
    {
        
        final double EPS = 1e-5;
        
        // Let's find the roots of:
        // y(x) = 2*(x-1)*(x-2) = 2*(2-3*x+x^2) = 4-6x+2x^2
        // The roots should be {1,2}
        double q0 = 4.0;
        double q1 = -6.0;
        double q2 = 2.0;
        Double[] r1 = PolynomialFunction.Quadratic.roots( q0, q1, q2 );
        assertEquals( 2, r1.length );
        assertEquals( 1.0, r1[0], EPS );
        assertEquals( 2.0, r1[1], EPS );

        assertEquals( 0.0, PolynomialFunction.Quadratic.evaluate( r1[0], q0, q1, q2 ), EPS );
        assertEquals( 0.0, PolynomialFunction.Quadratic.evaluate( r1[1], q0, q1, q2 ), EPS );
        assertEquals( 24.0, PolynomialFunction.Quadratic.evaluate( -2.0, q0, q1, q2 ), EPS );
        
        // Let's find the roots of:
        // y(x) = (x-1)*(x-1) = 1-2x+x^2
        Double[] r2 = PolynomialFunction.Quadratic.roots( 1.0, -2.0, 1.0 );
        assertEquals( 1, r2.length );
        assertEquals( 1.0, r2[0], EPS );

        // Here's a complex root:
        // y(x) = 2 + x + x^2
        Double[] r6 = PolynomialFunction.Quadratic.roots( 
            2.0, 1.0, 1.0 );
        assertEquals( 0, r6.length );
        // Let's find the roots of:
        //  y(x) = 8+2x
        Double[] r3 = PolynomialFunction.Quadratic.roots( 8.0, 2.0, 0.0 );
        assertEquals( 1, r3.length );
        assertEquals( -4.0, r3[0], EPS );
        
        // This shouldn't return any roots
        Double[] r4 = PolynomialFunction.Quadratic.roots( 8.0, 0.0, 0.0 );
        assertEquals( 0, r4.length );

        // This shouldn't return roots either
        Double[] r5 = PolynomialFunction.Quadratic.roots( 0.0, 0.0, 0.0 );
        assertEquals( 0, r5.length );
        
    }
    
    /**
     * Test of differentiate method, of class gov.sandia.isrc.learning.util.function.ElementWiseDifferentiableFunction.
     */
    public void testCubicStationaryPoint()
    {
        final double EPS = 1e-5;
        
        // Let's find the stationary points of
        // y(x) = (x-1)*(x-2)*(x+2) = (2 - 3*x + x^2) * (2+x)
        // y(x) = (4 - 6*x + 2*x^2) + (2*x - 3*x^2 + x^3)
        // y(x) = 4 - 4*x - x^2 + x^3
        double q0 = 4.0;
        double q1 = -4.0;
        double q2 = -1.0;
        double q3 = 1.0;
        Double[] stat = PolynomialFunction.Cubic.stationaryPoints( q0, q1, q2, q3 );
        assertEquals( 2, stat.length );
        assertEquals( -0.86851709182, stat[0], EPS );
        assertEquals( 1.53519, stat[1], EPS );
        
        assertEquals( -0.87942, PolynomialFunction.Cubic.evaluate( stat[1], q0, q1, q2, q3), EPS );
        
    }

    /**
     * Test of PolynomialFunction.Linear class
     */
    public void testLinear()
    {
        System.out.println( "Linear test" );
        
        // f(x) = 1-2x
        // dx(x) = -2
        PolynomialFunction.Linear f1 = new PolynomialFunction.Linear( 1.0, -2.0 );
        
        assertEquals( 1.0, f1.getQ0() );
        assertEquals( -2.0, f1.getQ1() );
        
        final double EPS = 1e-5;
        assertEquals( 1.0, f1.evaluate( 0.0 ), EPS );
        assertEquals( -1.0, f1.evaluate( 1.0 ), EPS );
        assertEquals( 5.0, f1.evaluate( -2.0 ), EPS );
        
        assertEquals( -2.0, f1.differentiate( 0.0 ), EPS );
        assertEquals( -2.0, f1.differentiate( 2.0 ), EPS );
        assertEquals( -2.0, f1.differentiate( -3.0 ), EPS );
        
        assertEquals( 0, f1.stationaryPoints().length );
        Double[] roots = f1.roots();
        assertEquals( 1, roots.length );
        assertEquals( 0.5, roots[0], EPS );
        
    }
    
    /**
     * Test the linear fit
     */
    public void testLinearFit2Points()
    {
        System.out.println( "Linear fit 2-points" );
        
        double q0 = -1.0;
        double q1 = 3.0;
        PolynomialFunction.Linear f1 = new PolynomialFunction.Linear( q0, q1 );
        
        System.out.println( "f1:  " + f1 );
        double x0 = 1.0;
        InputOutputPair<Double,Double> p0 = new DefaultInputOutputPair<Double, Double>( x0, f1.evaluate( x0 ) );
        
        double x1 = 10.0;
        InputOutputPair<Double,Double> p1 = new DefaultInputOutputPair<Double, Double>( x1, f1.evaluate( x1 ) );
        
        PolynomialFunction.Linear fhat = f1.fit( p0, p1 );
        
        final double EPS = 1e-5;
        assertEquals( f1.getQ0(), fhat.getQ0(), EPS );
        assertEquals( f1.getQ1(), fhat.getQ1(), EPS );
        
        System.out.println( "fhat: " + fhat );
        
        try
        {
            PolynomialFunction.Linear.fit( p0, p0 );
            fail( "Points are collinear and should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test the linear fit
     */
    public void testLinearFit1Point()
    {
        System.out.println( "Linear fit 1-point" );
        
        double q0 = -1.0;
        double q1 = 3.0;
        PolynomialFunction.Linear f1 = new PolynomialFunction.Linear( q0, q1 );
        
        System.out.println( "f1:  " + f1 );
        double x0 = 1.0;
        InputOutputSlopeTriplet p0 = new InputOutputSlopeTriplet( x0, f1.evaluate( x0 ), f1.differentiate( x0 ) );
        
        PolynomialFunction.Linear fhat1 = PolynomialFunction.Linear.fit( p0 );
        System.out.println( "fhat1: " + fhat1 );
        assertEquals( q0, fhat1.getQ0() );
        assertEquals( q1, fhat1.getQ1() );
        
        double x1 = 10.0;
        InputOutputSlopeTriplet p1 = new InputOutputSlopeTriplet( x1, f1.evaluate( x1 ), f1.differentiate( x1 ) );
        PolynomialFunction.Linear fhat2 = PolynomialFunction.Linear.fit( p1 );
        System.out.println( "fhat2: " + fhat2 );
        assertEquals( q0, fhat2.getQ0() );
        assertEquals( q1, fhat2.getQ1() );
    }    
    
    /**
     * Test of differentiate method, of class gov.sandia.isrc.learning.util.function.ElementWiseDifferentiableFunction.
     */
    public void testQuadratic()
    {
        System.out.println( "Quadratic test" );
        
        // f(x) = 4 + 3x + 2x^2
        // dx(x) = 3 + 4x
        PolynomialFunction.Quadratic f1 = new PolynomialFunction.Quadratic(
            4.0, 3.0, 2.0 );
        
        assertEquals( 4.0, f1.getQ0() );
        assertEquals( 3.0, f1.getQ1() );
        assertEquals( 2.0, f1.getQ2() );
        
        final double EPS = 1e-5;
        
        assertEquals( 4.0, f1.evaluate( 0.0 ), EPS );
        assertEquals( 3.0, f1.differentiate( 0.0 ), EPS );

        assertEquals( 4.0+3.0+2.0, f1.evaluate( 1.0 ), EPS );
        assertEquals( 3.0+4.0, f1.differentiate( 1.0 ), EPS );
        
        assertEquals( 4.0-3.0+2.0, f1.evaluate( -1.0 ), EPS );
        assertEquals( 3.0-4.0, f1.differentiate( -1.0 ), EPS );
        
        assertEquals( 4.0+6.0+8.0, f1.evaluate( 2.0 ), EPS );
        assertEquals( 3.0+8.0, f1.differentiate( 2.0 ), EPS );
        
        Double[] p = f1.stationaryPoints();
        assertEquals( 1, p.length );
        assertEquals( -3.0/4.0, p[0], EPS );
        
        assertEquals( 0, f1.roots().length );
        
    }
    
    public void testCubic()
    {
        System.out.println( "Cubic test" );

        // f(x) = 4 + 3x - 2x^2 + 2x^3
        // dx(x) = 3 - 4x + 6x^2
        PolynomialFunction.Cubic f1 = new PolynomialFunction.Cubic(
            4.0, 3.0, -2.0, 2.0 );
        
        assertEquals( 4.0, f1.getQ0() );
        assertEquals( 3.0, f1.getQ1() );
        assertEquals( -2.0, f1.getQ2() );
        assertEquals( 2.0, f1.getQ3() );
        
        final double EPS = 1e-5;
        
        assertEquals( 4.0, f1.evaluate( 0.0 ), EPS );
        assertEquals( 3.0, f1.differentiate( 0.0 ), EPS );

        assertEquals( 4.0+3.0-2.0+2.0, f1.evaluate( 1.0 ), EPS );
        assertEquals( 3.0-4.0+6.0, f1.differentiate( 1.0 ), EPS );
        
        assertEquals( 4.0-3.0-2.0-2.0, f1.evaluate( -1.0 ), EPS );
        assertEquals( 3.0+4.0+6.0, f1.differentiate( -1.0 ), EPS );
        
        assertEquals( 4.0+6.0-8.0+16.0, f1.evaluate( 2.0 ), EPS );
        assertEquals( 3.0-8.0+24.0, f1.differentiate( 2.0 ), EPS );
        
        Double[] p = f1.stationaryPoints();
        assertEquals( 0, p.length );
        
        try
        {
            f1.roots();
            fail( "Cubic roots should not be implemented!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }
    
    public void testQuadratic3PointFit()
    {
        System.out.println( "Quadratic 3-point fit" );
        
        // f(x) = 4 + 3x + 2x^2
        // dx(x) = 3 + 4x
        double q0 = 4.0;
        double q1 = 3.0;
        double q2 = 2.0;
        PolynomialFunction.Quadratic f1 = new PolynomialFunction.Quadratic(
            q0, q1, q2 );
        
        double x0 = 1.0;
        InputOutputPair<Double,Double> p0 = new DefaultInputOutputPair<Double, Double>( x0, f1.evaluate( x0 ) );
        
        double x1 = 2.0;
        InputOutputPair<Double,Double> p1 = new DefaultInputOutputPair<Double, Double>( x1, f1.evaluate( x1 ) );

        double x2 = 5.0;
        InputOutputPair<Double,Double> p2 = new DefaultInputOutputPair<Double, Double>( x2, f1.evaluate( x2 )  );
        
        PolynomialFunction.Quadratic fhat = PolynomialFunction.Quadratic.fit(
            p0, p1, p2 );
        
        System.out.println( "fhat: " + fhat );
        
        final double EPS = 1e-5;
        assertEquals( q0, fhat.getQ0(), EPS );
        assertEquals( q1, fhat.getQ1(), EPS );
        assertEquals( q2, fhat.getQ2(), EPS );
        
    }
   
    /**
     * Test of the 2-point quadratic fit
     */
    public void testQuadratic2PointFit()
    {
        System.out.println( "Quadratic 2-point fit" );
        
        // f(x) = 4 + 3x + 2x^2
        // dx(x) = 3 + 4x
        double q0 = 4.0;
        double q1 = 3.0;
        double q2 = 2.0;
        PolynomialFunction.Quadratic f1 = new PolynomialFunction.Quadratic(
            q0, q1, q2 );
        
        double x0 = -1.0;
        InputOutputSlopeTriplet p0 = new InputOutputSlopeTriplet(
            x0, f1.evaluate( x0 ), f1.differentiate( x0 ) );
        
        double x1 = 3.0;
        InputOutputPair<Double,Double> p1 = new DefaultInputOutputPair<Double, Double>(
            x1, f1.evaluate( x1 ) );
        
        PolynomialFunction.Quadratic fhat = PolynomialFunction.Quadratic.fit(
            p0, p1 );
        
        System.out.println( "fhat: " + fhat );
        
        final double EPS = 1e-5;
        assertEquals( q0, fhat.getQ0(), EPS );
        assertEquals( q1, fhat.getQ1(), EPS );
        assertEquals( q2, fhat.getQ2(), EPS );        
        
    }

    /**
     * Test of the 2-point quadratic fit
     */
    public void testCubic2PointFit()
    {
        System.out.println( "Cubic 2-point fit" );
        
        // f(x) = 4 + 3x + 2x^2
        // dx(x) = 3 + 4x
        double q0 = 4.0;
        double q1 = 3.0;
        double q2 = -2.0;
        double q3 = 0.5;
        PolynomialFunction.Cubic f1 = new PolynomialFunction.Cubic(
            q0, q1, q2, q3 );
        
        double x0 = -1.0;
        InputOutputSlopeTriplet p0 = new InputOutputSlopeTriplet(
            x0, f1.evaluate( x0 ), f1.differentiate( x0 ) );
        
        double x1 = 2.5;
        InputOutputSlopeTriplet p1 = new InputOutputSlopeTriplet(
            x1, f1.evaluate( x1 ), f1.differentiate( x1 ) );

        PolynomialFunction.Cubic fhat = PolynomialFunction.Cubic.fit( p0, p1 );
        
        System.out.println( "fhat: " + fhat );        
        
    }
    
}
