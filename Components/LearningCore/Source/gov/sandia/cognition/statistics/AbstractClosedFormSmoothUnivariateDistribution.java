/*
 * File:                AbstractClosedFormSmoothUnivariateDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Partial implementation of SmoothUnivariateDistribution
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractClosedFormSmoothUnivariateDistribution
    extends AbstractClosedFormUnivariateDistribution<Double>
    implements SmoothUnivariateDistribution
{

    @Override
    public Double getMean()
    {
        return this.getMeanAsDouble();
    }
    
    @Override
    public double sampleAsDouble(
        final Random random)
    {
        return this.sampleAsDoubles(random, 1)[0];
    }

    @Override
    public double[] sampleAsDoubles(
        final Random random,
        final int count)
    {
        final double[] result = new double[count];
        this.sampleInto(random, result, 0, count);
        return result;
    }
    
    @Override
    public void sampleInto(
        final Random random,
        final int sampleCount,
        final Collection<? super Double> output)
    {
        final double[] samples = this.sampleAsDoubles(random, sampleCount);
        for (final double sample : samples)
        {
            output.add(sample);
        }
    }
    
}
