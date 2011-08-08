/*
 * File:                ShafferStaticCorrection.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 18, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.util.ArrayIndexSorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * The Shaffer Static Correction uses logical relationships to tighten up the
 * Bonferroni/Sidak corrections when performing pairwise multiple hypothesis
 * comparisons.  This is uniformly tighter bound than the Bonferroni/Sidak
 * values and also uniformly tighter than the Holm correction.  The original
 * algorithm proposed by Shaffer appears to grow super exponentially as
 * O(2^(N^2)), where N is the number of treatments.  We have pre-computed
 * various quantities and used caching to minimize the amount of repeated
 * recursion and it appears that the runtime grows as O(N^4), where N is the
 * number of treatments. Since there are N(N-1)/2 comparisons, this quantity
 * is quadratic in the number of comparisons.  This means the computation is
 * reasonable for N=90 (4005 comparisons).  However, the algorithm seems
 * to slow down significantly above N=100 (4950 comparisons) or so.
 * This implementation uses the slightly tighter Sidak correction,
 * as opposed to the standard Bonferroni correction.
 * <BR><BR>
 * For example, if you have three hypothesis you are testing,
 * then there is no way that there can be 2 true hypotheses. (Because u1=u2 and
 * u2=u3 and therefore u1=u3.)  With this logic, Shaffer Static correction
 * can greatly reduce the false-negative rate while not impacting the
 * false-discovery rate.
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Juliet Popper Shaffer",
            title="Modified Sequentially Rejective Multiple Test Procedures",
            type=PublicationType.Journal,
            publication="Journal of the American Statistical Association",
            year=1986,
            url="http://www.jstor.org/stable/2289016"
        )
        ,
        @PublicationReference(
            author="Juliet Popper Shaffer",
            title="Multiple Hypothesis Testing",
            type=PublicationType.Journal,
            publication="Annual Review of Psychology",
            year=1995,
            url="http://www.dm.uba.ar/materias/optativas/aspectos_estadisticos_de_microarreglos/2010/1/teoricas/Shaffer%201995%20Multiple%20hypothesis%20testing.pdf"
        )
        ,
        @PublicationReference(
            author={
                "Salvador Garcia",
                "Francisco Herrera"
            },
            title="An Extension on \"Statistical Comparisons of Classiﬁers over Multiple Data Sets\" for all Pairwise Comparisons",
            type=PublicationType.Journal,
            publication="Journal of Machine Learning Research",
            year=2008,
            url="http://sci2s.ugr.es/publications/ficheros/2008-Garcia-JMLR.pdf"
        )
    }
)
public class ShafferStaticCorrection
    extends AbstractPairwiseMultipleHypothesisComparison<ShafferStaticCorrection.Statistic>
{

    /**
     * Default constructor
     */
    public ShafferStaticCorrection()
    {
        this( DEFAULT_PAIRWISE_TEST );
    }

    /**
     * Creates a new instance of BonferroniCorrection
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public ShafferStaticCorrection(
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest)
    {
        super( pairwiseTest );
    }

    @Override
    public ShafferStaticCorrection clone()
    {
        return (ShafferStaticCorrection) super.clone();
    }

    @Override
    public ShafferStaticCorrection.Statistic evaluateNullHypotheses(
        final Collection<? extends Collection<? extends Number>> data,
        final double uncompensatedAlpha)
    {
        return new ShafferStaticCorrection.Statistic(
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
            ArrayList<Integer> possibleTruths =
                possibleTruthsSorted( this.treatmentCount );
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
            int maxPossibleTruthIndex = possibleTruths.size()-1;
            int hypothesesRemaining = numComparisons;
            for( int i = 0; i < numComparisons; i++ )
            {
                int maxPossibleTrueHypotheses =
                    possibleTruths.get(maxPossibleTruthIndex);


//                adjustedAlpha[sortedIndices[i]] = BonferroniCorrection.adjust(
//                    this.uncompensatedAlpha, maxPossibleTrueHypotheses );
                adjustedAlpha[sortedIndices[i]] = SidakCorrection.adjust(
                    this.uncompensatedAlpha, maxPossibleTrueHypotheses );
                if( pvalues[sortedIndices[i]] >= adjustedAlpha[sortedIndices[i]] )
                {
                    // This is the critical value, there are no more remaining
                    // false hypotheses, so the remaining ones must be true
                    break;
                }
                else
                {
                    hypothesesRemaining--;
                    if( hypothesesRemaining < maxPossibleTrueHypotheses )
                    {
                        maxPossibleTruthIndex--;
                    }
                }

            }

            // Stuff the adjusted alphas back into the
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

        /**
         * Returns the sorted-ascending set of the possible set of true
         * hypothesis given the number of treatments being considered.
         * @param treatmentCount
         * Number of treatments to consider
         * @return
         * Set of possible true hypotheses given the number of treatments.
         */
        public static ArrayList<Integer> possibleTruthsSorted(
            int treatmentCount )
        {
            int[] jchoose2 = new int[ treatmentCount+1 ];
            for( int j = 2; j <= treatmentCount; j++ )
            {
                jchoose2[j] = (int) (Math.round(Math.exp(MathUtil.logBinomialCoefficient(j, 2))));
            }
            ArrayList<LinkedHashSet<Integer>> recusionCaches =
                new ArrayList<LinkedHashSet<Integer>>( treatmentCount );
            for( int j = 0; j < treatmentCount; j++ )
            {
                recusionCaches.add( null );
            }

            LinkedHashSet<Integer> set =
                possibleTruthsSet( treatmentCount, jchoose2, recusionCaches );
            ArrayList<Integer> sortedSet = CollectionUtil.asArrayList(set);
            Collections.sort(sortedSet);
            return sortedSet;
        }

        /**
         * Recursive algorithm to compute the possible set of true hypotheses
         * @param treatmentCount
         * Number of treatments to consider
         * @param jchoose2
         * Pre-computed cache of all "j choose 2" values to the max treatment
         * @param recursionCaches
         * Dynamic cache of recursion results for various treatmentCounts.
         * If entry j is null, then we have not recursed to a
         * treatmentCount of j yet.
         * @return
         * Unsorted set of all possible true hypothesis counts.
         */
        private static LinkedHashSet<Integer> possibleTruthsSet(
            final int treatmentCount,
            final int[] jchoose2,
            ArrayList<LinkedHashSet<Integer>> recursionCaches )
        {

            LinkedHashSet<Integer> set =
                new LinkedHashSet<Integer>( treatmentCount );
            if( treatmentCount <= 1 )
            {
                set.add( 0 );
            }
            else if( treatmentCount == 2 )
            {
                set.add( 1 );
            }
            else
            {
                for( int j = 1; j <= treatmentCount; j++ )
                {
                    // If we've already recursed using the give index (tcmj),
                    // then don't do it again... It leads to a super-polynomial
                    // explosion in the recursion.  (Shaffer's original This caching keeps it
                    // polynomial.
                    final int tcmj = treatmentCount-j;
                    LinkedHashSet<Integer> recursion =
                        recursionCaches.get( tcmj );
                    if( recursion == null )
                    {
                        recursion = possibleTruthsSet(
                            tcmj, jchoose2, recursionCaches );
                        recursionCaches.set( tcmj, recursion );
                    }
                    // Uncomment the line below to run Shaffer's original
                    // algorithm without caching... but it's superpolynomial,
                    // so I would suggest only doing it if you're insane.
                    //LinkedHashSet<Integer> recursion = possibleTruthsSet( treatmentCount - j, jchoose2, recursionCaches );

                    for( Integer value : recursion )
                    {
                        int choose;
                        if( j < 2 )
                        {
                            choose = 0;
                        }
                        else
                        {
                            choose = jchoose2[j];
                        }
                        set.add( value + choose );
                    }

                }
            }

            return set;
            
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
