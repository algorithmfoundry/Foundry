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
{

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
    
}
