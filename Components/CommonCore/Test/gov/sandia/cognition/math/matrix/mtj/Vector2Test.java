/*
 * File:                Vector2Test.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * This class implements JUnit tests for the following classes:
 *
 *     Vector2
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class Vector2Test
    extends TestCase
{
    public Vector2Test(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(Vector2Test.class);
        
        return suite;
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testClone()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        Vector2 clone = instance.clone();
        assertEquals(instance, clone);
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        
        clone.setX(1.0);
        clone.setY(2.0);
        
        assertEquals(new Vector2(4.0, 7.0), instance);
        assertEquals(new Vector2(1.0, 2.0), clone);
    }

    /**
     * Test of getDimensionality method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testGetDimensionality()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(2, instance.getDimensionality());
    }

    /**
     * Test of getX method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testGetX()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(4.0, instance.getX());
    }

    /**
     * Test of getY method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testGetY()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(7.0, instance.getY());
    }

    /**
     * Test of setX method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testSetX()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(4.0, instance.getX());
        instance.setX(1.0);
        assertEquals(1.0, instance.getX());
    }

    /**
     * Test of setY method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testSetY()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(7.0, instance.getY());
        instance.setY(2.0);
        assertEquals(2.0, instance.getY());
    }

    /**
     * Test of toString method, of class gov.sandia.isrc.math.matrix.mtj.Vector2.
     */
    public void testToString()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        
        assertEquals("<4.0, 7.0>", instance.toString());
    }


    /**
     * Test of getFirst method, of class Vector2.
     */
    public void testGetFirst()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(4.0, instance.getFirst());
        instance.setX(1.0);
        assertEquals(1.0, instance.getFirst());
    }

    /**
     * Test of getSecond method, of class Vector2.
     */
    public void testGetSecond()
    {
        Vector2 instance = new Vector2(4.0, 7.0);
        assertEquals(7.0, instance.getSecond());
        instance.setY(2.0);
        assertEquals(2.0, instance.getSecond());
    }

}
