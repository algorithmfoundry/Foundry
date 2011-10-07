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

import gov.sandia.cognition.statistics.DataDistribution;
import java.util.ArrayList;
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

    public static double TOLERANCE = 1e-5;

    public MapBasedDataHistogramTest(
        String testName)
    {
        super(testName);
    }


    public DefaultDataDistribution<String> createInstanceEmpty()
    {
        return new DefaultDataDistribution.PMF<String>();
    }

    public DefaultDataDistribution<String> createInstancePopulated()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        instance.increment("a");
        instance.increment("a");
        instance.increment("b");
        instance.increment("c");
        instance.increment("c", 4);
        instance.increment("d", 1);
        return instance;
    }

    public void testConstructors()
    {
        DefaultDataDistribution<String> instance =
            new DefaultDataDistribution<String>();
        assertEquals(0.0, instance.getTotal());
        assertTrue(instance.getDomain().isEmpty());

        instance = new DefaultDataDistribution<String>(2);
        assertEquals(0.0, instance.getTotal());
        assertTrue(instance.getDomain().isEmpty());

        instance = new DefaultDataDistribution<String>(
            Arrays.asList(new String[] { "a", "b"}));
        instance.increment("a", 4);
        instance.increment("c", 7);

        instance = new DefaultDataDistribution<String>(instance);
        assertEquals(13.0, instance.getTotal());
        assertEquals(5.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(7.0, instance.get("c"));
    }

    public void testClone()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        DefaultDataDistribution<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.asMap(), clone.asMap());
        assertEquals(0.0, clone.getTotal());
        assertTrue(clone.getDomain().isEmpty());
        assertEquals(0.0, instance.getTotal());
        assertTrue(instance.getDomain().isEmpty());

        instance.increment("a", 4);
        instance.increment("b", 7);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.asMap(), clone.asMap());
        assertEquals(11.0, clone.getTotal());
        assertEquals(4.0, clone.get("a"));
        assertEquals(7.0, clone.get("b"));
        assertEquals(11.0, instance.getTotal());
        assertEquals(4.0, instance.get("a"));
        assertEquals(7.0, instance.get("b"));
    }

    /**
     * Test of add method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testAdd()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("a"));

        instance.increment("a");
        assertEquals(1.0, instance.getTotal());
        assertEquals(1.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));

        instance.increment("a");
        assertEquals(2.0, instance.getTotal());
        assertEquals(2.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));

        instance.increment("b");
        assertEquals(3.0, instance.getTotal());
        assertEquals(2.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));

        instance.increment("a");
        assertEquals(4.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));

        instance.increment("c", 7);
        assertEquals(11.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(7.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.increment("d",-1.0);
        assertEquals( 0.0, instance.get("d" ) );
    }

    /**
     * Test of remove method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testRemove()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0, instance.getDomain().size());

        instance.decrement("a");
        instance.decrement("d", 7);

        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0, instance.getDomain().size());

        instance.increment("a");
        instance.increment("a");
        instance.increment("b");
        instance.increment("a");
        instance.increment("c", 7);
        instance.increment("b");
        assertEquals(12.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(2.0, instance.get("b"));
        assertEquals(7.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.decrement("b");
        assertEquals(11.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(7.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.decrement("b");
        assertEquals(10.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(7.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.decrement("b");
        assertEquals(10.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(7.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));


        instance.decrement("c", 4);
        assertEquals(6.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(3.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));


        instance.decrement("a", 100);
        assertEquals(3.0, instance.getTotal());
        assertEquals(0.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(3.0, instance.get("c"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.compact();
        assertEquals(1, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("a"));
        assertFalse(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));


        instance.decrement("d",-1.0);
        assertEquals( 1.0, instance.get("d") );
    }

    /**
     * Test of getDomain method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetDomain()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertTrue(instance.getDomain().isEmpty());
        assertFalse(instance.getDomain().contains("a"));
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));

        instance.increment("a");
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));

        instance.increment("a");
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));

        instance.increment("b");
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));

        instance.increment("c", 4);
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.decrement("a", 2);
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.decrement("c", 4);
        assertEquals(3, instance.getDomain().size());
        instance.compact();
        assertFalse(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));

        instance.decrement("b", 1);
        assertEquals(1, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        instance.compact();
        assertFalse(instance.getDomain().contains("a"));
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
    }

    /**
     * Test of get method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testget()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertEquals(0.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("d"));

        instance.increment("a");
        assertEquals(1.0, instance.get("a"));

        instance.increment("a");
        assertEquals(2.0, instance.get("a"));

        instance.increment("b");
        assertEquals(2.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));

        instance.increment("c", 4);
        assertEquals(2.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(4.0, instance.get("c"));

        instance.increment("a", 2);
        assertEquals(4.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(4.0, instance.get("c"));

        instance.decrement("a");
        assertEquals(3.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(4.0, instance.get("c"));

        instance.decrement("c", 3);
        assertEquals(3.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(1.0, instance.get("c"));

        instance.decrement("b", 1);
        assertEquals(3.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(1.0, instance.get("c"));

        instance.increment("d");
        assertEquals(3.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(1.0, instance.get("c"));
        assertEquals(1.0, instance.get("d"));
    }

    /**
     * Test of getFraction method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetFraction()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertEquals(0.0, instance.getFraction("a"));
        assertEquals(0.0, instance.getFraction("b"));
        assertEquals(0.0, instance.getFraction("c"));
        assertEquals(0.0, instance.getFraction("d"));

        instance.increment("a");
        assertEquals(1 / 1.0, instance.getFraction("a"));

        instance.increment("a");
        assertEquals(2 / 2.0, instance.getFraction("a"));

        instance.increment("b");
        assertEquals(2 / 3.0, instance.getFraction("a"));
        assertEquals(1 / 3.0, instance.getFraction("b"));

        instance.increment("c", 4);
        assertEquals(2 / 7.0, instance.getFraction("a"));
        assertEquals(1 / 7.0, instance.getFraction("b"));
        assertEquals(4 / 7.0, instance.getFraction("c"));

        instance.increment("a", 2);
        assertEquals(4 / 9.0, instance.getFraction("a"));
        assertEquals(1 / 9.0, instance.getFraction("b"));
        assertEquals(4 / 9.0, instance.getFraction("c"));

        instance.decrement("a");
        assertEquals(3 / 8.0, instance.getFraction("a"));
        assertEquals(1 / 8.0, instance.getFraction("b"));
        assertEquals(4 / 8.0, instance.getFraction("c"));

        instance.decrement("c", 3);
        assertEquals(3 / 5.0, instance.getFraction("a"));
        assertEquals(1 / 5.0, instance.getFraction("b"));
        assertEquals(1 / 5.0, instance.getFraction("c"));

        instance.decrement("b", 1);
        assertEquals(3 / 4.0, instance.getFraction("a"));
        assertEquals(0 / 4.0, instance.getFraction("b"));
        assertEquals(1 / 4.0, instance.getFraction("c"));

        instance.increment("d");
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
        DataDistribution.PMF<String> instance =
            (DataDistribution.PMF<String>) this.createInstanceEmpty();
        assertEquals(0.0, instance.getEntropy());

        instance.increment("a");
        assertEquals(0.0, instance.getEntropy());

        instance.increment("a");
        assertEquals(0.0, instance.getEntropy());

        instance.increment("b");
        assertEquals(0.9183, instance.getEntropy(), 0.0001);

        instance.increment("c");
        assertEquals(1.5000, instance.getEntropy(), 0.0001);

        instance.increment("c", 4);
        assertEquals(1.2988, instance.getEntropy(), 0.0001);

        instance.increment("d", 1);
        assertEquals(1.6577, instance.getEntropy(), 0.0001);
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testEvaluate()
    {
        DataDistribution.PMF<String> instance =
            (DataDistribution.PMF<String>) this.createInstanceEmpty();
        assertEquals(0.0, instance.evaluate("a"));
        assertEquals(0.0, instance.evaluate("b"));
        assertEquals(0.0, instance.evaluate("c"));
        assertEquals(0.0, instance.evaluate("d"));

        instance.increment("a");
        assertEquals(1 / 1.0, instance.evaluate("a"));

        instance.increment("a");
        assertEquals(2 / 2.0, instance.evaluate("a"));

        instance.increment("b");
        assertEquals(2 / 3.0, instance.evaluate("a"));
        assertEquals(1 / 3.0, instance.evaluate("b"));

        instance.increment("c", 4);
        assertEquals(2 / 7.0, instance.evaluate("a"));
        assertEquals(1 / 7.0, instance.evaluate("b"));
        assertEquals(4 / 7.0, instance.evaluate("c"));

        instance.increment("a", 2);
        assertEquals(4 / 9.0, instance.evaluate("a"));
        assertEquals(1 / 9.0, instance.evaluate("b"));
        assertEquals(4 / 9.0, instance.evaluate("c"));

        instance.decrement("a");
        assertEquals(3 / 8.0, instance.evaluate("a"));
        assertEquals(1 / 8.0, instance.evaluate("b"));
        assertEquals(4 / 8.0, instance.evaluate("c"));

        instance.decrement("c", 3);
        assertEquals(3 / 5.0, instance.evaluate("a"));
        assertEquals(1 / 5.0, instance.evaluate("b"));
        assertEquals(1 / 5.0, instance.evaluate("c"));

        instance.decrement("b", 1);
        assertEquals(3 / 4.0, instance.evaluate("a"));
        assertEquals(0 / 4.0, instance.evaluate("b"));
        assertEquals(1 / 4.0, instance.evaluate("c"));

        instance.increment("d");
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
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertEquals(0.0, instance.getMaxValue());

        instance.increment("a");
        assertEquals(1.0, instance.getMaxValue());
        instance.increment("b");
        assertEquals(1.0, instance.getMaxValue());
        instance.increment("b");
        assertEquals(2.0, instance.getMaxValue());
        instance.increment("c", 7);
        assertEquals(7.0, instance.getMaxValue());
    }

    /**
     * Test of getMaximumValue method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetMaximumValue()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertNull(instance.getMaxValueKey());

        instance.increment("a");
        assertEquals("a", instance.getMaxValueKey());
        instance.increment("b");
        assertTrue("a".equals(instance.getMaxValueKey())); // a should be the first value encountered.
        instance.increment("b");
        assertEquals("b", instance.getMaxValueKey());
        instance.increment("c", 7);
        assertEquals("c", instance.getMaxValueKey());
    }

    /**
     * Test of getMaximumValues method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testGetMaximumValues()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertTrue(instance.getMaxValueKeys().isEmpty());

        instance.increment("a");
        assertEquals(1, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("a"));
        instance.increment("b");
        assertEquals(2, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("a"));
        assertTrue(instance.getMaxValueKeys().contains("b"));
        instance.increment("b");
        assertEquals(1, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("b"));
        instance.increment("c", 7);
        assertEquals(1, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("c"));
    }

    /**
     * Test of getTotal method, of class gov.sandia.cognition.statistics.distribution.MapBasedDataHistogramTest.
     */
    public void testgetTotal()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();

        assertEquals(0.0, instance.getTotal());
        instance.increment("a");
        assertEquals(1.0, instance.getTotal());
        instance.increment("a");
        assertEquals(2.0, instance.getTotal());
        instance.increment("b");
        assertEquals(3.0, instance.getTotal());
        instance.increment("c", 4);
        assertEquals(7.0, instance.getTotal());
        instance.increment("a", 2);
        assertEquals(9.0, instance.getTotal());
        instance.decrement("a");
        assertEquals(8.0, instance.getTotal());
        instance.decrement("c", 3);
        assertEquals(5.0, instance.getTotal());
        instance.decrement("b", 1);
        assertEquals(4.0, instance.getTotal());
        instance.increment("d");
        assertEquals(5.0, instance.getTotal());
    }


    /**
     * Test of getMeanValue method, of class DefaultDataDistribution.
     */
    public void testgetMeanValue()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();

        assertEquals(0.0, instance.getMeanValue(), TOLERANCE);
        instance.increment("a");
        assertEquals(1.0, instance.getMeanValue(), TOLERANCE);
        instance.increment("a");
        assertEquals(2.0, instance.getMeanValue(), TOLERANCE);
        instance.increment("b");
        assertEquals(3.0 / 2.0, instance.getMeanValue(), TOLERANCE);
        instance.increment("c", 4);
        assertEquals(7.0 / 3.0, instance.getMeanValue(), TOLERANCE);
        instance.increment("a", 2);
        assertEquals(9.0 / 3.0, instance.getMeanValue(), TOLERANCE);
        instance.decrement("a");
        assertEquals(8.0 / 3.0, instance.getMeanValue(), TOLERANCE);
        instance.decrement("c", 3);
        assertEquals(5.0 / 3.0, instance.getMeanValue(), TOLERANCE);
        instance.decrement("b", 1);
        assertEquals(4.0 / 3.0, instance.getMeanValue(), TOLERANCE);
        instance.compact();
        assertEquals(4.0 / 2.0, instance.getMeanValue(), TOLERANCE);
        instance.increment("d");
        assertEquals(5.0 / 3.0, instance.getMeanValue(), TOLERANCE);
    }


    /**
     * Test of toString method, of class DefaultDataDistribution.
     */
    public void testToString()
    {
        DefaultDataDistribution<String> instance = this.createInstanceEmpty();
        assertNotNull(instance.toString());

        instance.increment("a");
        instance.increment("a");
        instance.increment("b");
        instance.increment("c");
        assertNotNull(instance.toString());
    }


    /**
     * logEvaluate
     */
    public void testLogEvaluate()
    {
        System.out.println( "logEvaluate" );

        DataDistribution.PMF<String> pmf =
            (DataDistribution.PMF<String>) this.createInstancePopulated();
        for( String s : pmf.getDomain() )
        {
            double plog = pmf.logEvaluate(s);
            double p = pmf.evaluate(s);
            double phat = Math.exp(plog);
            assertEquals( p, phat, 1e-5 );
        }

    }

    /**
     * Test of learn method, of class DefaultDataDistribution.
     */
    public void testLearn()
    {
        DefaultDataDistribution.Estimator<String> learner =
            new DefaultDataDistribution.Estimator<String>();
        ArrayList<String> data = new ArrayList<String>();

        DefaultDataDistribution<String> instance = learner.learn(data);
        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("a"));

        data.add("a");
        instance = learner.learn(data);
        assertEquals(1.0, instance.getTotal());
        assertEquals(1.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));

        data.add("a");
        instance = learner.learn(data);
        assertEquals(2.0, instance.getTotal());
        assertEquals(2.0, instance.get("a"));
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));

        data.add("b");
        instance = learner.learn(data);
        assertEquals(3.0, instance.getTotal());
        assertEquals(2.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));

        data.add("a");
        instance = learner.learn(data);
        assertEquals(4.0, instance.getTotal());
        assertEquals(3.0, instance.get("a"));
        assertEquals(1.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("a"));
        assertTrue(instance.getDomain().contains("b"));
    }

}
