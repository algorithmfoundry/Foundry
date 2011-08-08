/*
 * File:                MarkovInequality.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Implementation of the Markov Inequality hypothesis test.  This is a
 * distribution-free test that says "what is the probability that ANY
 * random variable can take a magnitude greater than 'a', given samples
 * drawn from the random variable"
 * Pr{abs(X)>=a} <= mean(abs(data))/a.  For example, assume we have data:
 * (3 -1 2) and would like to know what range of values are possible with 95%
 * confidence -> MarkovInequality.computeConfidenceInterval( (3 -1 2), 0.95 )
 * The sample mean of the absolute values is (3+1+2)/3 = 2.
 * 1-0.95 <= 2/a -> a=40.
 * Thus Pr{ -40 <= X <= 40 } >= 0.95
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Markov's Inequality",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Markov%27s_inequality"
)
public class MarkovInequality
    extends AbstractCloneableSerializable
    implements ConfidenceIntervalEvaluator<Collection<Double>>
{
    
    /** Creates a new instance of MarkovInequality */
    public MarkovInequality()
    {
        super();
    }

    /**
     * Computes the Markov Inequality Bound for the given data at the
     * given confidence level.  Answers the question: what range of values
     * can I expect at least "confidence" percent of the time?
     * 1-confidence = Pr{abs(a)>=a} <= mean(abs(data))/a ->
     * Pr{ -a <= X <= a } >= confidence
     * @param data 
     * Values drawn from the underlying distribution
     * @param confidence 
     * Confidence value to find the range of values for
     * @return 
     * ConfidenceInterval describing the worst-case range that
     * ANY random variable can take at the given confidence value
     */
    public ConfidenceInterval computeConfidenceInterval(
        final Collection<Double> data,
        final double confidence)
    {
        double sampleMean = 0.0;
        for( Double value : data )
        {
            sampleMean += Math.abs(value);
        }
        if( !data.isEmpty() )
        {
            sampleMean /= data.size();
        }
        
        return MarkovInequality.computeConfidenceInterval(
            sampleMean, data.size(), confidence );
    }
    
    
    /**
     * Computes the Markov Inequality Bound for the given data at the
     * given confidence level.  Answers the question: what range of values
     * can I expect at least "confidence" percent of the time?
     * 1-confidence = Pr{abs(a)>=a} <= mean(abs(data))/a ->
     * Pr{ -a <= X <= a } >= confidence
     * 
     * @param sampleMean 
     * Sample mean of the underlying data
     * @param numSamples 
     * Number of samples used in computing the mean
     * @param confidence 
     * Confidence value to find the range of values for
     * @return 
     * ConfidenceInterval describing the worst-case range that
     * ANY random variable can take at the given confidence value
     */
    public static ConfidenceInterval computeConfidenceInterval(
        final double sampleMean,
        final int numSamples,
        final double confidence )
    {
        
        if( (confidence <= 0.0) ||
            (confidence > 1.0) )
        {
            throw new IllegalArgumentException( 
                "Confidence must be 0 < confidence <= 1" );
        }        
        
        // 1-confidence = Pr{ abs(X) >= a } <= mean(abs(X)) / a
        
        double a;
        if( confidence < 1.0 )
        {
            a = sampleMean / (1-confidence);
        }
        else
        {
            a = Double.POSITIVE_INFINITY;
        }
        
        return new ConfidenceInterval( 
            sampleMean, -a, a, confidence, numSamples );        
        
    }
    
}
