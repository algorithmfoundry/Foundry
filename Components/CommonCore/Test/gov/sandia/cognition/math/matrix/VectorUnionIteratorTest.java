/*
 * File:            VectorUnionIteratorTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.Vector3;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author justin
 */
public class VectorUnionIteratorTest
{
    
    public VectorUnionIteratorTest()
    {
    }


    /**
     * Test of hasNext method, of class VectorUnionIterator.
     */
    @Test
    public void testHasNext()
    {
        System.out.println("hasNext");
        VectorUnionIterator instance = 
            new VectorUnionIterator(new Vector3(2.0, 0.0, 4.4), new Vector3(1.0, 3.3, 0.0));
        assertTrue(instance.hasNext());
        instance.next();
        assertTrue(instance.hasNext());
        instance.next();
        assertTrue(instance.hasNext());
        instance.next();
        assertFalse(instance.hasNext());
        
        VectorFactory<?> vf = VectorFactory.getSparseDefault();
        Vector a = vf.createVector(10);
        Vector b = vf.createVector(10);
        
        a.set(1, 3);
        b.set(1, 3);
        
        a.set(8, 4);
        b.set(7, 4);
        instance = new VectorUnionIterator(a, b);
        assertTrue(instance.hasNext());
        instance.next();
        assertTrue(instance.hasNext());
        instance.next();
        assertTrue(instance.hasNext());
        instance.next();
        assertFalse(instance.hasNext());
    }

    /**
     * Test of next method, of class VectorUnionIterator.
     */
    @Test
    public void testNext()
    {
        System.out.println("next");
        VectorUnionIterator instance = 
            new VectorUnionIterator(new Vector3(2.0, 0.0, 4.4), new Vector3(1.0, 3.3, 0.0));
        TwoVectorEntry result = null;
        assertTrue(instance.hasNext());
        result = instance.next();
        assertEquals(0, result.getIndex());
        assertEquals(2.0, result.getFirstValue(), 0.0);
        assertEquals(1.0, result.getSecondValue(), 0.0);
        assertTrue(instance.hasNext());
        result = instance.next();
        assertEquals(1, result.getIndex());
        assertEquals(0.0, result.getFirstValue(), 0.0);
        assertEquals(3.3, result.getSecondValue(), 0.0);
        assertTrue(instance.hasNext());
        result = instance.next();
        assertEquals(2, result.getIndex());
        assertEquals(4.4, result.getFirstValue(), 0.0);
        assertEquals(0.0, result.getSecondValue(), 0.0);
        assertFalse(instance.hasNext());
        
        VectorFactory<?> vf = VectorFactory.getSparseDefault();
        Vector a = vf.createVector(10);
        Vector b = vf.createVector(10);
        
        a.set(1, 3);
        b.set(1, 3);
        
        a.set(8, 4);
        b.set(7, 4);
        instance = new VectorUnionIterator(a, b);
        assertTrue(instance.hasNext());
        result = instance.next();
        assertEquals(1, result.getIndex());
        assertEquals(3.0, result.getFirstValue(), 0.0);
        assertEquals(3.0, result.getSecondValue(), 0.0);
        assertTrue(instance.hasNext());

        result = instance.next();
        assertEquals(7, result.getIndex());
        assertEquals(0.0, result.getFirstValue(), 0.0);
        assertEquals(4.0, result.getSecondValue(), 0.0);
        assertTrue(instance.hasNext());
  
        result = instance.next();
        assertEquals(8, result.getIndex());
        assertEquals(4.0, result.getFirstValue(), 0.0);
        assertEquals(0.0, result.getSecondValue(), 0.0);
        assertFalse(instance.hasNext());
    }

    /**
     * Test of remove method, of class VectorUnionIterator.
     */
    @Test
    public void testRemove()
    {
        VectorFactory<?> vf = VectorFactory.getSparseDefault();
        Vector a = vf.createVector(10);
        Vector b = vf.createVector(10);
        
        a.set(1, 3);
        b.set(1, 3);
        
        a.set(8, 4);
        b.set(7, 4);
        VectorUnionIterator instance = new VectorUnionIterator(a, b);
        instance.next();
        instance.remove();
        assertEquals(0.0, a.get(1), 0.0);
        assertEquals(0.0, b.get(1), 0.0);
        instance.next();
        instance.next();
        instance.remove();
        assertEquals(0.0, a.get(8), 0.0);
        assertEquals(0.0, b.get(8), 0.0);
        assertEquals(4.0, b.get(7), 0.0);
        assertFalse(instance.hasNext());
    }
    
}
