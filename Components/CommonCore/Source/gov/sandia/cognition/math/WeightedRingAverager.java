/*
 * File:                WeightedRingAverager.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Summarizer;
import gov.sandia.cognition.util.WeightedValue;
import java.util.Collection;

/**
 * A type of Summarizer for Rings (Matrices, Vectors, ComplexNumbers). Returns
 * the arithmetic weighted mean of a Collection of Rings.
 * @param <RingType> Type of Ring to average.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class WeightedRingAverager<RingType extends Ring<RingType>>
    extends AbstractCloneableSerializable
    implements Summarizer<WeightedValue<RingType>, RingType>
{

    /** 
     * Creates a new instance of WeightedRingAverager 
     */
    public WeightedRingAverager()
    {
    }

    public RingType summarize(
        Collection<? extends WeightedValue<RingType>> data)
    {

        double weightSum = 0.0;
        RingAccumulator<RingType> weightedSum =
            new RingAccumulator<RingType>();
        for( WeightedValue<RingType> value : data )
        {
            final double w = value.getWeight();
            weightSum += w;
            weightedSum.accumulate( value.getValue().scale( w ) );
        }

        return weightedSum.getSum().scale( 1.0/weightSum );
        
    }
    
}
