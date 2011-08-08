/*
 * File:                FisherSignConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * This is an implementation of the Fisher Sign Test, which is a robust
 * nonparameteric test to determine if two groups have a different mean.
 * However, because the test has essentially no assumptions, it generates
 * very loose confidence bounds.
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@ConfidenceTestAssumptions(
    name="Fisher Sign Test",
    alsoKnownAs="Sign Test",
    description={
        "Determines if there is a statistically significant between the means of two groups",
        "A robust nonparameteric alternative to the paired Student's t-test."
    },
    assumptions={
        "The data from each group is sampled independently of each other."
    },
    nullHypothesis="The means of the two groups is the same.",
    dataPaired=true,
    dataSameSize=true,
    distribution=BinomialDistribution.CDF.class,
    reference=@PublicationReference(
        author="Eric W. Weisstein",
        title="Fisher Sign Test",
        type=PublicationType.WebPage,
        year=2009,
        url="http://mathworld.wolfram.com/FisherSignTest.html"
    )
)
public class FisherSignConfidence
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<? extends Number>>
{

    /** 
     * Default Constructor
     */
    public FisherSignConfidence()
    {
    }

    @Override
    public FisherSignConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Number> data1,
        Collection<? extends Number> data2)
    {

        if (data1.size() != data2.size())
        {
            throw new IllegalArgumentException(
                "data1 and data2 must have same number of elements.");
        }

        int N = 0;
        int b = 0;

        Iterator<? extends Number> i1 = data1.iterator();
        Iterator<? extends Number> i2 = data2.iterator();
        while (i1.hasNext())
        {
            double v1 = i1.next().doubleValue();
            double v2 = i2.next().doubleValue();

            // N tells us how many samples aren't equal
            if (v1 != v2)
            {
                N++;
            }

            // b tells me how many samples where data1 is bigger
            if (v1 > v2)
            {
                b++;
            }

        }

        return new FisherSignConfidence.Statistic(b, N);
    }

    /**
     * Contains the parameters from the Sign Test null-hypothesis evaluation
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * Number of samples where data1 was different than data2
         */
        private int numDifferent;

        /**
         * Number of sample where data1 was greater than data2
         */
        private int numPositiveSign;

        /**
         * Creates a new instance of Statistic
         * 
         * @param numPositiveSign Number of samples where data1 was greater than data2
         * @param numDifferent Number of samples where data1 was different than data2
         */
        public Statistic(
            int numPositiveSign,
            int numDifferent)
        {
            // Insignificant chance is a binomial with p=0.5, so the pvalue of
            // Sign Test is the chance of seeing "b" times where data1 is bigger
            // out of N different samples, where chance is 0.5.  Thus, we have
            // cdf(b,N,0.5) as the chance of seeing more than chance
            super(2.0 * Math.min(
                BinomialDistribution.CDF.evaluate(numDifferent, numPositiveSign, 0.5),
                1.0 - BinomialDistribution.CDF.evaluate(numDifferent, numPositiveSign, 0.5)));

            if (numPositiveSign > numDifferent)
            {
                throw new IllegalArgumentException(
                    "numPositiveSign must be <= numDifferent");
            }
            this.setNumPositiveSign(numPositiveSign);
            this.setNumDifferent(numDifferent);
        }

        /**
         * Getter for numDifferent
         * @return 
         * Number of samples where data1 was different than data2
         */
        public int getNumDifferent()
        {
            return this.numDifferent;
        }

        /**
         * Setter for numDifferent
         * @param numDifferent 
         * Number of samples where data1 was different than data2
         */
        protected void setNumDifferent(
            int numDifferent)
        {
            this.numDifferent = numDifferent;
        }

        /**
         * Getter for numPositiveSign
         * @return 
         * Number of sample where data1 was greater than data2
         */
        public int getNumPositiveSign()
        {
            return this.numPositiveSign;
        }

        /**
         * Setter for numPositiveSign
         * @param numPositiveSign 
         * Number of sample where data1 was greater than data2
         */
        protected void setNumPositiveSign(
            int numPositiveSign)
        {
            this.numPositiveSign = numPositiveSign;
        }

        @Override
        public double getTestStatistic()
        {
            return this.numPositiveSign;
        }

    }

}
