/*
 * File:                NemenyiConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 9, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.distribution.StudentizedRangeDistribution;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The Nemenyi test is the rank-based analogue of the Tukey multiple-comparison
 * test.  Its primary use is in determining which treatments are significant
 * after a Friedman's test indicates that one of the treatments is statistically
 * different.
 * @author Kevin R. Dixon
 * @since 3.1
 */
@ConfidenceTestAssumptions(
    name="Nemenyi's test",
    description={
        "Nemenyi's test determines which treatment is statistically different from a multiple comparison.",
        "This is a nonparametric rank-based alternative to Tukey's multiple comparison test."
    },
    assumptions={
        "All data came from same distribution, without considering treatment effects.",
        "Measurements are independent and equivalent within a treatment.",
        "All observations are independent."
    },
    nullHypothesis="Each treatment has no effect on the rank-based outcome of the subjects",
    dataPaired=true,
    dataSameSize=true,
    distribution=StudentizedRangeDistribution.class,
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
            title="Multiple comparisons, Post-hoc testing of ANOVAs",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Multiple_comparisons#Post-hoc_testing_of_ANOVAs"
        )
    }
)
public class NemenyiConfidence 
    extends AbstractMultipleHypothesisComparison<Collection<? extends Number>, NemenyiConfidence.Statistic>
{

    /**
     * Default instance.
     */
    public static final NemenyiConfidence INSTANCE = new NemenyiConfidence();

    /** 
     * Creates a new instance of NemenyiConfidence 
     */
    public NemenyiConfidence()
    {
        super();
    }

    @Override
    public NemenyiConfidence.Statistic evaluateNullHypotheses(
        final Collection<? extends Collection<? extends Number>> data,
        final double uncompensatedAlpha)
    {
        // There are "K" treatments
        final int K = data.size();

        // There are "N" subjects for each treatment
        int N = CollectionUtil.getFirst(data).size();

        ArrayList<Double> treatmentRankMeans =
            FriedmanConfidence.computeTreatmentRankMeans(data);
        final double standardError = Math.sqrt( K*(K+1.0) / (6.0*N) );
        return new NemenyiConfidence.Statistic(
            uncompensatedAlpha, N, treatmentRankMeans, standardError );
    }

    /**
     * Statistic from Nemenyi's multiple comparison test
     */
    public static class Statistic
        extends AbstractMultipleHypothesisComparison.Statistic
    {

        /**
         * Number of subjects in each treatment
         */
        private int subjectCount;

        /**
         * Mean for each treatment
         */
        private ArrayList<Double> treatmentRankMeans;

        /**
         * Standard error of the entire experiment
         */
        private double standardError;

        /**
         * Creates a new instance of StudentizedMultipleComparisonStatistic
         * @param uncompensatedAlpha
         * Uncompensated alpha (p-value threshold) for the multiple comparison
         * test
         * @param subjectCount
         * Number of subjects in each treatment
         * @param treatmentRankMeans
         * Mean for each treatment
         * @param standardError
         * Standard error of the entire experiment
         */
        public Statistic(
            final double uncompensatedAlpha,
            final int subjectCount,
            final ArrayList<Double> treatmentRankMeans,
            final double standardError )
        {
            this.treatmentCount = treatmentRankMeans.size();
            this.uncompensatedAlpha = uncompensatedAlpha;
            this.subjectCount = subjectCount;
            this.treatmentRankMeans = treatmentRankMeans;
            this.standardError = standardError;
            this.testStatistics = this.computeTestStatistics(
                subjectCount, treatmentRankMeans, standardError);
            this.nullHypothesisProbabilities = this.computeNullHypothesisProbabilities(
                subjectCount, this.testStatistics );
        }

        /**
         * Computes the test statistic for all treatments
         * @param subjectCount
         * Number of subjects in each treatment
         * @param treatmentRankMeans
         * Mean for each treatment
         * @param standardError
         * Standard error of the entire experiment
         * @return
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         */
        public Matrix computeTestStatistics(
            final int subjectCount,
            final ArrayList<Double> treatmentRankMeans,
            final double standardError )
        {
            int K = treatmentRankMeans.size();
            Matrix Z = MatrixFactory.getDefault().createMatrix(K,K);
            for( int i = 0; i < K; i++ )
            {
                final double yi = treatmentRankMeans.get(i);
                for( int j = i+1; j < K; j++ )
                {
                    final double yj = treatmentRankMeans.get(j);
                    final double zij = Math.abs(yi-yj) / standardError;
                    Z.setElement(i, j, zij);
                    Z.setElement(j, i, zij);
                }
            }
            return Z;
        }

        /**
         * Computes null-hypothesis probability for the (i,j) treatment comparison
         * @param subjectCount
         * Number of subjects in the experiment
         * @param Z
         * Test statistic for the (i,j) treatment comparison
         * @return
         * Null-hypothesis probability for the (i,j) treatment comparison
         */
        protected Matrix computeNullHypothesisProbabilities(
            final int subjectCount,
            final Matrix Z )
        {
            final int K = Z.getNumRows();
            final int N = subjectCount;

            Matrix P = MatrixFactory.getDefault().createMatrix(K, K);
            StudentizedRangeDistribution.CDF cdf =
                new StudentizedRangeDistribution.CDF( K, K*N-1 );
            for( int i = 0; i < K; i++ )
            {
                // A classifier is equal to itself.
                P.setElement(i, i, 1.0);
                for( int j = i+1; j < K; j++ )
                {
                    // The difference is symmetric
                    double zij = Z.getElement(i,j);
                    double pij = 1.0-cdf.evaluate( zij*Math.sqrt(2) );
                    P.setElement(i, j, pij);
                    P.setElement(j, i, pij);
                }
            }

            return P;

        }

        @Override
        public Statistic clone()
        {
            Statistic clone = (Statistic) super.clone();
            clone.treatmentRankMeans = ObjectUtil.cloneSmartElementsAsArrayList(
                this.getTreatmentMeans() );
            return clone;
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
         * Getter for standardError
         * @return
         * Standard error of the entire experiment
         */
        public double getStandardError()
        {
            return this.standardError;
        }

        /**
         * Getter for treatmentRankMeans
         * @return
         * Mean for each treatment
         */
        public ArrayList<Double> getTreatmentMeans()
        {
            return this.treatmentRankMeans;
        }

        @Override
        public boolean acceptNullHypothesis(
            final int i,
            final int j)
        {
            return this.getNullHypothesisProbability(i, j) >= this.getUncompensatedAlpha();
        }


    }

}
