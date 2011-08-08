/*
 * File:                TukeyRangeConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 16, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import java.util.Arrays;
import java.util.Random;
import gov.sandia.cognition.statistics.method.TukeyRangeConfidence.Statistic;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krdixon
 */
public class TukeyRangeConfidenceTest {

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Default number of samples to test against, {@value}.
     */
    public final int NUM_SAMPLES = 1000;

    public TukeyRangeConfidenceTest()
    {
    }


    /**
     * Test of clone method, of class TukeyRangeConfidence.
     */
    @Test
    public void testConstructors()
    {
        System.out.println( "Constructors" );
    }

    /**
     * Test of clone method, of class TukeyRangeConfidence.
     */
    @Test
    public void testClone()
    {
        System.out.println("clone");
        TukeyRangeConfidence instance = new TukeyRangeConfidence();
        TukeyRangeConfidence clone = instance.clone();
        assertNotSame( instance, clone );
    }

    /**
     * Test of evaluateNullHypothesis method, of class TukeyRangeConfidence.
     */
    @Test
    public void testEvaluateNullHypothesis_Collection_Collection()
    {
        System.out.println("evaluateNullHypothesis");
        Collection<Double> A = Arrays.asList( 9.0, 9.5, 5.0, 7.5, 9.5, 7.5, 8.0, 7.0, 8.5, 6.0 );
        Collection<Double> C = Arrays.asList( 6.0, 8.0, 4.0, 6.0, 7.0, 6.5, 6.0, 4.0, 6.5, 3.0 );
        TukeyRangeConfidence instance = new TukeyRangeConfidence();
        TukeyRangeConfidence.Statistic result = instance.evaluateNullHypothesis(A,C);
        System.out.println( "Result: " + result );
    }

    /**
     * Test of evaluateNullHypothesis method, of class TukeyRangeConfidence.
     */
    @Test
    public void testEvaluateNullHypothesis_Collection()
    {
        System.out.println("evaluateNullHypothesis");
        
        // From: http://faculty.vassar.edu/lowry/ch15a.html
        Collection<Double> A = Arrays.asList( 9.0, 9.5, 5.0, 7.5, 9.5, 7.5, 8.0, 7.0, 8.5, 6.0 );
        Collection<Double> B = Arrays.asList( 7.0, 6.5, 7.0, 7.5, 5.0, 8.0, 6.0, 6.5, 7.0, 7.0 );
        Collection<Double> C = Arrays.asList( 6.0, 8.0, 4.0, 6.0, 7.0, 6.5, 6.0, 4.0, 6.5, 3.0 );

        @SuppressWarnings("unchecked")
        Collection<? extends Collection<Double>> experiment = Arrays.asList( A, B, C );

        TukeyRangeConfidence.Statistic result =
            TukeyRangeConfidence.evaluateNullHypothesis(experiment);
        System.out.println( "Result: " + result );

        assertEquals( 3, result.getTreatmentCount() );
        assertEquals( 10, result.getSubjectCount() );
        assertNotNull( result.getQ() );
        assertNotNull( result.getP() );
        assertEquals( 7.75, result.getTreatmentMeans().get(0), TOLERANCE );
        assertEquals( 6.75, result.getTreatmentMeans().get(1), TOLERANCE );
        assertEquals( 5.70, result.getTreatmentMeans().get(2), TOLERANCE );

        TukeyRangeConfidence.Statistic clone = result.clone();
        assertNotSame( result.getTreatmentMeans(), clone.getTreatmentMeans() );
        assertNotSame( result.getQ(), clone.getQ() );
        assertEquals( result.getQ(), clone.getQ() );
        assertNotSame( result.getP(), clone.getP() );
        assertEquals( result.getP(), clone.getP() );
        assertEquals( result.toString(), clone.toString() );
    }

}
