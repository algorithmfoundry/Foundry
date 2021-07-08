/*
 * File:            DefaultSummaryStatisticsTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.learning.test.PropertyChecker;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link DefaultSummaryStatistics}.
 * 
 * @author  Justin Basilico
 * @since   4.0.0
 */
public class DefaultSummaryStatisticsTest
    extends Object
{
    
    /** An array of doubles for testing. */
    public static double[] TEST_DOUBLES =
    {
        0.0, 1.0, -1.0, 0.1, -0.1, 2.3, -2.3,
        Double.MIN_VALUE, Double.MAX_VALUE,
        -Double.MIN_VALUE, -Double.MAX_VALUE,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
        Double.NaN
    };

    /** An array of some non-negative doubles. */
    public static double[] NON_NEGATIVE_DOUBLES =
    {
        0.0, 1.0, 0.1, 2.3,
        Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_NORMAL,
        Double.POSITIVE_INFINITY
    };

    /** An array of some negative doubles and NaN. */
    public static double[] NEGATIVE_AND_NAN_DOUBLES =
    {
        -1.0, -0.1, -2.3,
        -Double.MIN_VALUE, -Double.MAX_VALUE, -Double.MIN_NORMAL,
        Double.NEGATIVE_INFINITY,
        Double.NaN
    };

    /**
     * Creates a new test.
     */
    public DefaultSummaryStatisticsTest()
    {
        super();
    }

    @Test
    public void testConstructors()
    {
        double mean = 0.0;
        double variance = 0.0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics();
        assertEquals(mean, instance.getMean(), 0.0);
        assertEquals(variance, instance.getVariance(), 0.0);
        assertEquals(Math.sqrt(variance), instance.getStandardDeviation(), 0.0);
        assertEquals(min, instance.getMin(), 0.0);
        assertEquals(max, instance.getMax(), 0.0);

        mean = 43.21;
        variance = 98.7;
        min = 3.2;
        max = 100.1;
        instance = new DefaultSummaryStatistics(mean, variance, min, max);
        assertEquals(mean, instance.getMean(), 0.0);
        assertEquals(variance, instance.getVariance(), 0.0);
        assertEquals(Math.sqrt(variance), instance.getStandardDeviation(), 0.0);
        assertEquals(min, instance.getMin(), 0.0);
        assertEquals(max, instance.getMax(), 0.0);

        // Make sure that setting a negative variance causes an exception.
        boolean exceptionThrown = false;
        try
        {
            instance = new DefaultSummaryStatistics(mean, -1.0, min, max);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    @Test
    public void testClone()
    {
        double mean = 43.21;
        double variance = 98.7;
        double min = 3.2;
        double max = 100.1;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics(mean, variance, min, max);
        SummaryStatistics clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(clone, instance);
        assertNotSame(clone, instance.clone());

        assertEquals(mean, instance.getMean(), 0.0);
        assertEquals(variance, instance.getVariance(), 0.0);
        assertEquals(Math.sqrt(variance), instance.getStandardDeviation(), 0.0);
        assertEquals(min, instance.getMin(), 0.0);
        assertEquals(max, instance.getMax(), 0.0);

        assertEquals(mean, clone.getMean(), 0.0);
        assertEquals(variance, clone.getVariance(), 0.0);
        assertEquals(Math.sqrt(variance), clone.getStandardDeviation(), 0.0);
        assertEquals(min, clone.getMin(), 0.0);
        assertEquals(max, clone.getMax(), 0.0);
    }

    @Test
    public void testGetMean()
    {
        this.testSetMean();
    }

    @Test
    public void testSetMean()
    {
        double mean = 0.0;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics();
        assertEquals(mean, instance.getMean(), 0.0);

        instance.setMean(mean);
        assertEquals(mean, instance.getMean(), 0.0);

        PropertyChecker.checkGetSetDouble(instance, "mean", DefaultSummaryStatistics::getMean,
            DefaultSummaryStatistics::setMean, mean, TEST_DOUBLES, null);
    }

    @Test
    public void testGetVariance()
    {
        this.testSetVariance();
    }

    @Test
    public void testSetVariance()
    {
        double variance = 0.0;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics();
        assertEquals(variance, instance.getVariance(), 0.0);

        instance.setVariance(variance);
        assertEquals(variance, instance.getVariance(), 0.0);

        PropertyChecker.checkGetSetDouble(instance,
            "variance", DefaultSummaryStatistics::getVariance,
            DefaultSummaryStatistics::setVariance, variance,
            NON_NEGATIVE_DOUBLES, NEGATIVE_AND_NAN_DOUBLES);
    }


    @Test
    public void testGetStandardDeviation()
    {
        this.testSetStandardDeviation();
    }

    @Test
    public void testSetStandardDeviation()
    {
        double standardDeviation = 0.0;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics();
        assertEquals(standardDeviation, instance.getStandardDeviation(), 0.0);

        instance.setStandardDeviation(standardDeviation);
        assertEquals(standardDeviation, instance.getStandardDeviation(), 0.0);

        PropertyChecker.checkGetSetDouble(instance, "standardDeviation", 
            DefaultSummaryStatistics::getStandardDeviation,
            DefaultSummaryStatistics::setStandardDeviation,
            standardDeviation, NON_NEGATIVE_DOUBLES, NEGATIVE_AND_NAN_DOUBLES);
    }

    @Test
    public void testGetMin()
    {
        this.testSetMin();
    }

    @Test
    public void testSetMin()
    {
        double min = Double.POSITIVE_INFINITY;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics();
        assertEquals(min, instance.getMin(), 0.0);

        instance.setMin(min);
        assertEquals(min, instance.getMin(), 0.0);

        PropertyChecker.checkGetSetDouble(instance, "min", DefaultSummaryStatistics::getMin,
            DefaultSummaryStatistics::setMin, min, TEST_DOUBLES, null);
    }

    @Test
    public void testGetMax()
    {
        this.testSetMax();
    }

    @Test
    public void testSetMax()
    {
        double max = Double.NEGATIVE_INFINITY;
        DefaultSummaryStatistics instance = new DefaultSummaryStatistics();
        assertEquals(max, instance.getMax(), 0.0);

        instance.setMax(max);
        assertEquals(max, instance.getMax(), 0.0);

        PropertyChecker.checkGetSetDouble(instance, "max", DefaultSummaryStatistics::getMax,
            DefaultSummaryStatistics::setMax, max,  TEST_DOUBLES, null);
    }
    
// TODO: Put this in a more general place.
    

}
