/*
 * File:                HolmCorrection.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.util.ArrayIndexSorter;
import java.util.Collection;

/**
 * The Holm correction is a uniformly tighter bound than the Bonferroni/Sidak
 * correction by first sorting the pair-wide p-values and then adjusting the
 * p-values by the number of remaining hypotheses.  To reject the first p-value,
 * the smallest pair-wise p-value must be smaller than the Bonferroni corrected
 * value (alpha/N), then the next smallest p-value must be smaller than
 * (alpha/(N-1)) and so forth.  This is a uniformly looser bound than the
 * Shaffer static correction.  However, the computational complexity of the Holm
 * algorithm is quadratic in the number of treatments, meaning its linear
 * in the number of actual comparisons.
 * So, for treatments above 100 (4950 comparisons), the Holm correction may be
 * the most appropriate choice.  This implementation uses the slightly tighter
 * Sidak correction, as opposed to the standard Bonferroni correction.
 *
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Holm–Bonferroni method",
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Holm%E2%80%93Bonferroni_method",
    year=2011
)
public class HolmCorrection 
    extends AbstractPairwiseMultipleHypothesisComparison<HolmCorrection.Statistic>
{

    /**
     * Default constructor
     */
    public HolmCorrection()
    {
        this( DEFAULT_PAIRWISE_TEST );
    }

    /**
     * Creates a new instance of BonferroniCorrection
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public HolmCorrection(
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest)
    {
        super( pairwiseTest );
    }

    @Override
    public HolmCorrection clone()
    {
        return (HolmCorrection) super.clone();
    }

    @Override
    public HolmCorrection.Statistic evaluateNullHypotheses(
        final Collection<? extends Collection<? extends Number>> data,
        final double uncompensatedAlpha)
    {
        return new HolmCorrection.Statistic(
            data, uncompensatedAlpha, this.getPairwiseTest() );
    }

    /**
     * Test statistic from the Shaffer static multiple-comparison test
     */
    public static class Statistic
        extends AbstractPairwiseMultipleHypothesisComparison.Statistic
    {

        /**
         * Matrix of adjusted alphas (p-value thresholds) for each comparison
         */
        protected Matrix adjustedAlphas;

        /**
         * Creates a new instance of Statistic
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
            super( data, uncompensatedAlpha, pairwiseTest );
            final int numComparisons =
                this.treatmentCount * (this.treatmentCount-1) / 2;
            this.computePairwiseTestResults(data, pairwiseTest );
            double[] pvalues = new double[ numComparisons ];
            int index = 0;
            for( int i = 0; i < this.treatmentCount; i++ )
            {
                for( int j = i+1; j < this.treatmentCount; j++ )
                {
                    pvalues[index] =
                        this.nullHypothesisProbabilities.getElement(i, j);
                    index++;
                }
            }
            // Sort the p-values from smallest to largest
            int[] sortedIndices =
                ArrayIndexSorter.sortArrayAscending(pvalues);

            double[] adjustedAlpha = new double[ numComparisons ];
            int hypothesesRemaining = numComparisons;
            for( int i = 0; i < numComparisons; i++ )
            {
//                adjustedAlpha[sortedIndices[i]] = BonferroniCorrection.adjust(
//                    this.uncompensatedAlpha, maxPossibleTrueHypotheses );
                adjustedAlpha[sortedIndices[i]] = SidakCorrection.adjust(
                    this.uncompensatedAlpha, hypothesesRemaining );
                hypothesesRemaining--;
            }

            // Stuff the adjusted alphas back into the adjustedAlphas matrix
            Matrix alphaMatrix = MatrixFactory.getDefault().createMatrix(
                this.treatmentCount,this.treatmentCount);
            index = 0;
            for( int i = 0; i < this.treatmentCount; i++ )
            {
                for( int j = i+1; j < this.treatmentCount; j++ )
                {
                    alphaMatrix.setElement(i, j, adjustedAlpha[index]);
                    alphaMatrix.setElement(j, i, adjustedAlpha[index]);
                    index++;
                }
            }

            this.adjustedAlphas = alphaMatrix;

        }

        @Override
        public double getAdjustedAlpha(
            int i,
            int j)
        {
            return this.adjustedAlphas.getElement(i, j);
        }

    }

}
