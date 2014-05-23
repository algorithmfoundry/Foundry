/*
 * File:            IntegerDistribution.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics;

import java.util.Random;

/**
 * Defines a distribution over natural numbers. It is defined as a distribution 
 * over Number because the mean may itself not be an integer. However, it 
 * provides a way to get samples as integers.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public interface IntegerDistribution
    extends UnivariateDistribution<Number>,
        DiscreteDistribution<Number>
{
    
    /**
     * Draws a single random sample from the distribution as an int.
     * 
     * @param   random
     *      The random number generator to use.
     * @return 
     *      A sample from the distribution.
     */
    public int sampleAsInt(
        final Random random);
    
    /**
     * Samples values from this distribution as an array of ints. This is a
     * convenience method to potentially avoid boxing.
     * 
     * @param   random
     *      Random number generator to use.
     * @param   sampleCount
     *      The number of values to sample. Cannot be negative
     * @return 
     *      An array of values sampled from this distribution. Size is
     *      that of count.
     */
    public int[] sampleAsInts(
        final Random random,
        final int sampleCount);
    
    /**
     * Samples values from this distribution as an array of ints. This is a 
     * convenience method to potentially avoid boxing.
     * 
     * @param   random
     *      Random number generator to use.
     * @param   output
     *      The array to write the result into. Cannot be null.
     * @param   start
     *      The offset in the array to start writing at. Cannot be negative.
     * @param   length
     *      The number of values to sample. Cannot be negative.
     */
    public void sampleInto(
        final Random random,
        final int[] output,
        final int start,
        final int length);
}
