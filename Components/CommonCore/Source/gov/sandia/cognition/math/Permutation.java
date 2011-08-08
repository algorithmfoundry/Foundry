/*
 * File:                Permutation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 30, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The <code>Permutation</code> class contains methods for dealing with
 * permutations of object sets.
 *
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-26",
    changesNeeded=false,
    comments="Looks good."
)
public class Permutation
    extends Object
{

    /** Creates a new instance of Permutation */
    public Permutation()
    {
        super();
    }

    /**
     * Reorders the elements in a given array list to be a permuted ordering of
     * elements in the collection.
     *
     * @param   <DataType>
     *      The data type of the list to reorder.
     * @param   list
     *      The array list to reorder.
     * @param   random
     *      The random number generator to use.
     */
    public static <DataType> void reorder(
        final ArrayList<DataType> list,
        final Random random)
    {
        // Go through the list and for each index pick a random index from the
        // remaining part and then swap the two values.
        final int size = list.size();
        for (int i = 0; i < size; i++)
        {
            // Pick an index into the remaining part of the list to swap with
            // element i.
            final int randomIndex = i + random.nextInt(size - i);

            // Now swap the two elements.
            final DataType elementRandom = list.get(randomIndex);
            final DataType elementI = list.get(i);
            list.set(i, elementRandom);
            list.set(randomIndex, elementI);
        }
    }
    
    /**
     * Takes a collection and returns an array list that contains a permutation
     * of the elements of that collection.
     *
     * @param <DataType> Data class to reorder
     * @param  collection The collection to create a permutation of.
     * @param  random The random number generator to use.
     * @return A permutation of the given collection.
     */
    public static <DataType> ArrayList<DataType> createReordering(
        Collection<? extends DataType> collection,
        Random random )
    {
        ArrayList<DataType> result = new ArrayList<DataType>( collection );

        // Reorder the result.
        reorder(result, random);

        return result;
    }

    /**
     * Creates a random permutation of the numbers 0 (inclusive) through n 
     * (exclusive). The permutation contains the all the numbers 0 through n
     * in a random order. There are no duplicates.
     * 
     * @param  n The size of the permutation.
     * @param  random The Random generator to use.
     * @return A random permutation of the numbers 0 through n.
     */
    public static int[] createPermutation(
        int n,
        Random random )
    {
        // Initialize the array with the numbers 0 through n.
        int[] result = new int[n];
        for (int i = 0; i < n; i++)
        {
            result[i] = i;
        }

        // Walk the array and randomly pick the next element.
        for (int i = 0; i < n; i++)
        {
            int randomIndex = i + random.nextInt( n - i );
            int temp = result[i];
            result[i] = result[randomIndex];
            result[randomIndex] = temp;
        }

        return result;
    }

}
