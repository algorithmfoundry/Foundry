/*
 * File:                DefaultWeightedValueTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     WeightedValue
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-25",
    changesNeeded=false,
    comments="Looks fine."
)
public class DefaultWeightedValueTest
    extends TestCase
{

    public DefaultWeightedValueTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        DefaultWeightedValue<String> instance = new DefaultWeightedValue<String>();
        assertEquals(DefaultWeightedValue.DEFAULT_WEIGHT, instance.getWeight());
        assertNull(instance.getValue());

        instance = new DefaultWeightedValue<String>("test");
        assertEquals(DefaultWeightedValue.DEFAULT_WEIGHT, instance.getWeight());
        assertEquals("test", instance.getValue());

        instance = new DefaultWeightedValue<String>("test", 4.7);
        assertEquals(4.7, instance.getWeight());
        assertEquals("test", instance.getValue());

        instance = new DefaultWeightedValue<String>(instance);
        assertEquals(4.7, instance.getWeight());
        assertEquals("test", instance.getValue());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.util.WeightedValue.
     */
    public void testClone()
    {
        DefaultWeightedValue<String> instance = new DefaultWeightedValue<String>();
        assertEquals(DefaultWeightedValue.DEFAULT_WEIGHT, instance.getWeight());
        assertNull(instance.getValue());

        DefaultWeightedValue<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getWeight(), clone.getWeight());
        assertNull(clone.getValue());

        instance.setWeight(-4.7);
        instance.setValue("test");

        clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(-4.7, clone.getWeight());
        assertEquals("test", clone.getValue());
    }

    /**
     * Test of getWeight method, of class gov.sandia.cognition.util.WeightedValue.
     */
    public void testGetWeight()
    {
        this.testSetWeight();
    }

    /**
     * Test of setWeight method, of class gov.sandia.cognition.util.WeightedValue.
     */
    public void testSetWeight()
    {
        DefaultWeightedValue<String> instance = new DefaultWeightedValue<String>();
        assertEquals(DefaultWeightedValue.DEFAULT_WEIGHT, instance.getWeight());

        instance.setWeight(4.7);
        assertEquals(4.7, instance.getWeight());

        instance.setWeight(-7.4);
        assertEquals(-7.4, instance.getWeight());

        instance.setWeight(0.0);
        assertEquals(0.0, instance.getWeight());
    }

    /**
     * Test of getValue method, of class gov.sandia.cognition.util.WeightedValue.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class gov.sandia.cognition.util.WeightedValue.
     */
    public void testSetValue()
    {
        DefaultWeightedValue<String> instance = new DefaultWeightedValue<String>();
        assertNull(instance.getValue());

        instance.setValue("test1");
        assertEquals("test1", instance.getValue());

        instance.setValue("test2");
        assertEquals("test2", instance.getValue());

        instance.setValue("");
        assertEquals("", instance.getValue());

        instance.setValue(null);
        assertNull(instance.getValue());
    }

    public void testComparator()
    {
        WeightedValue<String> a = new DefaultWeightedValue<String>("a", 0.1);
        WeightedValue<String> b = new DefaultWeightedValue<String>("b", 5.4);
        WeightedValue<String> c = new DefaultWeightedValue<String>("c", 0.1);

        DefaultWeightedValue.WeightComparator comparator = DefaultWeightedValue.WeightComparator.getInstance();

        assertTrue(comparator.compare(a, a) == 0);
        assertTrue(comparator.compare(a, b) < 0);
        assertTrue(comparator.compare(b, a) > 0);
        assertTrue(comparator.compare(b, b) == 0);
        assertTrue(comparator.compare(a, c) == 0);
        assertTrue(comparator.compare(c, a) == 0);
        assertTrue(comparator.compare(c, c) == 0);
        assertTrue(comparator.compare(b, c) > 0);
        assertTrue(comparator.compare(c, b) < 0);
    }
}
