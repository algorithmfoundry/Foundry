/*
 * File:                DefaultValueDiscriminantPairTest.java
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
 * Unit tests for class DefaultValueDiscriminantPair.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultValueDiscriminantPairTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultValueDiscriminantPairTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class DefaultValueDiscriminantPair.
     */
    public void testConstructors()
    {
        String value = null;
        Integer discriminant = null;
        DefaultValueDiscriminantPair<String, Integer> instance =
            new DefaultValueDiscriminantPair<String, Integer>();
        assertSame(value, instance.getValue());
        assertSame(discriminant, instance.getDiscriminant());

        value = "some value";
        discriminant = 4;
        instance = new DefaultValueDiscriminantPair<String, Integer>(
            value, discriminant);
        assertSame(value, instance.getValue());
        assertSame(discriminant, instance.getDiscriminant());
    }

    /**
     * Test of clone method, of class DefaultValueDiscriminantPair.
     */
    public void testClone()
    {
        DefaultValueDiscriminantPair<String, Integer> instance =
            new DefaultValueDiscriminantPair<String, Integer>();

        DefaultValueDiscriminantPair<String, Integer> clone =
            instance.clone();
        assertNotSame(clone, instance);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of getValue method, of class DefaultValueDiscriminantPair.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class DefaultValueDiscriminantPair.
     */
    public void testSetValue()
    {
        String value = null;
        DefaultValueDiscriminantPair<String, Integer> instance =
            new DefaultValueDiscriminantPair<String, Integer>();
        assertSame(value, instance.getValue());

        value = "some value";
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = "";
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = null;
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = "another value";
        instance.setValue(value);
        assertSame(value, instance.getValue());
    }

    /**
     * Test of getDiscriminant method, of class DefaultValueDiscriminantPair.
     */
    public void testGetDiscriminant()
    {
        this.testSetDiscriminant();
    }

    /**
     * Test of setDiscriminant method, of class DefaultValueDiscriminantPair.
     */
    public void testSetDiscriminant()
    {
        Integer discriminant = null;
        DefaultValueDiscriminantPair<String, Integer> instance =
            new DefaultValueDiscriminantPair<String, Integer>();
        assertSame(discriminant, instance.getDiscriminant());

        discriminant = 4;
        instance.setDiscriminant(discriminant);
        assertSame(discriminant, instance.getDiscriminant());

        discriminant = 0;
        instance.setDiscriminant(discriminant);
        assertSame(discriminant, instance.getDiscriminant());

        discriminant = null;
        instance.setDiscriminant(discriminant);
        assertSame(discriminant, instance.getDiscriminant());

        discriminant = -1;
        instance.setDiscriminant(discriminant);
        assertSame(discriminant, instance.getDiscriminant());
    }

    /**
     * Test of create method, of class DefaultValueDiscriminantPair.
     */
    public void testCreate()
    {
        String value = "some value";
        Integer discriminant = 4;
        DefaultValueDiscriminantPair<String, Integer> instance =
            DefaultValueDiscriminantPair.create(value, discriminant);
        assertSame(value, instance.getValue());
        assertSame(discriminant, instance.getDiscriminant());
    }

}
