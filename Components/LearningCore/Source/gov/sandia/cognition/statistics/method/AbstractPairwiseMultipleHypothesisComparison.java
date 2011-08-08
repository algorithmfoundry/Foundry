/*
 * File:                AbstractPairwiseMultipleHypothesisComparison.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 1, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A multiple-hypothesis comparison algorithm based on making multiple
 * pair-wise null-hypothesis comparisons.
 * @param <StatisticType>
 * Type of statistic returned by the test
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
public abstract class AbstractPairwiseMultipleHypothesisComparison<StatisticType extends AbstractPairwiseMultipleHypothesisComparison.Statistic>
    extends AbstractMultipleHypothesisComparison<Collection<? extends Number>, StatisticType>
{

    /**
     * Default pair-wise confidence test: Student's Paired t-test.
     */
    public static final NullHypothesisEvaluator<Collection<? extends Number>> DEFAULT_PAIRWISE_TEST =
        StudentTConfidence.INSTANCE;
//        WilcoxonSignedRankConfidence.INSTANCE;

    /**
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    protected NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest;

    /**
     * Creates a new instance of BonferroniCorrection
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public AbstractPairwiseMultipleHypothesisComparison(
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest)
    {
        this.setPairwiseTest( pairwiseTest );
    }

    @Override
    public AbstractPairwiseMultipleHypothesisComparison<StatisticType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractPairwiseMultipleHypothesisComparison<StatisticType> clone =
            (AbstractPairwiseMultipleHypothesisComparison<StatisticType>) super.clone();
        clone.setPairwiseTest( ObjectUtil.cloneSafe( this.getPairwiseTest() ) );
        return clone;
    }

    /**
     * Getter for pairwiseTest
     * @return
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public NullHypothesisEvaluator<Collection<? extends Number>> getPairwiseTest()
    {
        return this.pairwiseTest;
    }

    /**
     * Setter for pairwiseTest
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public void setPairwiseTest(
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest)
    {
        this.pairwiseTest = pairwiseTest;
    }

    /**
     * Result from a pairwise multiple-comparison statistic.
     */
    public static abstract class Statistic
        extends AbstractMultipleHypothesisComparison.Statistic
    {

        /**
         * Results from the pair-wise confidence tests.
         */
        protected ArrayList<ArrayList<ConfidenceStatistic>> pairwiseTestStatistics;

        /**
         * Creates a new instance of StudentizedMultipleComparisonStatistic
         * @param data
         * Data from each treatment to consider
         * @param uncompensatedAlpha
         * Uncompensated alpha (p-value threshold) for the multiple comparison
         * test
         * @param pairwiseTest
         * Confidence test used for pair-wise null-hypothesis tests.
         */
        public Statistic(
            final Collection<? extends Collection<? extends Number>> data,
            final double uncompensatedAlpha,
            final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest )
        {
            this.treatmentCount = data.size();
            this.uncompensatedAlpha = uncompensatedAlpha;
            this.computePairwiseTestResults(data, pairwiseTest);
        }

        @Override
        public AbstractPairwiseMultipleHypothesisComparison.Statistic clone()
        {
            AbstractPairwiseMultipleHypothesisComparison.Statistic clone =
                (AbstractPairwiseMultipleHypothesisComparison.Statistic) super.clone();
            clone.pairwiseTestStatistics = ObjectUtil.cloneSmartElementsAsArrayList(
                this.getPairwiseTestStatistics() );
            return clone;
        }

        /**
         * Computes the pair-wise confidence test results
         * @param data
         * Data from each treatment to consider
         * @param pairwiseTest
         * Confidence test used for pair-wise null-hypothesis tests.
         */
        protected void computePairwiseTestResults(
            final Collection<? extends Collection<? extends Number>> data,
            final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest )
        {

            ArrayList<? extends Collection<? extends Number>> treatments =
                CollectionUtil.asArrayList(data);

            final int K = treatments.size();
            Matrix Z = MatrixFactory.getDefault().createMatrix(K, K);
            Matrix P = MatrixFactory.getDefault().createMatrix(K, K);
            ArrayList<ArrayList<ConfidenceStatistic>> stats =
                new ArrayList<ArrayList<ConfidenceStatistic>>( K );
            for( int i = 0; i < K; i++ )
            {
                ArrayList<ConfidenceStatistic> comp =
                    new ArrayList<ConfidenceStatistic>( K );
                for( int j = 0; j < K; j++ )
                {
                    comp.add( null );
                }
                stats.add( comp );
            }

            for( int i = 0; i < K; i++ )
            {
                // Comparisons to ourselves are perfect
                Z.setElement(i, i, 0.0);
                P.setElement(i, i, 1.0);
                Collection<? extends Number> datai = treatments.get(i);
                for( int j = i+1; j < K; j++ )
                {
                    Collection<? extends Number> dataj = treatments.get(j);
                    ConfidenceStatistic comparison =
                        pairwiseTest.evaluateNullHypothesis(datai, dataj);
                    final double pij = comparison.getNullHypothesisProbability();
                    final double zij = comparison.getTestStatistic();
                    Z.setElement(i, j, zij);
                    Z.setElement(j, i, zij);
                    P.setElement(i, j, pij);
                    P.setElement(j, i, pij);
                    stats.get(i).set(j, comparison );
                    stats.get(j).set(i, comparison );
                }
            }

            this.testStatistics = Z;
            this.nullHypothesisProbabilities = P;
            this.pairwiseTestStatistics = stats;

        }

        /**
         * Getter for pairwiseTestStatistics
         * @return
         * Results from the pair-wise confidence tests.
         */
        public ArrayList<ArrayList<ConfidenceStatistic>> getPairwiseTestStatistics()
        {
            return this.pairwiseTestStatistics;
        }

        @Override
        public boolean acceptNullHypothesis(
            int i,
            int j)
        {
            return this.getNullHypothesisProbability(i, j) >= this.getAdjustedAlpha(i, j);
        }

        /**
         * Gets the adjusted alpha (p-value threshold) for the given comparison
         * @param i
         * First treatment to compare
         * @param j
         * Second treatment to compare
         * @return
         * Adjusted alpha (p-value threshold) for the given comparison
         */
        public abstract double getAdjustedAlpha(
            int i,
            int j );

    }

}
