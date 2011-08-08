/*
 * File:                Vector1Test.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 30, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class Vector1.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class Vector1Test 
    extends TestCase
{
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public Vector1Test(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class Vector1.
     */
    public void testConstructors()
    {
        double x = 0.0;
        Vector1 instance = new Vector1();
        assertEquals(x, instance.getX(), 0.0);

        x = this.random.nextDouble();
        instance = new Vector1(x);
        assertEquals(x, instance.getX(), 0.0);

        instance = new Vector1((Vector) instance);
        assertEquals(x, instance.getX(), 0.0);

        instance = new Vector1(instance);
        assertEquals(x, instance.getX(), 0.0);
    }

    /**
     * Test of clone method, of class Vector1.
     */
    public void testClone()
    {
        Vector1 instance = new Vector1(1.2);
        Vector1 clone = instance.clone();
        assertEquals(instance, clone);
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());

        clone.setX(3.4);
        assertEquals(new Vector1(1.2), instance);
        assertEquals(new Vector1(3.4), clone);
    }

    /**
     * Test of getDimensionality method, of class Vector1.
     */
    public void testGetDimensionality()
    {
        assertEquals(1, new Vector1().getDimensionality());
    }

    /**
     * Test of getX method, of class Vector1.
     */
    public void testGetX()
    {
        this.testSetX();
    }

    /**
     * Test of setX method, of class Vector1.
     */
    public void testSetX()
    {
        double x = 0.0;
        Vector1 instance = new Vector1();
        assertEquals(x, instance.getX(), 0.0);

        x = this.random.nextDouble();
        instance.setX(x);
        assertEquals(x, instance.getX(), 0.0);

        x = -x;
        instance.setX(x);
        assertEquals(x, instance.getX(), 0.0);

        x = 0.0;
        instance.setX(x);
        assertEquals(x, instance.getX(), 0.0);
    }

    /**
     * Test of toString method, of class Vector1.
     */
    public void testToString()
    {
        assertEquals("<0.0>", new Vector1().toString());
        assertEquals("<4.7>", new Vector1(4.7).toString());
    }

}
