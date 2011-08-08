/*
 * File:                WeightedNumberAverager.java
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
 * Averages together given set of weighted values by adding up the weight times
 * the value and then dividing by the total weight. If the total weight is zero
 * then zero is returned.
 *
 * @author  Kevin R. Dixon
 * @since   3.0
 */
public class WeightedNumberAverager
    extends AbstractCloneableSerializable
    implements Summarizer<WeightedValue<? extends Number>, Double>
{

    /**
     * Instance of WeightedNumberAverager, since it has no state.
     */
    public static final WeightedNumberAverager INSTANCE =
        new WeightedNumberAverager();

    /** 
     * Creates a new WeightedNumberAverager 
     */
    public WeightedNumberAverager()
    {
        super();
    }

    public Double summarize(
        Collection<? extends WeightedValue<? extends Number>> data)
    {
        return weightedAverage(data);
    }

    /**
     * Computes the weighted average of the given data. It is computed by
     * summing each value times its associated weight and then dividing by the
     * sum of all weights.
     *
     * @param   data
     *      The collection of weighted values of numbers to compute the
     *      weighted average over.
     * @return
     *      The weighted average of the given data. It is 0.0 if the sum of
     *      the weights is 0.0.
     */
    public static double weightedAverage(
        final Iterable<? extends WeightedValue<? extends Number>> data)
    {
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        for (WeightedValue<? extends Number> item : data)
        {
            final double value = item.getValue().doubleValue();
            final double weight = item.getWeight();
            weightedSum += value * weight;
            totalWeight += weight;
        }

        // Make sure we do not divide by zero.
        if (totalWeight == 0.0)
        {
            return 0.0;
        }
        else
        {
            return weightedSum / totalWeight;
        }
    }

}
