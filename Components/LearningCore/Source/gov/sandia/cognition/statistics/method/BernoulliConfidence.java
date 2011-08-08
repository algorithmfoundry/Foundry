/*
 * File:                BinomialChebyshevConfidence.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Computes the Bernoulli confidence interval. In other words, computes
 * the Bernoulli parameter based on
 * the given data and the desired level of confidence.  This answers the
 * question, "What is true range of classification rates given a
 * collection of correct/incorrect guesses at a given level of confidence?"
 * For example, if my classifier gets
 * { Correct, Wrong, Correct, Correct, Correct, Wrong, Correct, Correct },
 * the true classification rate of my classifier at 50% confidence is
 * Pr{ 0.5335 <= p <= 0.9665 } >= 0.5
 *
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class BernoulliConfidence
    extends AbstractCloneableSerializable
    implements ConfidenceIntervalEvaluator<Collection<Boolean>>
{

    /**
     * This class has no members, so here's a static instance.
     */
    public static final BernoulliConfidence INSTANCE =
        new BernoulliConfidence();

    /** Creates a new instance of BernoulliConfidence */
    public BernoulliConfidence()
    {
    }
    
    /**
     * Computes the ConfidenceInterval for the Bernoulli parameter based on
     * the given data and the desired level of confidence.  This answers the
     * question, "What is true range of classification rates given a
     * collection of correct/incorrect guesses at a given level of confidence?"
     * For example, if my classifier gets
     * { Correct, Wrong, Correct, Correct, Correct, Wrong, Correct, Correct },
     * the true classification rate of my classifier at 50% confidence is
     * Pr{ 0.5335 <= p <= 0.9665 } >= 0.5
     * @param data
     * Correct/Wrong data
     * @param confidence
     * Confidence level to place on the confidence interval, must be (0,1]
     * @return
     * Range of values for the accuracy of the classifier at the desired 
     * confidence
     */
    public ConfidenceInterval computeConfidenceInterval(
        Collection<Boolean> data,
        double confidence)
    {
        
        int n = 0;
        for( Boolean value : data )
        {
            if( value == true )
            {
                n++;
            }
        }
        
        double p = ((double) n) / data.size();
        return BernoulliConfidence.computeConfidenceInterval(
            p, data.size(), confidence );
        
    }
    
    /**
     * Computes the ConfidenceInterval for the Bernoulli parameter based on
     * the given data and the desired level of confidence.  This answers the
     * question, "What is true range of classification rates given a
     * collection of correct/incorrect guesses at a given level of confidence?"
     * For example, if my classifier gets
     * { Correct, Wrong, Correct, Correct, Correct, Wrong, Correct, Correct },
     * the true classification rate of my classifier at 50% confidence is
     * Pr{ 0.5335 <= p <= 0.9665 } >= 0.5
     *
     * @param bernoulliParameter
     * Estimated Bernoulli parameter, classifier success rate, must be [0,1]
     * @param numSamples
     * Number of samples used in the determination
     * @param confidence
     * Confidence level to place on the confidence interval, must be (0,1]
     * @return
     * Range of values for the accuracy of the classifier at the desired 
     * confidence
     */
    @PublicationReference(
        author="Wikipedia",
        title="",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Margin_of_error"
    )
    public static ConfidenceInterval computeConfidenceInterval(
        double bernoulliParameter,
        int numSamples,
        double confidence )
    {
        double p = bernoulliParameter;
        double pvar = p*(1-p) / numSamples;
        return INSTANCE.computeConfidenceInterval(
            p, pvar, numSamples,confidence);
    }

    @Override
    public ConfidenceInterval computeConfidenceInterval(
        double mean,
        double variance,
        int numSamples,
        double confidence)
    {
        ProbabilityUtil.assertIsProbability(mean);
        return ChebyshevInequality.INSTANCE.computeConfidenceInterval(
            mean, variance, numSamples, confidence );
    }
    
    /**
     * Computes the number of samples needed to estimate the Bernoulli parameter
     * "p" (mean) within "accuracy" with probability at least "confidence".
     * Answers the question, "How many people do I need to survey to estimate
     * how many people would vote for Budweiser as the King of Beers within
     * a desired accuracy and a set confidence?"  For example, to correctly
     * determine the accuracy within 0.01 with confidence=0.95, we need up to
     * 50000 samples.
     * @param accuracy
     * Desired accuracy to estimate, on the interval (0,1]
     * @param confidence
     * Desired confidence, on the interval (0,1]
     * @return
     * Maximum number of samples needed to achieve the accuracy with the level
     * of confidence
     */
    @PublicationReference(
        author="Wikipedia",
        title="",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Margin_of_error"
    )
    public static int computeSampleSize(
        double accuracy,
        double confidence )
    {
        
        if( (accuracy <= 0.0) ||
            (accuracy > 1.0) )
        {
            throw new IllegalArgumentException( "Accuracy must be (0,1]" );
        }
        
        if( (confidence <= 0.0) ||
            (confidence > 1.0) )
        {
            throw new IllegalArgumentException(
                "Confidence must be (0,1]" );
        }
        
        
        // We're using the Chebyshev Inequality with a Binomial assumption here:
        // Pr{ abs(X-mean) >= a } <= variance / a^2
        // let a = k*sqrt(variance)
        // Pr{ abs(X-mean) >= k*sqrt(variance) } <= 1/k^2,
        // where k is the "number of standard deviations away from the mean"
        //
        // If we use a binomial assumption, then
        // mean = p, and variance=p(1-p)/n
        // Thus, confidence = Pr{ abs(X-p) < k*sqrt(p(1-p))/sqrt(n) } > 1-1/k^2
        // We don't know what "p" is, but we do know that 0<=p<=1 and thus
        // sqrt(p(1-p)) <= 0.5 (equal when p=0.5).
        // So, confidence = Pr{ abs(X-p) < k*0.5/sqrt(n) } > 1-1/k^2
        // However, we're interested in an "accuracy" value, when
        // accuracy = k*0.5/sqrt(n)
        
        // Number of standard deviations: confidence=1-1/k^2
        double numStdDevs = Math.sqrt( 1.0 / (1-confidence) );
        
        // accuracy = k*0.5/sqrt(n)
        double sqrtn = numStdDevs / (2*accuracy);
        int n = (int) Math.ceil( sqrtn*sqrtn );
        
        return n;
        
    }
    
}
