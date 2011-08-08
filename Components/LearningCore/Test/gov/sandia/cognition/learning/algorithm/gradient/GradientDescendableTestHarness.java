/*
 * File:                GradientDescendableTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.learning.algorithm.gradient;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.DifferentiableVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import junit.framework.*;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;

/**
 * Unit tests for GradientDescendableTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class GradientDescendableTestHarness extends TestCase
{
    protected Random random = new Random();
    
    public GradientDescendableTestHarness(String testName)
    {
        super(testName);
    }

    public static double EPS = 1e-2;
    
    /**
     * Tests the first-order approximation power of the gradient!
     * @param instance 
     * @param input 
     */
    public static void testGradient(
        GradientDescendable instance,
        Vector input )
    {
        
        System.out.print( "testGradient: " + instance.getClass() );

        // Test the (presumed) gradient against the first-order approximation
        GradientDescendableApproximator approx = 
            new GradientDescendableApproximator( instance, 1e-5 );
        
        // Compare the approximated forward-difference gradient to
        // the "claimed" actual gradient.
        // The theory here is that I believe the approximation algorithm
        // produces reasonable results, so the actual should be close to the
        // forward-difference approximation
        Matrix gradApprox = approx.computeParameterGradient( input );
        Matrix gradEstimate = instance.computeParameterGradient( input );
        
        assertEquals( gradApprox.getNumRows(), gradEstimate.getNumRows() );
        assertEquals( gradApprox.getNumColumns(), gradEstimate.getNumColumns() );

         
        double norm = gradApprox.minus( gradEstimate ).normFrobenius() /
            (gradApprox.getNumRows()*gradApprox.getNumColumns());
        System.out.println( " Norm = " + norm );
        if( norm > EPS )
        {
            System.out.println( "Approximated Gradient:\n" + gradApprox );
            System.out.println( "Estimated Actual Gradient:\n" + gradEstimate );
            assertEquals( 0.0, norm, EPS );
        }
    }
    
    
    /**
     * Test of differentiate method, of class gov.sandia.cognition.math.matrix.DifferentiableVectorFunction.
     */
    public static void testDifferentiate(
        DifferentiableVectorFunction function,
        Vector x1,
        Random random)
    {
        System.out.println("testDifferentiate:" + function.getClass() );
        
        double small = 0.1;
        Vector delta = VectorFactory.getDefault().createUniformRandom( x1.getDimensionality(), -small, small, random );
        Vector x2 = x1.plus( delta );
        
        Vector y1 = function.evaluate( x1 );
        Vector y2 = function.evaluate( x2 );
        
        Matrix d1 = function.differentiate( x1 );
        assertEquals( y1.getDimensionality(), d1.getNumRows() );
        assertEquals( x1.getDimensionality(), d1.getNumColumns() );
        
        Vector y2hat = y1.plus( d1.times( x2.minus( x1 ) ) );
        
        System.out.println( "Error norm2: " + y2.minus( y2hat ).norm2() );
        
        assertTrue( y2.equals( y2hat, small ) );
        
    }
    
}
