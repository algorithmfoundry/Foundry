/*
 * File:                Vector3Test.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 22, 2006, Sandia Corporation.  Under the terms of Contract
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
 *
 * @author Kevin R. Dixon
 */
public class Vector3Test extends TestCase
{
    
    public Vector3Test(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(Vector3Test.class);
        
        return suite;
    }

    /**
     * Test of getX method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testGetX()
    {
        System.out.println("getX");
        
        Vector3 v1 = new Vector3();
        assertEquals( 0.0, v1.getX() );
        
        double value = Math.random();
        Vector3 v2 = new Vector3( value, Math.random(), Math.random() );      
        assertEquals( value, v2.getX() );
    }

    /**
     * Test of setX method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testSetX()
    {
        System.out.println("setX");
        
        Vector3 v1 = new Vector3();
        assertEquals( 0.0, v1.getX() );
        
        double value = Math.random();
        v1.setX( value );
        assertEquals( value, v1.getX() );
    }

    /**
     * Test of getY method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testGetY()
    {
        System.out.println("getY");
        
        Vector3 v1 = new Vector3();
        assertEquals( 0.0, v1.getY() );
        
        double value = Math.random();
        Vector3 v2 = new Vector3( Math.random(), value, Math.random() );      
        assertEquals( value, v2.getY() );
    }

    /**
     * Test of setY method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testSetY()
    {
        System.out.println("setY");
        
        Vector3 v1 = new Vector3();
        assertEquals( 0.0, v1.getY() );
        
        double value = Math.random();
        v1.setY( value );
        assertEquals( value, v1.getY() );
    }

    /**
     * Test of getZ method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testGetZ()
    {
        System.out.println("getZ");
        
        Vector3 v1 = new Vector3();
        assertEquals( 0.0, v1.getZ() );
        
        double value = Math.random();
        Vector3 v2 = new Vector3( Math.random(), Math.random(), value );
        assertEquals( value, v2.getZ() );
    }

    /**
     * Test of setZ method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testSetZ()
    {
        System.out.println("setZ");
        
        Vector3 v1 = new Vector3();
        assertEquals( 0.0, v1.getZ() );
        
        double value = Math.random();
        v1.setZ( value );
        assertEquals( value, v1.getZ() );
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testClone()
    {
        Vector3 instance = new Vector3(4.0, 7.0, 47.0);
        Vector3 clone = instance.clone();
        assertEquals(instance, clone);
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        
        clone.setX(1.0);
        clone.setY(2.0);
        clone.setZ(3.0);
        
        assertEquals(new Vector3(4.0, 7.0, 47.0), instance);
        assertEquals(new Vector3(1.0, 2.0, 3.0), clone);
    }

    /**
     * Test of getDimensionality method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testGetDimensionality()
    {
        Vector3 instance = new Vector3();
        assertEquals(3, instance.getDimensionality());
    }

    /**
     * Test of toString method, of class gov.sandia.isrc.math.matrix.mtj.Vector3.
     */
    public void testToString()
    {
        Vector3 instance = new Vector3(4.0, 7.0, 47.0);
        assertEquals("<4.0, 7.0, 47.0>", instance.toString());
    }


    /**
     * Test of getFirst method of class Vector3.
     */
    public void testGetFirst()
    {
        Vector3 v1 = new Vector3();
        assertEquals(0.0, v1.getFirst());

        double value = Math.random();
        v1.setX(value);
        assertEquals(value, v1.getFirst());
    }

    /**
     * Test of getSecond method, of class Vector3.
     */
    public void testGetSecond()
    {
        Vector3 v1 = new Vector3();
        assertEquals(0.0, v1.getSecond());

        double value = Math.random();
        v1.setY(value);
        assertEquals(value, v1.getSecond());
    }

    /**
     * Test of getThird method, of class Vector3.
     */
    public void testGetThird()
    {
        Vector3 v1 = new Vector3();
        assertEquals(0.0, v1.getThird());

        double value = Math.random();
        v1.setZ(value);
        assertEquals(value, v1.getThird());
    }
}
