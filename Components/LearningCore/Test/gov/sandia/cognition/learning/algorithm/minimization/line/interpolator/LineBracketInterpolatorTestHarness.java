/*
 * File:                LineBracketInterpolatorTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 27, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization.line.interpolator;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class LineBracketInterpolator
 * @param <EvaluatorType> Type of Evaluator to consider
 * @author Kevin R. Dixon
 */
public abstract class LineBracketInterpolatorTestHarness<EvaluatorType extends Evaluator<Double,Double>> 
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class LineBracketInterpolatorTestHarness
     * @param testName name of this test
     */
    public LineBracketInterpolatorTestHarness(
        String testName)
    {
        super(testName);
    }

    public Random random = new Random( 0 );
    
    /**
     * Returns a new instance of the LineBracketInterpolator
     * @return
     */
    abstract public LineBracketInterpolator<EvaluatorType> createInstance();
    
    public static class CosineFunction
        extends AbstractDifferentiableUnivariateScalarFunction
    {

        @Override
        public CosineFunction clone()
        {
            return new CosineFunction();
        }

        public double evaluate(
            double input )
        {
            return Math.cos( input );
        }

        public double differentiate(
            double input )
        {
            return -Math.sin( input );
        }
    }
    
    public void testTolerance()
    {
        LineBracketInterpolator<EvaluatorType> instance = this.createInstance();
        assertTrue( instance.getTolerance() > 0.0 );        
    }
    
    @SuppressWarnings("unchecked")
    public void testCosine()
    {
        
        System.out.println( "Test Consine" );
        
        CosineFunction f = new CosineFunction();
        
        final int num = 100;
        final double range = 10.0;
        for( int n = 0; n < num; n++ )
        {
            LineBracketInterpolator<EvaluatorType> instance = this.createInstance();            
            LineBracket bracket = new LineBracket();
            double minx = range * (2.0*random.nextDouble()-1.0);
            double maxx = range * (2.0*random.nextDouble()-1.0);
            if( minx > maxx )
            {
                double temp = minx;
                minx = maxx;
                maxx = temp;
            }
            
            
            double x0 = range * (2.0*random.nextDouble()-1.0);
            bracket.setLowerBound( new InputOutputSlopeTriplet(
                x0, f.evaluate( x0 ), f.differentiate( x0 ) ) );
        
            // Nobody can interpolate using a single point
            assertFalse( instance.hasSufficientPoints( bracket ) );
            
            try
            {
                instance.findMinimum( bracket, minx, maxx, (EvaluatorType) f );
                fail( "Cannot interpolate with a single point" );
            }
            catch (Exception e)
            {
                //System.out.println( "Good: " + e );
            }
            
            double x1 = range * (2.0*random.nextDouble()-1.0);
            bracket.setUpperBound( new InputOutputSlopeTriplet(
                x1, f.evaluate( x1 ), f.differentiate( x1 ) ) );
            
            if( instance.hasSufficientPoints( bracket ) )
            {
                @SuppressWarnings("unchecked")
                double r2 = instance.findMinimum( bracket, minx, maxx, (EvaluatorType) f );
                assertTrue( minx <= r2 );
                assertTrue( r2 <= maxx );
            }
            
            double x2 = range * (2.0*random.nextDouble()-1.0);
            bracket.setOtherPoint( new InputOutputSlopeTriplet(
                x2, f.evaluate( x2 ), f.differentiate( x2 ) ) );

            // We have to be able to interpolate with three points
            assertTrue( instance.hasSufficientPoints( bracket ) );
            @SuppressWarnings("unchecked")
            double r3 = instance.findMinimum( bracket, minx, maxx, (EvaluatorType) f );
            assertTrue( minx <= r3 );
            assertTrue( r3 <= maxx );
            
            // Let's make sure we barf when all points are the same
            bracket.setUpperBound( bracket.getLowerBound() );
            bracket.setOtherPoint( bracket.getLowerBound() );
            try
            {
                instance.findMinimum( bracket, minx, maxx, (EvaluatorType) f );
                fail( "Bracket points are collinear" );
            }
            catch (Exception e)
            {
                //System.out.println( "Good: " + e );
            }
            
        }
        
    }
        
}
