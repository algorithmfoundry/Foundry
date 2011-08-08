/*
 * File:                ConfidenceIntervalTest.java
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

import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class ConfidenceIntervalTest extends TestCase
{

    public ConfidenceIntervalTest(String testName)
    {
        super(testName);
    }

    public final Random RANDOM = new Random(1);

    public ConfidenceInterval createInstance()
    {
        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        return new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);
    }

    /**
     * Test of constructor method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testConstructor()
    {
        System.out.println("constructor");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);
        instance = new ConfidenceInterval(centralValue, centralValue, centralValue, confidence, numSamples);
        instance = new ConfidenceInterval(centralValue, centralValue, upperBound, confidence, numSamples);
        instance = new ConfidenceInterval(centralValue, lowerBound, centralValue, confidence, numSamples);
        instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, 0.0, numSamples);
        instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, 1.0, numSamples);
        instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, 1);

        try
        {
            instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence + 1.0, numSamples);
            fail("Should have failed: " + instance);
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence - 1.0, numSamples);
            fail("Should have failed: " + instance);
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance = new ConfidenceInterval(centralValue, upperBound, lowerBound, confidence, numSamples);
            fail("Should have failed: " + instance);
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance = new ConfidenceInterval(lowerBound, centralValue, upperBound, confidence, numSamples);
            fail("Should have failed: " + instance);
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance = new ConfidenceInterval(upperBound, lowerBound, centralValue, confidence, numSamples);
            fail("Should have failed: " + instance);
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, 0);
            fail("Should have failed: " + instance);
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testClone()
    {
        System.out.println("clone");

        ConfidenceInterval instance = this.createInstance();

        ConfidenceInterval clone = (ConfidenceInterval) instance.clone();

        assertNotSame(clone, instance);

        assertEquals(clone.getCentralValue(), instance.getCentralValue());
        assertEquals(clone.getLowerBound(), instance.getLowerBound());
        assertEquals(clone.getUpperBound(), instance.getUpperBound());
        assertEquals(clone.getConfidence(), instance.getConfidence());
        assertEquals(clone.getNumSamples(), instance.getNumSamples());
    }

    /**
     * Test of getLowerBound method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testGetLowerBound()
    {
        System.out.println("getLowerBound");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(lowerBound, instance.getLowerBound());
    }

    /**
     * Test of setLowerBound method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testSetLowerBound()
    {
        System.out.println("setLowerBound");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(lowerBound, instance.getLowerBound());

        double v2 = lowerBound - 1.0;
        instance.setLowerBound(v2);
        assertEquals(v2, instance.getLowerBound());
    }

    /**
     * Test of getUpperBound method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testGetUpperBound()
    {
        System.out.println("getUpperBound");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(upperBound, instance.getUpperBound());
    }

    /**
     * Test of setUpperBound method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testSetUpperBound()
    {
        System.out.println("setUpperBound");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(upperBound, instance.getUpperBound());

        double v2 = upperBound += 1.0;
        instance.setUpperBound(v2);
        assertEquals(v2, instance.getUpperBound());
    }

    /**
     * Test of getCentralValue method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testGetCentralValue()
    {
        System.out.println("getCentralValue");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(centralValue, instance.getCentralValue());
    }

    /**
     * Test of setCentralValue method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testSetCentralValue()
    {
        System.out.println("setCentralValue");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(centralValue, instance.getCentralValue());

        instance.setCentralValue(lowerBound);
        assertEquals(lowerBound, instance.getCentralValue());
    }

    /**
     * Test of getConfidence method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testGetConfidence()
    {
        System.out.println("getConfidence");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(confidence, instance.getConfidence());
    }

    /**
     * Test of setConfidence method, of class gov.sandia.cognition.learning.util.function.cost.ConfidenceInterval.
     */
    public void testSetConfidence()
    {
        System.out.println("setConfidence");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(confidence, instance.getConfidence());

        double v2 = confidence / 2.0;
        instance.setConfidence(v2);
        assertEquals(v2, instance.getConfidence());
    }

    /**
     * Test of toString method, of class gov.sandia.cognition.learning.util.statistics.ConfidenceInterval.
     */
    public void testToString()
    {
        System.out.println("toString");
        ConfidenceInterval instance = this.createInstance();

        String s = instance.toString();
        System.out.println( "Interval: " + s );
        assertNotNull( s );
        assertTrue( s.length() > 0 );
    }

    /**
     * Test of getNumSamples method, of class gov.sandia.cognition.learning.util.statistics.ConfidenceInterval.
     */
    public void testGetNumSamples()
    {
        System.out.println("getNumSamples");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(numSamples, instance.getNumSamples());
    }

    /**
     * Test of setNumSamples method, of class gov.sandia.cognition.learning.util.statistics.ConfidenceInterval.
     */
    public void testSetNumSamples()
    {
        System.out.println("setNumSamples");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertEquals(numSamples, instance.getNumSamples());
        int n2 = numSamples + 1;
        instance.setNumSamples(n2);
        assertEquals(n2, instance.getNumSamples());

        instance.setNumSamples(1);

        try
        {
            instance.setNumSamples(0);
            fail("numSamples must be > 0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
    }

    /**
     * Test of withinInterval method, of class gov.sandia.cognition.learning.util.statistics.ConfidenceInterval.
     */
    public void testWithinInterval()
    {
        System.out.println("withinInterval");

        double lowerBound = RANDOM.nextGaussian();
        double centralValue = lowerBound + RANDOM.nextDouble();
        double upperBound = centralValue + RANDOM.nextDouble();
        double confidence = RANDOM.nextDouble();
        int numSamples = RANDOM.nextInt(1000) + 10;
        ConfidenceInterval instance = new ConfidenceInterval(centralValue, lowerBound, upperBound, confidence, numSamples);

        assertTrue(instance.withinInterval(lowerBound));
        assertTrue(instance.withinInterval(centralValue));
        assertTrue(instance.withinInterval(upperBound));

        assertFalse(instance.withinInterval(upperBound + 1.0));
        assertFalse(instance.withinInterval(lowerBound - 1.0));
    }

}
