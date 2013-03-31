/*
 * File:                ArrayUtil.java
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

/**
 * Utility class for handling arrays.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class ArrayUtil
    extends Object
{

    /**
     * Creates a new copy of the given array.
     *
     * @param   array
     *      The array to copy.
     * @return
     *      A copy of the given array.
     */
    public static boolean[] copy(
        final boolean[] array)
    {
        if (array == null)
        {
            return null;
        }
        else
        {
            return Arrays.copyOf(array, array.length);
        }
    }

    /**
     * Creates a new copy of the given array.
     *
     * @param   array
     *      The array to copy.
     * @return
     *      A copy of the given array.
     */
    public static int[] copy(
        final int[] array)
    {
        if (array == null)
        {
            return null;
        }
        else
        {
            return Arrays.copyOf(array, array.length);
        }
    }

    /**
     * Creates a new copy of the given array.
     *
     * @param   array
     *      The array to copy.
     * @return
     *      A copy of the given array.
     */
    public static long[] copy(
        final long[] array)
    {
        if (array == null)
        {
            return null;
        }
        else
        {
            return Arrays.copyOf(array, array.length);
        }
    }

    /**
     * Creates a new copy of the given array.
     *
     * @param   array
     *      The array to copy.
     * @return
     *      A copy of the given array.
     */
    public static double[] copy(
        final double[] array)
    {
        if (array == null)
        {
            return null;
        }
        else
        {
            return Arrays.copyOf(array, array.length);
        }
    }

    /**
     * Creates a new copy of the given array. Does not copy the elements.
     *
     * @param   array
     *      The array to copy. Does not copy the elements.
     * @return
     *      A copy of the given array.
     */
    public static <T> T[] copy(
        final T[] array)
    {
        if (array == null)
        {
            return null;
        }
        else
        {
            return Arrays.copyOf(array, array.length);
        }
    }

    /**
     * Reverses the ordering of elements in an array.
     *
     * @param   array
     *      The array to reverse the elements in.
     */
    public static void reverse(
        final boolean[] array)
    {
        if (array != null)
        {
            final int length = array.length;
            int i = 0;
            int j = length - 1;
            while (i < j)
            {
                final boolean temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
    }

    /**
     * Reverses the ordering of elements in an array.
     *
     * @param   array
     *      The array to reverse the elements in.
     */
    public static void reverse(
        final int[] array)
    {
        if (array != null)
        {
            final int length = array.length;
            int i = 0;
            int j = length - 1;
            while (i < j)
            {
                final int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
    }

    /**
     * Reverses the ordering of elements in an array.
     *
     * @param   array
     *      The array to reverse the elements in.
     */
    public static void reverse(
        final long[] array)
    {
        if (array != null)
        {
            final int length = array.length;
            int i = 0;
            int j = length - 1;
            while (i < j)
            {
                final long temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
    }

    /**
     * Reverses the ordering of elements in an array.
     *
     * @param   array
     *      The array to reverse the elements in.
     */
    public static void reverse(
        final double[] array)
    {
        if (array != null)
        {
            final int length = array.length;
            int i = 0;
            int j = length - 1;
            while (i < j)
            {
                final double temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
    }

    /**
     * Reverses the ordering of elements in an array.
     *
     * @param   array
     *      The array to reverse the elements in.
     */
    public static void reverse(
        final Object[] array)
    {
        if (array != null)
        {
            final int length = array.length;
            int i = 0;
            int j = length - 1;
            while (i < j)
            {
                final Object temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
    }

    /**
     * Determines if the given array is null or empty (length 0).
     *
     * @param   array
     *      The array.
     * @return
     *      True if the array is null or length 0. Otherwise, false.
     */
    public static boolean isEmpty(
        final boolean[] array)
    {
        return array == null || array.length <= 0;
    }

    /**
     * Determines if the given array is null or empty (length 0).
     *
     * @param   array
     *      The array.
     * @return
     *      True if the array is null or length 0. Otherwise, false.
     */
    public static boolean isEmpty(
        final int[] array)
    {
        return array == null || array.length <= 0;
    }

    /**
     * Determines if the given array is null or empty (length 0).
     *
     * @param   array
     *      The array.
     * @return
     *      True if the array is null or length 0. Otherwise, false.
     */
    public static boolean isEmpty(
        final long[] array)
    {
        return array == null || array.length <= 0;
    }
    
    /**
     * Determines if the given array is null or empty (length 0).
     *
     * @param   array
     *      The array.
     * @return
     *      True if the array is null or length 0. Otherwise, false.
     */
    public static boolean isEmpty(
        final double[] array)
    {
        return array == null || array.length <= 0;
    }
    
    /**
     * Determines if the given array is null or empty (length 0).
     *
     * @param   array
     *      The array.
     * @return
     *      True if the array is null or length 0. Otherwise, false.
     */
    public static boolean isEmpty(
        final Object[] array)
    {
        return array == null || array.length <= 0;
    }

}
