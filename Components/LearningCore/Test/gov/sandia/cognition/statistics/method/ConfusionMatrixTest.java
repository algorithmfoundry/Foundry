/*
 * File:                ConfusionMatrixTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 23, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.data.WeightedTargetEstimatePair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class ConfusionMatrixTest
    extends TestCase
{

    public ConfusionMatrixTest(String testName)
    {
        super(testName);
    }

    public static Random random = new Random( 1 );
    
    public static ConfusionMatrix createInstance()
    {
        int N = 1000;
        return new ConfusionMatrix(
            random.nextInt( N ), random.nextInt( N ), random.nextInt( N ), random.nextInt( N ) );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testClone()
    {
        System.out.println("clone");

        ConfusionMatrix instance = createInstance();
        ConfusionMatrix clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getFalsePositives(), clone.getFalsePositives());
        assertEquals(instance.getFalseNegatives(), clone.getFalseNegatives());
        assertEquals(instance.getTruePositives(), clone.getTruePositives());
        assertEquals(instance.getTrueNegatives(), clone.getTrueNegatives());
    }

    /**
     * Test of compute method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    @SuppressWarnings("unchecked")
    public void testCompute()
    {
        System.out.println("compute");

        int N = random.nextInt(1000) + 100;
        Collection<TargetEstimatePair<Boolean, Boolean>> input = 
            new ArrayList<TargetEstimatePair<Boolean, Boolean>>(N);
        Random r = new Random();
        double tn = 0;
        double tp = 0;
        double fn = 0;
        double fp = 0;
        boolean weightIfAvailable = true;
        for (int i = 0; i < 2; i++)
        {
            weightIfAvailable = !weightIfAvailable;
            for (int n = 0; n < N; n++)
            {
                boolean target = r.nextBoolean();
                boolean estimate = r.nextBoolean();


                double weight;
                if (weightIfAvailable)
                {
                    weight = random.nextDouble();
                }
                else
                {
                    weight = 1.0;
                }

                if (target && estimate)
                {
                    tp += weight;
                }
                if (!target && !estimate)
                {
                    tn += weight;
                }
                if (!target && estimate)
                {
                    fp += weight;
                }
                if (target && !estimate)
                {
                    fn += weight;
                }

                input.add(new WeightedTargetEstimatePair<Boolean, Boolean>(target, estimate, weight));
            }
            ConfusionMatrix result = ConfusionMatrix.compute(input, weightIfAvailable);
            assertEquals(tp, result.getTruePositives());
            assertEquals(tn, result.getTrueNegatives());
            assertEquals(fp, result.getFalsePositives());
            assertEquals(fn, result.getFalseNegatives());
        }

        ConfusionMatrix.PerformanceEvaluator evaluator =
            new ConfusionMatrix.PerformanceEvaluator();
        ConfusionMatrix result = evaluator.evaluatePerformance(input);
        assertNotNull( result );


    }

    /**
     * Test of getTrueNegatives method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetTrueNegatives()
    {
        System.out.println("getTrueNegatives");

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(tn, instance.getTrueNegatives());

    }

    /**
     * Test of getTrueNegativesPct method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetTrueNegativesPct()
    {
        System.out.println("getTrueNegativesPct");

        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(tn / (tn + fp), instance.getTrueNegativesPct(), eps);
    }

    /**
     * Test of getTruePositives method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetTruePositives()
    {
        System.out.println("getTruePositives");

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(tp, instance.getTruePositives());

    }

    /**
     * Test of getTruePositivesPct method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetTruePositivesPct()
    {
        System.out.println("getTruePositivesPct");

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(tp / (tp + fn), instance.getTruePositivesPct());
    }

    /**
     * Test of getFalsePositives method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetFalsePositives()
    {
        System.out.println("getFalsePositives");

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(fp, instance.getFalsePositives());
    }

    /**
     * Test of getFalsePositivesPct method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetFalsePositivesPct()
    {
        System.out.println("getFalsePositivesPct");

        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(fp / (fp + tn), instance.getFalsePositivesPct(), eps);
    }

    /**
     * Test of getFalseNegatives method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetFalseNegatives()
    {
        System.out.println("getFalseNegatives");

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(fn, instance.getFalseNegatives());
    }

    /**
     * Test of getFalseNegativesPct method, of class gov.sandia.cognition.learning.util.function.cost.ConfusionMatrix.
     */
    public void testGetFalseNegativesPct()
    {
        System.out.println("getFalseNegativesPct");

        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        ConfusionMatrix instance = new ConfusionMatrix(fp, fn, tp, tn);
        assertEquals(fn / (fn + tp), instance.getFalseNegativesPct(), eps);
    }

    /**
     * toString
     */
    public void testToString()
    {
        System.out.println( "toString" );

        ConfusionMatrix c = this.createInstance();
        String s = c.toString();
        System.out.println( "Matrix: " + s );
    }

}
