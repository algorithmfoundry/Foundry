/*
 * File:                TukeyRangeConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 16, 2011, Sandia Corporation.
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
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.statistics.distribution.StudentizedRangeDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Tukey's Range test is the multiple-comparison generalization of the paired
 * Student's t-test when conducting multiple comparisons.  The t-test and
 * Tukey's Range test are coincident when a single comparison is made.
 * Tukey's Range test is typically used as the post-hoc analysis technique
 * after detecting a difference using a 1-way ANOVA.
 * @author Kevin R. Dixon
 * @since 3.1
 */
@ConfidenceTestAssumptions(
    name="Tukey's Range test",
    alsoKnownAs="Tukey's Honestly Significant Difference test",
    description={
        "Tukey's test determines which treatment is statistically different from a multiple comparison.",
        "Tukey's test is a generalization of the paired Student's t-test for multiple comparisons using a population-correction factor."
    },
    assumptions={
        "All data came from same distribution, without considering treatment effects.",
        "The observations have equal variance.",
        "Measurements are independent and equivalent within a treatment.",
        "All observations are independent."
    },
    nullHypothesis="Each treatment has no effect on the mean outcome of the subjects",
    dataPaired=true,
    dataSameSize=true,
    distribution=StudentizedRangeDistribution.class,
    reference={
        @PublicationReference(
            author="Wikipedia",
            title="Tukey's range test",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Tukey's_range_test"
        )
    }
)
public class TukeyRangeConfidence
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<Double>>
{

    /** 
     * Creates a new instance of TukeyRangeConfidence
     */
    public TukeyRangeConfidence()
    {
    }

    @Override
    public TukeyRangeConfidence clone()
    {
        return (TukeyRangeConfidence) super.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TukeyRangeConfidence.Statistic evaluateNullHypothesis(
        Collection<Double> data1,
        Collection<Double> data2)
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
    public static TukeyRangeConfidence.Statistic evaluateNullHypothesis(
        Collection<? extends Collection<? extends Number>> data )
    {

        // There are "K" treatments
        final int K = data.size();

        // There are "N" subjects for each treatment
        int N = CollectionUtil.getFirst(data).size();

        ArrayList<Double> treatmentMeans = new ArrayList<Double>( K );
        LinkedList<Number> allData = new LinkedList<Number>();
        for( Collection<? extends Number> treatment : data )
        {
            final int Ni = treatment.size();
            if( N != Ni )
            {
                throw new IllegalArgumentException(
                    "Subject count must be equal in all treatments" );
            }
            treatmentMeans.add( UnivariateStatisticsUtil.computeMean(treatment) );
            allData.addAll(treatment);
        }

        final Pair<Double,Double> result =
            UnivariateStatisticsUtil.computeMeanAndVariance(allData);
        final double standardError = Math.sqrt(result.getSecond()/N);
        return new TukeyRangeConfidence.Statistic(N, treatmentMeans, standardError);

    }

    /**
     * The test statistic from Tukey's range test.
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * Number of treatments in the experiment
         */
        private int treatmentCount;

        /**
         * Number of subjects in each treatment
         */
        private int subjectCount;

        /**
         * Mean for each treatment
         */
        private ArrayList<Double> treatmentMeans;

        /**
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         */
        private Matrix Q;

        /**
         * Null hypothesis probability that treatment "i" is statistically
         * different than treatment "j"
         */
        private Matrix P;

        /**
         * Standard error of the entire experiment
         */
        private double standardError;

        /**
         * Creates a new instance of Statistic
         * @param subjectCount
         * Number of subjects in each treatment
         * @param treatmentMeans
         * Mean for each treatment
         * @param standardError
         * Standard error of the entire experiment
         */
        public Statistic(
            int subjectCount,
            ArrayList<Double> treatmentMeans,
            double standardError )
        {
            this( treatmentMeans.size(), subjectCount, treatmentMeans,
                computeTestStatistic(subjectCount, treatmentMeans, standardError), standardError );
        }

        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in each treatment
         * @param treatmentMeans
         * Mean for each treatment
         * @param Q
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         * @param standardError
         * Standard error of the entire experiment
         */
        public Statistic(
            int treatmentCount,
            int subjectCount,
            ArrayList<Double> treatmentMeans,
            Matrix Q,
            double standardError)
        {
            this( treatmentCount, subjectCount, treatmentMeans, Q,
                NemenyiConfidence.Statistic.computeNullHypothesisProbability(
                    treatmentCount, subjectCount, Q),
                standardError);
        }

        /**
         * Creates a new instance of Statistic
         * @param treatmentCount
         * Number of treatments in the experiment
         * @param subjectCount
         * Number of subjects in each treatment
         * @param treatmentMeans
         * Mean for each treatment
         * @param Q
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         * @param P
         * Null hypothesis probability that treatment "i" is statistically
         * different than treatment "j"
         * @param standardError
         * Standard error of the entire experiment
         */
        public Statistic(
            int treatmentCount,
            int subjectCount,
            ArrayList<Double> treatmentMeans,
            Matrix Q,
            Matrix P,
            double standardError)
        {
            super( NemenyiConfidence.Statistic.computeMinimumHypothesisProbability(P) );
            this.treatmentCount = treatmentCount;
            this.subjectCount = subjectCount;
            this.treatmentMeans = treatmentMeans;
            this.Q = Q;
            this.P = P;
            this.standardError = standardError;
        }

        /**
         * Computes the test statistic for all treatments
         * @param subjectCount
         * Number of subjects in each treatment
         * @param treatmentMeans
         * Mean for each treatment
         * @param standardError
         * Standard error of the entire experiment
         * @return
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         */
        public static Matrix computeTestStatistic(
            int subjectCount,
            ArrayList<Double> treatmentMeans,
            double standardError )
        {
            int K = treatmentMeans.size();
            Matrix Q = MatrixFactory.getDefault().createMatrix(K,K);
            for( int i = 0; i < K; i++ )
            {
                final double yi = treatmentMeans.get(i);
                for( int j = i+1; j < K; j++ )
                {
                    final double yj = treatmentMeans.get(j);
                    final double qij = Math.abs(yi-yj) / standardError;
                    Q.setElement(i, j, qij);
                    Q.setElement(j, i, qij);
                }
            }
            return Q;
        }

        @Override
        public Statistic clone()
        {
            Statistic clone = (Statistic) super.clone();
            clone.P = ObjectUtil.cloneSafe( this.getP() );
            clone.Q = ObjectUtil.cloneSafe( this.getQ() );
            clone.treatmentMeans = ObjectUtil.cloneSmartElementsAsArrayList(
                this.getTreatmentMeans() );
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
         * Getter for P
         * @return
         * Null-hypothesis probability for the (i,j) treatment comparison
         */
        public Matrix getP()
        {
            return this.P;
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
         * Getter for Q
         * @return
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         */
        public Matrix getQ()
        {
            return this.Q;
        }

        /**
         * Getter for treatmentMeans
         * @return
         * Mean for each treatment
         */
        public ArrayList<Double> getTreatmentMeans()
        {
            return this.treatmentMeans;
        }

    }

}
