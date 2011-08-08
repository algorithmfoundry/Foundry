/*
 * File:                ConfusionMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.performance.AbstractSupervisedPerformanceEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A default implementation of the {@code BinaryConfusionMatrix}. It stores the
 * four entries in the confusion matrix: true positives, false positives,
 * true negatives, and false negatives.
 *
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.1
 */
public class DefaultBinaryConfusionMatrix
    extends AbstractBinaryConfusionMatrix
{

    /** Number of true negatives. The (false, false) entry. */
    protected double trueNegativesCount;

    /** Number of false positives. The (false, true) entry. */
    protected double falsePositivesCount;

    /** Number of false negatives. The (true, false) entry. */
    protected double falseNegativesCount;

    /** Number of true positives. The (true, true) entry. */
    protected double truePositivesCount;
    
    /**
     * Creates a new, empty {@code DefaultBinaryConfusionMatrix}.
     */
    public DefaultBinaryConfusionMatrix()
    {
        super();

        this.setTruePositivesCount(0.0);
        this.setFalsePositivesCount(0.0);
        this.setTrueNegativesCount(0.0);
        this.setFalseNegativesCount(0.0);
    }

    @Override
    public DefaultBinaryConfusionMatrix clone()
    {
        return (DefaultBinaryConfusionMatrix) super.clone();
    }
    
    @Override
    public void add(
        final Boolean target,
        final Boolean estimate,
        final double value)
    {
        if (target)
        {
            if (estimate)
            {
                this.truePositivesCount += value;
            }
            else
            {
                this.falseNegativesCount += value;
            }
        }
        else
        {
            if (estimate)
            {
                this.falsePositivesCount += value;
            }
            else
            {
                this.trueNegativesCount += value;
            }
        }
    }

    @Override
    public void clear()
    {
        this.setTruePositivesCount(0.0);
        this.setFalseNegativesCount(0.0);
        this.setTrueNegativesCount(0.0);
        this.setFalsePositivesCount(0.0);
    }

    @Override
    public double getTruePositivesCount()
    {
        return this.truePositivesCount;
    }

    /**
     * Sets the number of true positives in the matrix.
     *
     * @param   truePositivesCount
     *      The number of true positives. Cannot be negative.
     */
    public void setTruePositivesCount(
        final double truePositivesCount)
    {
        ArgumentChecker.assertIsNonNegative(
            "truePositivesCount", truePositivesCount);
        this.truePositivesCount = truePositivesCount;
    }

    @Override
    public double getFalsePositivesCount()
    {
        return this.falsePositivesCount;
    }

    /**
     * Sets the number of false positives in the matrix.
     *
     * @param   falsePositivesCount
     *      The number of false positives. Cannot be negative.
     */
    public void setFalsePositivesCount(
        final double falsePositivesCount)
    {
        ArgumentChecker.assertIsNonNegative(
            "falsePositivesCount", falsePositivesCount);
        this.falsePositivesCount = falsePositivesCount;
    }

    @Override
    public double getTrueNegativesCount()
    {
        return this.trueNegativesCount;
    }

    /**
     * Sets the number of true negatives in the matrix.
     *
     * @param   trueNegativesCount
     *      The number of true negatives. Cannot be negative.
     */
    public void setTrueNegativesCount(
        final double trueNegativesCount)
    {
        ArgumentChecker.assertIsNonNegative(
            "trueNegativesCount", trueNegativesCount);
        this.trueNegativesCount = trueNegativesCount;
    }

    @Override
    public double getFalseNegativesCount()
    {
        return this.falseNegativesCount;
    }

    /**
     * Sets the number of false negatives in the matrix.
     *
     * @param   falseNegativesCount
     *      The number of false negatives. Cannot be negative.
     */
    public void setFalseNegativesCount(
        final double falseNegativesCount)
    {
        ArgumentChecker.assertIsNonNegative(
            "falseNegativesCount", falseNegativesCount);
        this.falseNegativesCount = falseNegativesCount;
    }


    /**
     * Gets the rate of negative targets correctly classified. Computed as:
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     *
     * @return
     *      True negative rate, which is in [0,1].
     */
    public double getTrueNegativesRate()
    {
        double denom = this.getTrueNegativesCount() + this.getFalsePositivesCount();
        double retval;
        if (denom <= 0.0)
        {
            retval = 1.0;
        }
        else
        {
            retval = this.getTrueNegativesCount() / denom;
        }

        return retval;

    }

    /**
     * Gets the rate of target trues that were correctly classified as true.
     * Computed as:
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     *
     * @return
     *      True positives rate, which is in [0,1].
     */
    public double getTruePositivesRate()
    {
        double denom = this.getTruePositivesCount() + this.getFalseNegativesCount();
        double retval;
        if (denom <= 0.0)
        {
            retval = 1.0;
        }
        else
        {
            retval = this.getTruePositivesCount() / denom;
        }

        return retval;

    }

    /**
     * Gets the rate of false targets incorrectly classified as true. Computed
     * as:
     * FalsePositives / TotalNegatives = FalsePositives / (TrueNegatives + FalsePositives)
     *
     * @return
     *      Rate of false positives, which is in [0,1]. Equal to
     *      1 - trueNegativeRate.
     */
    public double getFalsePositivesRate()
    {
        return 1.0 - this.getTrueNegativesRate();
    }

    /**
     * Gets the rate of true targets incorrectly classified as false. Computed
     * as:
     * FalseNegatives / TotalPositives = FalseNegatives / (TruePositives + FalseNegatives)
     *
     * @return
     *      Rate of false negatives, which is in [0,1]. Equal to
     *      1 - truePositiveRate
     */
    public double getFalseNegativesRate()
    {
        return 1.0 - this.getTruePositivesRate();
    }

    @Override
    public String toString()
    {
        return
            "True Negatives: " + this.getTrueNegativesCount()
            + ", " + "False Positives: " + this.getFalsePositivesCount()
            + ", " + "False Negatives: " + this.getFalseNegativesCount()
            + ", " + "True Positives: " + this.getTruePositivesCount();
    }

    /**
     * Takes a general confusion matrix and creates a binary form of it using
     * true category. All other categories are considered false.
     *
     * @param <CategoryType>
     *      The true category type.
     * @param   matrix
     *      The general confusion matrix to binarize.
     * @param   trueCategory
     *      The value of the true category for the binary confusion matrix.
     * @return
     *      A new binary confusion matrix.
     */
    public static <CategoryType> DefaultBinaryConfusionMatrix binarizeOnTrueCategory(
        final ConfusionMatrix<CategoryType> matrix,
        final CategoryType trueCategory)
    {
        return binarizeOnTrueSet(matrix, Collections.singleton(trueCategory));
    }

    /**
     * Takes a general confusion matrix and creates a binary form of it using
     * the given set of true categories. All other categories are considered
     * false.
     *
     * @param <CategoryType>
     *      The true category type.
     * @param   matrix
     *      The general confusion matrix to binarize.
     * @param   trueSet
     *      The set of categories in the true set for the binary confusion
     *      matrix.
     * @return
     *      A new binary confusion matrix.
     */
    public static <CategoryType> DefaultBinaryConfusionMatrix binarizeOnTrueSet(
        final ConfusionMatrix<CategoryType> matrix,
        final Set<? super CategoryType> trueSet)
    {
        final DefaultBinaryConfusionMatrix result =
            new DefaultBinaryConfusionMatrix();

        for (CategoryType actual : matrix.getActualCategories())
        {
            final boolean actualBinary = trueSet.contains(actual);

            for (CategoryType predicted : matrix.getPredictedCategories(actual))
            {
                final boolean predictedBinary = trueSet.contains(predicted);
                result.add(actualBinary, predictedBinary,
                    matrix.getCount(actual, predicted));
            }
        }
        return result;
    }

    /**
     * Creates a new {@code DefaultConfusionMatrix} from the given
     * actual-predicted pairs.
     *
     * @param   pairs
     *      The actual-category pairs.
     * @return
     *      A new confusion matrix populated from the given actual-category
     *      pairs.
     */
    public static DefaultBinaryConfusionMatrix createFromActualPredictedPairs(
        final Collection<? extends Pair<? extends Boolean, ? extends Boolean>> pairs)
    {
        final DefaultBinaryConfusionMatrix result =
            new DefaultBinaryConfusionMatrix();
        for (Pair<? extends Boolean, ? extends Boolean> pair
            : pairs)
        {
            result.add(pair.getFirst(), pair.getSecond());
        }
        return result;
    }

    /**
     * Creates a new {@code DefaultBinaryConfusionMatrix} from the given
     * target-estimate pairs.
     *
     * @param   input
     *      The target-estimate pairs.
     * @return
     *      A new confusion matrix populated from the given target-estimate
     *      pairs.
     */
    public static DefaultBinaryConfusionMatrix create(
        final Iterable<? extends TargetEstimatePair<? extends Boolean, ? extends Boolean>> input)
    {
        return DefaultBinaryConfusionMatrix.create(input, true);
    }

    /**
     * Creates a new {@code DefaultBinaryConfusionMatrix} from the given
     * target-estimate pairs.
     *
     * @param   input
     *      The target-estimate pairs.
     * @param   weightIfAvailable
     *      True uses the weight of each item (or 1.0 if there is none); false
     *      means 1.0 is used regardless of weight.
     * @return
     *      A new confusion matrix populated from the given target-estimate
     *      pairs.
     */
    public static DefaultBinaryConfusionMatrix create(
        final Iterable<? extends TargetEstimatePair<? extends Boolean, ? extends Boolean>> input,
        final boolean weightIfAvailable )
    {
        double falsePositives = 0;
        double falseNegatives = 0;
        double truePositives = 0;
        double trueNegatives = 0;
        for (TargetEstimatePair<? extends Boolean, ? extends Boolean> pair
            : input)
        {
            double increment;
            if (weightIfAvailable)
            {
                increment = DatasetUtil.getWeight(pair);
            }
            else
            {
                increment = 1.0;
            }

            boolean target = pair.getTarget();
            boolean estimate = pair.getEstimate();
            if (target == true)
            {
                if (estimate == true)
                {
                    truePositives += increment;
                }
                else
                {
                    falseNegatives += increment;
                }
            }
            else
            {
                if (estimate == true)
                {
                    falsePositives += increment;
                }
                else
                {
                    trueNegatives += increment;
                }
            }

        }

        final DefaultBinaryConfusionMatrix result =
            new DefaultBinaryConfusionMatrix();
        result.setFalsePositivesCount(falsePositives);
        result.setFalseNegativesCount(falseNegatives);
        result.setTruePositivesCount(truePositives);
        result.setTrueNegativesCount(trueNegatives);
        return result;
    }


    /**
     * An implementation of the {@code SupervisedPerformanceEvaluator} interface
     * for creating a {@code DefaultBinaryConfusionMatrix}.
     *
     * @param   <InputType>
     *      Input type for the evaluator to compute the confusion over.
     */
    public static class PerformanceEvaluator<InputType>
        extends AbstractSupervisedPerformanceEvaluator<InputType, Boolean, Boolean, DefaultBinaryConfusionMatrix>
    {

        /**
         * Creates a new {@code PerformanceEvaluator}.
         */
        public PerformanceEvaluator()
        {
            super();
        }

        public DefaultBinaryConfusionMatrix evaluatePerformance(
            final Collection<? extends TargetEstimatePair<Boolean, Boolean>> data )
        {
            return DefaultBinaryConfusionMatrix.create(data);
        }

    }

    /**
     * A confusion matrix summarizer that summarizes actual-predicted pairs.
     */
    public static class ActualPredictedPairSummarizer
        extends AbstractCloneableSerializable
        implements Summarizer<Pair<? extends Boolean, ? extends Boolean>, DefaultBinaryConfusionMatrix>
    {

        /**
         * Creates a new {@code CombineSummarizer}.
         */
        public ActualPredictedPairSummarizer()
        {
            super();
        }

        @Override
        public DefaultBinaryConfusionMatrix summarize(
            final Collection<? extends Pair<? extends Boolean, ? extends Boolean>> data)
        {
            return createFromActualPredictedPairs(data);
        }

    }

    /**
     * A confusion matrix summarizer that adds together confusion matrices.
     */
    public static class CombineSummarizer
        extends AbstractCloneableSerializable
        implements Summarizer<ConfusionMatrix<Boolean>, DefaultBinaryConfusionMatrix>
    {

        /**
         * Creates a new {@code CombineSummarizer}.
         */
        public CombineSummarizer()
        {
            super();
        }

        @Override
        public DefaultBinaryConfusionMatrix summarize(
            final Collection<? extends ConfusionMatrix<Boolean>> data)
        {
            final DefaultBinaryConfusionMatrix result =
                new DefaultBinaryConfusionMatrix();

            for (ConfusionMatrix<Boolean> item : data)
            {
                result.addAll(item);
            }

            return result;
        }

    }


}
