/*
 * File:                StudentTConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.LinkedList;
import java.util.Random;
import java.util.Collection;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class StudentTConfidenceTest
    extends TestCase
{

    public static Random RANDOM = new Random( 1 );

    public StudentTConfidenceTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of twoTailTStatistic method, of class gov.sandia.cognition.learning.util.function.cost.StudentTConfidence.
     */
    public void testStatisticTwoTailTStatistic()
    {
        System.out.println("Statistic.twoTailTStatistic");

        final double EPS = 1e-3;
        assertEquals(0.10, StudentTConfidence.Statistic.twoTailTStatistic(6.314, 1), EPS);
        assertEquals(0.05, StudentTConfidence.Statistic.twoTailTStatistic(12.706, 1), EPS);
        assertEquals(0.01, StudentTConfidence.Statistic.twoTailTStatistic(63.657, 1), EPS);

        assertEquals(0.10, StudentTConfidence.Statistic.twoTailTStatistic(2.920, 2), EPS);
        assertEquals(0.05, StudentTConfidence.Statistic.twoTailTStatistic(4.303, 2), EPS);
        assertEquals(0.01, StudentTConfidence.Statistic.twoTailTStatistic(9.925, 2), EPS);

        assertEquals(0.10, StudentTConfidence.Statistic.twoTailTStatistic(1.812, 10), EPS);
        assertEquals(0.05, StudentTConfidence.Statistic.twoTailTStatistic(2.228, 10), EPS);
        assertEquals(0.01, StudentTConfidence.Statistic.twoTailTStatistic(3.169, 10), EPS);

        assertEquals(0.10, StudentTConfidence.Statistic.twoTailTStatistic(1.684, 40), EPS);
        assertEquals(0.05, StudentTConfidence.Statistic.twoTailTStatistic(2.021, 40), EPS);
        assertEquals(0.01, StudentTConfidence.Statistic.twoTailTStatistic(2.704, 40), EPS);

        assertEquals(0.10, StudentTConfidence.Statistic.twoTailTStatistic(1.645, 1000), EPS);
        assertEquals(0.05, StudentTConfidence.Statistic.twoTailTStatistic(1.960, 1000), EPS);
        assertEquals(0.01, StudentTConfidence.Statistic.twoTailTStatistic(2.576, 1000), EPS);

    }

    /**
     * Test of computeConfidenceInterval method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.
     */
    public void testComputeConfidenceInterval()
    {
        System.out.println("computeConfidenceInterval");

        int N = RANDOM.nextInt(1000) + 100;
        double confidence = RANDOM.nextDouble();
        Collection<Double> data = new LinkedList<Double>();
        for (int i = 0; i < N; i++)
        {
            data.add(RANDOM.nextGaussian());
        }
        UnivariateGaussian g =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(data, 0.0);
        double dof = N - 1;
        double alpha = 1.0 - confidence;
        StudentTDistribution.CDF cdf = new StudentTDistribution.CDF( dof );
        double z = -cdf.inverse(alpha / 2.0);
        double delta = z * Math.sqrt(g.getVariance() / data.size());

        StudentTConfidence conf = new StudentTConfidence();
        ConfidenceInterval c = conf.computeConfidenceInterval(data, confidence);

        final double EPS = 1e-5;
        assertEquals(g.getMean(), c.getCentralValue(), EPS);
        assertEquals(g.getMean() - delta, c.getLowerBound(), EPS);
        assertEquals(g.getMean() + delta, c.getUpperBound(), EPS);
        assertEquals(N, c.getNumSamples());
        assertEquals(confidence, c.getConfidence());


    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");
        StudentTConfidence t = new StudentTConfidence();
        StudentTConfidence clone = (StudentTConfidence) t.clone();
        assertNotSame(t, clone);
    }

    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.
     */
    public void testEvaluateNullHypothesis()
    {
        System.out.println("evaluateNullHypothesis");

        StudentTConfidence t = new StudentTConfidence();

        // Result is from Microsoft Excel's function TTEST(data1,data2,2,1)
        Collection<Double> data1 = new LinkedList<Double>();
        data1.add(1.0);
        data1.add(2.0);
        data1.add(3.0);
        data1.add(4.0);

        Collection<Double> data2 = new LinkedList<Double>();
        data2.add(1.0);
        data2.add(2.0);
        data2.add(3.0);
        data2.add(5.0);

        double result1 = t.evaluateNullHypothesis(data1, data2).getNullHypothesisProbability();
        System.out.println("Result: " + result1);
        assertEquals(0.3910022, result1, 1e-5);

        data2.clear();
        data2.add(1.0);
        data2.add(1.0);
        data2.add(1.0);
        data2.add(2.0);
        double result2 = t.evaluateNullHypothesis(data1, data2).getNullHypothesisProbability();
        System.out.println("Result: " + result2);
        assertEquals(0.0796049808088472, result2, 1e-5);

        data2.clear();
        data2.add(1.0);
        data2.add(1.0);
        data2.add(1.0);
        data2.add(4.0);
        double result3 = t.evaluateNullHypothesis(data1, data2).getNullHypothesisProbability();
        System.out.println("Result: " + result3);
        assertEquals(0.21516994193023, result3, 1e-5);


        data1.addAll(data1);
        data2.addAll(data2);
        double result4 = t.evaluateNullHypothesis(data1, data2).getNullHypothesisProbability();
        System.out.println("Result: " + result4);
        assertEquals(0.0479447721419457, result4, 1e-5);

        data2.clear();
        for (int i = 1; i <= 8; i++)
        {
            data2.add((double) i);
        }
        double result5 = t.evaluateNullHypothesis(data1, data2).getNullHypothesisProbability();
        System.out.println("Result: " + result5);
        assertEquals(0.0331455001143753, result5, 1e-5);

    }

    public static StudentTConfidence.Statistic createStatisticInstance()
    {
        double dof = RANDOM.nextDouble() * 100 + 1.0;
        StudentTDistribution.CDF cdf = new StudentTDistribution.CDF( dof );
        double x = RANDOM.nextDouble() / 2.0 + 0.5;
        double t = cdf.inverse(x);
        return new StudentTConfidence.Statistic(t, dof);
    }

    /**
     * Test of Statistic.clone method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println("Statistic.clone");

        StudentTConfidence.Statistic instance = createStatisticInstance();
        StudentTConfidence.Statistic clone = instance.clone();
        assertEquals(instance.getT(), clone.getT());
        assertEquals(instance.getDegreesOfFreedom(), clone.getDegreesOfFreedom());
        assertEquals(instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability());

        double p = StudentTConfidence.Statistic.twoTailTStatistic(instance.getT(), instance.getDegreesOfFreedom());
        assertEquals(p, instance.getNullHypothesisProbability(), 1e-5);
    }

    /**
     * Test of getT method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.Statistic.
     */
    public void testStatisticGetT()
    {
        System.out.println("Statistic.getT");

        StudentTConfidence.Statistic instance = createStatisticInstance();
        assertTrue(instance.getT() != 0.0);

    }

    /**
     * Test of setT method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.Statistic.
     */
    public void testStatisticSetT()
    {
        System.out.println("Statistic.setT");

        StudentTConfidence.Statistic instance = createStatisticInstance();
        assertTrue(instance.getT() != 0.0);

        double t2 = instance.getT();
        instance.setT(t2);
        assertEquals(t2, instance.getT());

    }

    /**
     * Test of getDegreesOfFreedom method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.Statistic.
     */
    public void testStatisticGetDegreesOfFreedom()
    {
        System.out.println("Statistic.getDegreesOfFreedom");

        StudentTConfidence.Statistic instance = createStatisticInstance();
        assertTrue(instance.getDegreesOfFreedom() > 0.0);

    }

    /**
     * Test of setDegreesOfFreedom method, of class gov.sandia.cognition.learning.util.statistics.StudentTConfidence.Statistic.
     */
    public void testStatisticSetDegreesOfFreedom()
    {
        System.out.println("Statistic.setDegreesOfFreedom");

        StudentTConfidence.Statistic instance = createStatisticInstance();
        assertTrue(instance.getDegreesOfFreedom() > 0.0);

        double dof2 = instance.getDegreesOfFreedom();
        instance.setDegreesOfFreedom(dof2);
        assertEquals(dof2, instance.getDegreesOfFreedom());

        try
        {
            instance.setDegreesOfFreedom(0.0);
            fail("degreesOfFreedom > 0.0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
    }

}
