/*
 * File:                BernoulliConfidenceTest.java
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

import gov.sandia.cognition.statistics.method.BernoulliConfidence;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import java.util.Arrays;
import junit.framework.*;
import java.util.Collection;

/**
 *
 * @author Kevin R. Dixon
 */
public class BernoulliConfidenceTest extends TestCase
{
    
    public BernoulliConfidenceTest(String testName)
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
     * Test of computeConfidenceInterval method, of class gov.sandia.cognition.learning.util.statistics.BernoulliConfidence.
     */
    public void testComputeConfidenceInterval()
    {
        System.out.println("computeConfidenceInterval");
        
        Collection<Boolean> data = 
            Arrays.asList( true, false, true, true, true, false, true, true );
        double confidence = 0.50;
        BernoulliConfidence instance = new BernoulliConfidence();
        ConfidenceInterval result = 
            instance.computeConfidenceInterval(data, confidence);
        
        System.out.println( "Result: " + result );
        
        assertEquals( data.size(), result.getNumSamples() );
        assertEquals( 0.75, result.getCentralValue() );
        
        final double EPS = 1e-5;
        assertEquals( 0.5334936490538904, result.getLowerBound(), EPS );
        assertEquals( 0.9665063509461096, result.getUpperBound(), EPS );
    }

    /**
     * Test of computeSampleSize method, of class gov.sandia.cognition.learning.util.statistics.ChebyshevInequality.
     */
    public void testComputeSampleSize()
    {
        System.out.println("computeSampleSize");
        
        double accuracy = 0.01;
        double confidence = 0.95;
        
        int n = 50000;
        
        int nhat = BernoulliConfidence.computeSampleSize( accuracy, confidence );
        System.out.println( "Nhat: " + nhat );
        
        assertEquals( n, nhat );
        
    }
    
}
