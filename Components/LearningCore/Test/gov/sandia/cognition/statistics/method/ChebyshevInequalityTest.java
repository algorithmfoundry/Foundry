/*
 * File:                ChebyshevInequalityTest.java
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

import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.statistics.method.MarkovInequality;
import gov.sandia.cognition.statistics.method.ChebyshevInequality;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import java.util.Arrays;
import junit.framework.*;
import java.util.Collection;

/**
 *
 * @author Kevin R. Dixon
 */
public class ChebyshevInequalityTest extends TestCase
{
    
    public ChebyshevInequalityTest(String testName)
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
     * Test of computeConfidenceInterval method, of class gov.sandia.cognition.learning.util.statistics.ChebyshevInequality.
     */
    public void testComputeConfidenceInterval()
    {
        System.out.println("computeConfidenceInterval");
        
        Collection<Double> d1 = Arrays.asList( 3.0, -1.0, 2.0 );
        double c1 = 0.95;
        ChebyshevInequality cheby = new ChebyshevInequality();
        MarkovInequality markov = new MarkovInequality();
        GaussianConfidence gauss = new GaussianConfidence();
        StudentTConfidence t = new StudentTConfidence();
        
        ConfidenceInterval ci1 = cheby.computeConfidenceInterval( d1, c1 );
        ConfidenceInterval ci1m = markov.computeConfidenceInterval( d1, c1 );
        ConfidenceInterval ci1g = gauss.computeConfidenceInterval( d1, c1 );
        ConfidenceInterval ci1t = t.computeConfidenceInterval( d1, c1 );
        
        System.out.println( "Cheby:  " + ci1 );
        System.out.println( "Markov: " + ci1m );
        System.out.println( "Gauss:  " + ci1g );
        System.out.println( "Student:" + ci1t );
        
        // This example is from Leon-Garcia, "Probability and Random Processes
        // for Electrical Engineering", p.138, Example 3.41
        ConfidenceInterval ci2 = ChebyshevInequality.computeConfidenceInterval(
            15, 3*3, 2, 1-0.36 );

        System.out.println( "Example2: " + ci2 );
        final double EPS = 1e-5;
        
        assertEquals( 2, ci2.getNumSamples() );
        assertEquals( 15.0, ci2.getCentralValue() );
        assertEquals( 10.0, ci2.getLowerBound(), EPS );
        assertEquals( 20.0, ci2.getUpperBound(), EPS );
        
    }
    
    public void testBoundConditions()
    {
        System.out.println( "Test Boundary Conditions" );
        
        Collection<Double> d1 = Arrays.asList( 3.0, -1.0, 2.0 );
        double c1 = 1.0;
        ChebyshevInequality cheby = new ChebyshevInequality();
        
        ConfidenceInterval ci1 = cheby.computeConfidenceInterval( d1, c1 );
        System.out.println( "CI1: " + ci1 );
        
        try
        {
            ConfidenceInterval ci2 = cheby.computeConfidenceInterval( d1, 2.0 );
            fail( "Confidence must be (0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            ConfidenceInterval ci2 = cheby.computeConfidenceInterval( d1, 0.0 );
            fail( "Confidence must be (0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }        
        
    }

    
}
