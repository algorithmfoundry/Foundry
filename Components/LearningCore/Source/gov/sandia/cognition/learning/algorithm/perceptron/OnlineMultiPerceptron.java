 /*
 * File:                OnlineMultiPerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.LinearMultiCategorizer;
import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.LinkedList;

/**
 * An online, multiple category version of the Perceptron algorithm. It learns
 * a separate linear binary categorizer for each category.
 * 
 * @param <CategoryType>
 *      The type of output categories. Can be any type that has a valid
 *      equals and hashCode method.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlineMultiPerceptron<CategoryType>
    extends AbstractBatchAndIncrementalLearner<InputOutputPair<? extends Vectorizable, CategoryType>, LinearMultiCategorizer<CategoryType>>
    implements VectorFactoryContainer
{

    /** The default minimum margin is {@value}. */
    public static final double DEFAULT_MIN_MARGIN = 0.0;

    /** The minimum margin to enforce. Must be non-negative. */
    protected double minMargin;

    /** The factory to create weight vectors. */
    protected VectorFactory<?> vectorFactory;

    /**
     * Creates a new {@code OnlineMultiPerceptron}.
     */
    public OnlineMultiPerceptron()
    {
        this(DEFAULT_MIN_MARGIN);
    }

    /**
     * Creates a new {@code OnlineMultiPerceptron} with the
     * given minimum margin.
     *
     * @param   minMargin
     *      The minimum margin to consider an example correct.
     */
    public OnlineMultiPerceptron(
        final double minMargin)
    {
        this(minMargin, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code OnlineMultiPerceptron} with the
     * given minimum margin and backing vector factory.
     *
     * @param   minMargin
     *      The minimum margin to consider an example correct.
     * @param   vectorFactory
     *      The vector factory used to create the weight vectors.
     */
    public OnlineMultiPerceptron(
        final double minMargin,
        final VectorFactory<?> vectorFactory)
    {
        super();

        this.setMinMargin(minMargin);
        this.setVectorFactory(vectorFactory);
    }
    
    @Override
    public LinearMultiCategorizer<CategoryType> createInitialLearnedObject()
    {
        return new LinearMultiCategorizer<CategoryType>();
    }

    @Override
    public void update(
        final LinearMultiCategorizer<CategoryType> target,
        final InputOutputPair<? extends Vectorizable, CategoryType> example)
    {
        final Vector input = example.getInput().convertToVector();
        final CategoryType actual = example.getOutput();

        final boolean knownCategory = target.getCategories().contains(actual);
        if (!knownCategory)
        {
            // This category was never seen, so create a new prototype for it.
            final Vector initialWeights = this.getVectorFactory().createVector(
                input.getDimensionality());
            target.getPrototypes().put(actual,
                new LinearBinaryCategorizer(initialWeights, 0.0));
        }

        // See what the predicted category is.
        CategoryType predicted = null;
        double predictedScore = Double.NEGATIVE_INFINITY;
        for (CategoryType category : target.getCategories())
        {
            double score = target.evaluateAsDouble(input, category);
            if (actual.equals(category))
            {
                score -= this.minMargin;
            }

            if (score > predictedScore)
            {
                predicted = category;
                predictedScore = score;
            }
        }

        final boolean correct = ObjectUtil.equalsSafe(actual, predicted);
        if (!correct)
        {
            // Increment the prototype for the actual category.
            final LinearBinaryCategorizer actualPrototype =
                target.getPrototypes().get(actual);
            actualPrototype.getWeights().plusEquals(input);
            actualPrototype.setBias(actualPrototype.getBias() + 1.0);

            // Decrement the prototype for the predicted category.
            final LinearBinaryCategorizer predictedPrototype =
                target.getPrototypes().get(predicted);
            predictedPrototype.getWeights().minusEquals(input);
            predictedPrototype.setBias(predictedPrototype.getBias() - 1.0);
        }
        // else - Not an error, no need to update.
    }

    /**
     * Gets the minimum margin to enforce. Any value less than or equal to
     * this is considered an error for the algorithm.
     *
     * @return
     *      The minimum margin. Cannot be negative.
     */
    public double getMinMargin()
    {
        return this.minMargin;
    }

    /**
     * Gets the minimum margin to enforce. Any value less than or equal to
     * this is considered an error for the algorithm.
     *
     * @param   minMargin
     *      The minimum margin. Cannot be negative.
     */
    public void setMinMargin(
        final double minMargin)
    {
        ArgumentChecker.assertIsNonNegative("minMargin", minMargin);
        this.minMargin = minMargin;
    }

    /**
     * Gets the VectorFactory used to create the weight vector.
     *
     * @return The VectorFactory used to create the weight vector.
     */
    @Override
    public VectorFactory<?> getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the VectorFactory used to create the weight vector.
     *
     * @param  vectorFactory The VectorFactory used to create the weight vector.
     */
    public void setVectorFactory(
        final VectorFactory<?> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

    /**
     * Variant of a multi-category Perceptron that performs a uniform weight
     * update on all categories that are scored higher than the true category
     * such that the weights are equal and sum to -1.
     *
     * @param <CategoryType>
     *      The type of output categories. Can be any type that has a valid
     *      equals and hashCode method.
     */
    @PublicationReference(
      title="Ultraconservative online algorithms for multiclass problems",
      author={"Koby Crammer", "Yoram Singer"},
      year=2003,
      type=PublicationType.Journal,
      publication="The Journal of Machine Learning Research",
      pages={951, 991},
      url="http://portal.acm.org/citation.cfm?id=944936")
    public static class UniformUpdate<CategoryType>
        extends OnlineMultiPerceptron<CategoryType>
    {

        /**
         * Creates a new {@code OnlineMultiPerceptron.UniformUpdate}.
         */
        public UniformUpdate()
        {
            super();
        }

        /**
         * Creates a new {@code OnlineMultiPerceptron.UniformUpdate} with the
         * given minimum margin.
         *
         * @param   minMargin
         *      The minimum margin to consider an example correct.
         */
        public UniformUpdate(
            final double minMargin)
        {
            super(minMargin);
        }

        /**
         * Creates a new {@code OnlineMultiPerceptron.UniformUpdate} with the
         * given minimum margin and backing vector factory.
         *
         * @param   minMargin
         *      The minimum margin to consider an example correct.
         * @param   vectorFactory
         *      The vector factory used to create the weight vectors.
         */
        public UniformUpdate(
            final double minMargin,
            final VectorFactory<?> vectorFactory)
        {
            super(minMargin, vectorFactory);
        }

        @Override
        public void update(
            final LinearMultiCategorizer<CategoryType> target,
            final InputOutputPair<? extends Vectorizable, CategoryType> example)
        {
            // TODO: This shares a lot of code with the parent class update.
            // Figure out a way to combine them in a sensible way.
            // -- jdbasil (2011-01-30)
            final Vector input = example.getInput().convertToVector();
            final CategoryType actual = example.getOutput();

            final boolean knownCategory = target.getCategories().contains(actual);
            if (!knownCategory)
            {
                // This category was never seen, so create a new prototype for it.
                final Vector initialWeights = this.getVectorFactory().createVector(
                    input.getDimensionality());
                target.getPrototypes().put(actual,
                    new LinearBinaryCategorizer(initialWeights, 0.0));
            }

            // See what the predicted category is.
            final double actualScore =
                target.evaluateAsDouble(input, actual) - this.minMargin;
            final LinkedList<CategoryType> errors =
                 new LinkedList<CategoryType>();
            for (CategoryType category : target.getCategories())
            {
                final double score = target.evaluateAsDouble(input, category);
                if (!actual.equals(category) && score >= actualScore)
                {
                    errors.add(category);
                }
            }

            final boolean correct = errors.isEmpty();
            if (!correct)
            {
                // Increment the prototype for the actual category.
                final LinearBinaryCategorizer actualPrototype =
                    target.getPrototypes().get(actual);
                actualPrototype.getWeights().plusEquals(input);
                actualPrototype.setBias(actualPrototype.getBias() + 1.0);

                // Decrement the prototype for the predicted category.
                final double errorWeight = 1.0 / errors.size();
                final Vector errorUpdate = input.scale(errorWeight);
                for (CategoryType category : errors)
                {
                    final LinearBinaryCategorizer prototype =
                        target.getPrototypes().get(category);
                    prototype.getWeights().minusEquals(errorUpdate);
                    prototype.setBias(prototype.getBias() - errorWeight);
                }
            }
            // else - Not an error, no need to update.
        }
    }


    /**
     * Variant of a multi-category Perceptron that performs a proportional
     * weight update on all categories that are scored higher than the true
     * category such that the weights sum to 1.0 and are proportional how much
     * larger the score was for each incorrect category than the true category.
     *
     * @param <CategoryType>
     *      The type of output categories. Can be any type that has a valid
     *      equals and hashCode method.
     */
    @PublicationReference(
      title="Ultraconservative online algorithms for multiclass problems",
      author={"Koby Crammer", "Yoram Singer"},
      year=2003,
      type=PublicationType.Journal,
      publication="The Journal of Machine Learning Research",
      pages={951, 991},
      url="http://portal.acm.org/citation.cfm?id=944936")
    public static class ProportionalUpdate<CategoryType>
        extends OnlineMultiPerceptron<CategoryType>
    {

        /** The default minimum margin is {@value}. */
        public static final double DEFAULT_MIN_MARGIN = 0.001;

        /**
         * Creates a new {@code OnlineMultiPerceptron.ProportionalUpdate}.
         */
        public ProportionalUpdate()
        {
            this(DEFAULT_MIN_MARGIN);
        }

        /**
         * Creates a new {@code OnlineMultiPerceptron.ProportionalUpdate} with the
         * given minimum margin.
         *
         * @param   minMargin
         *      The minimum margin to consider an example correct.
         */
        public ProportionalUpdate(
            final double minMargin)
        {
            super(minMargin);
        }

        /**
         * Creates a new {@code OnlineMultiPerceptron.ProportionalUpdate} with the
         * given minimum margin and backing vector factory.
         *
         * @param   minMargin
         *      The minimum margin to consider an example correct.
         * @param   vectorFactory
         *      The vector factory used to create the weight vectors.
         */
        public ProportionalUpdate(
            final double minMargin,
            final VectorFactory<?> vectorFactory)
        {
            super(minMargin, vectorFactory);
        }

        @Override
        public void update(
            final LinearMultiCategorizer<CategoryType> target,
            final InputOutputPair<? extends Vectorizable, CategoryType> example)
        {
            // TODO: This shares a lot of code with the parent class update.
            // Figure out a way to combine them in a sensible way.
            // -- jdbasil (2011-01-30)
            final Vector input = example.getInput().convertToVector();
            final CategoryType actual = example.getOutput();

            final boolean knownCategory = target.getCategories().contains(actual);
            if (!knownCategory)
            {
                // This category was never seen, so create a new prototype for it.
                final Vector initialWeights = this.getVectorFactory().createVector(
                    input.getDimensionality());
                target.getPrototypes().put(actual,
                    new LinearBinaryCategorizer(initialWeights, 0.0));
            }

            // See what the predicted category is.
            final double actualScore = target.evaluateAsDouble(input, actual) - minMargin;
            final LinkedList<DefaultWeightedValue<CategoryType>> errors =
                 new LinkedList<DefaultWeightedValue<CategoryType>>();
            double differenceSum = 0.0;
            for (CategoryType category : target.getCategories())
            {
                final double score = target.evaluateAsDouble(input, category);
                double difference = score - actualScore;
                if (difference >= 0.0 && !actual.equals(category))
                {
                    errors.add(DefaultWeightedValue.create(category, difference));
                    differenceSum += difference;
                }
            }

            final boolean correct = errors.isEmpty();
            if (!correct)
            {
                // Increment the prototype for the actual category.
                final LinearBinaryCategorizer actualPrototype =
                    target.getPrototypes().get(actual);
                actualPrototype.getWeights().plusEquals(input);
                actualPrototype.setBias(actualPrototype.getBias() + 1.0);

                // Decrement the prototype for the predicted category.
                for (DefaultWeightedValue<CategoryType> category : errors)
                {
                    final LinearBinaryCategorizer prototype =
                        target.getPrototypes().get(category.getValue());
                    final double errorWeight = category.getWeight() / differenceSum;
                    prototype.getWeights().minusEquals(input.scale(errorWeight));
                    prototype.setBias(prototype.getBias() - errorWeight);
                }
            }
            // else - Not an error, no need to update.
        }

        @Override
        public void setMinMargin(
            final double minMargin)
        {
            ArgumentChecker.assertIsPositive("minMargin", minMargin);
            super.setMinMargin(minMargin);
        }

    }

}
