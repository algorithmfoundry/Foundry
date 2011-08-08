/*
 * File:                DefaultWeightedValueDiscriminantTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 03, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.data;

import junit.framework.TestCase;

/**
 * Unit tests for class DefaultWeightedValueDiscriminant.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultWeightedValueDiscriminantTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultWeightedValueDiscriminantTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class DefaultWeightedValueDiscriminant.
     */
    public void testConstructors()
    {
        String value = null;
        double weight = 0.0;
        DefaultWeightedValueDiscriminant<String> instance =
            new DefaultWeightedValueDiscriminant<String>();
        assertSame(value, instance.getValue());
        assertEquals(weight, instance.getWeight(), 0.0);

        value = "some value";
        weight = 4;
        instance = new DefaultWeightedValueDiscriminant<String>(
            value, weight);
        assertSame(value, instance.getValue());
        assertEquals(weight, instance.getWeight(), 0.0);

        instance = new DefaultWeightedValueDiscriminant<String>(instance);
        assertSame(value, instance.getValue());
        assertEquals(weight, instance.getWeight(), 0.0);
    }

    /**
     * Test of getDiscriminant method, of class DefaultWeightedValueDiscriminant.
     */
    public void testGetDiscriminant()
    {
        DefaultWeightedValueDiscriminant<String> instance =
            new DefaultWeightedValueDiscriminant<String>();
        assertEquals(0.0, instance.getDiscriminant(), 0.0);

        for (double weight : new double[] {1.0, 0.0, -1.0, 3.0, 4.5})
        {
            instance.setWeight(weight);
            assertEquals(weight, instance.getDiscriminant(), 0.0);
        }
    }

    /**
     * Test of getFirst method, of class DefaultWeightedValueDiscriminant.
     */
    public void testGetFirst()
    {
        String value = null;
        DefaultWeightedValueDiscriminant<String> instance =
            new DefaultWeightedValueDiscriminant<String>();
        assertSame(value, instance.getFirst());

        value = "some value";
        instance.setValue(value);
        assertSame(value, instance.getFirst());

        value = "";
        instance.setValue(value);
        assertSame(value, instance.getFirst());

        value = null;
        instance.setValue(value);
        assertSame(value, instance.getFirst());

        value = "another value";
        instance.setValue(value);
        assertSame(value, instance.getFirst());
    }

    /**
     * Test of getSecond method, of class DefaultWeightedValueDiscriminant.
     */
    public void testGetSecond()
    {
        DefaultWeightedValueDiscriminant<String> instance =
            new DefaultWeightedValueDiscriminant<String>();
        assertEquals(0.0, instance.getDiscriminant(), 0.0);
        
        for (double weight : new double[] {1.0, 0.0, -1.0, 3.0, 4.5})
        {
            instance.setWeight(weight);
            assertEquals(weight, instance.getSecond(), 0.0);
        }
    }

    /**
     * Test of create method, of class DefaultWeightedValueDiscriminant.
     */
    public void testCreate()
    {
        String value = "some value";
        double weight = 4.3;
        DefaultWeightedValueDiscriminant<String> instance =
            DefaultWeightedValueDiscriminant.create(value, weight);
        assertSame(value, instance.getValue());
        assertEquals(weight, instance.getWeight(), 0.0);

        instance = DefaultWeightedValueDiscriminant.create(instance);
        assertSame(value, instance.getValue());
        assertEquals(weight, instance.getWeight(), 0.0);
    }

}
