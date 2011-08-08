/*
 * File:                DefaultTwoVectorEntryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 31, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import java.util.Random;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DefaultTwoVectorEntry
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DefaultTwoVectorEntryTest
    extends TestCase
{
    protected Random random = new Random();
    
    public DefaultTwoVectorEntryTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DefaultTwoVectorEntryTest.class);
        
        return suite;
    }

    /**
     * Test of getFirstVector method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testGetFirstVector()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second);
        assertSame(first, instance.getFirstVector());
    }

    /**
     * Test of setFirstVector method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testSetFirstVector()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second);
        assertSame(first, instance.getFirstVector());
        Vector newFirst = VectorFactory.getDefault().createVector(47);
        instance.setFirstVector(newFirst);
        assertSame(newFirst, instance.getFirstVector());
    }

    /**
     * Test of getSecondVector method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testGetSecondVector()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second);
        assertSame(second, instance.getSecondVector());
    }

    /**
     * Test of setSecondVector method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testSetSecondVector()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second);
        assertSame(second, instance.getSecondVector());
        Vector newSecond = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        instance.setSecondVector(newSecond);
        assertSame(newSecond, instance.getSecondVector());
    }

    /**
     * Test of getFirstValue method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testGetFirstValue()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second, 4);
        assertEquals(first.getElement(4), instance.getFirstValue());
    }

    /**
     * Test of setFirstValue method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testSetFirstValue()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second, 4);
        assertEquals(first.getElement(4), instance.getFirstValue());
        double value = Math.random();
        instance.setFirstValue(value);
        assertEquals(value, instance.getFirstValue());
        assertEquals(value, first.getElement(4));
    }

    /**
     * Test of getIndex method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testGetIndex()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second);
        assertEquals(0, instance.getIndex());
        
        instance = new DefaultTwoVectorEntry(first, second, 4);
        assertEquals(4, instance.getIndex());
    }

    /**
     * Test of setIndex method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testSetIndex()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second);
        assertEquals(0, instance.getIndex());
        
        instance.setIndex(7);
        assertEquals(7, instance.getIndex());
    }

    /**
     * Test of getSecondValue method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testGetSecondValue()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second, 4);
        assertEquals(second.getElement(4), instance.getSecondValue());
    }

    /**
     * Test of setSecondValue method, of class gov.sandia.isrc.math.matrix.DefaultTwoVectorEntry.
     */
    public void testSetSecondValue()
    {
        Vector first  = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        Vector second = VectorFactory.getDefault().createUniformRandom(47, -1.0, 1.0, random);
        DefaultTwoVectorEntry instance = 
            new DefaultTwoVectorEntry(first, second, 4);
        assertEquals(second.getElement(4), instance.getSecondValue());
        double value = Math.random();
        instance.setSecondValue(value);
        assertEquals(value, instance.getSecondValue());
        assertEquals(value, second.getElement(4));
    }
}
