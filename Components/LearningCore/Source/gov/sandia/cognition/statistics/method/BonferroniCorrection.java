/*
 * File:                BonferroniCorrection.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 31, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import java.util.Collection;

/**
 * The Bonferroni correction takes a pair-wise null-hypothesis test and
 * generalizes it to multiple comparisons by adjusting the requisite p-value
 * to find significance as alpha / NumComparisons.  If there are "K" treatments
 * being compared, each treatment is compared to all others:
 * NumComparisons == K*(K-1)/2.  The Bonferroni correction is known to be
 * extremely conservative and tightly controls the false-discovery rate.
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@ConfidenceTestAssumptions(
    name="Bonferroni correction",
    alsoKnownAs="Bonferroni-Dunn test",
    description={
        "Bonferroni's correction is a conservative way to compensate for pair-wise null-hypothesis comparisons applied to multiple comparison to control false-discovery rate.",
        "The correction is known to be overly conservative, trading low false-discovery for high false-negative rates."
    },
    assumptions={
        "The assumptions of the underlying pair-wise test are met."
    },
    nullHypothesis="Each treatment has no effect on the measurements of the subjects",
    dataPaired=true,
    dataSameSize=true,
    distribution=AbstractClosedFormSmoothUnivariateDistribution.class,
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
            title="Bonferroni Correction",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Bonferroni_correction"
        )
    }
)
public class BonferroniCorrection
    extends AbstractPairwiseMultipleHypothesisComparison<AdjustedPValueStatistic>
{

    /**
     * Default constructor
     */
    public BonferroniCorrection()
    {
        this( DEFAULT_PAIRWISE_TEST );
    }

    /**
     * Creates a new instance of BonferroniCorrection 
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public BonferroniCorrection(
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest)
    {
        super( pairwiseTest );
    }

    @Override
    public BonferroniCorrection clone()
    {
        return (BonferroniCorrection) super.clone();
    }

    @Override
    public AdjustedPValueStatistic evaluateNullHypotheses(
        final Collection<? extends Collection<? extends Number>> data,
        final double uncompensatedAlpha)
    {
        // The Bonferroni correction is alpha/N, where "N" is the number
        // of comparisons being made.  In our case, we're comparing each
        // (K) treatment to all others (K-1): Thus, N == K*(K-1)/2
        final int K = data.size();
        final int N = K * (K-1)/2;
        final double adjustedAlpha = adjust(uncompensatedAlpha, N);
        return new AdjustedPValueStatistic(
            data, uncompensatedAlpha, adjustedAlpha, this.getPairwiseTest());
    }

    /**
     * Computes the Bonferroni multiple-comparison correction
     * @param uncompensatedAlpha
     * Uncompensated alpha (p-value threshold) for the multiple comparison
     * test, must be [0,1]
     * @param numComparisons
     * Number of comparisons to make.
     * @return
     * Bonferroni correction
     */
    public static double adjust(
        double uncompensatedAlpha,
        int numComparisons )
    {
        return uncompensatedAlpha / numComparisons;
    }

}
