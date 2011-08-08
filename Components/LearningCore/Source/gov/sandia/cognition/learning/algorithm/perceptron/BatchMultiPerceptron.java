/*
 * File:                BatchMultiPerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearMultiCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Set;

/**
 * Implements a multi-class version of the standard batch Perceptron learning
 * algorithm. It learns over labeled examples that have a vector input and an
 * arbitrary output label. This version keeps a separate Perceptron as the
 * prototype for each category. When it makes an error, it subtracts the input
 * from the incorrect prototype and adds it to the correct prototype.
 * This implementation also allows a margin to be enforced, which
 * in the multi-class case means that the output value for the actual class
 * must be at least the given margin from the next highest class.
 *
 * @param <CategoryType>
 *      The type of output categories. Can be any type that has a valid
 *      equals and hashCode method.
 * @author  Justin Basilico
 * @since   3.1.2
 * @see     Perceptron
 * @see     OnlinePerceptron
 */
@PublicationReference(
    title="Ultraconservative Online Algorithms for Multiclass Problems",
    author={"Koby Crammer", "Yoram Singer"},
    year=2003,
    type=PublicationType.Journal,
    publication="Journal of Machine Learning Research",
    pages={951, 991},
    url="http://portal.acm.org/citation.cfm?id=944936")
public class BatchMultiPerceptron<CategoryType>
    extends AbstractAnytimeSupervisedBatchLearner<Vectorizable, CategoryType, LinearMultiCategorizer<CategoryType>>
    implements MeasurablePerformanceAlgorithm, VectorFactoryContainer
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The default minimum margin is {@value}. */
    public static final double DEFAULT_MIN_MARGIN = 0.0;

    /** The minimum margin to enforce. Must be non-negative. */
    protected double minMargin;

    /** The factory to create weight vectors. */
    protected VectorFactory<?> vectorFactory;

    /** The linear categorizer created by the algorithm. */
    protected transient LinearMultiCategorizer<CategoryType> result;

    /** The number of errors on the most recent iteration. */
    protected transient int errorCount;

    /**
     * Creates a new {@code BatchMultiPerceptron} with default parameters.
     */
    public BatchMultiPerceptron()
    {
        this(DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new {@code BatchMultiPerceptron} with the given maximum number
     * of iterations and a default margin of 0.0.
     *
     * @param   maxIterations
     *      The maximum number of iterations. Must be positive.
     */
    public BatchMultiPerceptron(
        final int maxIterations)
    {
        this(maxIterations, DEFAULT_MIN_MARGIN);
    }

    /**
     * Creates a new {@code BatchMultiPerceptron} with the given maximum
     * number of iterations and margin to enforce.
     *
     * @param   maxIterations
     *      The maximum number of iterations. Must be positive.
     * @param   minMargin
     *      The minimum margin to enforce. Must be non-negative.
     */
    public BatchMultiPerceptron(
        final int maxIterations,
        final double minMargin)
    {
        this(maxIterations, minMargin, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code BatchMultiPerceptron} with the given parameters.
     *
     * @param   maxIterations
     *      The maximum number of iterations. Must be positive.
     * @param   minMargin
     *      The minimum margin to enforce. Must be non-negative.
     * @param   vectorFactory
     *      The factory to use to create weight vectors.
     */
    public BatchMultiPerceptron(
        final int maxIterations,
        final double minMargin,
        final VectorFactory<?> vectorFactory)
    {
        super(maxIterations);

        this.setMinMargin(minMargin);
        this.setVectorFactory(vectorFactory);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        if (CollectionUtil.isEmpty(this.getData()))
        {
            // No data to learn from.
            return false;
        }

        // Get the dimensionality of the data.
        final int dimensionality = DatasetUtil.getInputDimensionality(
            this.getData());

        // Create the categorizer we will learn and create the prototypes for
        // each category.
        this.result = new LinearMultiCategorizer<CategoryType>();
        final Set<CategoryType> categories = DatasetUtil.findUniqueOutputs(
            this.getData());
        for (CategoryType category : categories)
        {
            final LinearBinaryCategorizer prototype = 
                new LinearBinaryCategorizer(
                this.getVectorFactory().createVector(dimensionality), 0.0);
            this.result.getPrototypes().put(category, prototype);
        }

        // The algorithm is now initialized.
        return true;
    }

    @Override
    protected boolean step()
    {
        // Reset the number of errors for the new iteration.
        this.setErrorCount(0);

        // Loop over all the training instances.
        for (InputOutputPair<? extends Vectorizable, CategoryType> example
            : this.getData())
        {
            if (example == null)
            {
                // Ignore null examples.
                continue;
            }

            // Get the input as a Vector and the actual category.
            final Vector input = example.getInput().convertToVector();
            final CategoryType actual = example.getOutput();

            // See what the predicted category is.
            CategoryType predicted = null;
            double predictedScore = Double.NEGATIVE_INFINITY;
            for (CategoryType category : this.result.getCategories())
            {
                double score = this.result.evaluateAsDouble(input, category);
                if (this.minMargin != 0.0 && actual.equals(category))
                {
                    // Enforce a margin on the correct category.
                    score -= this.minMargin;
                }

                if (score > predictedScore)
                {
                    // This is the predicted category.
                    predicted = category;
                    predictedScore = score;
                }
            }

            // See if the algorithm was correct or not.
            final boolean correct = ObjectUtil.equalsSafe(actual, predicted);
            if (!correct)
            {
                // The classification was incorrect so we need to update.
                this.setErrorCount(this.getErrorCount() + 1);

                // Increment the prototype for the actual category.
                final LinearBinaryCategorizer actualPrototype =
                    this.result.getPrototypes().get(actual);
                actualPrototype.getWeights().plusEquals(input);
                actualPrototype.setBias(actualPrototype.getBias() + 1.0);

                // Decrement the prototype for the predicted category.
                final LinearBinaryCategorizer predictedPrototype =
                    this.result.getPrototypes().get(predicted);
                predictedPrototype.getWeights().minusEquals(input);
                predictedPrototype.setBias(predictedPrototype.getBias() - 1.0);
            }
            // else - Not an error, no need to update.
        }

        // Keep going while the error count is positive.
        return this.getErrorCount() > 0;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        // Nothing to clean up.
    }

    @Override
    public LinearMultiCategorizer<CategoryType> getResult()
    {
        return this.result;
    }

    /**
     * Sets the result of the algorithm.
     *
     * @param   result
     *      The result of the algorithm.
     */
    protected void setResult(
        final LinearMultiCategorizer<CategoryType> result)
    {
        this.result = result;
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
     * Gets the error count of the most recent iteration.
     *
     * @return The current error count.
     */
    public int getErrorCount()
    {
        return this.errorCount;
    }

    /**
     * Sets the error count of the most recent iteration.
     *
     * @param  errorCount The current error count.
     */
    protected void setErrorCount(
        final int errorCount)
    {
        this.errorCount = errorCount;
    }

    /**
     * Gets the performance, which is the error count on the last iteration.
     *
     * @return The performance of the algorithm.
     */
    @Override
    public NamedValue<Integer> getPerformance()
    {
        return new DefaultNamedValue<Integer>("error count",
            this.getErrorCount());
    }
    
}
