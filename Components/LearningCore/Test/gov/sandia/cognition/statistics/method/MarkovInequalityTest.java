/*
 * File:                MarkovInequalityTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 4, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.method.MarkovInequality;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import java.util.Arrays;
import junit.framework.*;
import java.util.Collection;

/**
 *
 * @author Kevin R. Dixon
 */
public class MarkovInequalityTest extends TestCase
{
    
    public MarkovInequalityTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    /**
     * Test of computeConfidenceInterval method, of class gov.sandia.cognition.learning.util.statistics.MarkovInequality.
     */
    public void testComputeConfidenceInterval()
    {
        System.out.println("computeConfidenceInterval");

        MarkovInequality instance = new MarkovInequality();        
        
        // I got this example from Leon-Garcia, "Probability and Random 
        // Processes for Electrical Engineers", p.137, Example 3.40
        double h1 = 3.5;
        Collection<Double> d1 = Arrays.asList( h1 );
        double c1 = 1-0.389;
        
        ConfidenceInterval ci1 = instance.computeConfidenceInterval( d1, c1 );
        
        System.out.println( "CI1: " + ci1 );
        
        assertEquals( d1.size(), ci1.getNumSamples() );
        assertEquals( h1, ci1.getCentralValue() );
        assertEquals( 9.0, ci1.getUpperBound(), 1e-2 );
        assertEquals( -ci1.getUpperBound(), ci1.getLowerBound() );
        
        // I wrote this example by hand... I hope it works.
        Collection<Double> d2 = Arrays.asList( 3.0, -1.0, 2.0 );
        double c2 = 0.95;
        ConfidenceInterval ci2 = instance.computeConfidenceInterval( d2, c2 );
        
        final double EPS = 1e-5;
        assertEquals( d2.size(), ci2.getNumSamples() );
        assertEquals( 2.0, ci2.getCentralValue() );
        assertEquals( -40.0, ci2.getLowerBound(), EPS );
        assertEquals( 40.0, ci2.getUpperBound(), EPS );
        
        ConfidenceInterval ci3 = instance.computeConfidenceInterval( d2, 1.0 );
        assertEquals( Double.NEGATIVE_INFINITY, ci3.getLowerBound() );
        assertEquals( Double.POSITIVE_INFINITY, ci3.getUpperBound() );
        
        
        try
        {
            instance.computeConfidenceInterval( d2, 0.0 );
            fail( "Confidence must be (0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            instance.computeConfidenceInterval( d2, 1.0 + Math.random() );
            fail( "Confidence must be (0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }
    
}
