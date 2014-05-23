/*
 * File:                SmoothUnivariateDistribution.java
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

import java.util.Random;

/**
 * A closed-form scalar distribution that is also smooth.  That is, this
 * type of distribution has a PDF and a CDF.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface SmoothUnivariateDistribution
    extends ClosedFormUnivariateDistribution<Double>,
    ClosedFormComputableDistribution<Double>
{

    @Override
    public UnivariateProbabilityDensityFunction getProbabilityFunction();
    
    @Override
    public SmoothCumulativeDistributionFunction getCDF();

    @Override
    public Double getMean();

    /**
     * Samples a value from this distribution as a double. This is a 
     * convenience method to potentially avoid boxing.
     * 
     * @param   random
     *      Random number generator to use.
     * @return 
     *      A value sampled from this distribution.
     */
    public double sampleAsDouble(
        final Random random);

    /**
     * Samples values from this distribution as an array of doubles. This is a 
     * convenience method to potentially avoid boxing.
     * 
     * @param   random
     *      Random number generator to use.
     * @param   count
     *      The number of values to sample. Cannot be negative
     * @return 
     *      An array of values sampled from this distribution. Size is
     *      that of count.
     */    
    public double[] sampleAsDoubles(
        final Random random,
        final int count);
    
    /**
     * Samples values from this distribution as an array of doubles. This is a 
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
        final double[] output,
        final int start,
        final int length);

}
