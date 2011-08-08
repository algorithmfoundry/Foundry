/*
 * File:                InverseTransformSampling.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 28, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.root.RootFinder;
import gov.sandia.cognition.learning.algorithm.root.RootFinderRiddersMethod;
import gov.sandia.cognition.learning.algorithm.root.SolverFunction;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DiscreteDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.SmoothUnivariateDistribution;
import java.util.ArrayList;
import java.util.Random;

/**
 * Inverse transform sampling is a method by which one can sample from an
 * arbitrary distribution using only a uniform random-number generator and
 * the ability to empirically invert the CDF.  This allows us to convert
 * Java's random-number generator (Random) into a Beta Distribution, Gamma
 * distribution, or any other scalar distribution.  It does, however, tend
 * to require several function evaluations to invert the CDF and is typically
 * fairly costly rather than those distributions that can be inverted
 * algebraically.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Inverse transform sampling",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Inverse_transform_sampling"
)
public class InverseTransformSampling
{

    /**
     * Default root finding method for the algorithm, RootFinderRiddersMethod.
     */
    public final static RootFinder DEFAULT_ROOT_FINDER =
        new RootFinderRiddersMethod();
//        new MinimizerBasedRootFinder();
//        new RootFinderSecantMethod();

    /**
     * Tolerance for Newton's method, {@value}.
     */
    public static final double DEFAULT_TOLERANCE = 1e-10;

    /**
     * Number of function evaluations needed to invert the distribution.
     */
    public static int FUNCTION_EVALUATIONS;
    
    /**
     * Samples from the given CDF using the inverseRootFinder transform sampling method.
     * @param cdf
     * CDF from which to sample.
     * @param random
     * Random number generator.
     * @param numSamples
     * Number of samples to draw from the CDF.
     * @return
     * Samples draw according to the given CDF.
     */
    public static ArrayList<Double> sample(
        CumulativeDistributionFunction<Double> cdf,
        Random random,
        int numSamples )
    {

        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            double p = random.nextDouble();
            InputOutputPair<Double,Double> result = inverse( cdf, p );
            if( Math.abs(result.getOutput()-p) > DEFAULT_TOLERANCE )
            {
                throw new IllegalArgumentException(
                    "Could not invert the CDF (" + cdf + ") at p = " + p + "(" + result.getOutput() + ")" );
            }
            samples.add( result.getInput() );
        }

        return samples;
    }

    /**
     * Inverts the given CDF, finding the value of "x" so that CDF(x)=p using
     * a root-finding algorithm.
     * @param <NumberType>
     * Type of number observation.
     * @param cdf
     * CDF to invert
     * @param p
     * Probability, [0,1].
     * @return
     * Root of the CDF.
     */
    @SuppressWarnings("unchecked")
    public static <NumberType extends Number> InputOutputPair<NumberType,Double> inverse(
        CumulativeDistributionFunction<NumberType> cdf,
        double p )
    {
        
        if( cdf instanceof DiscreteDistribution )
        {
            return ProbabilityMassFunctionUtil.inverse(cdf, p);
        }
        else if( cdf instanceof SmoothUnivariateDistribution )
        {
            return (InputOutputPair<NumberType, Double>) inverseNewtonsMethod(
                (SmoothUnivariateDistribution) cdf, p, DEFAULT_TOLERANCE );
        }
        else
        {
            return (InputOutputPair<NumberType, Double>) inverseRootFinder(
                DEFAULT_ROOT_FINDER, (CumulativeDistributionFunction<Double>) cdf, p);
        }
    }

    /**
     * Inverts the given CDF, finding the value of "x" so that CDF(x)=p using
     * a root-finding algorithm.
     * @param rootFinder
     * Root-finding algorithm to use.
     * @param cdf
     * CDF to invert
     * @param p
     * Probability, [0,1].
     * @return
     * Root of the CDF.
     */
    public static InputOutputPair<Double,Double> inverseRootFinder(
        RootFinder rootFinder,
        CumulativeDistributionFunction<Double> cdf,
        double p )
    {

        ProbabilityUtil.assertIsProbability(p);

        rootFinder.setTolerance(DEFAULT_TOLERANCE);
        double mean = cdf.getMean();
        double stddev = Math.sqrt( cdf.getVariance() );

        // If our standard deviation is too large, just cap it.
        final double maxDev = 10.0;
        if( (stddev > maxDev) || Double.isInfinite(stddev) )
        {
            stddev = maxDev;
        }
        double delta = stddev * 2.0 * (p-0.5);

        double xhat = mean + delta;
        SolverFunction fhat = new SolverFunction( p, cdf );
        rootFinder.setInitialGuess(xhat);
        InputOutputPair<Double,Double> root = rootFinder.learn(fhat);

        return DefaultInputOutputPair.create(
            root.getInput(), root.getOutput() + p);
    }

    /**
     * Inverts the given CDF, finding the value of "x" so that CDF(x)=p using
     * a root-finding algorithm.
     * @param distribution
     * Distribution to invert
     * @param p
     * Probability, [0,1].
     * @param tolerance
     * Tolerance below which we stop.
     * @return
     * Root of the CDF.
     */
    public static InputOutputPair<Double,Double> inverseNewtonsMethod(
        SmoothUnivariateDistribution distribution,
        double p,
        double tolerance )
    {
        ProbabilityUtil.assertIsProbability(p);

        SmoothCumulativeDistributionFunction cdf = distribution.getCDF();
        InputOutputPair<Double,Double> initialGuess =
            initializeNewtonsMethod(cdf, p, tolerance);

        double xhat = initialGuess.getInput();
        double phat = initialGuess.getOutput();
        if( Math.abs(p-phat) <= tolerance )
        {
            return initialGuess;
        }
        else
        {
            final double xmin = cdf.getMinSupport();
            final double xmax = cdf.getMaxSupport();
            UnivariateProbabilityDensityFunction pdf =
                distribution.getProbabilityFunction();
            double err = p - phat;
            double m = pdf.evaluate(xhat);
            final double stepScale = 0.5;
            double stepMultiplier = 1.0;
            final double maxStep = Math.min( (xmax-xmin)/2.0, 1000.0*cdf.getVariance() );
            for( int iteration = 0; iteration < 100; iteration++ )
            {
                // The PDF is flat at xhat, so just step over by
                // the error.
                double xproposed;
                if( Math.abs(m) <= Double.MIN_VALUE )
                {
                    xproposed = xhat + err*stepMultiplier;
                }

                // The PDF has a slope, so use this to estimate where
                // the value of "p" is... this is Newton's method.
                else
                {
                    double step = -(phat-p)*stepMultiplier / m;
                    if( Math.abs(step) > maxStep )
                    {
                        step = stepMultiplier * Math.signum(step) * maxStep;
                    }
                    xproposed = xhat + step;
                }

                // The proposed step takes us too far to the left.
                if( xproposed <= xmin )
                {
                    stepMultiplier *= stepScale;
                }

                // The proposed step takes us too far to the right.
                else if( xproposed >= xmax )
                {
                    stepMultiplier *= stepScale;
                }

                // The proposed step is within the support of the distribution.
                else
                {
                    // Let's see if the proposal is worse or better...
                    double pproposed = cdf.evaluate(xproposed);
                    double errproposed = p - pproposed;
                    if( Math.abs(errproposed) <= Math.abs(err) )
                    {
                        stepMultiplier = 1.0;
                        xhat = xproposed;
                        phat = pproposed;
                        // The PDF is the slope (derivative) of the CDF...
                        m = pdf.evaluate(xhat);
                        err = errproposed;
                    }
                    else
                    {
                        stepMultiplier *= stepScale;
                    }
                }

                if( Math.abs(err) <= tolerance)
                {
                    break;
                }
            }
        }

        return new DefaultInputOutputPair<Double, Double>( xhat, phat );

    }

    /**
     * Initializes Newton's method for inverse transform sampling.
     * @param cdf
     * CDF to invert.
     * @param p
     * Target value to invert, that is to find "x" such that p=cdf(x).
     * @param tolerance
     * Tolerance before stopping.
     * @return
     * Estimated (xhat,phat) to initialize Newton's method.
     */
    protected static InputOutputPair<Double,Double> initializeNewtonsMethod(
        SmoothCumulativeDistributionFunction cdf,
        double p,
        double tolerance )
    {

        double xmin = cdf.getMinSupport();
        double xmax = cdf.getMaxSupport();
        double xhat;
        double phat;

        if( Math.abs(p) <= tolerance )
        {
            xhat = xmin;
            phat = cdf.evaluate(xhat);
        }
        else if( p == 1.0 )
        {
            xhat = xmax;
            phat = cdf.evaluate(xhat);
        }
        else
        {
            final double mean = cdf.getMean();
            final double pmean = cdf.evaluate(mean);
            xhat = mean;
            phat = pmean;
            if( Math.abs(p-pmean) <= tolerance )
            {
                xhat = mean;
                phat = pmean;
            }
            
            // We're in the left segment.
            else if( p < pmean )
            {
                final double leftSegment = mean-xmin;
                final double leftFraction = p / pmean;
                xhat = leftFraction * leftSegment;

                // If the estimate is effectively zero, then
                // just guess the mean.
                phat = cdf.evaluate(xhat);
                if( Math.abs(phat) <= tolerance )
                {
                    xhat = mean;
                    phat = pmean;
                }
            }

            else
            {
                final double rightSegment = xmax-mean;
                final double rightFraction = (p-pmean) / (1-pmean);
                xhat = rightFraction * rightSegment + mean;

                // If the estimate is effectively 1.0, then
                // just guess the mean
                phat = cdf.evaluate(xhat);
                if( Math.abs(1.0-phat) <= tolerance )
                {
                    xhat = mean;
                    phat = pmean;
                }
            }

            if( Double.isInfinite(xhat) )
            {
                xhat = mean;
                phat = pmean;
            }
        }

        return new DefaultInputOutputPair<Double, Double>( xhat, phat );

    }

}
