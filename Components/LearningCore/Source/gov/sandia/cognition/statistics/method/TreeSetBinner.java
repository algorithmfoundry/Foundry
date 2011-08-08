/*
 * File:                TreeSetBinner.java
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

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Implements a {@code Binner} that employs a {@code TreeSet} to define the
 * boundaries of a contiguous set of bins. The user specified boundaries are
 * used to construct the bins, where bin membership is inclusive of the lower
 * bin bound and exclusive of the upper bin bound.  Bins are labeled with their
 * lower bound value.
 * 
 * For example, if the bounds 1.5, 3.7, 4.2, and 10.9 are provided, then
 * three contiguous bins will be created:
 *   [1.5, 3.7), with the label 1.5
 *   [3.7, 4.2), with the label 3.7
 *   [4.2, 10.9), with the label 4.2
 *
 * @param   <ValueType>
 *      Value type to be binned; must be Comparable.
 * @author  zobenz
 * @since   2.0
 */
@CodeReview(
    reviewer = "Justin Basilico",
    date = "2009-05-29",
    changesNeeded = false,
    comments = {"Changed the implementation slightly to mach the interface.",
        "Cleaned up the javadoc."}
)
public class TreeSetBinner<ValueType extends Comparable<? super ValueType>>
    extends AbstractCloneableSerializable
    implements Binner<ValueType, ValueType>
{

    /**
     * TreeSet used to store bin boundaries, determine which bin a value
     * goes in.
     */
    private TreeSet<ValueType> binSet;

    /** Minimum value, inclusive, allowed in bins. */
    private ValueType minValue;

    /** Maximum value, exclusive, allowed in bins. */
    private ValueType maxValue;

    /** 
     * Creates a new {@code TreeSetBinner} using the provided List of
     * bin boundaries.
     * 
     * @param   binBoundaries
     *      A List of values to be used as boundary cutoffs for bins; bin
     *      membership is inclusive of the lower bin bound and exclusive of
     *      the upper bin bound.
     */
    public TreeSetBinner(
        final Collection<? extends ValueType> binBoundaries)
    {
        super();

        // Throw all the bin boundaries in to initialize
        this.setBinSet(new TreeSet<ValueType>(binBoundaries));

        // Store the min and max boundaries for parameter checking use
        this.setMinValue(this.getBinSet().first());
        this.setMaxValue(this.getBinSet().last());

        // Now remove the highest bin boundary in order to get
        // proper getBin functionality
        this.getBinSet().remove(this.getBinSet().last());
    }

    /**
     * Creates a new {@code TreeSetBinner} using the provided list of
     * bin boundaries.
     * 
     * @param   binBoundaries
     *      A list of values to be used as boundary cutoffs for bins; bin
     *      membership is inclusive of the lower bin bound and exclusive of the
     *      upper bin bound
     */
    public TreeSetBinner(
        final ValueType... binBoundaries)
    {
        this(Arrays.asList(binBoundaries));
    }

    public ValueType findBin(
        final ValueType value)
    {
        // Check against min and max values
        if ((value.compareTo(this.getMinValue()) < 0) ||
            (value.compareTo(this.getMaxValue()) >= 0))
        {
            return null;
        }

        // Determine which bin
        if (this.getBinSet().contains(value))
        {
            return value;
        }
        else
        {
            return this.getBinSet().headSet(value).last();
        }
    }

    public int getBinCount()
    {
        return this.getBinSet().size();
    }

    public TreeSet<ValueType> getBinSet()
    {
        return this.binSet;
    }

    /**
     * Sets the TreeSet to use for bin boundary determination
     *
     * @param binSet TreeSet to use for bin boundary determination
     */
    private void setBinSet(
        final TreeSet<ValueType> binSet)
    {
        this.binSet = binSet;
    }


    /**
     * Gets the minimum value, inclusive, allowed in bins
     *
     * @return Minimum value, inclusive, allowed in bins
     */
    public ValueType getMinValue()
    {
        return this.minValue;
    }

    /**
     * Sets the minimum value, inclusive, allowed in bins
     *
     * @param minValue The minimum value, inclusive, to be allowed in bins
     */
    private void setMinValue(
        final ValueType minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Gets the maximum value, exclusive, allowed in bins
     *
     * @return Maximum value, exclusive, allowed in bins
     */
    public ValueType getMaxValue()
    {
        return this.maxValue;
    }

    /**
     * Sets the maximum value, exclusive, allowed in bins
     *
     * @param maxValue The maximum value, inclusive, to be allowed in bins
     */
    private void setMaxValue(
        final ValueType maxValue)
    {
        this.maxValue = maxValue;
    }

}
