/*
 * File:                ChebyshevInequality.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 4, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.Collection;

/**
 * Computes the Chebyshev Inequality for the given level of confidence.
 * Answers the question: "What range of values can a random variable take
 * if I can estimate its mean and variance at least confidence percent of
 * the time?"
 * 1-confidence = Pr{ abs(X-mean(data)) >= a } <= variance(data) / a^2 ->
 * Pr{ mean-a <= X <= mean+a } >= confidence
 *
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class ChebyshevInequality
    extends AbstractCloneableSerializable
    implements ConfidenceIntervalEvaluator<Collection<Double>>
{

    /**
     * This class has no members, so here's a static instance.
     */
    public static final ChebyshevInequality INSTANCE =
        new ChebyshevInequality();

    /** Creates a new instance of ChebyshevInequality */
    public ChebyshevInequality()
    {
    }
    
    
    /**
     * Computes the Chebyshev Inequality for the given level of confidence.
     * Answers the question: "What range of values can a random variable take
     * if I can estimate its mean and variance at least confidence percent of
     * the time?"
     * 1-confidence = Pr{ abs(X-mean(data)) >= a } <= variance(data) / a^2 ->
     * Pr{ mean-a <= X <= mean+a } >= confidence
     * @param data
     * Data from which to estimate the mean and variance
     * @param confidence
     * Confidence value to find the range of values for
     * @return
     * ConfidenceInterval describing the worst-case range that
     * ANY random variable can take at the given confidence value
     */
    public ConfidenceInterval computeConfidenceInterval(
        Collection<Double> data,
        double confidence)
    {
        Pair<Double,Double> result =
            UnivariateStatisticsUtil.computeMeanAndVariance(data);
        double mean = result.getFirst();
        double variance = result.getSecond();
        
        return computeConfidenceInterval(
            mean, variance, data.size(), confidence );
    }
    
    
    /**
     * Computes the Chebyshev Inequality for the given level of confidence.
     * @param sampleMean
     * The sample mean of the data
     * @param sampleVariance
     * The sample variance of the data
     * @param numSamples
     * The number of samples in the data
     * @param confidence
     * Confidence value to find the range of values for
     *
     * @return
     * ConfidenceInterval describing the worst-case range that
     * ANY random variable can take at the given confidence value
     */
    public ConfidenceInterval computeConfidenceInterval(
        double sampleMean,
        double sampleVariance,
        int numSamples,
        double confidence )
    {
        
        if( (confidence <= 0.0) ||
            (confidence > 1.0) )
        {
            throw new IllegalArgumentException(
                "Confidence must be 0 < confidence <= 1" );
        }
        
        // 1-confidence = Pr{ abs(X-mean) >= a } <= variance / a^2
        
        double a;
        if( confidence < 1.0 )
        {
            a = Math.sqrt( sampleVariance / (1-confidence) );
        }
        else
        {
            a = Double.POSITIVE_INFINITY;
        }
        
        return new ConfidenceInterval(
            sampleMean, sampleMean-a, sampleMean+a, confidence, numSamples );
        
    }
    
}
