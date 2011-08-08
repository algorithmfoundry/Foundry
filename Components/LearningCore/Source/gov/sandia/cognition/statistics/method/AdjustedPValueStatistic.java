/*
 * File:                AdjustedPValueStatistic.java
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
import java.util.Collection;

/**
 * A multiple-comparison statistic derived from a single adjusted p-value.
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Bonferroni correction",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/Bonferroni_correction"
)
public class AdjustedPValueStatistic 
    extends AbstractPairwiseMultipleHypothesisComparison.Statistic
{

    /**
     * Adjusted alpha term to account for multiple comparisons
     */
    protected double adjustedAlpha;

    /**
     * Creates a new instance of StudentizedMultipleComparisonStatistic
     * @param data
     * Data from each treatment to consider
     * @param uncompensatedAlpha
     * Uncompensated alpha (p-value threshold) for the multiple comparison
     * test
     * @param adjustedAlpha
     * Adjusted alpha term to account for multiple comparisons
     * @param pairwiseTest
     * Confidence test used for pair-wise null-hypothesis tests.
     */
    public AdjustedPValueStatistic(
        final Collection<? extends Collection<? extends Number>> data,
        final double uncompensatedAlpha,
        final double adjustedAlpha,
        final NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest )
    {
        super( data, uncompensatedAlpha, pairwiseTest );
        this.adjustedAlpha = adjustedAlpha;
    }

    @Override
    public AdjustedPValueStatistic clone()
    {
        return (AdjustedPValueStatistic) super.clone();
    }
    
    /**
     * Getter for adjustedAlpha
     * @return
     * Adjusted alpha term to account for multiple comparisons
     */
    public double getAdjustedAlpha()
    {
        return this.adjustedAlpha;
    }

    @Override
    public double getAdjustedAlpha(
        int i,
        int j)
    {
        return this.getAdjustedAlpha();
    }

}
