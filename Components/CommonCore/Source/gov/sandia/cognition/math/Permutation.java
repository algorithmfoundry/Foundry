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

        int size = result.size();
        for (int i = 0; i < size; i++)
        {
            int randomIndex = i + random.nextInt( size - i );
            DataType randomElement = result.get( randomIndex );
            DataType temp = result.get( i );
            result.set( i, randomElement );
            result.set( randomIndex, temp );
        }

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
