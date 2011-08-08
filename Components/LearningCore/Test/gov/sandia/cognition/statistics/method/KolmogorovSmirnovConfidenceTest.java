/*
 * File:                KolmogorovSmirnovConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 15, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.distribution.KolmogorovDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence.Statistic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class KolmogorovSmirnovConfidenceTest extends TestCase
{

    /**
     * RNG
     */
    public Random random = new Random( 1 );
    
    /**
     * Constructor
     * @param testName
     */
    public KolmogorovSmirnovConfidenceTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");

        KolmogorovSmirnovConfidence instance = new KolmogorovSmirnovConfidence();
        KolmogorovSmirnovConfidence clone = (KolmogorovSmirnovConfidence) instance.clone();
        assertNotSame(instance, clone);
    }

    /**
     * Test of kstest method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.
     */
    public void testEvaluateNullHypothesisCDF()
    {
        System.out.println("testEvaluateNullHypothesisCDF");

        LinkedList<Double> p1 = new LinkedList<Double>();
        LinkedList<Double> p2 = new LinkedList<Double>();
        int N = 100;
        double mean = 0.0;
        double variance = random.nextDouble();
        CumulativeDistributionFunction<Double> cdf1 = new UnivariateGaussian.CDF(mean, variance);
        CumulativeDistributionFunction<Double> cdf2 = new StudentTDistribution.CDF(2.0);
        for (int i = 0; i < 100; i++)
        {
            ArrayList<? extends Double> v1 = cdf1.sample( random, N );
            p1.add(KolmogorovSmirnovConfidence.evaluateNullHypothesis(v1, cdf1).getNullHypothesisProbability());
            p2.add(KolmogorovSmirnovConfidence.evaluateNullHypothesis(v1, cdf2).getNullHypothesisProbability());
        }

        double confidence = 0.95;
        GaussianConfidence gc = new GaussianConfidence();

        ConfidenceInterval c1 = gc.computeConfidenceInterval(p1, confidence);
        ConfidenceInterval c2 = gc.computeConfidenceInterval(p2, confidence);

        ConfidenceStatistic cs12 = gc.evaluateNullHypothesis(p1, p2);

        System.out.println("Gaussian:   " + c1);
        System.out.println("Student-t:  " + c2);
        System.out.println("Null Hypo:  " + cs12.getNullHypothesisProbability());

        assertTrue(c1.getCentralValue() > 1.0 - confidence);
        assertTrue(cs12.getNullHypothesisProbability() < 1.0 - confidence);


    }

    /**
     * Test of kstest method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.
     */
    public void testEvaluateNullHypothesisData()
    {
        System.out.println("evaluateNullHypothesisData");

        LinkedList<Double> p11 = new LinkedList<Double>();
        LinkedList<Double> p12 = new LinkedList<Double>();
        LinkedList<Double> p1bad = new LinkedList<Double>();
        LinkedList<Double> p2bad = new LinkedList<Double>();
        int N = 100;
        double mean = 0.0;
        double variance = random.nextDouble();
        CumulativeDistributionFunction<Double> cdf1 = new UnivariateGaussian.CDF(mean, variance);
        KolmogorovSmirnovConfidence instance = new KolmogorovSmirnovConfidence();
        for (int i = 0; i < 1000; i++)
        {
            Collection<? extends Double> v11 = cdf1.sample( random, N );
            Collection<? extends Double> v12 = cdf1.sample( random, N );
            Collection<Double> vbad = new UniformDistribution().sample( random, N );

            p11.add(instance.evaluateNullHypothesis(v11, v11).getNullHypothesisProbability());
            p12.add(instance.evaluateNullHypothesis(v11, v12).getNullHypothesisProbability());
            p1bad.add(instance.evaluateNullHypothesis(v11, vbad).getNullHypothesisProbability());
            p2bad.add(instance.evaluateNullHypothesis(v12, vbad).getNullHypothesisProbability());
        }

        double confidence = 0.95;
        GaussianConfidence gc = new GaussianConfidence();

        ConfidenceInterval c12 = gc.computeConfidenceInterval(p12, confidence);
        System.out.println("GC12:   " + c12);
        assertTrue(1.0 - confidence < c12.getCentralValue());

    }

    /**
     * Test of KSsignificance method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.
     */
    public void testStatisticKSSignificance()
    {
        System.out.println("Statistic.KSSignificance");

        double Ne = random.nextInt(100) + 10.0;
        double D = random.nextDouble();


        double expected = 1.0 - KolmogorovDistribution.CDF.evaluate(
            (Math.sqrt(Ne) + 0.12 + 0.11 / Math.sqrt(Ne)) * D);
        double result = KolmogorovSmirnovConfidence.Statistic.KSsignificance(Ne, D);
        assertEquals(expected, result);

    }

    /**
     * Test of computeAscendingArray method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.
     */
    public void testComputeAscendingArray()
    {
        System.out.println("computeAscendingArray");

        Collection<Double> data = new LinkedList<Double>();
        int N = random.nextInt(1000) + 100;
        for (int i = 0; i < N; i++)
        {
            data.add(random.nextDouble());
        }

        double[] expResult = null;
        double[] result = KolmogorovSmirnovConfidence.computeAscendingArray(data);
        assertEquals(data.size(), result.length);
        double previous, current = result[0];
        for (int i = 1; i < result.length; i++)
        {
            previous = current;
            current = result[i];
            assertTrue(previous <= current);
        }

    }

    public KolmogorovSmirnovConfidence.Statistic createStatisticInstance()
    {

        double D = random.nextDouble();
        double Ne = random.nextInt(1000) + 1.0;
        return new KolmogorovSmirnovConfidence.Statistic(Ne, D);

    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println("Statistic.clone");

        KolmogorovSmirnovConfidence.Statistic instance = createStatisticInstance();
        KolmogorovSmirnovConfidence.Statistic clone = (Statistic) instance.clone();
        double p = KolmogorovSmirnovConfidence.Statistic.KSsignificance(instance.getNe(), instance.getD());
        System.out.println("P = " + p);
        assertEquals(p, instance.getNullHypothesisProbability());
        assertEquals(instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability());
        assertEquals(instance.getD(), clone.getD());
        assertEquals(instance.getNe(), clone.getNe());

    }

    /**
     * Test of getD method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.Statistic.
     */
    public void testStatisticGetD()
    {
        System.out.println("Statistic.getD");

        KolmogorovSmirnovConfidence.Statistic instance = createStatisticInstance();
        assertTrue(instance.getD() >= 0.0);
        assertTrue(instance.getD() <= 1.0);
    }

    /**
     * Test of setD method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.Statistic.
     */
    public void testStatisticSetD()
    {
        System.out.println("Statistic.setD");

        KolmogorovSmirnovConfidence.Statistic instance = this.createStatisticInstance();
        assertTrue(instance.getD() >= 0.0);
        assertTrue(instance.getD() <= 1.0);

        double d2 = instance.getD() / 2.0;
        instance.setD(d2);
        assertEquals(d2, instance.getD());

        instance.setD(0.0);
        instance.setD(1.0);
        try
        {
            instance.setD(-1.0);
            fail("0.0 <= D <= 1.0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance.setD(1.1);
            fail("0.0 <= D <= 1.0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

    }

    /**
     * Test of getNe method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.Statistic.
     */
    public void testStatisticGetNe()
    {
        System.out.println("Statistic.getNe");

        KolmogorovSmirnovConfidence.Statistic instance = this.createStatisticInstance();
        assertTrue(instance.getNe() > 0.0);

    }

    /**
     * Test of setNe method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.Statistic.
     */
    public void testStatisticSetNe()
    {
        System.out.println("Statistic.setNe");

        KolmogorovSmirnovConfidence.Statistic instance = this.createStatisticInstance();
        assertTrue(instance.getNe() > 0.0);

        double ne2 = instance.getNe() + 1.0;
        instance.setNe(ne2);
        assertEquals(ne2, instance.getNe());

        instance.setNe(Double.POSITIVE_INFINITY);

    }

    /**
     * Test of evaluateGaussianHypothesis method, of class gov.sandia.cognition.learning.util.statistics.KolmogorovSmirnovConfidence.
     */
    public void testEvaluateGaussianHypothesis()
    {
        System.out.println("evaluateGaussianHypothesis");

        double r = 5;
        double mean = random.nextGaussian();
        double variance = random.nextDouble() * r;

        UnivariateGaussian.CDF cdf1 = new UnivariateGaussian.CDF(mean, variance);
        UnivariateGaussian.CDF cdf2 = new UnivariateGaussian.CDF(mean + r, variance);
        UnivariateGaussian.CDF cdf3 = new UnivariateGaussian.CDF(mean, variance / r);

        UniformDistribution.CDF cdf4 =
            new UniformDistribution.CDF(mean - variance, mean + variance);

        int N = 1000;
        Collection<Double> data = cdf1.sample( random, N );

        KolmogorovSmirnovConfidence.Statistic s0 = KolmogorovSmirnovConfidence.evaluateGaussianHypothesis(data);
        System.out.println("Gaussian test: " + s0.getNullHypothesisProbability());

        KolmogorovSmirnovConfidence.Statistic s1 = KolmogorovSmirnovConfidence.evaluateNullHypothesis(data, cdf1);
        System.out.println("CDF test 1: " + s1.getNullHypothesisProbability());
        KolmogorovSmirnovConfidence.Statistic s2 = KolmogorovSmirnovConfidence.evaluateNullHypothesis(data, cdf2);
        System.out.println("CDF test 2: " + s2.getNullHypothesisProbability());
        KolmogorovSmirnovConfidence.Statistic s3 = KolmogorovSmirnovConfidence.evaluateNullHypothesis(data, cdf3);
        System.out.println("CDF test 3: " + s3.getNullHypothesisProbability());
        KolmogorovSmirnovConfidence.Statistic s4 = KolmogorovSmirnovConfidence.evaluateNullHypothesis(data, cdf4);
        System.out.println("CDF test 4: " + s4.getNullHypothesisProbability());

    }

}
