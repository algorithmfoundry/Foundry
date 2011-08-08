/*
 * File:                DataHistogram.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import java.util.Collection;

/**
 * A data structure that allows the building of histograms.  That is, class
 * allows inputs to have different number of counts associated with them.
 * @param <DataType> Type of data to use.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface DataHistogram<DataType>
    extends Distribution<DataType>
{

    /**
     * Adds one of the given value to the histogram.
     *
     * @param  value The value to add.
     */
    void add(
        DataType value);
    
    /**
     * Adds the given number of the given value to the histogram.
     *
     * @param  value The value to add.
     * @param  count The number of value to add.
     */
    void add(
        DataType value,
        int count );
    
    /**
     * Removes one of the given value from the histogram.
     * 
     * @param  value The value to remove.
     */
    void remove(
        DataType value);
    
    /**
     * Removes the given number of the given value to the histogram.
     *
     * @param  value The value to remove.
     * @param  count The count of value to remove.
     */
    void remove(
        DataType value,
        int count );

    /**
     * Adds all of the counts from the given other histogram to this one.
     *
     * @param   <OtherDataType>
     *      The type of the other histogram, which must extend the data type of
     *      this histogram.
     * @param   other
     *      The other histogram whose values should be added.
     */
    <OtherDataType extends DataType> void addAll(
        final DataHistogram<OtherDataType> other);

    /**
     * Gets all of the values that have entries in the histogram.
     *
     * @return All of the values that have entries in the histogram.
     */
    Collection<DataType> getValues();
    
    /**
     * Gets the count associated with the given value.
     *
     * @param  input The value to get the count of.
     * @return The number of values associated with the input.
     */
    int getCount(
        DataType input);
    
    /**
     * Gets the total count of all values in the histogram.
     *
     * @return The total count of all values.
     */
    int getTotalCount();

    /**
     * Gets the fraction of the count that a given value has. If there is
     * no count, then 0.0 is returned. Otherwise, it is the value of the count
     * assigned to the value divided by the maximum value.
     *
     * @param   value
     *      The input to get the fraction of the count for.
     * @return
     *      The percentage of the count assigned to the given value. If the
     *      total count is zero or if the input has no count assigned to it,
     *      then zero is returned. Otherwise, it is the count assigned to the
     *      input divided by the total count.
     */
    public double getFraction(
        final DataType value);
    
}
