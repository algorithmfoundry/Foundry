/*
 * File:                DataCountTreeSetBinnedMapHistogram.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 27, 2007, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.statistics.method.TreeSetBinner;
import java.util.Arrays;
import java.util.Collection;

/**
 * The {@code DataCountTreeSetBinnedMapHistogram} class extends a
 * {@code MapBasedDataHistogram} by mapping values to user defined bins
 * using a {@code TreeSetBinner}.  The values in a given bin are inclusive
 * of the lower bound of the bin and exclusive of the upper bound of the bin.
 *
 * @param   <ValueType>
 *      Value for the domain (x-axis, independent variable), may be something
 *      like an Integer, etc; must be Comparable for use with TreeSetBinner.
 * @author  Zachary Benz
 * @since   2.0
 */
@CodeReview(
    reviewer = "Justin Basilico",
    date = "2009-05-29",
    changesNeeded = false,
    comments = "Cleaned up the formatting and javadoc."
)
public class DataCountTreeSetBinnedMapHistogram<ValueType extends Comparable<? super ValueType>>
    extends MapBasedDataHistogram<ValueType>
{

    /** The binner used to map values to bins */
    private TreeSetBinner<ValueType> binner;

    /**
     * Creates a new {@code DataCountTreeBinnedMapHistogram} using the provided
     * {@code TreeSetBinner}.
     * 
     * @param   binner
     *      The TreeSetBinner to use to perform value binning.
     */
    public DataCountTreeSetBinnedMapHistogram(
        final TreeSetBinner<ValueType> binner)
    {
        super();

        this.setBinner(binner);
    }

    /**
     * Creates a new {@code DataCountTreeBinnedMapHistogram} using
     * the provided list of bin boundaries.  The bin boundary list will
     * be used to initialize a TreeSetBinner for performing value binning.
     * 
     * @param   binBoundaries
     *      The values to be used as boundary cutoffs for bins; bin membership
     *      is inclusive of the lower bin bound and exclusive of the upper bin
     *      bound.
     */
    public DataCountTreeSetBinnedMapHistogram(
        final Collection<? extends ValueType> binBoundaries)
    {
        this(new TreeSetBinner<ValueType>(binBoundaries));
    }

    /**
     * Creates a new instance of DataCountTreeBinnedMapHistogram using
     * the provided list of bin boundaries.  The bin boundary list will
     * be used to initialize a TreeSetBinner for performing value binning.
     * 
     * @param   binBoundaries
     *      A list of values to be used as boundary cutoffs for bins; bin
     *      membership is inclusive of the lower bin bound and exclusive of the
     *      upper bin bound.
     */
    public DataCountTreeSetBinnedMapHistogram(
        final ValueType... binBoundaries)
    {
        this(Arrays.asList(binBoundaries));
    }

    /**
     * Adds one to the bin corresponding to the given value; does
     * nothing if given value does not have a corresponding bin.
     * 
     * @param   value
     *      {@inheritDoc}
     */
    @Override
    public void add(
        final ValueType value)
    {
        // Check for invalid value indicated by null bin.
        final ValueType bin = this.getBinner().findBin(value);

        if (bin != null)
        {
            super.add(bin);
        }
    }

    /**
     * Ads the given number to the bin corresponding to the given value; does
     * nothing if given value does not have a corresponding bin.
     * 
     * @param   value
     *      {@inheritDoc}
     * @param   number
     *      {@inheritDoc}
     */
    @Override
    public void add(
        final ValueType value,
        final int number)
    {
        // Check for invalid value indicated by null bin.
        final ValueType bin = this.getBinner().findBin(value);

        if (bin != null)
        {
            super.add(bin, number);
        }
    }

    /**
     * Removes one from the bin corresponding to the given value; does
     * nothing if given value does not have a corresponding bin.
     * 
     * @param   value
     *      {@inheritDoc}
     */
    @Override
    public void remove(
        final ValueType value)
    {
        // Check for invalid value indicated by null bin.
        ValueType bin = this.getBinner().findBin(value);

        if (bin != null)
        {
            super.remove(bin);
        }
    }

    /**
     * Removes the given number from the bin corresponding to the given value;
     * does nothing if given value does not have a corresponding bin.
     * 
     * @param   value
     *      {@inheritDoc}
     * @param   number
     *      {@inheritDoc}
     */
    @Override
    public void remove(
        final ValueType value,
        final int number)
    {
        // Check for invalid value indicated by null bin.
        final ValueType bin = this.getBinner().findBin(value);

        if (bin != null)
        {
            super.remove(bin, number);
        }
    }

    /**
     * Gets the total number of bins.
     * 
     * @return 
     *      The total number of bins.
     */
    public int getBinCount()
    {
        return this.getBinner().getBinCount();
    }


    /**
     * Gets the TreeSetBinner used to perform value binning.
     *
     * @return
     *      TreeSetBinner used to perform value binning.
     */
    public TreeSetBinner<ValueType> getBinner()
    {
        return this.binner;
    }

    /**
     * Sets the TreeSetBinner used to perform value binning
     *
     * @param   binner
     *      TreeSetBinner to be used to perform value binning.
     */
    private void setBinner(
        final TreeSetBinner<ValueType> binner)
    {
        this.binner = binner;
    }
    
}
