/*
 * File:                RingAccumulator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;

/**
 * The <code>RingAccumulator</code> class implements a simple object that
 * is used to accumulate objects that implement the Ring interface. This
 * provides an easy way to compute sums or means of Ring objects by simply
 * adding them to the accumulator.
 *
 * Note that if nothing is added to the accumulator, the results returned
 * will be null because it will not be given a default object to use to
 * start at "zero".
 *
 * @param <RingType> Type of Ring that this object can operate upon
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments="Looks good."
        ),
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=true,
            comments="A few minor changes.",
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2006-05-16",
                moreChangesNeeded=false,
                comments="Minor updates based on comments."
            )
        )
    }
)
public class RingAccumulator<RingType extends Ring<RingType>>
{

    /** The number of items added to the accumulator. */
    private int count;

    /** The sum of the items. This starts as null until one item is added. */
    private RingType sum;

    /**
     * Creates a new instance of RingAccumulator
     */
    public RingAccumulator()
    {
        super();
        this.clear();
    }

    /**
     * Creates a new instance of RingAccumulator, adding all of the given
     * items to start with.
     *
     * @param  items The initial set of items to add to the accumulator,
     *         throws NullPointerException if null
     */
    public RingAccumulator(
        Iterable<? extends RingType> items )
    {
        this();
        this.accumulate( items );
    }

    /**
     * Clears the accumulator. Resetting the count and sum.
     */
    public void clear()
    {
        this.setCount( 0 );
        this.setSum( null );
    }

    /**
     * Adds the given object to the accumulation.
     *
     * @param  item The object to add to the accumulation,
     *         throws NullPointerException if null.
     */
    public void accumulate(
        RingType item )
    {
        if (item == null)
        {
            // Error: Bad parameter.
            throw new NullPointerException( "Cannot add null." );
        }

        // We are adding an object so increment the count.
        this.setCount( this.getCount() + 1 );

        if (this.sum == null)
        {
            // This is the first object so copy it to get the initial
            // sum.
            this.setSum( item.clone() );
        }
        else
        {
            // Add the object to the sum.
            this.sum.plusEquals( item );
        }
    }

    /**
     * Adds all of the given set of objects onto the accumulation.
     *
     * @param  items The objects to add to the accumulation,
     *         throws NullPointerException if null
     */
    public void accumulate(
        Iterable<? extends RingType> items )
    {
        // Just call the accumulate method on each object.
        for (RingType item : items)
        {
            this.accumulate( item );
        }
    }

    /**
     * Returns the sum scaled by the given scale factor.
     *
     * @param  scaleFactor The amount to scale the sum by.
     * @return The sum scaled by the given amount or null if no sum
     *         has been defined.
     */
    public RingType scaleSum(
        double scaleFactor )
    {
        if (this.sum == null)
        {
            // There is no sum so just return null.
            return null;
        }
        else
        {
            // Scale the sum.
            return this.sum.scale( scaleFactor );
        }
    }

    /**
     * Computes the mean of the accumulated values.
     *
     * @return The mean of the accumulated values.
     */
    public RingType getMean()
    {
        if (this.getCount() <= 0)
        {
            // We return null because we do not have the default sum to
            // operate on.
            return null;
        }
        else
        {
            // Divide the sum by the count.
            return this.scaleSum( 1.0 / (double) this.count );
        }
    }

    /**
     * Returns the sum object underlying the accumulator.
     *
     * @return The sum of the accumulation.
     */
    public RingType getSum()
    {
        return this.sum;
    }

    /**
     * Gets the number of items that have been added to the accumulator.
     *
     * @return The number of items that have been added to the accumulator.
     */
    public int getCount()
    {
        return this.count;
    }

    /**
     * Sets the count.
     *
     * @param  count The new count.
     */
    private void setCount(
        int count )
    {
        this.count = count;
    }

    /** 
     * Sets the sum of items.
     *
     * @param  sum The new sum of items.
     */
    private void setSum(
        RingType sum )
    {
        this.sum = sum;
    }

}
