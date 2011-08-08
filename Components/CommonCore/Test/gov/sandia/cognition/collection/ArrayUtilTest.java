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
    public void testCopy()
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
     * Test of reverse method, of class ArrayUtil.
     */
    public void testReverse()
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

}
