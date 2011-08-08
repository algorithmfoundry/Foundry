/*
 * File:                RangeExcludedArrayList.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * The {@code RangeExcludedArrayList} class implements a light-weight list on
 * top of an {@code ArrayList} where a certain range of values is excluded from
 * the list. In some ways, this class is a mirror of the subList method that
 * exists on the {@code ArrayList} class, which is that it gives a list that
 * contains everything, except what is in the sub-list. The implementation
 * creates a read-only collection that remains O(1) for random access.
 *
 * Note: One difference from the subList method is that the both indices given
 * to this list are inclusive, because it makes more sense when excluding a
 * range.
 *
 * @param  <E> The type stored in the collection.
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer = "Kevin R. Dixon",
    date = "2008-02-08",
    changesNeeded = false,
    comments =
    {
        "I'm impressed: iteration (foreach) works on this class.  I was ready to flunk this code review, but my unit test passes.",
        "Looks fine."
    }
)
public class RangeExcludedArrayList<E>
    extends AbstractList<E>
    implements RandomAccess, MultiCollection<E>
{

    /** The underlying list. */
    private ArrayList<E> list;

    /** The minimum index to exclude (inclusive). */
    private int fromIndex;

    /** The size excluded. */
    private int sizeExcluded;

    /**
     * Creates a new instance of RangeExcludedArrayList.
     *
     * @param  list The list to apply the range exclusion to.
     * @param  fromIndex The lower index to exclude (inclusive).
     * @param  toIndex The upper index to exclude (inclusive).
     */
    public RangeExcludedArrayList(
        final ArrayList<E> list,
        final int fromIndex,
        final int toIndex)
    {
        if (fromIndex < 0)
        {
            throw new IndexOutOfBoundsException(
                "fromIndex = " + fromIndex);
        }
        else if (toIndex >= list.size())
        {
            throw new IndexOutOfBoundsException(
                "toIndex = " + toIndex);
        }
        else if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException(
                "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }

        // Save the data. We save the size exlcuded in order to avoid computing
        // the difference each time.
        this.list = list;
        this.fromIndex = fromIndex;
        this.sizeExcluded = toIndex + 1 - fromIndex;
    }

    public E get(
        final int index)
    {
        if (index < this.fromIndex)
        {
            return this.list.get(index);
        }
        else // ( index >= fromIndex )
        {
            return this.list.get(this.sizeExcluded + index);
        }
    }

    public int size()
    {
        return this.list.size() - this.sizeExcluded;
    }

    public List<? extends Collection<E>> subCollections()
    {
        if (this.sizeExcluded <= 0)
        {
            // The result is the whole list.
            return Collections.singletonList(this.list);
        }

        // Create the list to hold the result.
        final ArrayList<List<E>> result = new ArrayList<List<E>>(2);

        // Add the first segment, if there is one.
        if (this.fromIndex > 0)
        {
            result.add(this.list.subList(0, this.fromIndex));
        }

        // Add the second segment, if there is one.
        final int toIndex = this.fromIndex + this.sizeExcluded;
        final int listSize = this.list.size();
        if (toIndex < listSize)
        {
            result.add(this.list.subList(toIndex, listSize));
        }

        return result;
    }

    public int getSubCollectionsCount()
    {
        return this.subCollections().size();
    }

}
