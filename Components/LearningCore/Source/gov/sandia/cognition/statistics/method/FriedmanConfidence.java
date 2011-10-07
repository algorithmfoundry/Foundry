/*
 * File:                FriedmanConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 3, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;
import gov.sandia.cognition.statistics.distribution.SnedecorFDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The Friedman test determines if the rankings associated with various
 * treatments are equal.  This is a nonparametric alternative to ANOVA.
 * @author Kevin R. Dixon
 * @since 3.1
 */
@ConfidenceTestAssumptions(
    name="Friedman's test",
    alsoKnownAs="",
    description={
        "Friedman's test determines if the rankings associated with various treatments are equal.",
        "This is a nonparametric rank-based alternative to ANOVA, a multiple comparison generalization similar to the difference between Student's t-test and Wilcoxon rank-signed test.",
        "Friedman's test tends to have as much power as ANOVA, but without ANOVA's parameteric assumptions"
    },
    assumptions={
        "All data came from same distribution, without considering treatment effects.",
        "Measurements are independent and equivalent within a treatment.",
        "All observations are independent."
    },
    nullHypothesis="The treatments have no effect on experimental observations.",
    dataPaired=true,
    dataSameSize=true,
    distribution=SnedecorFDistribution.class,
    reference={
        @PublicationReference(
            author="Janez Demsar",
            title="Statistical Comparisons of Classifiers over Multiple Data Sets",
            type=PublicationType.Journal,
            publication="Journal of Machine Learning Research",
            year=2006,
            url="http://www.jmlr.org/papers/volume7/demsar06a/demsar06a.pdf"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Friedman test",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Friedman_test",
            notes="Our test uses the tighter F-statistic rather than the original chi-square statistic"
        )
    }
)
public class FriedmanConfidence 
    extends AbstractCloneableSerializable
    implements BlockExperimentComparison<Number>
{

    /**
     * Default instance.
     */
    public static final FriedmanConfidence INSTANCE =
        new FriedmanConfidence();
    
    /** 
     * Creates a new instance of FriedmanConfidence 
     */
    public FriedmanConfidence()
    {
    }

    @Override
    @SuppressWarnings("unchecked")
    public FriedmanConfidence.Statistic evaluateNullHypothesis(
        final Collection<? extends Number> data1,
        final Collection<? extends Number> data2)
    {
        return evaluateNullHypothesis( Arrays.asList( data1, data2 ) );
    }

    @Override
    public FriedmanConfidence.Statistic evaluateNullHypothesis(
        final Collection<? extends Collection<? extends Number>> data )
    {

        // There are "K" treatments
        int K = data.size();

        // There are "N" subjects for each treatment
        int N = CollectionUtil.getFirst(data).size();

        return new FriedmanConfidence.Statistic( K, N, computeTreatmentRankMeans(data) );

    }

    /**
     * Computes the mean rank of the treatments
     * @param data
     * Collection of treatments, where each treatment must have the same number
     * of subjects in each treatment
     * @return
     * An mean rank for each of the treatments
     */
    public static ArrayList<Double> computeTreatmentRankMeans(
        final Collection<? extends Collection<? extends Number>> data )
    {
        // There are "K" treatments
        int K = data.size();

        // There are "N" subjects for each treatment
        int N = CollectionUtil.getFirst(data).size();

        // Thus, there are "N*K" observations in the experiment

        // Go through and put everything into an ArrayList, but transposed
        ArrayList<ArrayList<Double>> dataTranspose =
            new ArrayList<ArrayList<Double>>( N );
        for( int n = 0; n < N; n++ )
        {
            // This is for each subject
            dataTranspose.add( new ArrayList<Double>( K ) );
        }
        ArrayList<ArrayList<Double>> allRanks =
            new ArrayList<ArrayList<Double>>( K );
        for( Collection<? extends Number> treatment : data )
        {
            if( treatment.size() != N )
            {
                throw new IllegalArgumentException(
                    "All treatments must be the same size!" );
            }
            int n = 0;
            for( Number observation : treatment )
            {
                dataTranspose.get(n).add( observation.doubleValue() );
                n++;
            }
            allRanks.add( new ArrayList<Double>( N ) );
        }

        // Now, go through each subject and compute the ranks for each treatment
        int n = 0;
        for( Collection<Double> subjects : dataTranspose )
        {
            // For the nth subject, how did the treatments do?
            double[] treatmentRanks =
                WilcoxonSignedRankConfidence.ranks(subjects);
            for( int k = 0; k < K; k++ )
            {
                // The allRanks transposes things back so that we can easily
                // compute the mean and variance for each treatment
                allRanks.get(k).add( treatmentRanks[k] );
            }
            n++;
        }

        ArrayList<Double> treatmentRankMeans = new ArrayList<Double>( K );
        for( ArrayList<Double> treatmentRanks : allRanks )
        {
            double treatmentRankMean =
                UnivariateStatisticsUtil.computeMean(treatmentRanks);
            treatmentRankMeans.add( treatmentRankMean );
        }

        return treatmentRankMeans;
    }


    /**
     * Confidence statistic associated with the Friedman test using the tighter
     * F-statistic.
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * Number of treatments in the experiment
         */
        private int treatmentCount;

        /**
         * Number of subjects in the experiment
         */
        private int subjectCount;

        /**
         * Value of the chi-square error for the treatment ranks
         */
        private double chiSquare;

        /**
         * Degrees of freedom of the chi-square
         */
        private double degreesOfFreedom;

        /**
         * Null-hypothesis using the chi-square statistic
         */
        private double chiSquareNullHypothesisProbability;

        /**
         * F-statistic for the corrected chi-square using Snedecor's F distribution
         */
        private double F;

        /**
         * Mean rank for each treatment
         */
        private ArrayList<Double> treatmentRankMeans;

        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         */
        public Statistic(
            final int treatmentCount,
            final int subjectCount,
            final ArrayList<Double> treatmentRankMeans )
        {
            this( treatmentCount, subjectCount, treatmentRankMeans,
                computeChiSquare(treatmentCount, subjectCount,treatmentRankMeans) );
        }

        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         * @param chiSquare
         * Value of the chi-square error for the treatment ranks
         */
        protected Statistic(
            final int treatmentCount,
            final int subjectCount,
            final ArrayList<Double> treatmentRankMeans,
            final double chiSquare )
        {
            this( treatmentCount, subjectCount, treatmentRankMeans, chiSquare,
                ((subjectCount-1) * chiSquare) / (subjectCount*(treatmentCount-1) - chiSquare));
        }

        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         * @param chiSquare
         * Value of the chi-square error for the treatment ranks
         * @param F
         * F-statistic for the corrected chi-square using Snedecor's F distribution
         */
        protected Statistic(
            final int treatmentCount,
            final int subjectCount,
            final ArrayList<Double> treatmentRankMeans,
            final double chiSquare,
            final double F )
        {
            super(1.0 - SnedecorFDistribution.CDF.evaluate(
                F, treatmentCount-1.0, (treatmentCount-1.0)*(subjectCount-1.0) ));
            this.treatmentCount = treatmentCount;
            this.subjectCount = subjectCount;
            this.treatmentRankMeans = treatmentRankMeans;
            this.chiSquare = chiSquare;
            this.degreesOfFreedom = treatmentCount-1.0;
            this.chiSquareNullHypothesisProbability =
                1.0 - ChiSquareDistribution.CDF.evaluate(chiSquare, treatmentCount-1.0 );
            this.F = F;
        }


        /**
         * Computes the chi-square error for the rank means
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         * @return
         * Value of the chi-square error for the treatment ranks
         */
        protected static double computeChiSquare(
            final int treatmentCount,
            final int subjectCount,
            final ArrayList<Double> treatmentRankMeans )
        {
            final double rankMean = UnivariateStatisticsUtil.computeMean(treatmentRankMeans);
            final double treatmentSumSquared = UnivariateStatisticsUtil.computeSumSquaredDifference(treatmentRankMeans, 0.0);
            final double delta = treatmentSumSquared - treatmentCount*rankMean*rankMean;
            final double chiSquare = 12.0*subjectCount/(treatmentCount*(treatmentCount+1.0)) * delta;
            return chiSquare;
        }

        @Override
        public FriedmanConfidence.Statistic clone()
        {
            Statistic clone = (Statistic) super.clone();
            clone.treatmentRankMeans = ObjectUtil.cloneSmartElementsAsArrayList(
                this.getTreatmentRankMeans() );
            return clone;
        }

        /**
         * Getter for treatmentCount
         * @return
         * Number of treatments in the experiment
         */
        public int getTreatmentCount()
        {
            return this.treatmentCount;
        }

        /**
         * Getter for subjectCount
         * @return 
         * Number of subjects in the experiment
         */
        public int getSubjectCount()
        {
            return this.subjectCount;
        }

        /**
         * Getter for chiSquare
         * @return
         * Value of the chi-square error for the treatment ranks
         */
        public double getChiSquare()
        {
            return this.chiSquare;
        }

        /**
         * Getter for treatmentRankMeans
         * @return
         * Mean rank for each treatment
         */
        public ArrayList<Double> getTreatmentRankMeans()
        {
            return this.treatmentRankMeans;
        }

        /**
         * Getter for degreesOfFreedom
         * @return
         * Degrees of freedom of the chi-square
         */
        public double getDegreesOfFreedom()
        {
            return this.degreesOfFreedom;
        }

        /**
         * Getter for chiSquareNullHypothesisProbability
         * @return 
         * Null-hypothesis using the chi-square statistic
         */
        public double getChiSquareNullHypothesisProbability()
        {
            return this.chiSquareNullHypothesisProbability;
        }

        /**
         * Getter for F.
         * @return 
         * F-statistic for the corrected chi-square using Snedecor's F distribution
         */
        public double getF()
        {
            return this.F;
        }

        @Override
        public double getTestStatistic()
        {
            return this.getF();
        }

    }

}
