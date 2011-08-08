/*
 * File:                MannWhitneyUConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Performs a Mann-Whitney U-test on the given data (usually simply called a
 * "U-test", sometimes called a Wilcoxon-Mann-Whitney U-test, or
 * Wilcoxon rank-sum test).
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@ConfidenceTestAssumptions(
    name="Mann-Whitney U-test",
    alsoKnownAs={
        "Mann-Whitney-Wolcoxon",
        "Wilcoxon rank-sum test",
        "Wilcoxon-Mann-Whitney test",
        "U-test"
    },
    description="A nonparameteric test to determine is two groups of data were drawn from the same underlying distribution.",
    assumptions={
        "The groups were sampled independently.",
        "The data are orginal and we can determine which of two samples is greater than the other.",
        "Although the two populations don't have to follow any particular distribution, the two distributions must have a similar shape."
    },
    nullHypothesis="The data were drawn from the same distribution.",
    dataPaired=false,
    dataSameSize=false,
    distribution=UnivariateGaussian.CDF.class,
    reference=@PublicationReference(
        author="Wikipedia",
        title="Mann-Whitney U",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Mann-Whitney_U"
    )
)
public class MannWhitneyUConfidence
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<? extends Number>>
{

    /** Creates a new instance of MannWhitneyUConfidence */
    public MannWhitneyUConfidence()
    {
    }

    /**
     * Performs a U-test on the score-class pairs.  The first element in the
     * pair is a score, the second is a flag to determine which group the score
     * belongs to.  For example {<true,1.0>, <false,0.9> means that data1=1.0
     * and data2=0.9 and so forth.  This is useful for computing that 
     * classified data partitions data better than chance.
     * @param scoreClassPairs 
     * Pairs of scores with the corresponding class "label" for the score
     * @return 
     * Statistics from the Mann-Whitney U-test
     */
    public MannWhitneyUConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends InputOutputPair<? extends Number, Boolean>> scoreClassPairs )
    {

        DefaultPair<LinkedList<Number>, LinkedList<Number>> pair =
            DatasetUtil.splitDatasets( scoreClassPairs );
        LinkedList<Number> data1 = pair.getFirst();
        LinkedList<Number> data2 = pair.getSecond();
        return this.evaluateNullHypothesis( data1, data2 );

    }

    @Override
    public MannWhitneyUConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Number> data1,
        Collection<? extends Number> data2 )
    {

        int N1 = data1.size();
        int N2 = data2.size();

        ArrayList<Number> allData = new ArrayList<Number>( N1 + N2 );
        allData.addAll( data1 );
        allData.addAll( data2 );

        double ranks[] = WilcoxonSignedRankConfidence.ranks( allData );

        double sum1 = 0.0;
        for (int i = 0; i < N1; i++)
        {
            sum1 += ranks[i];
        }
        double sum2 = 0.0;
        for (int i = N1; i < (N1 + N2); i++)
        {
            sum2 += ranks[i];
        }
        return new MannWhitneyUConfidence.Statistic( sum1, N1, sum2, N2 );

    }

    /**
     * Statistics from the Mann-Whitney U-test
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * U statistic, minimum rank sum above "chance"
         */
        private double U;

        /**
         * z-value for a UnivariateGaussian CDF, number of standard deviations
         * away from chance
         */
        private double z;

        /**
         * Number of samples in data1
         */
        private int N1;

        /**
         * Number of samples in data2
         */
        private int N2;

        /**
         * Creates a new instance of Statistic
         * @param sum1 
         * Rank sum for data1
         * @param N1 
         * Number of samples in data1
         * @param sum2 
         * Rank sum for data2
         * @param N2 
         * Number of samples in data2
         */
        public Statistic(
            double sum1,
            int N1,
            double sum2,
            int N2 )
        {
            this( computeU( sum1, N1, sum2, N2 ), N1, N2 );
        }

        /**
         * Creates a new instance of Statistic
         * @param U 
         * U statistic, minimum rank sum above "chance"
         * @param N1 
         * Number of samples in data1
         * @param N2 
         * Number of samples in data2
         */
        private Statistic(
            double U,
            int N1,
            int N2 )
        {
            this( U, N1, N2, computeZ( U, N1, N2 ) );
        }

        /**
         * Creates a new instance of Statistic
         * @param U 
         * U statistic, minimum rank sum above "chance"
         * @param N1 
         * Number of samples in data1
         * @param N2 
         * Number of samples in data2
         * @param z 
         * z-value for a UnivariateGaussian CDF, number of standard deviations
         * away from chance
         */
        private Statistic(
            double U,
            int N1,
            int N2,
            double z )
        {
            super( computeNullHypothesisProbability( z ) );
            this.setU( U );
            this.setN1( N1 );
            this.setN2( N2 );
            this.setZ( z );
        }

        /**
         * Copy Constructor
         * @param other Statistic to copy
         */
        public Statistic(
            Statistic other )
        {
            this( other.getU(), other.getN1(), other.getN2() );
        }
         
        /**
         * Computes the U-statistic, the minimum rank sum above "chance"
         * @param sum1 
         * Rank-sum value for data1
         * @param N1 
         * Number of samples in data1
         * @param sum2 
         * Rank-sum value for data2
         * @param N2 
         * Number of samples in data2
         * @return 
         * U statistic, minimum rank sum above "chance"
         */
        public static double computeU(
            double sum1,
            int N1,
            double sum2,
            int N2 )
        {
            double U1 = sum1 - N1 * (N1 + 1) / 2.0;
            double U2 = sum2 - N2 * (N2 + 1) / 2.0;
            return Math.min( U1, U2 );
        }

        /**
         * Computes the z-value, used in the UnivariateGaussian CDF
         * @param U 
         * U statistic, minimum rank sum above "chance"
         * @param N1 
         * Number of samples in data1
         * @param N2 
         * Number of samples in data2
         * @return 
         * z-value for a UnivariateGaussian CDF, number of standard deviations
         * away from chance
         */
        public static double computeZ(
            double U,
            int N1,
            int N2 )
        {
            double m = N1 * N2 / 2.0;
            double stddev = Math.sqrt( (double) N1 * N2 * (N1 + N2 + 1) / 12.0 );
            double z;
            if( stddev > 0.0 )
            {
                z = (U - m) / stddev;
            }
            else
            {
                z = UnivariateGaussian.BIG_Z;
            }
            return z;
        }

        /**
         * Computes the p-value for the test, given the z-value
         * @param z 
         * z-value for a UnivariateGaussian CDF, number of standard deviations
         * away from chance
         * @return 
         * Chance that the null hypothesis is correct
         */
        public static double computeNullHypothesisProbability(
            double z )
        {
            return 2.0 * UnivariateGaussian.CDF.evaluate(
                -Math.abs( z ), 0, 1 );
        }

        /**
         * Getter for U
         * @return 
         * U statistic, minimum rank sum above "chance"
         */
        public double getU()
        {
            return this.U;
        }

        /**
         * Setter for U
         * @param U 
         * U statistic, minimum rank sum above "chance"
         */
        protected void setU(
            double U )
        {
            this.U = U;
        }

        /**
         * Getter for z
         * @return 
         * z-value for a UnivariateGaussian CDF, number of standard deviations
         * away from chance
         */
        public double getZ()
        {
            return this.z;
        }

        /**
         * Setter for z
         * @param z 
         * z-value for a UnivariateGaussian CDF, number of standard deviations
         * away from chance
         */
        protected void setZ(
            double z )
        {
            this.z = z;
        }

        /**
         * Getter for N1
         * @return 
         * Number of samples in data1
         */
        public int getN1()
        {
            return this.N1;
        }

        /**
         * Setter for N1
         * @param N1 
         * Number of samples in data1
         */
        protected void setN1(
            int N1 )
        {
            this.N1 = N1;
        }

        /**
         * Getter for N2
         * @return 
         * Number of samples in data2
         */
        public int getN2()
        {
            return this.N2;
        }

        /**
         * Setter for N2
         * @param N2 
         * Number of samples in data1
         */
        protected void setN2(
            int N2 )
        {
            this.N2 = N2;
        }

        @Override
        public double getTestStatistic()
        {
            return this.getZ();
        }

    }

}
