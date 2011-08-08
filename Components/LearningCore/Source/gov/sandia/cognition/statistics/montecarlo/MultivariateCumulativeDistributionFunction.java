/*
 * File:                MultivariateCumulativeDistributionFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 31, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.Random;

/**
 * Utility class for multivariate cumulative distribution functions (CDF).
 *
 * @author  Kevin R. Dixon
 * @since   3.3.0
 */
public class MultivariateCumulativeDistributionFunction 
{

    /**
     * Computes a multi-variate cumulative distribution for a given input
     * according to the given distribution.
     *
     * @param   input
     *      An input vector.
     * @param   distribution
     *      A multivariate distribution.
     * @param   random
     *      A random number generator.
     * @param   probabilityTolerance
     *      The probability tolerance. Must be between 0.0 and 1.0.
     * @return
     *      The cumulative distribution.
     */
    public static UnivariateGaussian compute(
        Vector input,
        Distribution<Vector> distribution,
        Random random,
        double probabilityTolerance )
    {

        ProbabilityUtil.assertIsProbability(probabilityTolerance);
        double factor = 4*0.25 / probabilityTolerance;
        int numSamples = (int) Math.ceil( factor*factor );
//        System.out.println( "NumSamples = " + numSamples );
        ArrayList<? extends Vector> samples =
            distribution.sample( random, numSamples );
        int numNotLess = 0;
        final int N = input.getDimensionality();
        for( Vector sample : samples )
        {
            input.assertSameDimensionality(sample);
            for( int i = 0; i < N; i++ )
            {
                final double x = input.getElement(i);
                final double y = sample.getElement(i);
                if( x > y )
                {
                    numNotLess++;
                    break;
                }
            }
        }

        double p = 1.0 - (((double) numNotLess) / numSamples);
        double pVariance = p * (1.0-p) / numSamples;
        return new UnivariateGaussian( p, pVariance );

    }

    
}
