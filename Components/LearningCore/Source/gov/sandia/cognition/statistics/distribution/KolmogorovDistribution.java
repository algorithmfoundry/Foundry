/*
 * File:                KolmogorovDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.method.InverseTransformSampling;
import java.util.ArrayList;
import java.util.Random;

/**
 * Contains the Cumulative Distribution Function description for the "D"
 * statistic used within the Kolmogorov-Smirnov test.  
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Kolmogorov-Smirnov test, Kolmogorov distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Kolmogorov_distribution#Kolmogorov_distribution"
)
public class KolmogorovDistribution
    extends AbstractClosedFormUnivariateDistribution<Double>
{

    /**
     * Value of the mean, found empirically, as I can't seem to find the
     * answer in any reference I can get my hands on, {@value}.
     */
    public static final double MEAN = 0.868481392844716;

    /**
     * Value of the variance, found empirically, as I can't seem to find the
     * answer in any reference I can get my hands on, {@value}.
     */
    public static final double VARIANCE = 0.06759934611527044;

    /**
     * Creates a new instance of CumulativeDistribution
     */
    public KolmogorovDistribution()
    {
    }

    @Override
    public Double getMean()
    {
        return MEAN;
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        return InverseTransformSampling.sample(
            this.getCDF(), random, numSamples);
    }

    @Override
    public double getVariance()
    {
        return VARIANCE;
    }

    @Override
    public KolmogorovDistribution.CDF getCDF()
    {
        return new KolmogorovDistribution.CDF();
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().createVector(0);
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(0);
    }

    @Override
    public Double getMinSupport()
    {
        return 0.0;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Contains the Cumulative Distribution Function description for the "D"
     * statistic used within the Kolmogorov-Smirnov test.
     * This is taken from the probks() method from Numerical Recipes in C,
     * p. 626.
     */
    public static class CDF
        extends KolmogorovDistribution
        implements ClosedFormCumulativeDistributionFunction<Double>
    {

        /**
         * Creates a new instance of CDF
         */
        public CDF()
        {
            super();
        }

        @Override
        public Double evaluate(
            final Double input )
        {
            return evaluate( input.doubleValue() );
        }

        /**
         * This is the probks() function from Numerical Recipes in C, pp. 626
         * The NR probks() function actually computes the complement to the CDF,
         * so all the return values in this method will be 1.0-NR, when
         * comparing the code to the NR code.
         * @param input 
         * Value at which to evaluate the CDF
         * @return 
         * CDF value at the input
         */
        @PublicationReference(
            author={
                "William H. Press",
                "Saul A. Teukolsky",
                "Willim T. Vetterling",
                "Brian P. Flannery"
            },
            title="Numerical Recipes in C, Second Edition",
            type=PublicationType.Book,
            year=1992,
            pages=626,
            notes={
                "Loosely based on the NRC probks() method.",
                "Returns complement (1.0-value) of the NRC probks() method."
            },
            url="http://www.nrbook.com/a/bookcpdf.php"
        )
        public static double evaluate(
            final double input )
        {

            if (input < 0.0)
            {
                return 0.0;
            }

            double fac = 2.0;
            double sum = 0.0;
            double term;
            double termbf = 0.0;

            double a2 = -2.0 * input * input;

            final double EPS1 = 1e-3;
            final double EPS2 = 1e-8;
            final int MAX_ITERATIONS = 100;


            for (int j = 1; j <= MAX_ITERATIONS; j++)
            {
                term = fac * Math.exp( a2 * j * j );
                sum += term;
                if ((Math.abs( term ) < EPS1 * termbf) ||
                    (Math.abs( term ) <= EPS2 * sum))
                {
                    // Note: we're returning the complement of the NR function
                    // Sometimes the NR code will return a very small (~-1e-10)
                    // negative value.  This just checks for that and floors it
                    double cdf = 1.0 - sum;
                    if (cdf < 0.0)
                    {
                        cdf = 0.0;
                    }
                    return cdf;
                }

                // Alternating signs in sum
                fac = -fac;
                termbf = Math.abs( term );
            }

            // We only reach here if we fail to converge
            // Note: we're returning the complement of the NR function
            return 0.0;

        }

        @Override
        public KolmogorovDistribution.CDF getCDF()
        {
            return this;
        }

    }

}
