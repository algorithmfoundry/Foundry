/*
 * File:                SidakCorrection.java
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
 * The Sidak correction takes a pair-wise null-hypothesis test and
 * generalizes it to multiple comparisons by adjusting the requisite p-value
 * to find significance as alpha / NumComparisons.  If there are "K" treatments
 * being compared, each treatment is compared to all others:
 * NumComparisons == K*(K-1).  The Bonferroni correction is known to be
 * extremely conservative and tightly controls the false-discovery rate.
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@ConfidenceTestAssumptions(
    name="Sidak correction",
    alsoKnownAs="Dunn-Sidak correction",
    description={
        "Sidak's correction is a conservative way to compensate for pair-wise null-hypothesis comparisons applied to multiple comparison to control false-discovery rate.",
        "The correction is known to be overly conservative, trading low false-discovery for high false-negative rates.",
        "The Sidak correction is tighter than the Bonferroni correction, and the Sidak correction is generally preferred."
    },
    assumptions={
        "The assumptions of the underlying pair-wise test are met.",
        "The treatments must be independent."
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
public class SidakCorrection 
    extends AbstractPairwiseMultipleHypothesisComparison<AdjustedPValueStatistic>
{

    /** 
     * Creates a new instance of SidakCorrection 
     */
    public SidakCorrection()
    {
        this( DEFAULT_PAIRWISE_TEST );
    }

    /**
     * Creates a new instance of SidakCorrection
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public SidakCorrection(
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest )
    {
        super( pairwiseTest );
    }

    @Override
    public SidakCorrection clone()
    {
        return (SidakCorrection) super.clone();
    }

    @Override
    public AdjustedPValueStatistic evaluateNullHypotheses(
        Collection<? extends Collection<? extends Number>> data,
        double uncompensatedAlpha)
    {
        final int K = data.size();
        final int N = K*(K-1)/2;
        final double adjustedAlpha = adjust(uncompensatedAlpha, N);
        return new AdjustedPValueStatistic(
            data, uncompensatedAlpha, adjustedAlpha, this.getPairwiseTest());
    }

    /**
     * Computes the Sidak multiple-comparison correction
     * @param uncompensatedAlpha
     * Uncompensated alpha (p-value threshold) for the multiple comparison
     * test, must be [0,1]
     * @param numComparisons
     * Number of comparisons to make.
     * @return
     * Sidak correction
     */
    public static double adjust(
        double uncompensatedAlpha,
        int numComparisons )
    {
        return 1.0 - Math.pow(1.0-uncompensatedAlpha, 1.0/numComparisons);
    }

}
