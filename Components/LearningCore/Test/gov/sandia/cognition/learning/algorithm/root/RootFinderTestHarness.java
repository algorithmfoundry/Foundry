/*
 * File:                RootFinderTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.root;

import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorTestHarness.CosineFunction;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class RootFinderTestHarness
 * @author Kevin R. Dixon
 */
public abstract class RootFinderTestHarness
    extends TestCase
{
    
    /**
     * Random number generator.
     */
    public Random random = new Random( 1 );    

    /**
     * Entry point for JUnit tests for class RootFinderTestHarness
     * @param testName name of this test
     */
    public RootFinderTestHarness(
        String testName)
    {
        super(testName);
    }

    public static abstract class RootFinderFunction
        extends AbstractUnivariateScalarFunction
        implements DifferentiableEvaluator<Double,Double,Double>
    {

        public int NUM_EVALUATIONS;

        public DifferentiableEvaluator<Double,Double,Double> f;

        public RootFinderFunction(
            DifferentiableEvaluator<Double,Double,Double> f )

        {
            this.f = f;
            NUM_EVALUATIONS = 0;
        }

        public double evaluate(
            double input)
        {
            NUM_EVALUATIONS++;
            return this.f.evaluate(input);
        }

        public Double differentiate(
            Double input)
        {
            return this.f.differentiate(input);
        }

    }

    public static class AtanRootFunction
        extends RootFinderFunction
    {
        public AtanRootFunction()
        {
            super( new AtanFunction() );
        }
    }
    
    public static class CosineRootFunction
        extends RootFinderFunction
    {
        public CosineRootFunction()
        {
            super( new CosineFunction() );
        }
    }

    public static class CubicRootFunction
        extends RootFinderFunction
    {
        public static class CubicFunction
            extends AbstractUnivariateScalarFunction
            implements DifferentiableEvaluator<Double,Double,Double>
        {
            public double evaluate(
                double input )
            {
                double x = input;
                return (x-1.0)*(x+2.0)*(x-3.0);
            }

            public Double differentiate(
                Double input)
            {
                double x = input;
                return 3*x*x - 4*x - 5;
            }

        }

        public CubicRootFunction()
        {
            super( new CubicFunction() );
        }
    }

    /**
     * Creates a new RootFinder instance to test.
     * @return
     */
    public abstract RootFinder createInstance();

    public void testRootFunction(
        RootFinderFunction f,
        double toleranceMultiplier )
    {
        String name = f.getClass().getSimpleName();
        System.out.println( "Testing " + name );
        int num = 100;
        ArrayList<Double> evals = new ArrayList<Double>();
        for( int n = 0; n < num; n++ )
        {
            RootFinder instance = this.createInstance();
            instance.setInitialGuess( random.nextGaussian() );
            f.NUM_EVALUATIONS = 0;
            InputOutputPair<Double,Double> root = instance.learn( f );
            assertEquals( f.evaluate(root.getInput()), root.getOutput() );
            assertEquals( 0.0, root.getOutput(), instance.getTolerance()*toleranceMultiplier );
            evals.add( (double) f.NUM_EVALUATIONS );
        }

        StudentTConfidence ttest = new StudentTConfidence();
        final double confidence = 0.95;
        System.out.println( name + " Evals: " +
            ttest.computeConfidenceInterval( evals, confidence ) );

    }


    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        RootFinder instance = this.createInstance();
        RootFinder clone = (RootFinder) instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getInitialGuess(), clone.getInitialGuess() );
        assertEquals( instance.getTolerance(), clone.getTolerance() );
    }

    /**
     * Test cosine
     */
    public void testCosine()
    {
        System.out.println( "Cosine" );
        this.testRootFunction( new CosineRootFunction(), 100.0 );
    }
    
    /**
     * Atan()
     */
    public void testAtan()
    {
        System.out.println( "Atan" );
        this.testRootFunction( new AtanRootFunction(), 1.0 );
    }
    
    /**
     * Cubic()
     */
    public void testCubic()
    {
        System.out.println( "Cubic" );
        this.testRootFunction( new CubicRootFunction(), 1000.0 );
    }
    

    /**
     * Test of getResult method, of class RootFinderBisection.
     */
    public void testGetResult()
    {
        System.out.println( "getResult" );
        RootFinder instance = this.createInstance();
        assertNull( instance.getResult() );
        
        AtanFunction f = new AtanFunction();
        instance.learn( f );
        assertNotNull( instance.getResult() );
        
    }

    /**
     * Test of getTolerance method, of class RootFinderBisection.
     */
    public void testGetTolerance()
    {
        System.out.println( "getTolerance" );
        RootFinder instance = this.createInstance();
        assertEquals( AbstractBracketedRootFinder.DEFAULT_TOLERANCE, instance.getTolerance() );
    }

    /**
     * Test of setTolerance method, of class RootFinderBisection.
     */
    public void testSetTolerance()
    {
        System.out.println( "setTolerance" );
        double tolerance = random.nextDouble();
        RootFinder instance = this.createInstance();
        instance.setTolerance( tolerance );
        assertEquals( tolerance, instance.getTolerance() );
        
        instance.setTolerance( 0.0 );
        assertEquals( 0.0, instance.getTolerance() );
        
        try
        {
            instance.setTolerance( -tolerance );
            fail( "Tolerance must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getInitialGuess method, of class RootFinderBisection.
     */
    public void testGetInitialGuess()
    {
        System.out.println( "getInitialGuess" );
        RootFinder instance = this.createInstance();
        assertEquals( 0.0, instance.getInitialGuess() );
        
        double guess = random.nextGaussian();
        instance.setInitialGuess( guess );
        assertEquals( guess, instance.getInitialGuess() );
    }

    /**
     * Test of setInitialGuess method, of class RootFinderBisection.
     */
    public void testSetInitialGuess()
    {
        System.out.println( "setInitialGuess" );
        RootFinder instance = this.createInstance();
        
        double guess = random.nextGaussian();
        instance.setInitialGuess( guess );
        assertEquals( guess, instance.getInitialGuess() );
    }

    /**
     * Test of getBracketer method, of class RootFinderBisection.
     */
    public void testGetBracketer()
    {
        System.out.println( "getBracketer" );
        RootFinder instance = this.createInstance();
        if( instance instanceof AbstractBracketedRootFinder )
        {
            assertSame( AbstractBracketedRootFinder.DEFAULT_ROOT_BRACKETER,
                ((AbstractBracketedRootFinder) instance).getBracketer() );
        }
    }

    /**
     * Test of setBracketer method, of class RootFinderBisection.
     */
    public void testSetBracketer()
    {
        System.out.println( "setBracketer" );
        RootFinder base = this.createInstance();
        if( base instanceof AbstractBracketedRootFinder )
        {
            AbstractBracketedRootFinder instance = (AbstractBracketedRootFinder) base;
            RootBracketer b = instance.getBracketer();
            assertNotNull( b );
            instance.setBracketer( null );
            assertNull( instance.getBracketer() );
            instance.setBracketer( b );
            assertSame( b, instance.getBracketer() );
        }
    }

    /**
     * Test of getRootBracket method, of class RootFinderBisection.
     */
    public void testGetRootBracket()
    {
        System.out.println( "getRootBracket" );
        RootFinder base = this.createInstance();
        if( base instanceof AbstractBracketedRootFinder )
        {
            AbstractBracketedRootFinder instance = (AbstractBracketedRootFinder) base;
            assertNull( instance.getRootBracket() );

            AtanFunction f = new AtanFunction();
            instance.learn( f );
            assertNotNull( instance.getRootBracket() );
        }
    }

    /**
     * Test of setRootBracket method, of class RootFinderBisection.
     */
    public void testSetRootBracket()
    {
        System.out.println( "setRootBracket" );
        
        RootFinder base = this.createInstance();
        if( base instanceof AbstractBracketedRootFinder )
        {
            AbstractBracketedRootFinder instance = (AbstractBracketedRootFinder) base;
            assertNull( instance.getRootBracket() );

            AtanFunction f = new AtanFunction();
            instance.learn( f );
            assertNotNull( instance.getRootBracket() );

            instance.setRootBracket( null );
            assertNull( instance.getRootBracket() );
        }
    }

}
