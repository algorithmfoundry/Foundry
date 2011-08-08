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

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.data.DefaultTargetEstimatePair;
import gov.sandia.cognition.learning.data.DefaultWeightedTargetEstimatePair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultBinaryConfusionMatrix.
 *
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.1
 */
public class DefaultBinaryConfusionMatrixTest
{

    /** Random number generator. */
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public DefaultBinaryConfusionMatrixTest()
    {
    }
    
    public DefaultBinaryConfusionMatrix createRandomInstance()
    {
        return createRandomInstance(this.random);
    }

    protected DefaultBinaryConfusionMatrix createEmptyInstance()
    {
        return new DefaultBinaryConfusionMatrix();
    }

    protected DefaultBinaryConfusionMatrix createPopulatedInstance()
    {
        DefaultBinaryConfusionMatrix instance = new DefaultBinaryConfusionMatrix();
        instance.setTruePositivesCount(random.nextDouble() * 10.0);
        instance.setTrueNegativesCount(random.nextDouble() * 10.0);
        instance.setFalsePositivesCount(random.nextDouble() * 10.0);
        instance.setFalseNegativesCount(random.nextDouble() * 10.0);
        return instance;
    }

    public static DefaultBinaryConfusionMatrix createRandomInstance(
        final Random random)
    {
        int N = 1000;
        DefaultBinaryConfusionMatrix result = new DefaultBinaryConfusionMatrix();

        result.setTruePositivesCount(random.nextInt(N));
        result.setTrueNegativesCount(random.nextInt(N));
        result.setFalsePositivesCount(random.nextInt(N));
        result.setFalseNegativesCount(random.nextInt(N));
        return result;
    }

    public static DefaultBinaryConfusionMatrix createFPFNTPTN(
        final double falsePositives,
        final double falseNegatives,
        final double truePositives,
        final double trueNegatives)
    {
        DefaultBinaryConfusionMatrix result = new DefaultBinaryConfusionMatrix();
        result.setFalsePositivesCount(falsePositives);
        result.setFalseNegativesCount(falseNegatives);
        result.setTruePositivesCount(truePositives);
        result.setTrueNegativesCount(trueNegatives);
        return result;
    }

    /**
     * Test of constructors of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testConstructors()
    {
        DefaultBinaryConfusionMatrix instance = new DefaultBinaryConfusionMatrix();
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);
    }

    /**
     * Test of clone method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testClone()
    {
        System.out.println("clone");

        DefaultBinaryConfusionMatrix instance = createRandomInstance();
        DefaultBinaryConfusionMatrix clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getFalsePositivesCount(), clone.getFalsePositivesCount(), 0.0);
        assertEquals(instance.getFalseNegativesCount(), clone.getFalseNegativesCount(), 0.0);
        assertEquals(instance.getTruePositivesCount(), clone.getTruePositivesCount(), 0.0);
        assertEquals(instance.getTrueNegativesCount(), clone.getTrueNegativesCount(), 0.0);
    }

    /**
     * Test of getTrueNegativesRate method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetTrueNegativesRate()
    {
        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        DefaultBinaryConfusionMatrix instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(tn / (tn + fp), instance.getTrueNegativesRate(), eps);
    }

    /**
     * Test of getTruePositivesRate method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetTruePositivesRate()
    {
        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        DefaultBinaryConfusionMatrix instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(tp / (tp + fn), instance.getTruePositivesRate(), eps);
    }

    /**
     * Test of getFalsePositivesRate method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetFalsePositivesRate()
    {
        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        DefaultBinaryConfusionMatrix instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(fp / (fp + tn), instance.getFalsePositivesRate(), eps);
    }


    /**
     * Test of getFalseNegativesRate method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetFalseNegativesRate()
    {
        double eps = 1e-6;
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        DefaultBinaryConfusionMatrix instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(fn / (fn + tp), instance.getFalseNegativesRate(), eps);
    }


    /**
     * Test of add method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testAdd()
    {
        DefaultBinaryConfusionMatrix instance = new DefaultBinaryConfusionMatrix();
        assertTrue(instance.isEmpty());
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);

        instance.add(true, false, 1);
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(1.0, instance.getFalseNegativesCount(), 0.0);

        instance.add(false, false, 3);
        assertEquals(4.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(3.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(1.0, instance.getFalseNegativesCount(), 0.0);

        instance.add(false, true, 6);
        assertEquals(10.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(3.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(6.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(1.0, instance.getFalseNegativesCount(), 0.0);

        instance.add(true, true, 4.4);
        assertEquals(14.4, instance.getTotalCount(), 0.0);
        assertEquals(4.4, instance.getTruePositivesCount(), 0.0);
        assertEquals(3.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(6.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(1.0, instance.getFalseNegativesCount(), 0.0);
    }

    /**
     * Test of clear method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testClear()
    {
        DefaultBinaryConfusionMatrix instance =
            new DefaultBinaryConfusionMatrix();
        instance.clear();
        assertEquals(0.0, instance.getTotalCount(), 0.0);

        instance = this.createPopulatedInstance();
        instance.clear();
        assertEquals(0.0, instance.getTotalCount(), 0.0);
    }

    /**
     * Test of getTruePositivesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetTruePositivesCount()
    {
        this.testSetTruePositivesCount();
    }

    /**
     * Test of setTruePositivesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testSetTruePositivesCount()
    {
        double truePositivesCount = 0.0;
        DefaultBinaryConfusionMatrix instance =
            new DefaultBinaryConfusionMatrix();
        assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);

        truePositivesCount = 1.0;
        instance.setTruePositivesCount(truePositivesCount);
        assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);

        truePositivesCount = 2.3;
        instance.setTruePositivesCount(truePositivesCount);
        assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);

        truePositivesCount = 0.6;
        instance.setTruePositivesCount(truePositivesCount);
        assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);

        truePositivesCount = 0.0;
        instance.setTruePositivesCount(truePositivesCount);
        assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);

        truePositivesCount = random.nextDouble();
        instance.setTruePositivesCount(truePositivesCount);
        assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);

        double[] badValues = {-1.0, -0.1, -2.3, -random.nextDouble()};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setTruePositivesCount(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(truePositivesCount, instance.getTruePositivesCount(), 0.0);
        }

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(tp, instance.getTruePositivesCount(), 0.0);
    }

    /**
     * Test of getFalsePositivesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetFalsePositivesCount()
    {
        this.testSetFalsePositivesCount();
    }

    /**
     * Test of setFalsePositivesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testSetFalsePositivesCount()
    {
        double falsePositivesCount = 0.0;
        DefaultBinaryConfusionMatrix instance =
            new DefaultBinaryConfusionMatrix();
        assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);

        falsePositivesCount = 1.0;
        instance.setFalsePositivesCount(falsePositivesCount);
        assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);

        falsePositivesCount = 2.3;
        instance.setFalsePositivesCount(falsePositivesCount);
        assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);

        falsePositivesCount = 0.6;
        instance.setFalsePositivesCount(falsePositivesCount);
        assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);

        falsePositivesCount = 0.0;
        instance.setFalsePositivesCount(falsePositivesCount);
        assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);

        falsePositivesCount = random.nextDouble();
        instance.setFalsePositivesCount(falsePositivesCount);
        assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);

        double[] badValues = {-1.0, -0.1, -2.3, -random.nextDouble()};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setFalsePositivesCount(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(falsePositivesCount, instance.getFalsePositivesCount(), 0.0);
        }

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(fp, instance.getFalsePositivesCount(), 0.0);
    }

    /**
     * Test of getTrueNegativesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetTrueNegativesCount()
    {
        this.testSetTrueNegativesCount();
    }

    /**
     * Test of setTrueNegativesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testSetTrueNegativesCount()
    {
        double trueNegativesCount = 0.0;
        DefaultBinaryConfusionMatrix instance =
            new DefaultBinaryConfusionMatrix();
        assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);

        trueNegativesCount = 1.0;
        instance.setTrueNegativesCount(trueNegativesCount);
        assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);

        trueNegativesCount = 2.3;
        instance.setTrueNegativesCount(trueNegativesCount);
        assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);

        trueNegativesCount = 0.6;
        instance.setTrueNegativesCount(trueNegativesCount);
        assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);

        trueNegativesCount = 0.0;
        instance.setTrueNegativesCount(trueNegativesCount);
        assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);

        trueNegativesCount = random.nextDouble();
        instance.setTrueNegativesCount(trueNegativesCount);
        assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);

        double[] badValues = {-1.0, -0.1, -2.3, -random.nextDouble()};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setTrueNegativesCount(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(trueNegativesCount, instance.getTrueNegativesCount(), 0.0);
        }
        
        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(tn, instance.getTrueNegativesCount(), 0.0);
    }

    /**
     * Test of getFalseNegativesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testGetFalseNegativesCount()
    {
        this.testSetFalseNegativesCount();
    }

    /**
     * Test of setFalseNegativesCount method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testSetFalseNegativesCount()
    {
        double falseNegativesCount = 0.0;
        DefaultBinaryConfusionMatrix instance =
            new DefaultBinaryConfusionMatrix();
        assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);

        falseNegativesCount = 1.0;
        instance.setFalseNegativesCount(falseNegativesCount);
        assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);

        falseNegativesCount = 2.3;
        instance.setFalseNegativesCount(falseNegativesCount);
        assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);

        falseNegativesCount = 0.6;
        instance.setFalseNegativesCount(falseNegativesCount);
        assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);

        falseNegativesCount = 0.0;
        instance.setFalseNegativesCount(falseNegativesCount);
        assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);

        falseNegativesCount = random.nextDouble();
        instance.setFalseNegativesCount(falseNegativesCount);
        assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);

        double[] badValues = {-1.0, -0.1, -2.3, -random.nextDouble()};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setFalseNegativesCount(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(falseNegativesCount, instance.getFalseNegativesCount(), 0.0);
        }

        double fp = random.nextDouble();
        double fn = random.nextDouble();
        double tp = random.nextDouble();
        double tn = random.nextDouble();
        instance = createFPFNTPTN(fp, fn, tp, tn);
        assertEquals(fn, instance.getFalseNegativesCount(), 0.0);
    }


    /**
     * Test of toString method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testToString()
    {
        DefaultBinaryConfusionMatrix c = this.createRandomInstance();
        String s = c.toString();
        assertNotNull(s);
    }

    /**
     * Test of binarizeOnTrueCategory method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testBinarizeOnTrueCategory()
    {
        ConfusionMatrix<String> other = new DefaultConfusionMatrix<String>();
        DefaultBinaryConfusionMatrix instance =
            DefaultBinaryConfusionMatrix.binarizeOnTrueCategory(other, "good");
        assertTrue(instance.isEmpty());
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);

        other.add("good", "good", 10.0);
        other.add("good", "bad", 9.0);
        other.add("bad", "bad", 8.0);
        other.add("bad", "good", 7.0);
        other.add("other", "good", 6.0);
        other.add("other", "bad", 5.0);
        other.add("other", "other", 4.0);
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueCategory(other, "good");
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(10.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(17.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(13.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(9.0, instance.getFalseNegativesCount(), 0.0);

        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueCategory(other, "bad");
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(8.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(20.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(14.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(7.0, instance.getFalseNegativesCount(), 0.0);


        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueCategory(other, "nothing");
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(49.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);
    }

    /**
     * Test of binarizeOnTrueSet method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testBinarizeOnTrueSet()
    {
        Set<String> trueSet = new LinkedHashSet<String>();
        trueSet.add("good");
        ConfusionMatrix<String> other = new DefaultConfusionMatrix<String>();
        DefaultBinaryConfusionMatrix instance =
            DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertTrue(instance.isEmpty());
        assertEquals(other.getTotalCount(), instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);

        other.add("good", "good", 10.0);
        other.add("good", "bad", 9.0);
        other.add("bad", "bad", 8.0);
        other.add("bad", "good", 7.0);
        other.add("other", "good", 6.0);
        other.add("other", "bad", 5.0);
        other.add("other", "other", 4.0);
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(10.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(17.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(13.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(9.0, instance.getFalseNegativesCount(), 0.0);

        trueSet.add("other");
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(20.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(8.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(7.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(14.0, instance.getFalseNegativesCount(), 0.0);

        trueSet.add("unknown");
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(20.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(8.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(7.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(14.0, instance.getFalseNegativesCount(), 0.0);

        trueSet.add("bad");
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(49.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);

        trueSet = Collections.singleton("bad");
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(8.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(20.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(14.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(7.0, instance.getFalseNegativesCount(), 0.0);

        trueSet = Collections.emptySet();
        instance = DefaultBinaryConfusionMatrix.binarizeOnTrueSet(other, trueSet);
        assertFalse(instance.isEmpty());
        assertEquals(49.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(49.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);
    }


    /**
     * Test of create method, of classDefaultBinaryConfusionMatrix.
     */
    @Test
    public void testCreate()
    {
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

                input.add(DefaultWeightedTargetEstimatePair.create(
                    target, estimate, weight));
            }
            DefaultBinaryConfusionMatrix result = DefaultBinaryConfusionMatrix.create(input, weightIfAvailable);
            assertEquals(tp, result.getTruePositivesCount(), 0.0);
            assertEquals(tn, result.getTrueNegativesCount(), 0.0);
            assertEquals(fp, result.getFalsePositivesCount(), 0.0);
            assertEquals(fn, result.getFalseNegativesCount(), 0.0);
        }

        DefaultBinaryConfusionMatrix.PerformanceEvaluator<Object> evaluator =
            new DefaultBinaryConfusionMatrix.PerformanceEvaluator<Object>();
        DefaultBinaryConfusionMatrix result = evaluator.evaluatePerformance(input);
        assertNotNull( result );


        Collection<TargetEstimatePair<Boolean, Boolean>> data = new LinkedList<TargetEstimatePair<Boolean, Boolean>>();
        DefaultBinaryConfusionMatrix instance = DefaultBinaryConfusionMatrix.create(data, false);
        assertTrue(instance.isEmpty());
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);

        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        instance = DefaultBinaryConfusionMatrix.create(data, false);
        assertFalse(instance.isEmpty());
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(1.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);


        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, false));
        instance = DefaultBinaryConfusionMatrix.create(data);
        assertFalse(instance.isEmpty());
        assertEquals(10.0, instance.getTotalCount(), 0.0);
        assertEquals(4.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(3.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(2.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(1.0, instance.getFalseNegativesCount(), 0.0);
    }

    /**
     * Test of createFromActualPredictedPairs method, of class DefaultBinaryConfusionMatrix.
     */
    @Test
    public void testCreateFromActualPredictedPairs()
    {
        Collection<Pair<Boolean, Boolean>> data = new LinkedList<Pair<Boolean, Boolean>>();
        DefaultBinaryConfusionMatrix instance = DefaultBinaryConfusionMatrix.createFromActualPredictedPairs(data);
        assertTrue(instance.isEmpty());
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(0.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);

        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        instance = DefaultBinaryConfusionMatrix.createFromActualPredictedPairs(data);
        assertFalse(instance.isEmpty());
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(0.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(1.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(0.0, instance.getFalseNegativesCount(), 0.0);


        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, false));
        instance = DefaultBinaryConfusionMatrix.createFromActualPredictedPairs(data);
        assertFalse(instance.isEmpty());
        assertEquals(10.0, instance.getTotalCount(), 0.0);
        assertEquals(4.0, instance.getTruePositivesCount(), 0.0);
        assertEquals(3.0, instance.getTrueNegativesCount(), 0.0);
        assertEquals(2.0, instance.getFalsePositivesCount(), 0.0);
        assertEquals(1.0, instance.getFalseNegativesCount(), 0.0);
    }


}
