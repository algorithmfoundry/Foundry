/*
 * File:                CollectionUtil.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 25, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * The {@code CollectionUtil} class implements static methods for dealing with
 * {@code Collection} and {@code Iterable} objects. They are both put into the
 * same utility class so that they can be interchanged without changing the
 * method call.
 *
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments="Looks good."
)
public class CollectionUtil
    extends Object
{
    /** The default load factor for a hash map is {@value}. */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Returns true if the given collection is null or empty.
     *
     * @param   collection
     *      The collection to determine if it is null or empty.
     * @return
     *      True if the given collection is null or empty.
     */
    public static boolean isEmpty(
        final Collection<?> collection)
    {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns true if the given iterable is null or empty.
     *
     * @param   iterable
     *      The iterable to determine if it is null or empty.
     * @return
     *      True if the given iterable is null or empty.
     */
    public static boolean isEmpty(
        final Iterable<?> iterable)
    {
        if (iterable == null)
        {
            // It is null, so it is empty.
            return true;
        }
        else if (iterable instanceof Collection)
        {
            return ((Collection<?>) iterable).isEmpty();
        }
        else
        {
            return !iterable.iterator().hasNext();
        }
    }

    /**
     * Returns the Collection as an ArrayList.  It first checks to see if
     * data is already an ArrayList and returns the casted value.  Otherwise,
     * this method creates a new ArrayList from the data without copying each
     * value.
     * @param <DataType> Type of data in the Collection.
     * @param data Collection to return as an ArrayList.
     * @return ArrayList of the given Collection.
     */
    public static <DataType> ArrayList<DataType> asArrayList(
        Iterable<DataType> data )
    {
        if( data == null )
        {
            return null;
        }
        else if( data instanceof ArrayList )
        {
            return (ArrayList<DataType>) data;
        }
        else if( data instanceof Collection )
        {
            return new ArrayList<DataType>( (Collection<? extends DataType>) data );
        }
        else
        {
            final int num = CollectionUtil.size(data);
            ArrayList<DataType> retval = new ArrayList<DataType>( num );
            for( DataType value : data )
            {
                retval.add( value );
            }
            return retval;
        }
    }

    /**
     * Determines the size of the given collection, checking for null.
     *
     * @param   collection
     *      The collection to get the size of.
     * @return
     *      The size of the collection. If it is null, zero is returned.
     */
    public static int size(
        final Collection<?> collection)
    {
        if (collection == null)
        {
            return 0;
        }
        else
        {
            return collection.size();
        }
    }

    /**
     * Determines the size of the given iterable. If it is null, zero is
     * returned. If it is a {@code Collection}, then the size method is used.
     * Otherwise, the iterable is iterated over to get the size.
     *
     * @param   iterable
     *      The iterable to determine the size of.
     * @return
     *      The size of the given iterable.
     */
    public static int size(
        final Iterable<?> iterable)
    {
        if (iterable == null)
        {
            // The size is zero.
            return 0;
        }
        else if (iterable instanceof Collection)
        {
            // Get the size from the collection. This cast is justified by
            // not having to loop over all the elements.
            return ((Collection<?>) iterable).size();
        }
        else
        {
            // Cound up the elements in the iterable.
            int counter = 0;
            final Iterator<?> iterator = iterable.iterator();
            while (iterator.hasNext())
            {
                iterator.next();
                counter++;
            }
            return counter;
        }
    }

    /**
     * Gets the first element from an iterable. If the iterable is null or
     * empty, null is returned.
     *
     * @param   <T>
     *      The type of element.
     * @param   iterable
     *      The iterable to get the first element from.
     * @return
     *      The first element from the iterable, if one exists. Otherwise,
     *      null.
     */
    public static <T> T getFirst(
        final Iterable<? extends T> iterable)
    {
        if (iterable == null)
        {
            // No first element.
            return null;
        }

        final Iterator<? extends T> iterator = iterable.iterator();
        if (iterator.hasNext())
        {
            return iterator.next();
        }
        else
        {
            // No first element.
            return null;
        }
    }

    /**
     * Gets the first element of the list. If the list is null or empty, null
     * is returned.
     *
     * @param   <T>
     *      The type of element in the list.
     * @param   list
     *      The list to get the first element from.
     * @return
     *      The first element from the list, if one exists. Otherwise, null.
     */
    public static <T> T getFirst(
        final List<? extends T> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }
        else
        {
            return list.get(0);
        }
    }

    /**
     * Gets the last element of the list. If the list is null or empty, null
     * is returned.
     *
     * @param   <T>
     *      The type of element in the list.
     * @param   list
     *      The list to get the last element from.
     * @return
     *      The last element from the list, if one exists. Otherwise, null.
     */
    public static <T> T getLast(
        final List<? extends T> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }
        else
        {
            return list.get(list.size() - 1);
        }
    }

    /**
     * Returns the set of indices of the data array such that
     * data[return[0..k-1]] <= data[return[k]] <= data[return[k+1...N-1]].
     * This algorithm will partition the data set in O(N) time.  This is
     * faster than the typical sort and split time of O(N*log(N)).
     * Note that the subsets data[return[0..k-1]] and data[return[k+1..N-1]]
     * are themselves unsorted.  Because of this, NRC calls "Selection is
     * sorting's austere sister."
     * @param <ComparableType> Type of data to compare to.
     * @param k
     * kth largest value to split upon.
     * @param data
     * Data to partition, left unchanged by this method.
     * @param comparator Comparator used to determine if two values
     * are greater than, less than, or equal to each other.
     * @return
     * Indices into data so that
     * data[return[0..k-1]] <= data[return[k]] <= data[return[k+1...N-1]].
     */
    @PublicationReference(
        author={
            "William H Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes, Third Edition",
        type=PublicationType.Book,
        year=2007,
        pages=1104,
        notes="Loosely based on the selecti() function"
    )
    public static <ComparableType> int[] findKthLargest(
        int k,
        ArrayList<? extends ComparableType> data,
        Comparator<? super ComparableType> comparator )
    {

        final int num = data.size();
        final int[] indices = new int[ num ];
        for( int i = 0; i < num; i++ )
        {
            indices[i] = i;
        }

        int leftIndex = 0;
        int rightIndex = num-1;

        while( true )
        {
            if( rightIndex <= leftIndex+1 )
            {
                if( rightIndex == leftIndex+1 )
                {
                    swapIfAGreaterThanB(leftIndex, rightIndex, indices, data, comparator);
                }
                return indices;
            }
            else
            {
                final int mid = (leftIndex + rightIndex) / 2;
                swapArrayValues(mid, leftIndex+1, indices);
                swapIfAGreaterThanB(leftIndex,   rightIndex,  indices, data, comparator);
                swapIfAGreaterThanB(leftIndex+1, rightIndex,  indices, data, comparator);
                swapIfAGreaterThanB(leftIndex,   leftIndex+1, indices, data, comparator);
                int i = leftIndex + 1;
                final int originali = indices[i];
                int j = rightIndex;
                ComparableType valueOriginali = data.get(originali);
                while( true )
                {
                    // Find from the left a value that is >= valueOriginali
                    do
                    {
                        i++;
                    } while( comparator.compare( data.get(indices[i]), valueOriginali ) < 0 );

                    // Find from the right a value that is <= valueOriginali
                    do
                    {
                        j--;
                    } while( comparator.compare( data.get(indices[j]), valueOriginali ) > 0 );

                    if( j < i )
                    {
                        break;
                    }

                    swapArrayValues(i, j, indices);
                }

                indices[leftIndex+1] = indices[j];
                indices[j] = originali;
                if( j >= k )
                {
                    rightIndex = j-1;
                }
                if( j <= k )
                {
                    leftIndex = i;
                }

            }
        }

    }

    /**
     * Swaps the indices "a" and "b" in the array "indices" if the corresponding
     * data values data[indices[a]] is greater than data[indices[b]].
     * @param <ComparableType> Type of data to compare to.
     * @param a first index
     * @param b second index
     * @param indices array of indices to index into "data", which is
     * modified by this method.
     * @param data ArrayList of values, unchanged.
     * @param comparator Comparator used to determine if two values
     * are greater than, less than, or equal to each other.
     * @return
     * True if swapped, false if left alone.
     */
    private static <ComparableType> boolean swapIfAGreaterThanB(
        int a,
        int b,
        int[] indices,
        ArrayList<? extends ComparableType> data,
        Comparator<? super  ComparableType> comparator )
    {
        final boolean doSwap = comparator.compare(
            data.get(indices[a]), data.get(indices[b]) ) > 0;
        if( doSwap )
        {
            swapArrayValues(a, b, indices);
        }
        return doSwap;

    }


    /**
     * Swaps the two indexed values in the indices array.
     * @param i1 First index
     * @param i2 Second index
     * @param indices Array of indices to swap
     */
    private static void swapArrayValues(
        int i1,
        int i2,
        int[] indices )
    {
        int temp = indices[i1];
        indices[i1] = indices[i2];
        indices[i2] = temp;
    }

    /**
     * Creates a partition of the given data into "numPartition" roughly equal
     * sets, preserving their pre-existing sequential ordering, with the
     * nonzero remainder elements going into the final partition.
     *
     * @param <DataType> Type of data to partition.
     * @param data Collection of data to partition
     * @param numPartitions Number of partitions to create.
     * @return
     * List of Lists of size data.size()/numPartitions, with the remainder of
     * data elements going into the final partition.
     */
    public static <DataType> ArrayList<List<? extends DataType>> createSequentialPartitions(
        Iterable<? extends DataType> data,
        int numPartitions )
    {
        if (data instanceof List<?>)
        {
            @SuppressWarnings("unchecked")
            final List<? extends DataType> list = (List<? extends DataType>) data;
            return createSequentialPartitions(list,
                numPartitions);
        }

        final int numData = CollectionUtil.size( data );
        final int numEach = numData / numPartitions;
        ArrayList<List<? extends DataType>> retval =
            new ArrayList<List<? extends DataType>>( numPartitions );

        int index = 0;
        Iterator<? extends DataType> iterator = data.iterator();
        for( int n = 0; n < numPartitions; n++ )
        {
            // The remainder goes into the final partition
            int numThis = (n < (numPartitions-1)) ? numEach : (numData-index);
            ArrayList<DataType> partition = new ArrayList<DataType>( numThis );

            for( int i = 0; i < numThis; i++ )
            {
                partition.add( iterator.next() );
                index++;
            }

            retval.add( partition );

        }
        return retval;
    }

    /**
     * Creates a partition of the given data into "numPartition" roughly equal
     * sets, preserving their pre-existing sequential ordering, with the
     * nonzero remainder elements going into the final partition.
     *
     * @param <DataType> Type of data to partition.
     * @param data Collection of data to partition
     * @param numPartitions Number of partitions to create.
     * @return
     * List of Lists of size data.size()/numPartitions, with the remainder of
     * data elements going into the final partition.
     */
    public static <DataType> ArrayList<List<? extends DataType>> createSequentialPartitions(
        List<? extends DataType> data,
        int numPartitions)
    {
        final int numData = CollectionUtil.size(data);
        final int numEach = numData / numPartitions;
        ArrayList<List<? extends DataType>> result =
            new ArrayList<List<? extends DataType>>(numPartitions);
        int beginIndex = 0;
        int endIndex = beginIndex + numEach;
        for (int i = 0; i < numPartitions; i++)
        {
            if (i == numPartitions - 1)
            {
                endIndex = numData;
            }
            result.add(data.subList(beginIndex, endIndex));
            beginIndex = endIndex;
            endIndex += numEach;
        }

        return result;
    }

    /**
     * Returns the indexed value into the {@code Iterable}. It first checks to
     * see if the {@code Iterable} is a {@code List}, and if so calls the get
     * method. Otherwise, it walks the {@code Iterable} to get to the element.
     *
     * @param <DataType>
     *      The type of data.
     * @param iterable
     *      The iterable to pull the value from.
     * @param index
     *      The 0-based index to pull from the iterable.
     * @return
     *      The value at the given spot in the iterable.
     * @throws IndexOutOfBoundsException
     *      If the index is less than zero or greater than or equal to the
     *      number of elements in the iterable.
     */
    public static <DataType> DataType getElement(
        final Iterable<DataType> iterable,
        int index)
    {
        if (iterable instanceof List<?>)
        {
           return ((List<DataType>) iterable).get(index);
        }
        else
        {
            if (index < 0)
            {
                // Bad index.
                throw new IndexOutOfBoundsException("index must be >= 0");
            }

            for (DataType v : iterable)
            {
                if (index == 0)
                {
                    return v;
                }

                index--;
            }

            // Bad index.
            throw new IndexOutOfBoundsException("index >= iterable size");
        }

    }

    /**
     * Removes and returns the indexed value into the {@code Iterable}. It
     * first checks to see if the {@code Iterable} is a {@code List}, and if so
     * calls the remove method. Otherwise, it walks the {@code Iterable} to
     * get to the element and remove it. This only works on {@code Iterable}s
     * that are {@code List}s or whose {@code Iterator} implements the optional
     * {@code remove} method.
     *
     * @param <DataType>
     *      The type of data.
     * @param iterable
     *      The iterable to remove the value from.
     * @param index
     *      The 0-based index to remove from the iterable.
     * @return
     *      The value removed from the given index in the iterable.
     * @throws IndexOutOfBoundsException
     *      If the index is less than zero or greater than or equal to the
     *      number of elements in the iterable.
     * @throws UnsupportedOperationException
     *      If the iterable does not support remove.
     */
    public static <DataType> DataType removeElement(
        final Iterable<DataType> iterable,
        int index)
    {
        if (iterable instanceof List<?>)
        {
           return ((List<DataType>) iterable).remove(index);
        }
        else
        {
            if (index < 0)
            {
                // Bad index.
                throw new IndexOutOfBoundsException("index must be >= 0");
            }

            Iterator<DataType> iterator = iterable.iterator();

            while (iterator.hasNext())
            {
                DataType value = iterator.next();

                if (index == 0)
                {
                    iterator.remove();
                    return value;
                }

                index--;
            }

            // Bad index.
            throw new IndexOutOfBoundsException("index >= iterable size");
        }

    }

    /**
     * Performs a toString on each element given iterable with a given delimiter
     * between elements.
     *
     * @param   list
     *      The list to call toString on each element for.
     * @param   delimiter
     *      The delimiter.
     * @return
     *      A string with the toString on each element in the list called with
     *      a given delimiter between elements. If null is given, then
     *      "null" is returned. If an empty list is given, "" is returned.
     */
    public static String toStringDelimited(
        final Iterable<?> list,
        final String delimiter)
    {
        if (list == null)
        {
            return "null";
        }

        final StringBuffer result = new StringBuffer();
        final Iterator<?> iterator = list.iterator();

        if (iterator.hasNext())
        {
            result.append(iterator.next());
        }

        while (iterator.hasNext())
        {
            result.append(delimiter);
            result.append(iterator.next());
        }

        return result.toString();
    }


    /**
     * Creates a new ArrayList from the given pair of values.
     *
     * @param <DataType>
     *      The data type.
     * @param   first
     *      The first value.
     * @param   second
     *      The second value.
     * @return
     *      A new array list with the two elements in it.
     */
    public static <DataType> ArrayList<DataType> createArrayList(
        final DataType first,
        final DataType second)
    {
        final ArrayList<DataType> result = new ArrayList<DataType>(2);
        result.add(first);
        result.add(second);
        return result;
    }

    /**
     * Creates a new {@link HashMap} with the given expected size. It uses the
     * default load factor (0.75) to estimate the proper number of elements for
     * the data structure to avoid a rehash or resize when the given number of
     * elements are added.
     *
     * @param   <KeyType>
     *      The type for the key of the map.
     * @param   <ValueType>
     *      The type of the value in the map.
     * @param   size
     *      The size. Must be positive.
     * @return
     *      A new hash map with the given expected size.
     */
    public static <KeyType, ValueType> HashMap<KeyType, ValueType> createHashMapWithSize(
        final int size)
    {
        final int initialCapacity = (int) Math.ceil(size / DEFAULT_LOAD_FACTOR);
        return new HashMap<KeyType, ValueType>(initialCapacity, 
            DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates a new {@link LinkedHashMap} with the given expected size. It uses the
     * default load factor (0.75) to estimate the proper number of elements for
     * the data structure to avoid a rehash or resize when the given number of
     * elements are added.
     *
     * @param   <KeyType>
     *      The type for the key of the map.
     * @param   <ValueType>
     *      The type of the value in the map.
     * @param   size
     *      The size. Must be positive.
     * @return
     *      A new hash map with the given expected size.
     */
    public static <KeyType, ValueType> LinkedHashMap<KeyType, ValueType> createLinkedHashMapWithSize(
        final int size)
    {
        final int initialCapacity = (int) Math.ceil(size / DEFAULT_LOAD_FACTOR);
        return new LinkedHashMap<KeyType, ValueType>(
            initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates a new {@link HashSet} with the given expected size. It uses the
     * default load factor (0.75) to estimate the proper number of elements for
     * the data structure to avoid a rehash or resize when the given number of
     * elements are added.
     *
     * @param   <ValueType>
     *      The type of the value in the set.
     * @param   size
     *      The size. Must be positive.
     * @return
     *      A new hash map with the given expected size.
     */
    public static <ValueType> HashSet<ValueType> createHashSetWithSize(
        final int size)
    {
        final int initialCapacity = (int) Math.ceil(size / DEFAULT_LOAD_FACTOR);
        return new HashSet<ValueType>(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates a new {@link LinkedHashSet} with the given expected size. It uses the
     * default load factor (0.75) to estimate the proper number of elements for
     * the data structure to avoid a rehash or resize when the given number of
     * elements are added.
     *
     * @param   <ValueType>
     *      The type of the value in the set.
     * @param   size
     *      The size. Must be positive.
     * @return
     *      A new hash map with the given expected size.
     */
    public static <ValueType> LinkedHashSet<ValueType> createLinkedHashSetWithSize(
        final int size)
    {
        final int initialCapacity = (int) Math.ceil(size / DEFAULT_LOAD_FACTOR);
        return new LinkedHashSet<ValueType>(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
}
