/*
 * File:            AbstractClosedFormIntegerDistribution.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics;

import java.util.Random;

/**
 * An abstract class for closed-form integer distributions. Defines utility
 * methods.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public abstract class AbstractClosedFormIntegerDistribution 
    extends AbstractClosedFormUnivariateDistribution<Number>
    implements ClosedFormDiscreteUnivariateDistribution<Number>,
        IntegerDistribution
{

    /**
     * Creates a new {@link AbstractClosedFormIntegerDistribution}.
     */
    public AbstractClosedFormIntegerDistribution()
    {
        super();
    }

    @Override
    public int[] sampleAsInts(
        final Random random,
        final int sampleCount)
    {
        final int[] result = new int[sampleCount];
        this.sampleInto(random, result, 0, sampleCount);
        return result;
    }

    @Override
    public void sampleInto(
        final Random random,
        final int[] output,
        final int start,
        final int length)
    {
        final int end = start + length;
        for (int i = start; i < end; i++)
        {
            output[i] = this.sampleAsInt(random);
        }
    }
    
}
