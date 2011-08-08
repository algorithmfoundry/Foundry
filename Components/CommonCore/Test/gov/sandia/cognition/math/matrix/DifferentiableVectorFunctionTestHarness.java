/*
 * File:                DifferentiableVectorFunctionTestHarness.java
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
package gov.sandia.cognition.math.matrix;

import java.util.Random;
import junit.framework.*;

/**
 * Unit tests for DifferentiableVectorFunctionTestHarness
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class DifferentiableVectorFunctionTestHarness extends TestCase
{
    
    public DifferentiableVectorFunctionTestHarness(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DifferentiableVectorFunctionTestHarness.class);
        
        return suite;
    }

    /**
     * Test of differentiate method, of class gov.sandia.isrc.math.matrix.DifferentiableVectorFunction.
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
