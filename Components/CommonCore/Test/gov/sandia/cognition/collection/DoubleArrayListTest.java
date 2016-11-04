/*
 * File:                DoubleVectorTest.java
 * Authors:             Jeremy Wendt
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

package gov.sandia.cognition.collection;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author jdwendt
 */
public class DoubleArrayListTest
{

    @Test
    public void basicTest()
    {
        DoubleArrayList v = new DoubleArrayList(10);
        v.add(1);
        v.add(2);
        v.add(3);
        v.add(4);

        assertEquals(4, v.size());
        for (int i = 0; i < v.size(); ++i)
        {
            assertEquals(i + 1.0, v.get(i), 1e-12);
        }

        v.decreaseTo(3);
        assertEquals(3, v.size());
        for (int i = 0; i < v.size(); ++i)
        {
            assertEquals(i + 1.0, v.get(i), 1e-12);
        }
        // decreaseTo has to be less than the current size
        try
        {
            v.decreaseTo(4);
            assertTrue(false);
        }
        catch (IllegalArgumentException iae)
        {
            // This is the correct path
        }

        v.increaseTo(15);
        // Just increases the backing-store size, not the visible size
        assertEquals(3, v.size());
        for (int i = 0; i < v.size(); ++i)
        {
            assertEquals(i + 1.0, v.get(i), 1e-12);
        }
        try
        {
            v.increaseTo(4);
            assertTrue(false);
        }
        catch (IllegalArgumentException iae)
        {
            // This is the correct path
        }

        DoubleArrayList d = new DoubleArrayList(10);
        d.add(1.0);
        d.add(2.0);
        d.add(3.0);
        assertTrue(v.equals(d));
        assertTrue(d.equals(v));
        assertTrue(v.equals(v));
        assertEquals(v.hashCode(), d.hashCode());

        try
        {
            v.get(4);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }

        try
        {
            v.get(-1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }

        v.plusEquals(1, 3);
        v.set(0, 2);
        assertEquals(2.0, v.get(0), 1e-12);
        assertEquals(5.0, v.get(1), 1e-12);
        try
        {
            v.set(-1, 1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }
        try
        {
            v.set(5, 1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }
        try
        {
            v.plusEquals(-1, 1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }
        try
        {
            v.plusEquals(5, 1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }

        v.swap(0, 1);
        assertEquals(5.0, v.get(0), 1e-12);
        assertEquals(2.0, v.get(1), 1e-12);
        try
        {
            v.swap(5, 1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }
        try
        {
            v.swap(-1, 1);
            assertTrue(false);
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            // The correct path
        }

        v.clear();
        assertEquals(0, v.size());
        v.shrinkToFit();
    }

}
