/*
 * File:                DiscreteSamplingUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class {@code DiscreteSamplingUtil}.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DiscreteSamplingUtilTest
    extends TestCase
{
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DiscreteSamplingUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of sampleIndexFromProbabilities method, of class DiscreteSamplingUtil.
     */
    public void testSampleIndexFromProbabilities()
    {
        double[] probabilities = {0.1, 0.7, 0.2};

        int[] counts = new int[3];
        for (int i = 0; i < 100; i++)
        {
            int index =
                DiscreteSamplingUtil.sampleIndexFromProbabilities(random, probabilities);
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }

        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);

        counts = new int[3];
        for (int i = 0; i < 100; i++)
        {
            int index =
                DiscreteSamplingUtil.sampleIndexFromProbabilities(random,
                    VectorFactory.getDefault().copyArray(probabilities));
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }

        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);

    }

    /**
     * Test of sampleIndexFromProportions method, of class DiscreteSamplingUtil.
     */
    public void testSampleIndexFromProportions()
    {
        double[] proportions = {0.5, 3.6, 1.1};

        int[] counts = new int[3];
        for (int i = 0; i < 100; i++)
        {
            int index =
                DiscreteSamplingUtil.sampleIndexFromProportions(random, proportions);
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }

        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);

        counts = new int[3];
        double sum = 0.5 + 3.6 + 1.1;
        for (int i = 0; i < 100; i++)
        {
            int index =
                DiscreteSamplingUtil.sampleIndexFromProportions(random, proportions, sum);
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }
        
        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);

    }

    /**
     * Test of sampleIndicesFromProportions method, of class DiscreteSamplingUtil.
     */
    public void testSampleIndicesFromProportions()
    {
        double[] proportions = {0.5, 3.6, 1.1};

        int[] counts = new int[3];
        int[] samples =
                DiscreteSamplingUtil.sampleIndicesFromProportions(random, proportions, 100);
        for (int i = 0; i < samples.length; i++)
        {
            int index = samples[i];
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }

        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);

    }

    /**
     * Test of sampleIndexFromCumulativeProportions method, of class DiscreteSamplingUtil.
     */
    public void testSampleIndexFromCumulativeProportions()
    {
        double[] cumulativeProportions = {0.5, 4.1, 5.2};

        int[] counts = new int[3];
        for (int i = 0; i < 100; i++)
        {
            int index =
                DiscreteSamplingUtil.sampleIndexFromCumulativeProportions(random,
                    cumulativeProportions);
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }

        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);
    }

    /**
     * Test of sampleIndicesFromCumulativeProportions method, of class DiscreteSamplingUtil.
     */
    public void testSampleIndicesFromCumulativeProportions()
    {
        double[] cumulativeProportions = {0.5, 4.1, 5.2};

        int[] counts = new int[3];
        int[] samples = DiscreteSamplingUtil.sampleIndicesFromCumulativeProportions(
            random, cumulativeProportions, 100);
        for (int i = 0; i < samples.length; i++)
        {
            int index = samples[i];
            assertTrue(index >= 0 && index < 3);
            counts[index]++;
        }

        assertTrue(counts[0] > 0);
        assertTrue(counts[1] > 0);
        assertTrue(counts[2] > 0);
        assertTrue(counts[0] < counts[1]);
        assertTrue(counts[1] > counts[2]);
        assertTrue(counts[0] < counts[1]);
    }

    /**
     * Test of sampleWithReplacement method, of class DiscreteSamplingUtil.
     */
    public void testSampleWithReplacement()
    {
        List<String> data = new ArrayList<String>();
        data.add("a");
        data.add("b");
        data.add("c");

        List<String> result =
            DiscreteSamplingUtil.sampleWithReplacement(random, data, 1);
        assertEquals(1, result.size());
        assertTrue(result.contains("a") || result.contains("b") || result.contains("c"));

        result = DiscreteSamplingUtil.sampleWithReplacement(random, data, 2);
        assertEquals(2, result.size());
        assertTrue(result.contains("a") || result.contains("b") || result.contains("c"));


        result = DiscreteSamplingUtil.sampleWithoutReplacement(random, data, 3);
        assertEquals(3, result.size());
        assertTrue(result.contains("a") || result.contains("b") || result.contains("c"));


        result = DiscreteSamplingUtil.sampleWithReplacement(random, data, 4);
        assertEquals(4, result.size());
        assertTrue(result.contains("a") || result.contains("b") || result.contains("c"));

        result = DiscreteSamplingUtil.sampleWithReplacement(random, data, 100);
        assertEquals(100, result.size());
        assertTrue(result.contains("a") && result.contains("b") && result.contains("c"));
        
        result = DiscreteSamplingUtil.sampleWithReplacement(random, data, 0);
        assertEquals(0, result.size());
    }

    /**
     * Test of sampleWithoutReplacement method, of class DiscreteSamplingUtil.
     */
    public void testSampleWithoutReplacement()
    {
        List<String> data = new ArrayList<String>();
        data.add("a");
        data.add("b");
        data.add("c");

        List<String> result =
            DiscreteSamplingUtil.sampleWithoutReplacement(random, data, 1);
        assertEquals(1, result.size());
        assertTrue(result.contains("a") || result.contains("b") || result.contains("c"));

        result = DiscreteSamplingUtil.sampleWithoutReplacement(random, data, 2);
        assertEquals(2, result.size());
        
        int unique = 0;
        if (result.contains("a")) unique++;
        if (result.contains("b")) unique++;
        if (result.contains("c")) unique++;
        assertEquals(2, unique);


        result = DiscreteSamplingUtil.sampleWithoutReplacement(random, data, 3);
        assertEquals(3, result.size());
        assertTrue(result.contains("a") && result.contains("b") && result.contains("c"));

        boolean exceptionThrown = false;
        try
        {
            DiscreteSamplingUtil.sampleWithoutReplacement(random, data, 4);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            DiscreteSamplingUtil.sampleWithoutReplacement(random, data, 0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            DiscreteSamplingUtil.sampleWithoutReplacement(random, data, -1);
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

}
