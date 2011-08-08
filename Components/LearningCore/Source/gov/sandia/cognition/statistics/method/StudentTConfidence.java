/*
 * File:                StudentTConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2007, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.util.Summarizer;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class implements Student's t-tests for different uses.  The confidence
 * test is the Paired Student's t-test to determine if the difference between
 * the pairs have zero mean.  The confidence interval calculation computes
 * the Student-t confidence interval for the mean of the given data.
 * @author Kevin R. Dixon
 * @since  2.0
 */
@ConfidenceTestAssumptions(
    name="Paired Student's t-test",
    description="Computes the value of the null hypothesis that the differences between paired samples have zero mean and that the data are sampled from a Gaussian distributions with equal variances.",
    alsoKnownAs="Dependent t-test for paired samples",
    nullHypothesis="The means of the groups are equal.",
    assumptions={
        "The data for the pairs are iid samples from a Gaussian distribution with equal variances.",
        "The common variances times the degrees of freedom is a chi-square distribution.",
        "The data pairs should be sampled independently from each other."
    },
    distribution=StudentTDistribution.CDF.class,
    dataPaired=true,
    dataSameSize=true,
    reference=@PublicationReference(
        author="Wikipedia",
        title="Student's t-test, Dependent t-test for paired samples",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Student_t_test#Dependent_t-test_for_paired_samples"
    )
)
public class StudentTConfidence
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<? extends Double>>,
    ConfidenceIntervalEvaluator<Collection<? extends Double>>
{
    
    /** Creates a new instance of StudentTConfidence */
    public StudentTConfidence()
    {
    }

    /**
     * Computes a paired Student-t test for the given data.  The datasets must
     * be the same size.
     * @param data1 First dataset to consider
     * @param data2 Second dataset to consider
     * @return 
     * ConfidenceStatistic for a Student-t test
     */
    @PublicationReference(
        author={
            "William H. Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes in C, Second Edition",
        type=PublicationType.Book,
        year=1992,
        pages=618,
        notes="Function tptest()",
        url="http://www.nrbook.com/a/bookcpdf.php"
    )
    public StudentTConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Double> data1,
        Collection<? extends Double> data2 )
    {

        if (data1.size() != data2.size())
        {
            throw new IllegalArgumentException(
                "Data collections must have same number of elements" );
        }

        int N = data1.size();

        Pair<Double,Double> g1 = UnivariateStatisticsUtil.computeMeanAndVariance(data1);
        Pair<Double,Double> g2 = UnivariateStatisticsUtil.computeMeanAndVariance(data2);

        double mean1 = g1.getFirst();
        double var1 = g1.getSecond();

        double mean2 = g2.getFirst();
        double var2 = g2.getSecond();

        double dof = N - 1;
        Iterator<? extends Number> i1 = data1.iterator();
        Iterator<? extends Number> i2 = data2.iterator();
        double cov = 0.0;
        for (int n = 0; n < N; n++)
        {
            double v1 = i1.next().doubleValue();
            double v2 = i2.next().doubleValue();
            cov += (v1 - mean1) * (v2 - mean2);
        }
        cov /= dof;

        double sd = Math.sqrt( (var1 + var2 - 2 * cov) / N );
        double t = Math.abs( (mean1 - mean2) / sd );

        return new StudentTConfidence.Statistic( t, dof );
    }

    public ConfidenceInterval computeConfidenceInterval(
        Collection<? extends Double> data,
        double confidence )
    {
        UnivariateGaussian g =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(
            data, UnivariateGaussian.MaximumLikelihoodEstimator.DEFAULT_VARIANCE );
        

        return StudentTConfidence.computeConfidenceInterval( g, data.size(), confidence );
    }

    /**
     * Computes the Student-t confidence interval given a distribution of
     * data, number of samples, and corresponding confidence interval
     * @param dataDistribution 
     * UnivariateGaussian describing the distribution of the underlying data
     * @param numSamples 
     * Number of samples in the underlying data
     * @param confidence 
     * Confidence value to assume for the ConfidenceInterval
     * @return 
     * ConfidenceInterval capturing the range of the mean of the data
     * at the desired level of confidence
     */
    public static ConfidenceInterval computeConfidenceInterval(
        UnivariateGaussian dataDistribution,
        int numSamples,
        double confidence )
    {

        if ((confidence <= 0.0) ||
            (confidence > 1.0))
        {
            throw new IllegalArgumentException(
                "Confidence must be on the interval (0,1]" );
        }

        double alpha = 1.0 - confidence;
        int dof = numSamples - 1;

        StudentTDistribution.CDF cdf = new StudentTDistribution.CDF( dof );
        double z = -cdf.inverse( 0.5 * alpha );
        double delta = z * Math.sqrt( dataDistribution.getVariance() / numSamples );
        double mean = dataDistribution.getMean();

        if (delta < 0.0)
        {
            delta = 0.0;
        }

        return new ConfidenceInterval(
            mean, mean - delta, mean + delta, confidence, numSamples );

    }

    /**
     * Confidence statistics for a Student-t test
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * Value that is used in the Student-t CDF to compute the probability.
         * Usually just called the "t-statistic"
         */
        private double t;

        /**
         * Number of degrees of freedom in the Student-t distribution, usually
         * the number of data points - 1
         */
        private double degreesOfFreedom;

        /**
         * Creates a new instance of Statistic
         * @param t 
         * Value that is used in the Student-t CDF to compute the probability.
         * Usually just called the "t-statistic"
         * @param degreesOfFreedom 
         * Number of degrees of freedom in the Student-t distribution, usually
         * the number of data points - 1
         */
        public Statistic(
            double t,
            double degreesOfFreedom )
        {
            super( Statistic.twoTailTStatistic( t, degreesOfFreedom ) );
            this.setT( t );
            this.setDegreesOfFreedom( degreesOfFreedom );
        }

        /**
         * Copy Constructor
         * @param other Statistic to copy
         */
        public Statistic(
            Statistic other )
        {
            this( other.getT(), other.getDegreesOfFreedom() );
        }

        @Override
        public Statistic clone()
        {
            return (Statistic) super.clone();
        }

        /**
         * Computes the likelihood that a StudentTDistribution would generate
         * a LESS LIKELY sample as "t", given the degrees of freedom.  This is a
         * two tailed test, thus, we're computing the probability that a Student-t
         * distribution would be as far away as "t" (both tails)
         * @param t 
         * Sample to determine how likely a worse sample is than "t"
         * @param degreesOfFreedom 
         * Number of degrees of freedom in the Student-t distribution
         * @return 
         * Probability that a Student-t distribution would generate as bad of
         * a sample as "t"
         */
        public static double twoTailTStatistic(
            double t,
            double degreesOfFreedom )
        {
            StudentTDistribution.CDF cdf =
                new StudentTDistribution.CDF( degreesOfFreedom );
            return 2.0 * cdf.evaluate( -t );
        }

        /**
         * Getter for t
         * @return 
         * Value that is used in the Student-t CDF to compute the probability.
         * Usually just called the "t-statistic"
         */
        public double getT()
        {
            return this.t;
        }

        /**
         * Setter for t
         * @param t 
         * Value that is used in the Student-t CDF to compute the probability.
         * Usually just called the "t-statistic"
         */
        protected void setT(
            double t )
        {
            this.t = t;
        }

        /**
         * Getter for degreesOfFreedom
         * @return 
         * Number of degrees of freedom in the Student-t distribution, usually
         * the number of data points - 1
         */
        public double getDegreesOfFreedom()
        {
            return this.degreesOfFreedom;
        }

        /**
         * Setter for degreesOfFreedom
         * @param degreesOfFreedom 
         * Number of degrees of freedom in the Student-t distribution, usually
         * the number of data points - 1
         */
        protected void setDegreesOfFreedom(
            double degreesOfFreedom )
        {
            if (degreesOfFreedom <= 0.0)
            {
                throw new IllegalArgumentException( "degreesOfFreedom > 0.0" );
            }
            this.degreesOfFreedom = degreesOfFreedom;
        }

    }

    /**
     * An implementation of the {@code Summarizer} interface for creating a
     * {@code ConfidenceInterval}
     */
    public static class Summary
        extends AbstractCloneableSerializable
        implements Summarizer<Double, ConfidenceInterval>
    {

        /** The confidence for the created interval. */
        private double confidence;

        /**
         * Creates a new Summarizer.
         *
         * @param  confidence The confidence for the interval.
         */
        public Summary(
            final double confidence )
        {
            super();

            this.setConfidence( confidence );
        }

        public ConfidenceInterval summarize(
            final Collection<? extends Double> data )
        {
            return new StudentTConfidence().computeConfidenceInterval(
                data, this.getConfidence() );
        }

        /** 
         * Gets the confidence for created the interval. 
         *
         * @return The confidence for the created interval.
         */
        public double getConfidence()
        {
            return this.confidence;
        }

        /** 
         * Sets the confidence for created the interval. 
         *
         * @param  confidence The confidence for the created interval.
         */
        public void setConfidence(
            final double confidence )
        {
            ProbabilityUtil.assertIsProbability( confidence );
            this.confidence = confidence;
        }

    }

}
