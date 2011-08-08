/*
 * File:                MultipleHypothesisComparisonTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.math.UnivariateSummaryStatistics;
import java.util.ArrayList;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class MultipleHypothesisComparisonTestHarness.
 * @author krdixon
 */
public abstract class MultipleHypothesisComparisonTestHarness
{

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
    public final int NUM_SAMPLES = 10;

    /**
     * Default Constructor
     */
    public MultipleHypothesisComparisonTestHarness()
    {
    }

    /**
     * Creates instance
     * @return
     * instance
     */
    public abstract MultipleHypothesisComparison<Collection<? extends Number>> createInstance();

    /**
     * Creates data
     * @return
     * Data
     */
    public static Collection<? extends Collection<Double>> createData1()
    {
        // From: http://faculty.vassar.edu/lowry/ch15a.html
        Collection<Double> A = Arrays.asList( 9.0, 9.5, 5.0, 7.5, 9.5, 7.5, 8.0, 7.0, 8.5, 6.0 );
        Collection<Double> B = Arrays.asList( 7.0, 6.5, 7.0, 7.5, 5.0, 8.0, 6.0, 6.5, 7.0, 7.0 );
        Collection<Double> C = Arrays.asList( 6.0, 8.0, 4.0, 6.0, 7.0, 6.5, 6.0, 4.0, 6.5, 3.0 );

        @SuppressWarnings("unchecked")
        Collection<? extends Collection<Double>> experiment = Arrays.asList( A, B, C );
        return experiment;
    }

    /**
     * Creates data
     * @return
     * data
     */
    public static Collection<? extends Collection<Integer>> createData2()
    {
        // From: http://web.mst.edu/~psyworld/anovaexample.htm
        Collection<Integer> A = Arrays.asList( 7, 4, 6, 8, 6, 6, 2, 9 );
        Collection<Integer> B = Arrays.asList( 5, 5, 3, 4, 4, 7, 2, 2 );
        Collection<Integer> C = Arrays.asList( 2, 4, 7, 1, 2, 1, 5, 5 );

        @SuppressWarnings("unchecked")
        Collection<? extends Collection<Integer>> experiment = Arrays.asList( A, B, C );
        return experiment;
    }

    /**
     * Creates data
     * @return
     * Data
     */
    public static Collection<? extends Collection<Double>> createData3()
    {
        // From: http://faculty.vassar.edu/lowry/ch15a.html
        Collection<Double> A = Arrays.asList( 40.11, 41.58, 36.81, 30.01, 31.67, 33.29, 37.10 );
        Collection<Double> B = Arrays.asList( 56.02, 38.71, 39.91, 33.70, 35.42, 26.64, 48.01, 39.79 );
        Collection<Double> C = Arrays.asList( 62.98, 26.84, 37.60, 48.32, 38.56, 34.74, 23.32, 40.06 );
        Collection<Double> D = Arrays.asList( 26.59, 24.26, 25.58, 33.34, 22.26, 31.51, 19.68 );
        Collection<Double> E = Arrays.asList( 38.67, 39.09, 36.26, 33.88, 28.38, 47.18, 34.66, 39.54 );
        Collection<Double> F = Arrays.asList( 41.10, 16.48, 29.90, 37.04, 29.62, 39.29, 24.69 );


        @SuppressWarnings("unchecked")
        Collection<? extends Collection<Double>> experiment = Arrays.asList( A, B, C, D, E, F );
        return experiment;
    }


    /**
     * Tests the constructors of class MultipleHypothesisComparisonTestHarness.
     */
    @Test
    public abstract void testConstructors();

    /**
     * Tests against known values
     */
    @Test
    public abstract void testKnownValues();

    /**
     * Clone
     */
    @Test
    public void testClone()
    {
        System.out.println( "Clone" );
        
        Collection<? extends Collection<Double>> data = createData1();
        MultipleHypothesisComparison<Collection<? extends Number>> instance =
            this.createInstance();
        @SuppressWarnings("unchecked")
        MultipleHypothesisComparison<Collection<? extends Number>> clone =
            (MultipleHypothesisComparison<Collection<? extends Number>>) instance.clone();
        assertNotSame( instance, clone );
        MultipleHypothesisComparison.Statistic r1 =
            instance.evaluateNullHypotheses(data);
        MultipleHypothesisComparison.Statistic r2 =
            clone.evaluateNullHypotheses(data);
        assertEquals( r1.toString(), r2.toString() );

        MultipleHypothesisComparison.Statistic r1clone =
            (MultipleHypothesisComparison.Statistic) r1.clone();
        assertNotSame( r1, r1clone );
        assertEquals( r1.toString(), r1clone.toString() );

    }

    /**
     * Test of evaluateNullHypotheses method, of class MultipleHypothesisComparison.
     */
    @Test
    public void testEvaluateNullHypotheses_Collection()
    {
        System.out.println("evaluateNullHypotheses");
        Collection<? extends Collection<Double>> data = createData1();
        MultipleHypothesisComparison<Collection<? extends Number>> instance =
            this.createInstance();
        MultipleHypothesisComparison.Statistic result =
            instance.evaluateNullHypotheses(data);
        System.out.println( "Result: " + result );
        assertEquals( MultipleHypothesisComparison.DEFAULT_UNCOMPENSATED_ALPHA, result.getUncompensatedAlpha(), TOLERANCE );
        assertEquals( data.size(), result.getTreatmentCount() );
//        assertFalse( result.acceptNullHypothesis(0, 2) );

        MultipleHypothesisComparison.Statistic clone =
            (MultipleHypothesisComparison.Statistic) result.clone();
        assertEquals( result.getTreatmentCount(), clone.getTreatmentCount() );
        assertEquals( result.getUncompensatedAlpha(), clone.getUncompensatedAlpha(), TOLERANCE );
        assertNotSame( clone, result );
        assertEquals( result.toString(), clone.toString() );
        for( int i = 0; i < result.getTreatmentCount(); i++ )
        {
            for( int j = 0; j < result.getTreatmentCount(); j++ )
            {
                assertEquals( result.getNullHypothesisProbability(i, j),
                    clone.getNullHypothesisProbability(i, j), TOLERANCE );
                assertEquals( result.getTestStatistic(i, j),
                    clone.getTestStatistic(i, j), TOLERANCE );
            }
        }
    }

    /**
     * Test of evaluateNullHypotheses method, of class MultipleHypothesisComparison.
     */
    @Test
    public void testEvaluateNullHypotheses_Collection_double()
    {
        System.out.println("evaluateNullHypotheses");
        final double alpha = 0.1;
        Collection<? extends Collection<Double>> data = createData1();
        MultipleHypothesisComparison<Collection<? extends Number>> instance =
            this.createInstance();
        MultipleHypothesisComparison.Statistic result =
            instance.evaluateNullHypotheses(data, alpha);
        System.out.println( "Result: " + result );
        assertEquals( alpha, result.getUncompensatedAlpha(), TOLERANCE );

        assertEquals( data.size(), result.getTreatmentCount() );

        assertFalse( result.acceptNullHypothesis(0, 2) );
    }

    /**
     * 
     * @param treatments
     */
    public void treatmentStatistics(
        ArrayList<ArrayList<Number>> treatments )
    {
        MultipleHypothesisComparison<Collection<? extends Number>> instance =
            this.createInstance();
        MultipleHypothesisComparison.Statistic result =
            instance.evaluateNullHypotheses(treatments);
        int rejectCount = 0;
        int comparisonCount = 0;
        final int K = treatments.size();
        LinkedList<Double> pvalues = new LinkedList<Double>();
        for( int i = 0; i < K; i++ )
        {
            for( int j = i+1; j < K; j++ )
            {
                comparisonCount++;
                pvalues.add( result.getNullHypothesisProbability(i, j) );
                if( !result.acceptNullHypothesis(i, j) )
                {
                    rejectCount++;
                }
            }
        }
        System.out.println( "K = " + K );
        System.out.println( "Reject: " + rejectCount + ", Pct: " + rejectCount*100.0/comparisonCount);

        System.out.println( "P-Values: " + UnivariateSummaryStatistics.create(pvalues) );

    }


    @Test
    public void testFalseNegativeRate()
    {
        System.out.println( "False Negative Rate" );
        BinomialDistribution target = new BinomialDistribution( 100, 0.5 );

        final int treatmentCount = 10;
        ArrayList<ArrayList<Number>> treatments =
            new ArrayList<ArrayList<Number>>( treatmentCount );
        for( int n = 0; n < treatmentCount; n++ )
        {
            // Put 50 samples in each treatment
            treatments.add( target.sample(RANDOM, NUM_SAMPLES) );
        }

        this.treatmentStatistics(treatments);
    }

    @Test
    public void testTrueNegativeRate()
    {
        System.out.println( "True Negative Rate" );

        final int treatmentCount = 10;
        ArrayList<ArrayList<Number>> treatments =
            new ArrayList<ArrayList<Number>>( treatmentCount );
        for( int n = 0; n < treatmentCount; n++ )
        {
            final double p = ((double) n)/treatmentCount;
            BinomialDistribution target = new BinomialDistribution( 100, p );
            treatments.add( target.sample(RANDOM, NUM_SAMPLES) );
        }

        this.treatmentStatistics(treatments);

    }

}
