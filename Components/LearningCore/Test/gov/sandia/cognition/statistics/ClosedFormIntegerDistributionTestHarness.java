/*
 * File:            ClosedFormIntegerDistributionTestHarness.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics;

import java.util.Random;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import org.junit.Assert;

/**
 * Unit tests for class {@link AbstractClosedFormIntegerDistribution}.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public abstract class ClosedFormIntegerDistributionTestHarness
    extends ClosedFormDiscreteUnivariateDistributionTestHarness<Number>
{

    /**
     * Creates a new test.
     * 
     * @param   testName 
     *      The test name.
     */
    public ClosedFormIntegerDistributionTestHarness(
        final String testName)
    {
        super(testName);
    }
    
    public abstract AbstractClosedFormIntegerDistribution createInstance();

    /**
     * Test of sampleAsInt method, of class IntegerDistribution.
     */
    public void testDistributionSampleAsInt()
    {
        System.out.println("IntegerDistribution.sampleAsInt(random)");

        // Make sure that when we re-feed an identical RANDOM seed, then
        // we get an equal sample from the distribution.  But different seeds
        // should give us different results... maybe.
        Random random1a = new Random(1);
        IntegerDistribution instance = this.createInstance();
        int r11 = instance.sampleAsInt(random1a);
        int rx2 = instance.sampleAsInt(random1a);

        Random random1b = new Random(1);
        int r13 = instance.sampleAsInt(random1b);
        assertEquals(r11, r13);

        int r21 = instance.sampleAsInt(new Random(2));
        assertEquals(instance.sample(new Random(2)), r21);
        
        Random random3 = new Random(3);
        int r31 = instance.sampleAsInt(random3);
        int streak = 1;
        while (streak < 100)
        {
            int sample = instance.sampleAsInt(random3);
            if (sample != r31)
            {
                break;
            }
        }
        assertTrue(streak < 100);

        Random random1c = new Random(1);
        int r14 = instance.sampleAsInt(random1c);
        assertEquals(r11, r14);
        assertEquals(r13, r14);
    }
    
    /**
     * Test of sampleAsInts method, of class IntegerDistribution.
     */
    public void testDistributionSampleAsInts()
    {
        System.out.println("IntegerDistribution.sampleAsInts(random,int)");
        IntegerDistribution instance = this.createInstance();

        // Identical RANDOM seeds should produce equal sequences.
        // (Can't say anything about different seeds because deterministic
        // distributions always return the same result, regardless of seed.)
        Random r1a = new Random(1);
        int[] s1a = instance.sampleAsInts(r1a, NUM_SAMPLES);
        assertEquals(NUM_SAMPLES, s1a.length);

        Random r1b = new Random(1);
        int[] s1b = instance.sampleAsInts(r1b, NUM_SAMPLES);
        assertEquals(NUM_SAMPLES, s1b.length);

        assertEquals(s1a.length, s1b.length);
        Assert.assertArrayEquals(s1a, s1b);
    }
    
    /**
     * Test of sampleInto method, of class IntegerDistribution.
     */
    public void testDistributionSampleInto_ints()
    {
        System.out.println("SmoothUnivariateDistribution.sampleInto(random,int[],int,int)");
        IntegerDistribution instance = this.createInstance();

        // Identical RANDOM seeds should produce equal squences.
        // (Can't say anything about different seeds because deterministic
        // distributions always return the same result, regardless of seed.)
        Random r1a = new Random(1);
        int[] s1a = new int[NUM_SAMPLES];
        instance.sampleInto(r1a, s1a, 0, NUM_SAMPLES);

        Random r1b = new Random(1);
        int[] s1b = instance.sampleAsInts(r1b, NUM_SAMPLES);

        assertEquals(s1a.length, s1b.length);
        Assert.assertArrayEquals(s1a, s1b);
        
        // Make sure the data isn't over-written.
        int[] r1c = new int[3 * NUM_SAMPLES + 5];
        for (int i = 0; i < r1c.length; i++)
        {
            r1c[i] = i;
        }
        instance.sampleInto(new Random(1), r1c, 2, 3 * NUM_SAMPLES);
        assertEquals(0, r1c[0]);
        assertEquals(1, r1c[1]);
        assertEquals(r1c.length - 3, r1c[r1c.length - 3]);
        assertEquals(r1c.length - 2, r1c[r1c.length - 2]);
        assertEquals(r1c.length - 1, r1c[r1c.length - 1]);
    }
    
    
}
