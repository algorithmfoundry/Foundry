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
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The Nemenyi test is the rank-based analogue of the Tukey multiple-comparison
 * test.  It's primary use is in determining which treatments are significant
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
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<? extends Number>>
{

    /** 
     * Creates a new instance of NemenyiConfidence 
     */
    public NemenyiConfidence()
    {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public NemenyiConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Number> data1,
        Collection<? extends Number> data2)
    {
        return evaluateNullHypothesis( Arrays.asList( data1, data2 ) );
    }

    /**
     * Computes the Confidence test results for the given experiment tabluea
     * @param data
     * Collection of treatments, where each treatment must have the same number
     * of subjects in each treatment
     * @return
     * Confidence statistic describing the confidence test results
     */
    public static NemenyiConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Collection<? extends Number>> data )
    {

        // There are "K" treatments
        int K = data.size();

        // There are "N" subjects for each treatment
        int N = CollectionUtil.getFirst(data).size();

        return new NemenyiConfidence.Statistic(K, N,
            FriedmanConfidence.computeTreatmentRankMeans(data) );

    }

    /**
     * The test statistic from Nemenyi's test.
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
         * Mean rank for each treatment
         */
        private ArrayList<Double> treatmentRankMeans;

        /**
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         */
        private Matrix Z;

        /**
         * Null hypothesis probability that treatment "i" is statistically
         * different than treatment "j"
         */
        private Matrix P;

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
            int treatmentCount,
            int subjectCount,
            ArrayList<Double> treatmentRankMeans )
        {
            this( treatmentCount, subjectCount, treatmentRankMeans,
                computeTestStatistics(treatmentCount, subjectCount,treatmentRankMeans) );
        }

        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         * @param Z
         * Test statistic for the (i,j) treatment comparison
         */
        public Statistic(
            int treatmentCount,
            int subjectCount,
            ArrayList<Double> treatmentRankMeans,
            Matrix Z )
        {
            this( treatmentCount, subjectCount, treatmentRankMeans, Z,
                computeNullHypothesisProbability(treatmentCount, subjectCount, Z) );
        }


        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         * @param Z
         * Test statistic for the (i,j) treatment comparison
         * @param P
         * Null-hypothesis probability for the (i,j) treatment comparison
         */
        public Statistic(
            int treatmentCount,
            int subjectCount,
            ArrayList<Double> treatmentRankMeans,
            Matrix Z,
            Matrix P)
        {
            super( computeMinimumHypothesisProbability(P) );
            this.treatmentCount = treatmentCount;
            this.subjectCount = subjectCount;
            this.treatmentRankMeans = treatmentRankMeans;
            this.Z = Z;
            this.P = P;
        }

        /**
         * Computes the test statistic for the (i,j) treatment comparison
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param treatmentRankMeans
         * Mean rank for each treatment
         * @return
         * test statistic for the (i,j) treatment comparison
         */
        protected static Matrix computeTestStatistics(
            int treatmentCount,
            int subjectCount,
            ArrayList<Double> treatmentRankMeans )
        {
            int K = treatmentRankMeans.size();
            int N = subjectCount;
            Matrix Z = MatrixFactory.getDefault().createMatrix(K, K);
            for( int i = 0; i < K; i++ )
            {
                for( int j = i+1; j < K; j++ )
                {
                    // The test statistic is symmetric
                    double ri = treatmentRankMeans.get(i);
                    double rj = treatmentRankMeans.get(j);
                    double zij = Math.abs(ri-rj) / Math.sqrt( K*(K+1.0) / (6.0*N) );
                    Z.setElement(i, j, zij);
                    Z.setElement(j, i, zij);
                }
            }
            return Z;
        }

        /**
         * Computes null-hypothesis probability for the (i,j) treatment comparison
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in the experiment
         * @param Z
         * Test statistic for the (i,j) treatment comparison
         * @return
         * Null-hypothesis probability for the (i,j) treatment comparison
         */
        protected static Matrix computeNullHypothesisProbability(
            int treatmentCount,
            int subjectCount,
            Matrix Z )
        {
            final int K = treatmentCount;
            final int N = subjectCount;

            Matrix P = MatrixFactory.getDefault().createMatrix(K, K);
            StudentizedRangeDistribution.CDF cdf =
                new StudentizedRangeDistribution.CDF( K, N );
            for( int i = 0; i < K; i++ )
            {
                // A classifier is equal to itself.
                P.setElement(i, i, 1.0);
                for( int j = i+1; j < K; j++ )
                {
                    // The difference is symmetric
                    double zij = Z.getElement(i,j);
                    double pij = 1.0-cdf.evaluate( zij*Math.sqrt(2) );
//                    double pij = 1.0-StudentizedRangeDistribution.inverse( zij*Math.sqrt(2), K, N );
                    P.setElement(i, j, pij);
                    P.setElement(j, i, pij);
                }
            }

            return P;

        }

        /**
         * Computes the minimum null-hypothesis probability
         * @param P
         * Null-hypothesis probability for the (i,j) treatment comparison
         * @return
         * Minimum null-hypothesis probability
         */
        public static double computeMinimumHypothesisProbability(
            Matrix P )
        {
            int K = P.getNumRows();
            double minP = 1.0;
            for( int i = 0; i < K; i++ )
            {
                for( int j = i+1; j < K; j++ )
                {
                    double pij = P.getElement(i, j);
                    if( minP > pij )
                    {
                        minP = pij;
                    }
                }
            }
            return minP;
        }

        @Override
        public NemenyiConfidence.Statistic clone()
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
         * Getter for treatmentRankMeans
         * @return 
         * Mean rank for each treatment
         */
        public ArrayList<Double> getTreatmentRankMeans()
        {
            return this.treatmentRankMeans;
        }

        /**
         * Getter for Z
         * @return 
         * Test statistic for the (i,j) treatment comparison
         */
        public Matrix getZ()
        {
            return this.Z;
        }

        /**
         * Getter for P
         * @return
         * Null-hypothesis probability for the (i,j) treatment comparison
         */
        public Matrix getP()
        {
            return this.P;
        }

    }

}
