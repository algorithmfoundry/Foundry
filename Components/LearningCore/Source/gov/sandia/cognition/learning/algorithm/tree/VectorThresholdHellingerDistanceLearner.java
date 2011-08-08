/*
 * File:                VectorThresholdHellingerDistanceLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;

/**
 * A categorization tree decision function learner on vector data that learns a
 * vector value threshold function using the Hellinger distance. The Hellinger
 * distance is supposed to be less sensitive to skewed data than the more
 * well known information gain method. It also behaves about the same as
 * information gain on balanced data. Thus, it is thought that the Hellinger
 * method may be superior to information gain.
 *
 * For a given split (sets X and Y) for two categories (a and b)
 *
 *    d(X, Y) = sqrt(   (sqrt(Xa / Na) - sqrt(Xb / Nb))^2
 *                    + (sqrt(Ya / Na) - sqrt(Yb / Nb))^2)
 * where
 *    Xa = number of a's in X,
 *    Xb = number of b's in X,
 *    Ya = number of a's in Y,
 *    Yb = number of b's in Y,
 *    Na = total number of a's (= Xa + Ya), and
 *    Nb = total number of b's (= Xb + Yb).
 *
 * The Hellinger distance ranges between 0 and sqrt(2), inclusive.
 *
 * In a problem where there are more than two categories, the Hellinger
 * distance is computed for each unique pair of categories and averaged to
 * compute the Hellinger distance for that split.
 *
 * @param   <OutputType>
 *      The output category type for the training data.
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    author = { "David A. Cieslak", "Nitesh V. Chawla" },
    title = "Increasing Skew Insensitivity of Decision Trees with Hellinger Distance",
    type = PublicationType.TechnicalReport,
    year = 2008,
    publication = "Notre Dame University Computer Science and Engineering Technical Reports",
    url = "http://www.cse.nd.edu/Reports/2008/TR-2008-06.pdf"
)
public class VectorThresholdHellingerDistanceLearner<OutputType>
    extends AbstractVectorThresholdMaximumGainLearner<OutputType>
{

    /**
     * Creates a new {@code VectorThresholdHellingerDistanceLearner}.
     */
    public VectorThresholdHellingerDistanceLearner()
    {
        super();
    }

    /**
     * Computes the split gain by computing the mean Hellinger distance for the
     * given split. The gain is equal to the distance since the base has a
     * distance of 0.0 with itself.
     * 
     * @param   baseCounts
     *      The histogram of counts before the split.
     * @param   positiveCounts
     *      The counts on the positive side of the threshold.
     * @param   negativeCounts
     *      The counts on the negative side of the threshold.
     * @return
     *      The split gain by computing the mean Hellinger distance for
     *      the given split.
     */
    public double computeSplitGain(
        final MapBasedDataHistogram<OutputType> baseCounts,
        final MapBasedDataHistogram<OutputType> positiveCounts,
        final MapBasedDataHistogram<OutputType> negativeCounts)
    {
        // Get the number of categories.
        final int categoryCount = baseCounts.getDomain().size();
        if (categoryCount <= 1)
        {
            // If there is only one category, the Hellinger distance is zero.
            // The algorithm should catch this case before getting here, but
            // this is a sanity check.
            return 0.0;
        }

        // We want to look at the mean Hellinger distance between each unique
        // pair of categories. To do this we break the computation down into
        // two steps. One to compute the relevant information for each category
        // on its own. The other is to compute the pairwise Hellinger distance
        // from those precomputed values. One reason we do the first pass is to
        // remove duplicate computations. The other reason is to provide an
        // indexing of the labels so that we can avoid having to compute both
        // the distances a -> b and b -> a, which will be identical. This way
        // uses more memory, but should be a bit faster since it caches all the
        // necessary values in the algorithm instead of computing each of them
        // (potentially) twice.
        final double[] sqrtPositiveProportions = new double[categoryCount];
        final double[] sqrtNegativeProportions = new double[categoryCount];
        int categoryIndex = 0;
        for (OutputType category : baseCounts.getDomain())
        {
            // Get the counts for the category.
            final int total    = baseCounts.getCount(category);
            final int positive = positiveCounts.getCount(category);
            final int negative = negativeCounts.getCount(category);

            // We use these two values to compute the category-to-category
            // Hellinger distance. Its the square root of the proportion of the
            // instances of the label that are positive or negative.
            sqrtPositiveProportions[categoryIndex] =
                Math.sqrt((double) positive / total);
            sqrtNegativeProportions[categoryIndex] = 
                Math.sqrt((double) negative / total);
            
            categoryIndex++;
        }

        // Now we loop over all the unique pairs of categories and compute the
        // sum of the Hellinger distances between them. We then use the sum to
        // compute the mean.
        double hellingerSum = 0.0;
        for (int i = 0; i < categoryCount; i++)
        {
            // Get the information for label i.
            final double sqrtPositiveProportionsI = sqrtPositiveProportions[i];
            final double sqrtNegativeProportionsI = sqrtNegativeProportions[i];

            // Loop over the other categories that we haven't counted.
            for (int j = i + 1; j < categoryCount; j++)
            {
                // Get the information for label j.
                final double sqrtPositiveProportionsJ =
                    sqrtPositiveProportions[j];
                final double sqrtNegativeProportionsJ =
                    sqrtNegativeProportions[j];

                // Compute the two parts we need for the Hellinger distance.
                // We compute the parts individually here since we are going
                // to need to square them for the final distance.
                final double positivePart =
                    sqrtPositiveProportionsI - sqrtPositiveProportionsJ;
                final double negativePart =
                    sqrtNegativeProportionsI - sqrtNegativeProportionsJ;

                // Compute the Hellinger distance for this pair of categories.
                final double hellinger =
                    Math.sqrt(
                          positivePart * positivePart
                        + negativePart * negativePart);

                // Add the value to the sum.
                hellingerSum += hellinger;
            }
        }

        // This is the number of pairs that there are, since we are only
        // computing the upper-right triangle and exclude the diagonal.
        final int count = (categoryCount * (categoryCount - 1)) / 2;
        return hellingerSum / (double) count;
    }
}
