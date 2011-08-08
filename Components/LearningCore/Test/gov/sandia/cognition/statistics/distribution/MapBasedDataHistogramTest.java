/*
 * File:                MapBasedDataHistogramTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MapBasedDataHistogramTest
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class MapBasedDataHistogramTest
    extends TestCase
{

    public MapBasedDataHistogramTest(
        String testName)
    {
        super(testName);
    }

    public ProbabilityMassFunction<String> createPMFinstance()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        instance.add("a");
        instance.add("a");
        instance.add("b");
        instance.add("c");
        instance.add("c", 4);
        instance.add("d", 1);
        return instance;
    }

    public void testConstructors()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0, instance.getTotalCount());
        assertTrue(instance.getValues().isEmpty());

        instance = new MapBasedDataHistogram<String>(2);
        assertEquals(0, instance.getTotalCount());
        assertTrue(instance.getValues().isEmpty());

        instance = new MapBasedDataHistogram<String>(
            Arrays.asList(new String[] { "a", "b"}));
        instance.add("a", 4);
        instance.add("c", 7);

        instance = new MapBasedDataHistogram<String>(instance);
        assertEquals(13, instance.getTotalCount());
        assertEquals(5, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(7, instance.getCount("c"));
    }

    public void testClone()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        MapBasedDataHistogram<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.countMap, clone.countMap);
        assertEquals(0, clone.getTotalCount());
        assertTrue(clone.getValues().isEmpty());
        assertEquals(0, instance.getTotalCount());
        assertTrue(instance.getValues().isEmpty());

        instance.add("a", 4);
        instance.add("b", 7);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.countMap, clone.countMap);
        assertEquals(11, clone.getTotalCount());
        assertEquals(4, clone.getCount("a"));
        assertEquals(7, clone.getCount("b"));
        assertEquals(11, instance.getTotalCount());
        assertEquals(4, instance.getCount("a"));
        assertEquals(7, instance.getCount("b"));
    }

    /**
     * Test of add method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testAdd()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getValues().size());
        assertFalse(instance.getValues().contains("a"));

        instance.add("a");
        assertEquals(1, instance.getTotalCount());
        assertEquals(1, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(1, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));

        instance.add("a");
        assertEquals(2, instance.getTotalCount());
        assertEquals(2, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(1, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));

        instance.add("b");
        assertEquals(3, instance.getTotalCount());
        assertEquals(2, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(2, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));

        instance.add("a");
        assertEquals(4, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(2, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));

        instance.add("c", 7);
        assertEquals(11, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(7, instance.getCount("c"));
        assertEquals(3, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));

        boolean exceptionThrown = false;
        try
        {
            instance.add("d", -1);
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

    /**
     * Test of remove method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testRemove()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getValues().size());

        instance.remove("a");
        instance.remove("d", 7);

        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getValues().size());

        instance.add("a");
        instance.add("a");
        instance.add("b");
        instance.add("a");
        instance.add("c", 7);
        instance.add("b");
        assertEquals(12, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(2, instance.getCount("b"));
        assertEquals(7, instance.getCount("c"));
        assertEquals(3, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));

        instance.remove("b");
        assertEquals(11, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(7, instance.getCount("c"));
        assertEquals(3, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));

        instance.remove("b");
        assertEquals(10, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(7, instance.getCount("c"));
        assertEquals(2, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));

        instance.remove("b");
        assertEquals(10, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(7, instance.getCount("c"));
        assertEquals(2, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));


        instance.remove("c", 4);
        assertEquals(6, instance.getTotalCount());
        assertEquals(3, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(3, instance.getCount("c"));
        assertEquals(2, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));


        instance.remove("a", 100);
        assertEquals(3, instance.getTotalCount());
        assertEquals(0, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(3, instance.getCount("c"));
        assertEquals(1, instance.getValues().size());
        assertFalse(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));



        boolean exceptionThrown = false;
        try
        {
            instance.remove("d", -1);
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

    /**
     * Test of getValues method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetValues()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertTrue(instance.getValues().isEmpty());
        assertFalse(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertFalse(instance.getValues().contains("c"));

        instance.add("a");
        assertEquals(1, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertFalse(instance.getValues().contains("c"));

        instance.add("a");
        assertEquals(1, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertFalse(instance.getValues().contains("c"));

        instance.add("b");
        assertEquals(2, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertFalse(instance.getValues().contains("c"));

        instance.add("c", 4);
        assertEquals(3, instance.getValues().size());
        assertTrue(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));

        instance.remove("a", 2);
        assertEquals(2, instance.getValues().size());
        assertFalse(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertTrue(instance.getValues().contains("c"));

        instance.remove("c", 4);
        assertEquals(1, instance.getValues().size());
        assertFalse(instance.getValues().contains("a"));
        assertTrue(instance.getValues().contains("b"));
        assertFalse(instance.getValues().contains("c"));

        instance.remove("b", 1);
        assertEquals(0, instance.getValues().size());
        assertFalse(instance.getValues().contains("a"));
        assertFalse(instance.getValues().contains("b"));
        assertFalse(instance.getValues().contains("c"));
    }

    /**
     * Test of getCount method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetCount()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("d"));

        instance.add("a");
        assertEquals(1, instance.getCount("a"));

        instance.add("a");
        assertEquals(2, instance.getCount("a"));

        instance.add("b");
        assertEquals(2, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));

        instance.add("c", 4);
        assertEquals(2, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(4, instance.getCount("c"));

        instance.add("a", 2);
        assertEquals(4, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(4, instance.getCount("c"));

        instance.remove("a");
        assertEquals(3, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(4, instance.getCount("c"));

        instance.remove("c", 3);
        assertEquals(3, instance.getCount("a"));
        assertEquals(1, instance.getCount("b"));
        assertEquals(1, instance.getCount("c"));

        instance.remove("b", 1);
        assertEquals(3, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(1, instance.getCount("c"));

        instance.add("d");
        assertEquals(3, instance.getCount("a"));
        assertEquals(0, instance.getCount("b"));
        assertEquals(1, instance.getCount("c"));
        assertEquals(1, instance.getCount("d"));
    }

    /**
     * Test of getFraction method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetFraction()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0.0, instance.getFraction("a"));
        assertEquals(0.0, instance.getFraction("b"));
        assertEquals(0.0, instance.getFraction("c"));
        assertEquals(0.0, instance.getFraction("d"));

        instance.add("a");
        assertEquals(1 / 1.0, instance.getFraction("a"));

        instance.add("a");
        assertEquals(2 / 2.0, instance.getFraction("a"));

        instance.add("b");
        assertEquals(2 / 3.0, instance.getFraction("a"));
        assertEquals(1 / 3.0, instance.getFraction("b"));

        instance.add("c", 4);
        assertEquals(2 / 7.0, instance.getFraction("a"));
        assertEquals(1 / 7.0, instance.getFraction("b"));
        assertEquals(4 / 7.0, instance.getFraction("c"));

        instance.add("a", 2);
        assertEquals(4 / 9.0, instance.getFraction("a"));
        assertEquals(1 / 9.0, instance.getFraction("b"));
        assertEquals(4 / 9.0, instance.getFraction("c"));

        instance.remove("a");
        assertEquals(3 / 8.0, instance.getFraction("a"));
        assertEquals(1 / 8.0, instance.getFraction("b"));
        assertEquals(4 / 8.0, instance.getFraction("c"));

        instance.remove("c", 3);
        assertEquals(3 / 5.0, instance.getFraction("a"));
        assertEquals(1 / 5.0, instance.getFraction("b"));
        assertEquals(1 / 5.0, instance.getFraction("c"));

        instance.remove("b", 1);
        assertEquals(3 / 4.0, instance.getFraction("a"));
        assertEquals(0 / 4.0, instance.getFraction("b"));
        assertEquals(1 / 4.0, instance.getFraction("c"));

        instance.add("d");
        assertEquals(3 / 5.0, instance.getFraction("a"));
        assertEquals(0 / 5.0, instance.getFraction("b"));
        assertEquals(1 / 5.0, instance.getFraction("c"));
        assertEquals(1 / 5.0, instance.getFraction("d"));
    }

    /**
     * Test of getEntropy method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetEntropy()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0.0, instance.getEntropy());

        instance.add("a");
        assertEquals(0.0, instance.getEntropy());

        instance.add("a");
        assertEquals(0.0, instance.getEntropy());

        instance.add("b");
        assertEquals(0.9183, instance.getEntropy(), 0.0001);

        instance.add("c");
        assertEquals(1.5000, instance.getEntropy(), 0.0001);

        instance.add("c", 4);
        assertEquals(1.2988, instance.getEntropy(), 0.0001);

        instance.add("d", 1);
        assertEquals(1.6577, instance.getEntropy(), 0.0001);
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testEvaluate()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0.0, instance.evaluate("a"));
        assertEquals(0.0, instance.evaluate("b"));
        assertEquals(0.0, instance.evaluate("c"));
        assertEquals(0.0, instance.evaluate("d"));

        instance.add("a");
        assertEquals(1 / 1.0, instance.evaluate("a"));

        instance.add("a");
        assertEquals(2 / 2.0, instance.evaluate("a"));

        instance.add("b");
        assertEquals(2 / 3.0, instance.evaluate("a"));
        assertEquals(1 / 3.0, instance.evaluate("b"));

        instance.add("c", 4);
        assertEquals(2 / 7.0, instance.evaluate("a"));
        assertEquals(1 / 7.0, instance.evaluate("b"));
        assertEquals(4 / 7.0, instance.evaluate("c"));

        instance.add("a", 2);
        assertEquals(4 / 9.0, instance.evaluate("a"));
        assertEquals(1 / 9.0, instance.evaluate("b"));
        assertEquals(4 / 9.0, instance.evaluate("c"));

        instance.remove("a");
        assertEquals(3 / 8.0, instance.evaluate("a"));
        assertEquals(1 / 8.0, instance.evaluate("b"));
        assertEquals(4 / 8.0, instance.evaluate("c"));

        instance.remove("c", 3);
        assertEquals(3 / 5.0, instance.evaluate("a"));
        assertEquals(1 / 5.0, instance.evaluate("b"));
        assertEquals(1 / 5.0, instance.evaluate("c"));

        instance.remove("b", 1);
        assertEquals(3 / 4.0, instance.evaluate("a"));
        assertEquals(0 / 4.0, instance.evaluate("b"));
        assertEquals(1 / 4.0, instance.evaluate("c"));

        instance.add("d");
        assertEquals(3 / 5.0, instance.evaluate("a"));
        assertEquals(0 / 5.0, instance.evaluate("b"));
        assertEquals(1 / 5.0, instance.evaluate("c"));
        assertEquals(1 / 5.0, instance.evaluate("d"));
    }

    /**
     * Test of getMaximumCount method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetMaximumCount()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertEquals(0, instance.getMaximumCount());

        instance.add("a");
        assertEquals(1, instance.getMaximumCount());
        instance.add("b");
        assertEquals(1, instance.getMaximumCount());
        instance.add("b");
        assertEquals(2, instance.getMaximumCount());
        instance.add("c", 7);
        assertEquals(7, instance.getMaximumCount());
    }

    /**
     * Test of getMaximumValue method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetMaximumValue()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertNull(instance.getMaximumValue());

        instance.add("a");
        assertEquals("a", instance.getMaximumValue());
        instance.add("b");
        assertTrue("a".equals(instance.getMaximumValue())); // a should be the first value encountered.
        instance.add("b");
        assertEquals("b", instance.getMaximumValue());
        instance.add("c", 7);
        assertEquals("c", instance.getMaximumValue());
    }

    /**
     * Test of getMaximumValues method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetMaximumValues()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();
        assertTrue(instance.getMaximumValues().isEmpty());

        instance.add("a");
        assertEquals(1, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("a"));
        instance.add("b");
        assertEquals(2, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("a"));
        assertTrue(instance.getMaximumValues().contains("b"));
        instance.add("b");
        assertEquals(1, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("b"));
        instance.add("c", 7);
        assertEquals(1, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("c"));
    }

    /**
     * Test of getTotalCount method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetTotalCount()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();

        assertEquals(0, instance.getTotalCount());
        instance.add("a");
        assertEquals(1, instance.getTotalCount());
        instance.add("a");
        assertEquals(2, instance.getTotalCount());
        instance.add("b");
        assertEquals(3, instance.getTotalCount());
        instance.add("c", 4);
        assertEquals(7, instance.getTotalCount());
        instance.add("a", 2);
        assertEquals(9, instance.getTotalCount());
        instance.remove("a");
        assertEquals(8, instance.getTotalCount());
        instance.remove("c", 3);
        assertEquals(5, instance.getTotalCount());
        instance.remove("b", 1);
        assertEquals(4, instance.getTotalCount());
        instance.add("d");
        assertEquals(5, instance.getTotalCount());
    }


    /**
     * Test of getMeanCount method, of class MapBasedDataHistogram.
     */
    public void testGetMeanCount()
    {
        MapBasedDataHistogram<String> instance =
            new MapBasedDataHistogram<String>();

        final double epsilon = 0.0001;

        assertEquals(0.0, instance.getMeanCount(), epsilon);
        instance.add("a");
        assertEquals(1.0, instance.getMeanCount(), epsilon);
        instance.add("a");
        assertEquals(2.0, instance.getMeanCount(), epsilon);
        instance.add("b");
        assertEquals(3.0 / 2.0, instance.getMeanCount(), epsilon);
        instance.add("c", 4);
        assertEquals(7.0 / 3.0, instance.getMeanCount(), epsilon);
        instance.add("a", 2);
        assertEquals(9.0 / 3.0, instance.getMeanCount(), epsilon);
        instance.remove("a");
        assertEquals(8.0 / 3.0, instance.getMeanCount(), epsilon);
        instance.remove("c", 3);
        assertEquals(5.0 / 3.0, instance.getMeanCount(), epsilon);
        instance.remove("b", 1);
        assertEquals(4.0 / 2.0, instance.getMeanCount(), epsilon);
        instance.add("d");
        assertEquals(5.0 / 3.0, instance.getMeanCount(), epsilon);
    }

    /**
     * logEvaluate
     */
    public void testLogEvaluate()
    {
        System.out.println( "logEvaluate" );

        ProbabilityMassFunction<String> pmf = this.createPMFinstance();
        for( String s : pmf.getDomain() )
        {
            double plog = pmf.logEvaluate(s);
            double p = pmf.evaluate(s);
            double phat = Math.exp(plog);
            assertEquals( p, phat, 1e-5 );
        }

    }
}
