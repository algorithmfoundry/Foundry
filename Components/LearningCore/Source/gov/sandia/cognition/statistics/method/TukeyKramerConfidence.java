/*
 * File:                TukeyKramerConfidence.java
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
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.statistics.distribution.StudentizedRangeDistribution;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Tukey-Kramer test is the multiple-comparison generalization of the unpaired
 * Student's t-test when conducting multiple comparisons.  The t-test and
 * Tukey's Range test are coincident when a single comparison is made.
 * Tukey's Range test is typically used as the post-hoc analysis technique
 * after detecting a difference using a 1-way ANOVA.  This class implements
 * Kramer's generalization to unequal subjects in different treatments.
 * @author Kevin R. Dixon
 * @since 3.1
 */
@ConfidenceTestAssumptions(
    name="Tukey-Kramer Range test",
    alsoKnownAs={
        "Tukey's Range test",
        "Tukey's Honestly Significant Difference test",
        "Tukey's HSD test"
    },
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
    dataPaired=false,
    dataSameSize=false,
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
public class TukeyKramerConfidence
    extends AbstractMultipleHypothesisComparison<Collection<? extends Number>, TukeyKramerConfidence.Statistic>
{

    /** 
     * Creates a new instance of TukeyKramerConfidence
     */
    public TukeyKramerConfidence()
    {
        super();
    }

    @Override
    public TukeyKramerConfidence clone()
    {
        return (TukeyKramerConfidence) super.clone();
    }

    @Override
    public TukeyKramerConfidence.Statistic evaluateNullHypotheses(
        Collection<? extends Collection<? extends Number>> data,
        double uncompensatedAlpha)
    {
        // There are "K" treatments
        final int K = data.size();

        // Each treatment can have a different number of subjects
        ArrayList<Integer> subjectCounts = new ArrayList<Integer>( K );

        ArrayList<Double> treatmentMeans = new ArrayList<Double>( K );
        double treatmentVariancesSum = 0.0;
        
        // There are "N" total subjects
        int N = 0;
        for( Collection<? extends Number> treatment : data )
        {
            final int Ni = treatment.size();
            N += Ni;
            subjectCounts.add( Ni );
            Pair<Double,Double> meanAndVariance = UnivariateStatisticsUtil.computeMeanAndVariance(treatment);
            treatmentMeans.add( meanAndVariance.getFirst() );
            treatmentVariancesSum += meanAndVariance.getSecond() * (Ni-1);
        }

        final double meanSquaredResiduals = treatmentVariancesSum / (N-K);        
        return new TukeyKramerConfidence.Statistic(
            uncompensatedAlpha, subjectCounts, treatmentMeans, meanSquaredResiduals );       
    }
    
    /**
     * Statistic from Tukey-Kramer's multiple comparison test
     */
    public static class Statistic
        extends AbstractMultipleHypothesisComparison.Statistic
    {

        /**
         * Number of subjects in each treatment
         */
        protected ArrayList<Integer> subjectCounts;

        /**
         * Mean for each treatment
         */
        protected ArrayList<Double> treatmentMeans;

        /**
         * Mean-squared difference over all subjects
         */
        protected double meanSquaredResiduals;

        /**
         * Gets the standard errors in the experiment
         */
        protected Matrix standardErrors;
        
        /**
         * Creates a new instance of Statistic
         * @param uncompensatedAlpha
         * Uncompensated alpha (p-value threshold) for the multiple comparison
         * test
         * @param subjectCounts
         * Number of subjects in each treatment
         * @param treatmentMeans
         * Mean for each treatment
         * @param meanSquaredResiduals
         * Mean-squared difference over all subjects
         */
        public Statistic(
            final double uncompensatedAlpha,
            final ArrayList<Integer> subjectCounts,
            final ArrayList<Double> treatmentMeans,
            final double meanSquaredResiduals )
        {
            this.treatmentCount = treatmentMeans.size();
            this.uncompensatedAlpha = uncompensatedAlpha;
            this.subjectCounts = subjectCounts;
            this.treatmentMeans = treatmentMeans;
            this.meanSquaredResiduals = meanSquaredResiduals;
            this.testStatistics = this.computeTestStatistics(
                subjectCounts, treatmentMeans, meanSquaredResiduals);
            this.nullHypothesisProbabilities = this.computeNullHypothesisProbabilities(
                subjectCounts, this.testStatistics );
        }

        /**
         * Computes the test statistic for all treatments
         * @param subjectCounts
         * Number of subjects in each treatment
         * @param treatmentMeans
         * Mean for each treatment
         * @param meanSquaredResiduals
         * Mean-squared difference over all subjects
         * @return
         * Test statistics, where the (i,j) element compares treatment "i" to
         * treatment "j", the statistic is symmetric
         */
        public Matrix computeTestStatistics(
            final ArrayList<Integer> subjectCounts,
            final ArrayList<Double> treatmentMeans,
            final double meanSquaredResiduals )
        {
            int K = treatmentMeans.size();
            Matrix Z = MatrixFactory.getDefault().createMatrix(K,K);
            this.standardErrors = MatrixFactory.getDefault().createMatrix(K, K);
            for( int i = 0; i < K; i++ )
            {
                final double yi = treatmentMeans.get(i);
                final int ni = subjectCounts.get(i);
                for( int j = i+1; j < K; j++ )
                {
                    final int nj = subjectCounts.get(j);
                    final double yj = treatmentMeans.get(j);
                    double standardError =
                        Math.sqrt( meanSquaredResiduals * 0.5 * ((1.0/ni) + (1.0/nj)));
                    final double zij = Math.abs(yi-yj) / standardError;
                    Z.setElement(i, j, zij);
                    Z.setElement(j, i, zij);
                    this.standardErrors.setElement(i, j, standardError);
                    this.standardErrors.setElement(j, i, standardError);
                }
            }
            return Z;
        }

        /**
         * Computes null-hypothesis probability for the (i,j) treatment comparison
         * @param subjectCounts
         * Number of subjects in the experiment
         * @param Z
         * Test statistic for the (i,j) treatment comparison
         * @return
         * Null-hypothesis probability for the (i,j) treatment comparison
         */
        protected Matrix computeNullHypothesisProbabilities(
            final ArrayList<Integer> subjectCounts,
            final Matrix Z )
        {
            final int K = Z.getNumRows();
            final double N = UnivariateStatisticsUtil.computeSum(subjectCounts);

            Matrix P = MatrixFactory.getDefault().createMatrix(K, K);
            StudentizedRangeDistribution.CDF cdf =
                new StudentizedRangeDistribution.CDF( K, N-K );
            for( int i = 0; i < K; i++ )
            {
                // A classifier is equal to itself.
                P.setElement(i, i, 1.0);
                for( int j = i+1; j < K; j++ )
                {
                    // The difference is symmetric
                    double zij = Z.getElement(i,j);
                    double pij = 1.0-cdf.evaluate( zij );
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
            clone.treatmentMeans = ObjectUtil.cloneSmartElementsAsArrayList(
                this.getTreatmentMeans() );
            clone.subjectCounts = ObjectUtil.cloneSmartElementsAsArrayList(
                this.getSubjectCounts());
            return clone;
        }

        /**
         * Getter for subjectCounts
         * @return
         * Number of subjects in the experiment
         */
        public ArrayList<Integer> getSubjectCounts()
        {
            return this.subjectCounts;
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

        /**
         * Getter for meanSquaredResiduals
         * @return 
         * Mean-squared difference over all subjects
         */
        public double getMeanSquaredResiduals()
        {
            return this.meanSquaredResiduals;
        }        
        
        @Override
        public boolean acceptNullHypothesis(
            final int i,
            final int j)
        {
            return this.getNullHypothesisProbability(i, j) >= this.getUncompensatedAlpha();
        }

        /**
         * Getter for standardErrors
         * @return
         * Gets the standard errors in the experiment
         */
        public Matrix getStandardErrors()
        {
            return this.standardErrors;
        }

    }
    
}
