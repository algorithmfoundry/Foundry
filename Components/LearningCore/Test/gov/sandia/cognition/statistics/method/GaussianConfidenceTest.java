/*
 * File:                GaussianConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class GaussianConfidenceTest extends TestCase
{

    public static final Random random = new Random(1);

    public static final double TOLERANCE = 1e-5;

    public GaussianConfidenceTest(String testName)
    {
        super(testName);
    }

    public static GaussianConfidence createInstance()
    {
        return new GaussianConfidence();
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.cost.GaussianConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");

        GaussianConfidence instance = createInstance();
        GaussianConfidence clone = (GaussianConfidence) instance.clone();
        assertNotSame(instance, clone);
    }

    public static GaussianConfidence.Statistic createStatisticInstance()
    {
        double z = Math.abs(UnivariateGaussian.CDF.Inverse.evaluate(random.nextDouble(), 0.0, 1.0));
        return new GaussianConfidence.Statistic(z);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.GaussianConfidence.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println("Statistic.clone");

        GaussianConfidence.Statistic instance = createStatisticInstance();
        GaussianConfidence.Statistic clone = (GaussianConfidence.Statistic) instance.clone();

        assertNotSame(instance, clone);
        assertEquals(instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability());
        assertEquals(instance.getZ(), clone.getZ());
    }

    /**
     * Test of getZ method, of class gov.sandia.cognition.learning.util.statistics.GaussianConfidence.Statistic.
     */
    public void testStatisticGetZ()
    {
        System.out.println("Statistic.getZ");

        double z = random.nextDouble();
        GaussianConfidence.Statistic instance = new GaussianConfidence.Statistic(z);
        assertEquals(z, instance.getZ());
        assertEquals(2.0 * UnivariateGaussian.CDF.evaluate(-z, 0, 1), instance.getNullHypothesisProbability());

    }

    /**
     * Test of setZ method, of class gov.sandia.cognition.learning.util.statistics.GaussianConfidence.Statistic.
     */
    public void testStatisticSetZ()
    {
        System.out.println("Statistic.setZ");

        double z = random.nextDouble();
        GaussianConfidence.Statistic instance = new GaussianConfidence.Statistic(z);
        assertEquals(z, instance.getZ());

        double z2 = z + 1.0;
        instance.setZ(z2);
        assertEquals(z2, instance.getZ());
    }

    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.GaussianConfidence.
     */
    public void testEvaluateNullHypothesis()
    {
        System.out.println("evaluateNullHypothesis");

        int N1 = random.nextInt(1000) + 10;
        Collection<Double> data1 = new LinkedList<Double>();
        UnivariateGaussian g1 = new UnivariateGaussian(random.nextGaussian(), random.nextDouble());
        for (int i = 0; i < N1; i++)
        {
            data1.add(random.nextGaussian() * Math.sqrt(g1.getVariance()) + g1.getMean());
        }

        int N2 = random.nextInt(1000) + 10;
        Collection<Double> data2 = new LinkedList<Double>();
        UnivariateGaussian g2 = new UnivariateGaussian(random.nextGaussian(), random.nextDouble());
        for (int i = 0; i < N2; i++)
        {
            data2.add(random.nextGaussian() * Math.sqrt(g2.getVariance()) + g2.getMean());
        }


        GaussianConfidence instance = new GaussianConfidence();
        GaussianConfidence.Statistic s1 = instance.evaluateNullHypothesis(data1, data1);

        assertEquals(0.0, s1.getZ());
        assertEquals(1.0, s1.getNullHypothesisProbability());

        GaussianConfidence.Statistic s2 = instance.evaluateNullHypothesis(data1, data2);
        assertTrue(s2.getZ() > 0.0);
        assertEquals(2.0 * UnivariateGaussian.CDF.evaluate(-s2.getZ(), 0, 1), s2.getNullHypothesisProbability(), 1e-5);
    }

    /**
     * Test of computeConfidenceInterval method, of class gov.sandia.cognition.learning.util.statistics.GaussianConfidence.
     */
    public void testComputeConfidenceInterval()
    {
        System.out.println("computeConfidenceInterval");

        int N = random.nextInt(1000) + 100;
        double confidence = random.nextDouble();
        Collection<Double> data = new LinkedList<Double>();
        for (int i = 0; i < N; i++)
        {
            data.add(random.nextGaussian());
        }
        UnivariateGaussian g = UnivariateGaussian.MaximumLikelihoodEstimator.learn(data, 0.0);
        double alpha = 1.0 - confidence;
        double z = -UnivariateGaussian.CDF.Inverse.evaluate(alpha / 2.0, 0, 1);
        double delta = z * Math.sqrt(g.getVariance() / data.size());

        GaussianConfidence conf = new GaussianConfidence();
        ConfidenceInterval c = conf.computeConfidenceInterval(data, confidence);

        assertEquals(g.getMean(), c.getCentralValue(), TOLERANCE);
        assertEquals(g.getMean() - delta, c.getLowerBound(), TOLERANCE);
        assertEquals(g.getMean() + delta, c.getUpperBound(), TOLERANCE);
        assertEquals(N, c.getNumSamples());
        assertEquals(confidence, c.getConfidence());
    }

}
