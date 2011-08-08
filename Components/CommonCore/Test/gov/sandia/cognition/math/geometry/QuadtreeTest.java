/*
 * File:                QuadtreeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 21, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.geometry;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Justin Basilico
 */
public class QuadtreeTest extends TestCase
{
    protected Random random;
    public QuadtreeTest(String testName)
    {
        super(testName);
        
        this.random = new Random();
    }
    
    public void testConstructors()
    {
        int splitThreshold = Quadtree.DEFAULT_SPLIT_THRESHOLD;
        Quadtree<Vector> instance = new Quadtree<Vector>();
        assertEquals(splitThreshold, instance.getSplitThreshold());
        
        splitThreshold /= 2;
        instance = new Quadtree<Vector>(splitThreshold);
        assertEquals(splitThreshold, instance.getSplitThreshold());
        
        ArrayList<Vector2> items = new ArrayList<Vector2>();
        
        for (int i = 0; i < 100; i++)
        {
            items.add(new Vector2(Math.random(), Math.random()));
        }
        
        instance = new Quadtree<Vector>(2, items);
    }

    /**
     * Test of add method, of class Quadtree.
     */
    public void testAdd()
    {
        Quadtree<Vector> instance = new Quadtree<Vector>();
        Vector2 item1 = new Vector2(1.0, 1.0);
        Vector2 item2 = new Vector2(-1.0, 5.0);
        Vector2 item3 = new Vector2(0.0, 1.0);
        
        assertFalse(instance.boundsContain(item1));
        assertFalse(instance.boundsContain(item2));
        assertFalse(instance.boundsContain(item3));
        
        instance.add(item1);
        assertTrue(instance.boundsContain(item1));
        assertFalse(instance.boundsContain(item2));
        assertFalse(instance.boundsContain(item3));
        
        instance.add(item2);
        assertTrue(instance.boundsContain(item1));
        assertTrue(instance.boundsContain(item2));
        assertTrue(instance.boundsContain(item3));
        
        instance.setSplitThreshold(1);
        assertTrue(instance.boundsContain(item1));
        assertTrue(instance.boundsContain(item2));
        assertTrue(instance.boundsContain(item3));
    }

    /**
     * Test of addAll method, of class Quadtree.
     */
    public void testAddAll()
    {
        Quadtree<Vector> instance = new Quadtree<Vector>();
        Vector2 item1 = new Vector2(1.0, 1.0);
        Vector2 item2 = new Vector2(-1.0, 5.0);
        Vector2 item3 = new Vector2(0.0, 1.0);
        Vector2 item4 = new Vector2(0.0, -1.0);
        
        assertFalse(instance.boundsContain(item1));
        assertFalse(instance.boundsContain(item2));
        assertFalse(instance.boundsContain(item3));
        assertFalse(instance.boundsContain(item4));
        
        ArrayList<Vector2> items = new ArrayList<Vector2>();
        instance.addAll(items);
        assertFalse(instance.boundsContain(item1));
        assertFalse(instance.boundsContain(item2));
        assertFalse(instance.boundsContain(item3));
        assertFalse(instance.boundsContain(item4));
        
        items.add(item1);
        items.add(item2);
        items.add(item3);
        instance.addAll(items);
        assertTrue(instance.boundsContain(item1));
        assertTrue(instance.boundsContain(item2));
        assertTrue(instance.boundsContain(item3));
        assertFalse(instance.boundsContain(item4));
    }

    /**
     * Test of convertTo2D method, of class Quadtree.
     */
    public void testConvertTo2D()
    {
        Quadtree<Vector> instance = new Quadtree<Vector>();
        Vector2 item = new Vector2(random.nextDouble(), random.nextDouble());
        Vector2 result = instance.convertTo2D(item);
        assertEquals(item, result);
        
        boolean exceptionThrown = false;
        try
        {
            instance.convertTo2D(new Vector3());
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
     * Test of find method, of class Quadtree.
     */
    public void testFind()
    {
        Quadtree<Vector> instance = new Quadtree<Vector>(2);
        Vector2 item1 = new Vector2(1.0, 1.0);
        Vector2 item2 = new Vector2(-1.0, 5.0);
        Vector2 item3 = new Vector2(0.0, 1.0);
        Vector2 item4 = new Vector2(0.0, -1.0);
        
        assertNull(instance.find(item1));
        assertNull(instance.find(item2));
        assertNull(instance.find(item3));
        assertNull(instance.find(item4));
        
        instance.add(item1);
        
        assertSame(instance.getRoot(), instance.find(item1));
        assertNull(instance.find(item2));
        assertNull(instance.find(item3));
        assertNull(instance.find(item4));
        
        instance.add(item2);
        assertSame(instance.getRoot(), instance.find(item1));
        assertSame(instance.getRoot(), instance.find(item2));
        assertSame(instance.getRoot(), instance.find(item3));
        assertNull(instance.find(item4));
        
        instance.add(item3);
        assertNotSame(instance.getRoot(), instance.find(item1));
        assertNotSame(instance.getRoot(), instance.find(item2));
        assertNotSame(instance.getRoot(), instance.find(item3));
        assertNull(instance.find(item4));
        assertSame(instance.find(item1), instance.find(item3));
        assertNotSame(instance.find(item1), instance.find(item2));
        assertEquals(1, instance.find(item1).getDepth());
        assertEquals(1, instance.find(item2).getDepth());
        assertEquals(1, instance.find(item3).getDepth());
        
        instance.add(item2);
        instance.add(item2);
        instance.add(item2);
        assertEquals(1, instance.find(item1).getDepth());
        assertEquals(1, instance.find(item2).getDepth());
        assertEquals(1, instance.find(item3).getDepth());
        
        instance.add(item4);
        assertEquals(2, instance.find(item1).getDepth());
        assertEquals(1, instance.find(item2).getDepth());
        assertEquals(2, instance.find(item3).getDepth());
        assertEquals(2, instance.find(item4).getDepth());
        
        instance.setSplitThreshold(10);
        assertSame(instance.getRoot(), instance.find(item1));
        assertSame(instance.getRoot(), instance.find(item2));
        assertSame(instance.getRoot(), instance.find(item3));
        assertSame(instance.getRoot(), instance.find(item4));
    }
    
    public void testFindNodes()
    {
        
        Quadtree<Vector> instance = new Quadtree<Vector>(2);
        instance.add(new Vector2(1.0, 1.0));
        instance.add(new Vector2(-1.0, 5.0));
        instance.add(new Vector2(0.0, 1.0));
        instance.add(new Vector2(0.0, -1.0));
        
        LinkedList<Quadtree<Vector>.Node> result = instance.findNodes(
            new Rectangle2D.Double(-10.0, -10.0, 20, 20));
        
        assertEquals(1, result.size());
    }
    
    /**
     * Test of isInBounds method, of class Quadtree.
     */
    public void testIsInBounds()
    {
        Quadtree<Vector> instance = new Quadtree<Vector>(2);
        Vector2 item1 = new Vector2(1.0, 1.0);
        Vector2 item2 = new Vector2(-1.0, 5.0);
        Vector2 item3 = new Vector2(0.0, 1.0);
        Vector2 item4 = new Vector2(0.0, -1.0);
        
        assertFalse(instance.boundsContain(0.0, 0.0));
        assertFalse(instance.boundsContain(item1));
        assertFalse(instance.boundsContain(item2));
        assertFalse(instance.boundsContain(item3));
        assertFalse(instance.boundsContain(item4));
        
        instance.add(item1);
        assertTrue(instance.boundsContain(item1));
        assertFalse(instance.boundsContain(item2));
        assertFalse(instance.boundsContain(item3));
        assertFalse(instance.boundsContain(item4));
        
        instance.add(item2);
        assertTrue(instance.boundsContain(item1));
        assertTrue(instance.boundsContain(item2));
        assertTrue(instance.boundsContain(item3));
        assertFalse(instance.boundsContain(item4));
    }
    
    /**
     * Test of getSplitThreshold method, of class Quadtree.
     */
    public void testGetSplitThreshold()
    {
        this.testSetSplitThreshold();
    }

    /**
     * Test of setSplitThreshold method, of class Quadtree.
     */
    public void testSetSplitThreshold()
    {
        int splitThreshold = Quadtree.DEFAULT_SPLIT_THRESHOLD;
        Quadtree<Vector2> instance = new Quadtree<Vector2>();
        assertEquals(splitThreshold, instance.getSplitThreshold());
        
        splitThreshold = 1;
        instance.setSplitThreshold(splitThreshold);
        assertEquals(splitThreshold, instance.getSplitThreshold());
        
        splitThreshold = 100;
        instance.setSplitThreshold(splitThreshold);
        assertEquals(splitThreshold, instance.getSplitThreshold());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setSplitThreshold(0);
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
     * Test of getRoot method, of class Quadtree.
     */
    public void testGetRoot()
    {
        Quadtree<Vector2> instance = new Quadtree<Vector2>();
        assertNotNull(instance.getRoot());
    }
}
