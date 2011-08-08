/*
 * File:                FisherSignConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.method.FisherSignConfidence.Statistic;
import java.util.Arrays;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class FisherSignConfidenceTest
    extends TestCase
{

    public FisherSignConfidenceTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");

        FisherSignConfidence instance = new FisherSignConfidence();
        FisherSignConfidence clone = (FisherSignConfidence) instance.clone();
        assertNotSame(instance, clone);
    }

    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.
     */
    public void testEvaluateNullHypothesis()
    {
        System.out.println("evaluateNullHypothesis");

        LinkedList<Double> data1 = new LinkedList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        LinkedList<Double> data2 = new LinkedList<Double>(Arrays.asList(5.0, 4.0, 3.0, 2.0, 1.0));

        final double EPS = 1e-5;

        // I got these numbers from octave's sign_test(data1,data2) function

        FisherSignConfidence st = new FisherSignConfidence();
        FisherSignConfidence.Statistic stat = st.evaluateNullHypothesis(data1, data2);
        assertEquals(4, stat.getNumDifferent());
        assertEquals(2, stat.getNumPositiveSign());
        assertEquals(0.625, stat.getNullHypothesisProbability(), EPS);

        data1.add(6.0);
        try
        {
            st.evaluateNullHypothesis(data1, data2);
            fail("Datasets must be the same size");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        data2.add(0.0);
        stat = st.evaluateNullHypothesis(data1, data2);
        assertEquals(5, stat.getNumDifferent());
        assertEquals(3, stat.getNumPositiveSign());
        assertEquals(0.375, stat.getNullHypothesisProbability(), EPS);

        int N = 3;
        for (int i = 0; i < N; i++)
        {
            data1.add(Math.random());
            data2.add(0.0);
        }
        stat = st.evaluateNullHypothesis(data1, data2);
        assertEquals(data1.size() - 1, stat.getNumDifferent());
        assertEquals(3 + N, stat.getNumPositiveSign());
        assertEquals(0.0703125, stat.getNullHypothesisProbability(), EPS);
    }

    public FisherSignConfidence.Statistic createStatInstance()
    {
        int N = (int) (Math.random() * 100) + 10;
        int x = (int) (Math.random() * N);
        return new FisherSignConfidence.Statistic(x, N);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println("Statistic.clone");

        FisherSignConfidence.Statistic instance = this.createStatInstance();
        FisherSignConfidence.Statistic clone = (Statistic) instance.clone();
        assertNotSame(instance, clone);

        assertEquals(instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability());
        assertEquals(instance.getNumDifferent(), clone.getNumDifferent());
        assertEquals(instance.getNumPositiveSign(), clone.getNumPositiveSign());
    }

    /**
     * Test of getNumDifferent method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.Statistic.
     */
    public void testStatisticGetNumDifferent()
    {
        System.out.println("Statistic.getNumDifferent");

        int b = 10;
        int N = 2 * b;
        FisherSignConfidence.Statistic instance = new FisherSignConfidence.Statistic(b, N);

        assertEquals(N, instance.getNumDifferent());
    }

    /**
     * Test of setNumDifferent method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.Statistic.
     */
    public void testStatisticSetNumDifferent()
    {
        System.out.println("Statistic.setNumDifferent");

        int b = 10;
        int N = 2 * b;
        FisherSignConfidence.Statistic instance = new FisherSignConfidence.Statistic(b, N);

        assertEquals(N, instance.getNumDifferent());

        int N2 = N + 1;
        instance.setNumDifferent(N2);
        assertEquals(N2, instance.getNumDifferent());

        instance = new FisherSignConfidence.Statistic(N, N);

        try
        {
            instance = new FisherSignConfidence.Statistic(N, N - 1);
            fail("NumDifferent must be >= numPositiveSign");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
    }

    /**
     * Test of getNumPositiveSign method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.Statistic.
     */
    public void testStatisticGetNumPositiveSign()
    {
        System.out.println("Statistic.getNumPositiveSign");

        int b = 10;
        int N = 2 * b;
        FisherSignConfidence.Statistic instance = new FisherSignConfidence.Statistic(b, N);

        assertEquals(b, instance.getNumPositiveSign());

    }

    /**
     * Test of setNumPositiveSign method, of class gov.sandia.cognition.learning.util.statistics.FisherSignConfidence.Statistic.
     */
    public void testStatisticSetNumPositiveSign()
    {
        System.out.println("Statistic.setNumPositiveSign");

        int b = 10;
        int N = 2 * b;
        FisherSignConfidence.Statistic instance = new FisherSignConfidence.Statistic(b, N);

        assertEquals(b, instance.getNumPositiveSign());

        int b2 = b - 1;
        instance.setNumPositiveSign(b2);
        assertEquals(b2, instance.getNumPositiveSign());

        instance = new FisherSignConfidence.Statistic(b, b);

        try
        {
            instance = new FisherSignConfidence.Statistic(b + 1, b);
            fail("NumDifferent must be >= numPositiveSign");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
    }

}
