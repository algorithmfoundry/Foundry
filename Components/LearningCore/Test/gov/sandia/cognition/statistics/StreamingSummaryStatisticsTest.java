/*
 * File:                SummaryStatisticsTest.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2016, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.statistics;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the summary statistics class
 *
 * @author jdwendt
 */
public class StreamingSummaryStatisticsTest
{

    /**
     * Helper that creates a summary statistics instance with the input values
     *
     * @param vals The values to load in
     * @return the summary statistics instance with the input values
     */
    private static StreamingSummaryStatistics createAndLoadStats(double[] vals)
    {
        StreamingSummaryStatistics ret = new StreamingSummaryStatistics();
        for (double val : vals)
        {
            ret.addValue(val);
        }

        return ret;
    }

    /**
     * Tests that the input instance has the input values
     *
     * @param s The instance to test
     * @param min The expected minimum
     * @param max The expected maximum
     * @param mean The expected mean
     * @param variance The expected variance
     * @param count The expected count
     */
    private static void testStats(StreamingSummaryStatistics s,
        double min,
        double max,
        double mean,
        double variance,
        int count)
    {
        assertEquals(min, s.min(), 1e-12);
        assertEquals(max, s.max(), 1e-12);
        assertEquals(mean, s.mean(), 1e-12);
        assertEquals(variance, s.variance(), 1e-8);
        assertEquals(Math.sqrt(variance), s.standardDeviation(), 1e-8);
        assertEquals(count, s.numEntries(), 1e-12);
    }

    /**
     * Tests various portions of the summary statistics class
     */
    @Test
    public void tests()
    {
        double[] v1 =
        {
            1, 2, 3, 4, 5, 6
        };
        double[] v2 =
        {
            3, 4, 5, 6, 7, 8
        };
        StreamingSummaryStatistics s1 = createAndLoadStats(v1);
        StreamingSummaryStatistics s2 = createAndLoadStats(v2);
        testStats(s1, 1.0, 6.0, 3.5, 3.5, 6);
        testStats(s2, 3.0, 8.0, 5.5, 3.5, 6);
        s1.merge(s2);
        testStats(s1, 1.0, 8.0, 4.5, 4.27272727272727272727272, 12);
        // Note this effectively replicates all the scores from s2 twice
        s1.merge(s2);
        testStats(s1, 1.0, 8.0, 4.8333333333333333333333, 4.029411765, 18);

        s1 = new StreamingSummaryStatistics();
        for (double v : v1)
        {
            s1.addValue(v);
        }
        for (double v : v2)
        {
            s1.addValue(v);
            s1.addValue(v);
        }
        testStats(s1, 1.0, 8.0, 4.8333333333333333333333, 4.029411765, 18);

        s1 = createAndLoadStats(v1);
        s2 = new StreamingSummaryStatistics();
        for (double v : v2)
        {
            s2.addValue(v);
            s2.addValue(v);
        }
        testStats(s1, 1.0, 6.0, 3.5, 3.5, 6);
        testStats(s2, 3.0, 8.0, 5.5, 3.1818181818181818, 12);
        s1.merge(s2);
        testStats(s1, 1.0, 8.0, 4.8333333333333333333333, 4.029411765, 18);
        // Test that nothing changes when I add an empty set of stats
        s1.merge(new StreamingSummaryStatistics());
        testStats(s1, 1.0, 8.0, 4.8333333333333333333333, 4.029411765, 18);
        // Test that an empty instance gets all of the values from a full instance
        s2 = new StreamingSummaryStatistics();
        s2.merge(s1);
        testStats(s2, 1.0, 8.0, 4.8333333333333333333333, 4.029411765, 18);
    }

}
