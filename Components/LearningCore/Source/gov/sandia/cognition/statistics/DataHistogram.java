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

import java.util.List;

/**
 * A data structure that allows the building of histograms.  That is, class
 * allows inputs to have different number of counts associated with them.
 * @param <DataType> Type of data to use.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface DataHistogram<DataType>
    extends DiscreteDistribution<DataType>
{

    /**
     * Adds one of the given value to the histogram.
     *
     * @param  value The value to add.
     */
    public void add(
        final DataType value);
    
    /**
     * Adds the given number of the given value to the histogram.
     *
     * @param  value The value to add.
     * @param  count The number of value to add.
     */
    public void add(
        final DataType value,
        final int count );
    
    /**
     * Removes one of the given value from the histogram.
     * 
     * @param  value The value to remove.
     */
    public void remove(
        final DataType value);
    
    /**
     * Removes the given number of the given value to the histogram.
     *
     * @param  value The value to remove.
     * @param  count The count of value to remove.
     */
    public void remove(
        final DataType value,
        final int count );

    /**
     * Adds all of the given values to the histogram.
     *
     * @param  values
     *      The values to add.
     */
    public void addAll(
        final Iterable<? extends DataType> values);

    /**
     * Adds all of the counts from the given other histogram to this one.
     *
     * @param   <OtherDataType>
     *      The type of the other histogram, which must extend the data type of
     *      this histogram.
     * @param   other
     *      The other histogram whose values should be added.
     */
    public <OtherDataType extends DataType> void addAll(
        final DataHistogram<OtherDataType> other);

    /**
     * Gets the count associated with the given value.
     *
     * @param  input The value to get the count of.
     * @return The number of values associated with the input.
     */
    public int getCount(
        final DataType input);

    /**
     * Determines whether or not the histogram is empty. It is empty if the
     * total count is zero.
     *
     * @return
     *      True if the histogram is empty; otherwise, false.
     */
    public boolean isEmpty();
    
    /**
     * Gets the total count of all values in the histogram.
     *
     * @return The total count of all values.
     */
    public int getTotalCount();

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

    /**
     * Finds the maximum count over all values.
     *
     * @return
     *      The maximum count over all values.
     */
    public int getMaximumCount();

    /**
     * Finds the first value with the maximum count.
     *
     * @return
     *      First value with the maximum count, if one exists. If all the counts
     *      are 0, null is returned.
     */
    public DataType getMaximumValue();

    /**
     * Gets all of the values that have the maximum count.
     *
     * @return
     *      All of the values that have the maximum count. If all the counts are
     *      zero, the list is empty.
     */
    public List<DataType> getMaximumValues();
    
    /**
     * Gets the entropy of the values in the histogram.
     *
     * @return The entropy of the values in the histogram.
     */
    public double getEntropy();

    @Override
    public DataHistogram.PMF<DataType> getProbabilityFunction();

    /**
     * A PMF of a DataHistogram
     * @param <DataType> Type of data to use.
     */
    public static interface PMF<DataType>
        extends DataHistogram<DataType>,
        ProbabilityMassFunction<DataType>
    {
    }

}
