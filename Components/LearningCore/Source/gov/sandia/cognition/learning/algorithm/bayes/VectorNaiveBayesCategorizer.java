/*
 * File:                VectorNaiveBayesCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright November 24, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.bayes;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.ScalarDistribution;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A naive Bayesian categorizer that takes an input vector and applies an
 * independent scalar probability density function to each one.
 *
 * @param   <CategoryType>
 *      The output category type for the categorizer. Must implement equals and
 *      hash code.
 * @author  Justin Basilico
 * @since   3.1
 */
public class VectorNaiveBayesCategorizer<CategoryType>
    extends AbstractCloneableSerializable
    implements Categorizer<Vectorizable, CategoryType>,
    VectorInputEvaluator<Vectorizable, CategoryType>
{

    /** The prior distribution for the categorizer. */
    protected DataHistogram<CategoryType> priors;

    /** The mapping of category to the conditional distribution for the category
     *  with one probability density function for each dimension. */
    protected Map<CategoryType, List<ScalarProbabilityDensityFunction>> conditionals;

    /**
     * Creates a new {@code VectorNaiveBayesCategorizer} with an empty prior
     * and conditionals.
     */
    public VectorNaiveBayesCategorizer()
    {
        this(new MapBasedDataHistogram<CategoryType>(),
            new LinkedHashMap<CategoryType, List<ScalarProbabilityDensityFunction>>());
    }

    /**
     * Creates a new {@code VectorNaiveBayesCategorizer} with the given prior
     * and conditionals.
     *
     * @param   priors
     *      The prior distribution.
     * @param   conditionals
     *      The conditional distribution.
     */
    public VectorNaiveBayesCategorizer(
        final DataHistogram<CategoryType> priors,
        final Map<CategoryType, List<ScalarProbabilityDensityFunction>> conditionals)
    {
        super();

        this.setPriors(priors);
        this.setConditionals(conditionals);
    }

    @Override
    public VectorNaiveBayesCategorizer<CategoryType> clone()
    {
        @SuppressWarnings("unchecked")
        final VectorNaiveBayesCategorizer<CategoryType> clone =
            (VectorNaiveBayesCategorizer<CategoryType>) super.clone();

        clone.priors = ObjectUtil.cloneSafe(this.priors);
        clone.conditionals =
            new LinkedHashMap<CategoryType, List<ScalarProbabilityDensityFunction>>(
            this.conditionals.size());
        for (CategoryType category : this.conditionals.keySet())
        {
            clone.conditionals.put(category,
                ObjectUtil.cloneSmartElementsAsArrayList(
                this.conditionals.get(category)));
        }

        return clone;
    }

    @Override
    public CategoryType evaluate(
        final Vectorizable input)
    {
        final Vector vector = input.convertToVector();

        // We want to find the category with the maximum posterior distribution.
        // This means we only have to compute the numerator of the class
        // probability formula, since the denominator is the same for every
        // class.
        double maxPosterior = -1.0;
        CategoryType maxCategory = null;
        for (CategoryType category : this.getCategories())
        {
            // Get the prior for the class.
            final double priorProbability = this.priors.getFraction(category);

            // Now compute the posterior by looking at the probability density
            // function for each dimension. We loop until
            double posterior = priorProbability;
            final List<ScalarProbabilityDensityFunction> probabilityFunctions =
                this.conditionals.get(category);
            final int size = probabilityFunctions.size();
            for (int i = 0; i < size && posterior > 0.0; i++)
            {
                // Get the value for the element.
                final double value = vector.getElement(i);

                // Update the posterior.
                posterior *= probabilityFunctions.get(i).evaluate(value);
            }

            // See if the new posterior
            if (posterior > maxPosterior)
            {
                maxPosterior = posterior;
                maxCategory = category;
            }
        }

        return maxCategory;
    }

    @Override
    public Set<CategoryType> getCategories()
    {
        return this.conditionals.keySet();
    }

    @Override
    public int getInputDimensionality()
    {
        // The dimensionality is the size of the first list (which should be
        // the same as the size of all the others).
        final List<ScalarProbabilityDensityFunction> first =
            CollectionUtil.getFirst(this.conditionals.values());

        return first == null ? 0 : first.size();
    }

    /**
     * Gets the prior distribution over the categories.
     *
     * @return
     *      The prior distribution over the categories.
     */
    public DataHistogram<CategoryType> getPriors()
    {
        return this.priors;
    }

    /**
     * Sets the prior distribution over the categories.
     *
     * @param   priors
     *      The prior distribution over the categories.
     */
    public void setPriors(
        final DataHistogram<CategoryType> priors)
    {
        this.priors = priors;
    }

    /**
     * Gets the conditional distributions, which is a mapping of category to
     * the list of probability density functions, one for each dimension of the
     * vector.
     *
     * @return
     *      The conditional distributions for each category.
     */
    public Map<CategoryType, List<ScalarProbabilityDensityFunction>> getConditionals()
    {
        return this.conditionals;
    }

    /**
     * Sets the conditional distributions, which is a mapping of category to
     * the list of probability density functions, one for each dimension of the
     * vector.
     *
     * @param   conditionals
     *      The conditional distributions for each category.
     */
    public void setConditionals(
        final Map<CategoryType, List<ScalarProbabilityDensityFunction>> conditionals)
    {
        this.conditionals = conditionals;
    }

    /**
     * A supervised batch learner for a vector Naive Bayes categorizer.
     *
     * @param   <CategoryType>
     *      The output category type for the categorizer. Must implement equals and
     *      hash code.
     */
    public static class Learner<CategoryType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<Vectorizable, CategoryType, VectorNaiveBayesCategorizer<CategoryType>>
    {

        /** The learner for the distribution of each dimension of each category. */
        protected DistributionEstimator<? super Double, ? extends ScalarProbabilityDensityFunction> distributionEstimator;

        /**
         * Creates a new {@code BatchLearner} with a univariate Gaussian
         * distribution maximum likelihood estimator.
         */
        public Learner()
        {
            this(new UnivariateGaussian.MaximumLikelihoodEstimator());
        }

        /**
         * Creates a new {@code BatchLearner} with the given distribution
         * estimator.
         *
         * @param   distributionEstimator
         *      The estimator for the distribution of each dimension of each
         *      category.
         */
        public Learner(
            final DistributionEstimator<? super Double, ? extends ScalarProbabilityDensityFunction> distributionEstimator)
        {
            super();

            this.setDistributionEstimator(distributionEstimator);
        }

        @Override
        public VectorNaiveBayesCategorizer<CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends Vectorizable, CategoryType>> data)
        {
            // Split the data by category.
            final int dimensionality = DatasetUtil.getInputDimensionality(data);
            final Map<CategoryType, List<Vectorizable>> examplesPerCategory =
                DatasetUtil.splitOnOutput(data);

            // Create the categorizer to store the result.
            final VectorNaiveBayesCategorizer<CategoryType> result =
                new VectorNaiveBayesCategorizer<CategoryType>();

            final ArrayList<Double> values = new ArrayList<Double>(data.size());

            // Go through the categories.
            for (CategoryType category : examplesPerCategory.keySet())
            {
                // Get the examples for that category.
                final List<Vectorizable> examples =
                    examplesPerCategory.get(category);
                final int count = examples.size();

                // Go through all the dimensions and create the conditional
                // distribution for it.
                final List<ScalarProbabilityDensityFunction> conditionals =
                    new ArrayList<ScalarProbabilityDensityFunction>(
                        dimensionality);
                for (int i = 0; i < dimensionality; i++)
                {
                    // Add the values for the given dimension to the array.
                    for (Vectorizable input : examples)
                    {
                        values.add(input.convertToVector().getElement(i));
                    }

                    // Create the univariate gaussian PDF.
                    conditionals.add(this.distributionEstimator.learn(values));
                    
                    // Clear the reusable array of values.
                    values.clear();
                }

                // Add the category to the priors and its conditional.
                result.priors.add(category, count);
                result.conditionals.put(category, conditionals);
            }

            return result;
        }

        /**
         * Gets the estimation method for the distribution of each dimension of
         * each category.
         *
         * @return
         *      The estimator for the distribution of each dimension of each
         *      category.
         */
        public DistributionEstimator<? super Double, ? extends ScalarProbabilityDensityFunction> getDistributionEstimator()
        {
            return this.distributionEstimator;
        }

        /**
         * Sets the estimation method for the distribution of each dimension of
         * each category.
         *
         * @param   distributionEstimator
         *      The estimator for the distribution of each dimension of each
         *      category.
         */
        public void setDistributionEstimator(
            final DistributionEstimator<? super Double, ? extends ScalarProbabilityDensityFunction> distributionEstimator)
        {
            this.distributionEstimator = distributionEstimator;
        }

    }

    /**
     * A supervised batch learner for a vector Naive Bayes categorizer that fits
     * a Gaussian.
     *
     * @param   <CategoryType>
     *      The output category type for the categorizer. Must implement equals and
     *      hash code.
     */
    public static class BatchGaussianLearner<CategoryType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<Vectorizable, CategoryType, VectorNaiveBayesCategorizer<CategoryType>>
    {

        /**
         * Creates a new {@code BatchGaussianLearner}.
         */
        public BatchGaussianLearner()
        {
            super();
        }

        @Override
        public VectorNaiveBayesCategorizer<CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends Vectorizable, CategoryType>> data)
        {
            // Split the data by category.
            final int dimensionality = DatasetUtil.getInputDimensionality(data);
            final Map<CategoryType, List<Vectorizable>> examplesPerCategory =
                DatasetUtil.splitOnOutput(data);

            // Create the categorizer to store the result.
            final VectorNaiveBayesCategorizer<CategoryType> result =
                new VectorNaiveBayesCategorizer<CategoryType>();

            // Go through the categories.
            for (CategoryType category : examplesPerCategory.keySet())
            {
                // Get the examples for that category.
                final List<Vectorizable> examples =
                    examplesPerCategory.get(category);

                // Try to compute the mean and variance for each dimension in
                // one pass by using the sum of values and the sum of squared
                // values.
                final RingAccumulator<Vector> sumsAccumulator =
                    new RingAccumulator<Vector>();
                final RingAccumulator<Vector> sumsOfSquaresAccumulator =
                    new RingAccumulator<Vector>();
                for (Vectorizable input : examples)
                {
                    final Vector vector = input.convertToVector();
                    sumsAccumulator.accumulate(vector);
                    sumsOfSquaresAccumulator.accumulate(vector.dotTimes(vector));
                }

                // Transform the accumuators into vectors.
                final Vector sums = sumsAccumulator.getSum();
                final Vector sumsOfSquares = sumsOfSquaresAccumulator.getSum();

                // Figure out the number of instances and the denominator for
                // the variance. We check for values greater than 1 to avoid a
                // divide-by-zero.
                final int count = examples.size();
                final long varianceDenominator =
                    count > 1 ? (count - 1) : 1;
                final List<ScalarProbabilityDensityFunction> conditionals =
                    new ArrayList<ScalarProbabilityDensityFunction>(
                    dimensionality);
                for (int i = 0; i < dimensionality; i++)
                {
                    // Figure out the mean and variance.
                    final double sum = sums.getElement(i);
                    final double sumOfSquares = sumsOfSquares.getElement(i);
                    final double mean = sum / count;
                    final double variance = (sumOfSquares - sum * mean)
                        / varianceDenominator;

                    // Create the univariate gaussian PDF.
                    conditionals.add(
                        new UnivariateGaussian.PDF(mean, variance));
                }

                // Add the category to the priors and its conditional.
                result.priors.add(category, count);
                result.conditionals.put(category, conditionals);
            }

            return result;
        }

    }


}
