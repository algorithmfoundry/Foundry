/*
 * File:                ArrayUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 23, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

import java.util.Arrays;
import junit.framework.TestCase;

/**
 * Unit tests for class ArrayUtil.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class ArrayUtilTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ArrayUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of copy method, of class ArrayUtil.
     */
    public void testCopyBooleanArray()
    {
        boolean[] array = null;
        assertNull(ArrayUtil.copy(array));

        array = new boolean[] { true, false, false };
        boolean[] result = ArrayUtil.copy(array);
        assertNotSame(array, result);
        assertTrue(Arrays.equals(array, result));
        assertEquals(true, array[0]);
        assertEquals(false, array[1]);
        assertEquals(false, array[2]);
    }

    /**
     * Test of copy method, of class ArrayUtil.
     */
    public void testCopyIntArray()
    {
        int[] array = null;
        assertNull(ArrayUtil.copy(array));

        array = new int[] { 1, 0, 5 };
        int[] result = ArrayUtil.copy(array);
        assertNotSame(array, result);
        assertTrue(Arrays.equals(array, result));
        assertEquals(1, array[0]);
        assertEquals(0, array[1]);
        assertEquals(5, array[2]);
    }

    /**
     * Test of copy method, of class ArrayUtil.
     */
    public void testCopyLongArray()
    {
        long[] array = null;
        assertNull(ArrayUtil.copy(array));

        array = new long[] { 1, 0, Long.MAX_VALUE };
        long[] result = ArrayUtil.copy(array);
        assertNotSame(array, result);
        assertTrue(Arrays.equals(array, result));
        assertEquals(1, array[0]);
        assertEquals(0, array[1]);
        assertEquals(Long.MAX_VALUE, array[2]);
    }

    /**
     * Test of copy method, of class ArrayUtil.
     */
    public void testCopyDoubleArray()
    {
        double[] array = null;
        assertNull(ArrayUtil.copy(array));

        array = new double[] { 1.0, 0.0, -0.5 };
        double[] result = ArrayUtil.copy(array);
        assertNotSame(array, result);
        assertTrue(Arrays.equals(array, result));
        assertEquals(1, array[0], 0.0);
        assertEquals(0, array[1], 0.0);
        assertEquals(-0.5, array[2], 0.0);
    }

    /**
     * Test of copy method, of class ArrayUtil.
     */
    public void testCopyObjectArray()
    {
        Object[] array = null;
        assertNull(ArrayUtil.copy(array));

        array = new Object[0];
        Object[] result = ArrayUtil.copy(array);
        assertNotSame(array, result);
        assertTrue(Arrays.equals(array, result));

        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = null;
        array = new Object[] { o1, o2, o3};
        result = ArrayUtil.copy(array);
        assertNotSame(array, result);
        assertTrue(Arrays.equals(array, result));
        assertSame(o1, array[0]);
        assertSame(o2, array[1]);
        assertSame(o3, array[2]);
    }

    /**
     * Test of reverse method, of class ArrayUtil.
     */
    public void testReverseBooleanArray()
    {
        boolean[] array = null;
        ArrayUtil.reverse(array);

        array = new boolean[0];
        ArrayUtil.reverse(array);

        array = new boolean[] { true };
        ArrayUtil.reverse(array);
        assertEquals(true, array[0]);

        array = new boolean[] { false, true };
        ArrayUtil.reverse(array);
        assertEquals(true, array[0]);
        assertEquals(false, array[1]);

        array = new boolean[] { false, false, true };
        ArrayUtil.reverse(array);
        assertEquals(true, array[0]);
        assertEquals(false, array[1]);
        assertEquals(false, array[2]);
    }

    /**
     * Test of reverse method, of class ArrayUtil.
     */
    public void testReverseIntArray()
    {
        int[] array = null;
        ArrayUtil.reverse(array);
        
        array = new int[0];
        ArrayUtil.reverse(array);

        array = new int[] { 9 };
        ArrayUtil.reverse(array);
        assertEquals(9, array[0]);

        array = new int[] { 8, 1 };
        ArrayUtil.reverse(array);
        assertEquals(1, array[0]);
        assertEquals(8, array[1]);

        array = new int[] { 1, 0, 5 };
        ArrayUtil.reverse(array);
        assertEquals(5, array[0]);
        assertEquals(0, array[1]);
        assertEquals(1, array[2]);
    }

    /**
     * Test of reverse method, of class ArrayUtil.
     */
    public void testReverseLongArray()
    {
        long[] array = null;
        ArrayUtil.reverse(array);

        array = new long[0];
        ArrayUtil.reverse(array);

        array = new long[] { 9 };
        ArrayUtil.reverse(array);
        assertEquals(9, array[0]);

        array = new long[] { 8, 1 };
        ArrayUtil.reverse(array);
        assertEquals(1, array[0]);
        assertEquals(8, array[1]);

        array = new long[] { 1, 0, Long.MAX_VALUE };
        ArrayUtil.reverse(array);
        assertEquals(Long.MAX_VALUE, array[0]);
        assertEquals(0, array[1]);
        assertEquals(1, array[2]);
    }

    /**
     * Test of reverse method, of class ArrayUtil.
     */
    public void testReverseDoubleArray()
    {
        double[] array = null;
        ArrayUtil.reverse(array);

        array = new double[0];
        ArrayUtil.reverse(array);

        array = new double[] { 9.3 };
        ArrayUtil.reverse(array);
        assertEquals(9.3, array[0], 0.0);

        array = new double[] { 8.2, -1.6 };
        ArrayUtil.reverse(array);
        assertEquals(-1.6, array[0], 0.0);
        assertEquals(8.2, array[1], 0.0);

        array = new double[] { 1.0, 0.0, 5.0 };
        ArrayUtil.reverse(array);
        assertEquals(5.0, array[0], 0.0);
        assertEquals(0.0, array[1], 0.0);
        assertEquals(1.0, array[2], 0.0);
    }

    /**
     * Test of reverse method, of class ArrayUtil.
     */
    public void testReverseObjectArray()
    {
        Object[] array = null;
        ArrayUtil.reverse(array);

        array = new Object[0];
        ArrayUtil.reverse(array);

        Object o1 = new Object();
        array = new Object[] { o1 };
        ArrayUtil.reverse(array);
        assertSame(o1, array[0]);

        Object o2 = new Object();
        array = new Object[] { o1, o2 };
        ArrayUtil.reverse(array);
        assertSame(o2, array[0]);
        assertSame(o1, array[1]);

        array = new Object[] { o1, o2, null};
        ArrayUtil.reverse(array);
        assertSame(null, array[0]);
        assertSame(o2, array[1]);
        assertSame(o1, array[2]);
    }

    /**
     * Test of isEmpty method, of class ArrayUtil.
     */
    public void testIsEmptyBooleanArray()
    {
        assertTrue(ArrayUtil.isEmpty((boolean[]) null));
        assertTrue(ArrayUtil.isEmpty(new boolean[0]));
        assertFalse(ArrayUtil.isEmpty(new boolean[1]));
        assertFalse(ArrayUtil.isEmpty(new boolean[2]));
        assertFalse(ArrayUtil.isEmpty(new boolean[10]));
    }

    /**
     * Test of isEmpty method, of class ArrayUtil.
     */
    public void testIsEmptyIntArray()
    {
        assertTrue(ArrayUtil.isEmpty((int[]) null));
        assertTrue(ArrayUtil.isEmpty(new int[0]));
        assertFalse(ArrayUtil.isEmpty(new int[1]));
        assertFalse(ArrayUtil.isEmpty(new int[2]));
        assertFalse(ArrayUtil.isEmpty(new int[10]));
    }

    /**
     * Test of isEmpty method, of class ArrayUtil.
     */
    public void testIsEmptyLongArray()
    {
        assertTrue(ArrayUtil.isEmpty((long[]) null));
        assertTrue(ArrayUtil.isEmpty(new long[0]));
        assertFalse(ArrayUtil.isEmpty(new long[1]));
        assertFalse(ArrayUtil.isEmpty(new long[2]));
        assertFalse(ArrayUtil.isEmpty(new long[10]));
    }

    /**
     * Test of isEmpty method, of class ArrayUtil.
     */
    public void testIsEmptyDoubleArray()
    {
        assertTrue(ArrayUtil.isEmpty((double[]) null));
        assertTrue(ArrayUtil.isEmpty(new double[0]));
        assertFalse(ArrayUtil.isEmpty(new double[1]));
        assertFalse(ArrayUtil.isEmpty(new double[2]));
        assertFalse(ArrayUtil.isEmpty(new double[10]));
    }


    /**
     * Test of isEmpty method, of class ArrayUtil.
     */
    public void testIsEmptyObjectArray()
    {
        assertTrue(ArrayUtil.isEmpty((Object[]) null));
        assertTrue(ArrayUtil.isEmpty(new Object[0]));
        assertFalse(ArrayUtil.isEmpty(new Object[1]));
        assertFalse(ArrayUtil.isEmpty(new Object[2]));
        assertFalse(ArrayUtil.isEmpty(new Object[10]));
    }

}
