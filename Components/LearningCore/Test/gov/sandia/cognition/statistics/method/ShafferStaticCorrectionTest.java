/*
 * File:                ShafferStaticCorrectionTest.java
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

import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class ShafferStaticCorrectionTest.
 * @author krdixon
 */
public class ShafferStaticCorrectionTest
    extends MultipleHypothesisComparisonTestHarness
{

    /**
     * Default Constructor
     */
    public ShafferStaticCorrectionTest()
    {
    }

    @Override
    public ShafferStaticCorrection createInstance()
    {
        return new ShafferStaticCorrection();
    }

    /**
     * Tests the constructors of class ShafferStaticCorrectionTest.
     */
    @Test
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ShafferStaticCorrection instance = new ShafferStaticCorrection();
        assertNotNull( instance );
    }

    @Override
    @Test
    public void testKnownValues()
    {
//        this.testFalseNegativeRate();
//        this.testTrueNegativeRate();
    }

//    @Test
//    public void testRuntime()
//    {
//        System.out.println( "True Negative Rate" );
//
//        final int numSamples = 100;
//        ThreadPoolExecutor threadPool = ParallelUtil.createThreadPool();
//
//        for( int treatmentCount = 3; treatmentCount < 100; treatmentCount++ )
//        {
//            ArrayList<ArrayList<Number>> treatments =
//                new ArrayList<ArrayList<Number>>( treatmentCount );
//            for( int n = 0; n < treatmentCount; n++ )
//            {
//                final double p = ((double) n)/treatmentCount;
//                BinomialDistribution target = new BinomialDistribution( 100, p );
//                treatments.add( target.sample(RANDOM, NUM_SAMPLES) );
//            }
//
//            ArrayList<TimerTask> tasks = new ArrayList<TimerTask>( numSamples );
//            for( int n = 0; n < numSamples; n++ )
//            {
//                tasks.add( new TimerTask(ObjectUtil.cloneSmartElementsAsArrayList(treatments)) );
//            }
//
//            ArrayList<Double> times;
//            try
//            {
//                times =
//                    ParallelUtil.executeInParallel(tasks, threadPool);
//            }
//            catch (Exception ex)
//            {
//                throw new RuntimeException( ex );
//            }
//
//
//            System.out.println( treatmentCount + " " + StudentTConfidence.INSTANCE.computeConfidenceInterval(times,0.95) );
//
//
//        }
//
//
//    }


    public class TimerTask
        implements Callable<Double>
    {

        ArrayList<ArrayList<Number>> treatments;

        MultipleHypothesisComparison<Collection<? extends Number>> instance;

        public TimerTask(ArrayList<ArrayList<Number>> treatments)
        {
            this.instance = createInstance();
            this.treatments = treatments;
        }

        @Override
        public Double call()
            throws Exception
        {
            long start = System.currentTimeMillis();
            instance.evaluateNullHypotheses(treatments);
            long stop = System.currentTimeMillis();
            return (stop-start)/1000.0;
        }



    }

    /**
     *
     * @param treatments
     */
    public double treatmentStatisticsTimer(
        ArrayList<ArrayList<Number>> treatments )
    {
        MultipleHypothesisComparison<Collection<? extends Number>> instance =
            this.createInstance();
        long start = System.currentTimeMillis();
        MultipleHypothesisComparison.Statistic result =
            instance.evaluateNullHypotheses(treatments);
        long stop = System.currentTimeMillis();
        return (stop-start)/1000.0;
    }

}
