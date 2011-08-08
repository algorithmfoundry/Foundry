/*
 * File:                RingAverager.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;

/**
 * A type of Averager for Rings (Matrices, Vectors, ComplexNumbers). Returns
 * the arithmetic mean of a Collection of Rings
 *
 * @param <RingType> Type of Ring to average
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-26",
    changesNeeded=false,
    comments="Looks good."
)
public class RingAverager<RingType extends Ring<RingType>>
    extends AbstractCloneableSerializable
    implements Summarizer<RingType, RingType>
{

    /** Creates a new instance of RingAverager */
    public RingAverager()
    {
    }

    public RingType summarize(
        Collection<? extends RingType> data )
    {
        RingAccumulator<RingType> accumulator =
            new RingAccumulator<RingType>( data );

        return accumulator.getMean();
    }

}
