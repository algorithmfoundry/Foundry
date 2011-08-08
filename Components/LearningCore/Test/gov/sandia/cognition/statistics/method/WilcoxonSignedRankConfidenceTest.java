/*
 * File:                WilcoxonSignedRankConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.method.WilcoxonSignedRankConfidence.Statistic;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class WilcoxonSignedRankConfidenceTest
    extends TestCase
{

    public WilcoxonSignedRankConfidenceTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");

        WilcoxonSignedRankConfidence instance = new WilcoxonSignedRankConfidence();
        WilcoxonSignedRankConfidence clone = (WilcoxonSignedRankConfidence) instance.clone();
        assertNotSame(instance, clone);

    }

    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.
     */
    public void testEvaluateNullHypothesis()
    {
        System.out.println("evaluateNullHypothesis");

        Collection<Double> data11 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Collection<Double> data21 = Arrays.asList(5.0, 4.0, 3.0, 2.0, 1.0);
        WilcoxonSignedRankConfidence instance = new WilcoxonSignedRankConfidence();

        WilcoxonSignedRankConfidence.Statistic stat1 = instance.evaluateNullHypothesis(data11, data21);

        assertEquals(5.0, stat1.getT());
        assertEquals(4, stat1.getNumNonZero());

        assertEquals(1.0, stat1.getNullHypothesisProbability());

    }

    /**
     * Test of ranks method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.
     */
    public void testRanks()
    {
        System.out.println("ranks");

        Collection<Double> values1 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 5.0);

        double[] ranks1 = WilcoxonSignedRankConfidence.ranks(values1);

        assertEquals(values1.size(), ranks1.length);

        double[] expected1 = {1, 2, 3, 4, 5.5, 5.5};

        for (int i = 0; i < values1.size(); i++)
        {
            assertEquals(expected1[i], ranks1[i]);
        }


        Collection<Double> values2 = Arrays.asList(5.0, 5.0, 5.0, 4.0, 2.0);

        double[] ranks2 = WilcoxonSignedRankConfidence.ranks(values2);

        assertEquals(values2.size(), ranks2.length);

        double[] expected2 = {4, 4, 4, 2, 1};

        for (int i = 0; i < values2.size(); i++)
        {
            assertEquals(expected2[i], ranks2[i]);
        }


        Collection<Double> values3 = Arrays.asList(9.0, 5.0, 8.0, 2.0, 4.0);

        double[] ranks3 = WilcoxonSignedRankConfidence.ranks(values3);

        assertEquals(values3.size(), ranks3.length);

        double[] expected3 = {5, 3, 4, 1, 2};

        for (int i = 0; i < values3.size(); i++)
        {
            assertEquals(expected3[i], ranks3[i]);
        }


        Collection<Double> values4 = Arrays.asList(4.0, 2.0, 2.0, 4.0);

        double[] ranks4 = WilcoxonSignedRankConfidence.ranks(values4);

        assertEquals(values4.size(), ranks4.length);

        double[] expected4 = {3.5, 1.5, 1.5, 3.5};

        for (int i = 0; i < values4.size(); i++)
        {
            assertEquals(expected4[i], ranks4[i]);
        }

    }

    public WilcoxonSignedRankConfidence.Statistic createStatisticInstance()
    {

        double T = Math.random() * 100.0 + 1;
        int numNonzero = (int) (Math.sqrt(T) + Math.random() / Math.random()) + 1;
        return new WilcoxonSignedRankConfidence.Statistic(T, numNonzero);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println("Statistic.clone");

        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();
        WilcoxonSignedRankConfidence.Statistic clone = (Statistic) instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getNumNonZero(), clone.getNumNonZero());
        assertEquals(instance.getT(), clone.getT());
        assertEquals(instance.getZ(), clone.getZ());
        assertEquals(instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability());
    }

    /**
     * Test of getT method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testStatisticGetT()
    {
        System.out.println("Statistic.getT");

        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();
        assertTrue(instance.getT() != 0.0);


    }

    /**
     * Test of setT method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testStatisticSetT()
    {
        System.out.println("Statistic.setT");

        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();
        assertTrue(instance.getT() != 0.0);

        double T = instance.getT() + 1.0;
        instance.setT(T);
        assertEquals(T, instance.getT());

    }

    /**
     * Test of getNumNonZero method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testStatisticGetNumNonZero()
    {
        System.out.println("Statistic.getNumNonZero");

        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();
        assertTrue(instance.getNumNonZero() >= 0);

    }

    /**
     * Test of setNumNonZero method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testSetNumNonZero()
    {
        System.out.println("Statistic.setNumNonZero");

        int numNonZero = 0;
        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();

        numNonZero = instance.getNumNonZero();

        instance.setNumNonZero(numNonZero);
        assertEquals(numNonZero, instance.getNumNonZero());

    }

    /**
     * Test of getZ method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testStatisticGetZ()
    {
        System.out.println("Statistic.getZ");

        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();

        assertTrue(instance.getZ() != 0.0);

    }

    /**
     * Test of setZ method, of class gov.sandia.cognition.learning.util.statistics.WilcoxonSignedRankConfidence.Statistic.
     */
    public void testStatisticSetZ()
    {
        System.out.println("Statistic.setZ");

        double z = 0;
        WilcoxonSignedRankConfidence.Statistic instance = this.createStatisticInstance();

        z = instance.getZ();

        instance.setZ(z);
        assertEquals(z, instance.getZ());
    }

}
