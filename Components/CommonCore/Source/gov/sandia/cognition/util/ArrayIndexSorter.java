/*
 * File:                ArrayIndexSorter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.collection.DefaultComparator;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Returns the indices of the array sorted in ascending or descending order
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-02",
            changesNeeded=false,
            comments="Looks fine."
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=false,
            comments={
                "Added a couple comments.",
                "Otherwise, looks fine."
            }
        )
    }
)
public class ArrayIndexSorter
{
    
    /**
     * Returns the indices of the array sorted in ascending order
     * @param valuesToSort values that should be sorted in ascending order,
     * via a call to Arrays.sort(), does not change values of "valuesToSort"
     * @return indices of the ascending-ordered valuesToSort, so that
     * index[0] is the smallest value, and index[index.length-1] is the biggest
     */
    public static int[] sortArrayAscending(
        double[] valuesToSort )
    {
        int M = valuesToSort.length;
        DoubleIntegerPair[] pairs =
            new DoubleIntegerPair[ M ];
        
        for( int i = 0; i < M; i++ )
        {
            pairs[ i ] = new DoubleIntegerPair( valuesToSort[i], i );
        }
    
        Arrays.sort( pairs, new DefaultComparator<DoubleIntegerPair>() );
        
        int[] indices = new int[ M ];
        for( int i = 0; i < M; i++ )
        {
            indices[i] = pairs[i].getIndex();
        }
        
        return indices;
        
    }

    /**
     * Returns the indices of the array sorted in ascending order. This is
     * accomplished by a call to sortArrayAscending and then swapping the
     * result. As such, this will always be the slower of the two calls.
     * @param valuesToSort values that should be sorted in descending order
     * @return indices of the descending-ordered valuesToSort, so that
     * index[0] is the biggest value, and index[index.length-1] is the smallest
     */
    public static int[] sortArrayDescending(
        double[] valuesToSort )
    {
        
        int M = valuesToSort.length;
        int[] ascendingIndices =
            ArrayIndexSorter.sortArrayAscending( valuesToSort );
        int[] descendingIndices = new int[ M ];
        for( int i = 0; i < M; i++ )
        {
            descendingIndices[i] = ascendingIndices[M-i-1];
        }
    
        return descendingIndices;
    }
        
    
    /**
     * Container class for a value (double) and index (int)
     */
    static private class DoubleIntegerPair
        implements Comparable<DoubleIntegerPair>, Serializable
    {
        /** Value to consider and sort */
        private double value;
        
        /** Index place holder */
        private int index;
        
        /**
         * Creates a new DoubleIntegerPair
         * @param value Value to consider and sort
         * @param index index place holder
         */
        DoubleIntegerPair(
            double value,
            int index )
        {
            this.setValue(value);
            this.setIndex(index);
        }

        public int compareTo(
            DoubleIntegerPair o)
        {
            return Double.compare(this.getValue(), o.getValue());
        }

        @Override
        public boolean equals(
            final Object object)
        {
            return object instanceof DoubleIntegerPair 
                && this.equals((DoubleIntegerPair) object);
        }
        
        /**
         * Determines if this object is equal to the given object.
         * 
         * @param   other The given object.
         * @return  True if the two are equal; otherwise, false.
         */
        public boolean equals(
            final DoubleIntegerPair other)
        {
            return other != null && this.getIndex() == other.getIndex()
                && this.getValue() == other.getValue();
        }

        @Override
        public int hashCode()
        {
            final long doubleBits = Double.doubleToLongBits(this.value);
            return this.index + (int) (doubleBits ^ doubleBits >>> 32);
        }

        /**
         * Getter for value
         * @return Value to consider and sort
         */
        public double getValue()
        {
            return this.value;
        }

        /**
         * Setter for value
         * @param value Value to consider and sort
         */
        public void setValue(
            double value)
        {
            this.value = value;
        }

        /**
         * Getter for index
         * @return Index place holder
         */
        public int getIndex()
        {
            return this.index;
        }

        /**
         * Setter for index
         * @param index Index place holder
         */
        public void setIndex(
            int index)
        {
            this.index = index;
        }

    }
    
}
