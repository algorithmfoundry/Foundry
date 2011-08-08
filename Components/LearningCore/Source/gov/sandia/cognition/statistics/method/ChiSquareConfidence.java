/*
 * File:                ChiSquareConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 23, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is the chi-square goodness-of-fit test. This test allows us to compare
 * observations against expected results, where the observations and 
 * expectations are recorded for discrete groups/conditions/bins.  The null
 * hypothesis is that the observed values were drawn from the same distribution
 * as the expected values.
 * <BR><BR>
 * The chi-square goodness-of-fit test is a discrete version of the more 
 * general Kolmogorov-Smirnov test.  If your data were drawn from a continuous
 * distribution, I would recommend the K-S test instead.
 * 
 * @author Kevin R. Dixon
 * @since 2.0
 */
@ConfidenceTestAssumptions(
    name="Chi-Squre test",
    alsoKnownAs="Pearson's Chi-Square test",
    description="The chi-square test determines if the given data were generated from the same discrete distributions.",
    assumptions={
        "A large sample, typically above 30.",
        "Typically, each bin from the discrete distribution must have at least 5 samples.",
        "The underlying discrete distribution must obey the weak law of large numbers.",
        "The observations are assumed to be independent."
    },
    nullHypothesis="The frequency of events in the two datasets is consistent.",
    dataPaired=true,
    dataSameSize=true,
    distribution=ChiSquareDistribution.CDF.class,
    reference=@PublicationReference(
        author="Wikipedia",
        title="Pearson's chi-square test",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Pearson%27s_chi-square_test"
    )
)
public class ChiSquareConfidence
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<? extends Number>>
{

    /**
     * Default instance variable since the class has no members.
     */
    public static final ChiSquareConfidence INSTANCE = new ChiSquareConfidence();

    /** 
     * Creates a new instance of ChiSquareConfidence 
     */
    public ChiSquareConfidence()
    {
    }

    /**
     * Computes the chi-square test between a collection of data and a
     * Probability Mass Function that may have create the observed data.
     * @param <DomainType> Domain type of the PMF.
     * @param data
     * Data observed from some discrete distribution.
     * @param pmf
     * Probability mass function that may have created the observed data.
     * @return
     * Chi-square test results.
     */
    public static <DomainType> ChiSquareConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends DomainType> data,
        ProbabilityMassFunction<DomainType> pmf )
    {

        // Add up all the counts from the sampled data.
        Collection<? extends DomainType> domain = pmf.getDomain();
        Map<DomainType,Double> counts =
            new HashMap<DomainType,Double>( domain.size() );
        for( DomainType x : data )
        {
            // Ensure that the data are drawn from a subset of the
            // domain of the PMF
            if( !domain.contains(x) )
            {
                throw new IllegalArgumentException(
                    "Observed data " + x + " is not in domain of PMF" );
            }

            Double c = counts.get(x);
            if( c == null )
            {
                c = 0.0;
            }
            c++;
            counts.put(x,c);
        }

        // Compute the expected counts, as well as the observed counts.
        final int numSamples = data.size();
        ArrayList<Double> expected = new ArrayList<Double>( domain.size() );
        ArrayList<Double> observed = new ArrayList<Double>( domain.size() );
        for( DomainType d : domain )
        {
            double p = pmf.evaluate(d);
            double expectedCount = p*numSamples;
            if( expectedCount <= 0.0 )
            {
                expectedCount = Double.MIN_VALUE;
            }
            expected.add( expectedCount );

            Double count = counts.get(d);
            if( count == null )
            {
                count = 0.0;
            }
            observed.add( count );
        }

        // Run a chi-square test on the observed versus expected counts.
        return INSTANCE.evaluateNullHypothesis(observed, expected);

    }

    public ChiSquareConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Number> data1,
        Collection<? extends Number> data2)
    {
        if (data1.size() != data2.size())
        {
            throw new IllegalArgumentException(
                "data1 must have the same size as data2!");
        }

        double degreesOfFreedom = data1.size() - 1.0;
        double chiSquare = 0.0;
        Iterator<? extends Number> i1 = data1.iterator();
        Iterator<? extends Number> i2 = data2.iterator();
        for (int i = 0; i < data1.size(); i++)
        {
            double v1 = i1.next().doubleValue();
            double v2 = i2.next().doubleValue();
            if (v2 <= 0.0)
            {
                throw new IllegalArgumentException(
                    "Value of bin " + i + " in data2 cannot be <= 0.0!");
            }
            double temp = v1 - v2;
            chiSquare += temp * temp / v2;
        }

        return new ChiSquareConfidence.Statistic(chiSquare, degreesOfFreedom);
    }

    /**
     * Confidence Statistic for a chi-square test
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * Chi-square value
         */
        private double chiSquare;

        /**
         * Number of degrees of freedom in the test
         */
        private double degreesOfFreedom;

        /**
         * Creates a new instance of chiSquare
         * @param chiSquare
         * Chi-square value
         * @param degreesOfFreedom
         * Number of degrees of freedom in the test
         */
        public Statistic(
            double chiSquare,
            double degreesOfFreedom)
        {
            super(1.0 - ChiSquareDistribution.CDF.evaluate(
                chiSquare, degreesOfFreedom));
            this.chiSquare = chiSquare;
            this.degreesOfFreedom = degreesOfFreedom;
        }

        /**
         * Gets for chiSquare
         * @return
         * Chi-square value
         */
        public double getChiSquare()
        {
            return this.chiSquare;
        }

        /**
         * Getter for degreesOfFreedom
         * @return
         * Number of degrees of freedom
         */
        public double getDegreesOfFreedom()
        {
            return this.degreesOfFreedom;
        }

    }

}
