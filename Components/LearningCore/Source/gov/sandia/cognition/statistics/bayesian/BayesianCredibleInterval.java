/*
 * File:                BayesianCredibleInterval.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 7, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DiscreteDistribution;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.statistics.ScalarDistribution;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.InverseTransformSampling;

/**
 * A Bayesian credible interval defines a bound that a scalar parameter is
 * within the given interval.  This is the Bayesian analogue to the frequentist
 * notion of a confidence interval.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Credible interval",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Credible_interval"
)
public class BayesianCredibleInterval 
    extends ConfidenceInterval
{

    /**
     * Creates a new instance of ConfidenceInterval
     * @param median
     * Median value of the parameter
     * @param lowerBound
     * Lower bound for the parameter
     * @param upperBound
     * Upper bound for the parameter
     * @param confidence
     * Confidence that the parameter is within the bound, or 1-pvalue, on the
     * interval [0,1], where confidence=0 means the bound will be zero, while
     * confidence=1 means that the confidence bound will be the entire support
     * of the underlying distribution.
     */
    public BayesianCredibleInterval(
        double median,
        double lowerBound,
        double upperBound,
        double confidence )
    {
        super( median, lowerBound, upperBound, confidence, 1 );
    }

    /**
     * Creates a Bayesian credible interval by inverting the given CDF.
     * @param <NumberType>
     * Type of number to consider
     * @param distribution
     * Distribution to compute the credible interval of, typically the
     * posterior from a Bayesian estimation algorithm.
     * @param confidence
     * Confidence that the parameter is within the bound, or 1-pvalue, on the
     * interval [0,1], where confidence=0 means the bound will be zero, while
     * confidence=1 means that the confidence bound will be the entire support
     * of the underlying distribution.
     * @return
     * Confidence interval of the parameter given the distribution
     */
    @SuppressWarnings("unchecked")
    public static <NumberType extends Number> BayesianCredibleInterval compute(
        ScalarDistribution<NumberType> distribution,
        double confidence )
    {

        CumulativeDistributionFunction<NumberType> cdf = distribution.getCDF();
        final double pvalue = 1.0-confidence;
        final double pmin = pvalue/2.0;
        final double pmax = 1.0-pvalue/2.0;

        double xmin;
        double xmax;
        double xmedian;

        if( cdf instanceof InvertibleCumulativeDistributionFunction )
        {
            InvertibleCumulativeDistributionFunction<NumberType> icdf =
                (InvertibleCumulativeDistributionFunction<NumberType>) cdf;
            xmin = icdf.inverse(pmin).doubleValue();
            xmax = icdf.inverse(pmax).doubleValue();
            xmedian = icdf.inverse(0.5).doubleValue();
        }
        else if( cdf instanceof DiscreteDistribution )
        {
            xmin = ProbabilityMassFunctionUtil.inverse(cdf, pmin).getInput().doubleValue();
            xmax = ProbabilityMassFunctionUtil.inverse(cdf, pmax).getInput().doubleValue();
            xmedian = ProbabilityMassFunctionUtil.inverse(cdf, 0.5).getInput().doubleValue();
        }
        else
        {
            xmin = InverseTransformSampling.inverse( cdf, pmin).getInput().doubleValue();
            xmax = InverseTransformSampling.inverse( cdf, pmax).getInput().doubleValue();
            xmedian = InverseTransformSampling.inverse( cdf, 0.5 ).getInput().doubleValue();
        }

        return new BayesianCredibleInterval( xmedian, xmin, xmax, confidence );

    }
    
}
