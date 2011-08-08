/*
 * File:                Perceptron.java
 * Authors:             Justin Basilico
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

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;

/**
 * The <code>Perceptron</code> class implements the standard Perceptron learning 
 * algorithm that learns a binary classifier based on vector input. This 
 * implementation also allows for margins to be defined in learning in order to 
 * find a hyperplane.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Added PublicationReference to Wikiepedia article.",
        "Minor changes to javadoc.",
        "Looks fine."
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Perceptron Learning algorithm",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Perceptron#Learning_algorithm"
)
public class Perceptron
    extends AbstractAnytimeSupervisedBatchLearner<Vectorizable, Boolean, LinearBinaryCategorizer>
    implements MeasurablePerformanceAlgorithm, CloneableSerializable
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The default positive margin, {@value}. */
    public static final double DEFAULT_MARGIN_POSITIVE = 0.0;

    /** The default negative margin, {@value}. */
    public static final double DEFAULT_MARGIN_NEGATIVE = 0.0;

    /** The positive margin to enforce. */
    private double marginPositive;

    /** The negative margin to enforce. */
    private double marginNegative;

    /** The VectorFactory to use to create vectors. */
    private VectorFactory vectorFactory;

    /** The result categorizer. */
    private LinearBinaryCategorizer result;

    /** The number of errors on the most recent iteration. */
    private int errorCount;

    /**
     * Creates a new instance of Perceptron.
     */
    public Perceptron()
    {
        this(DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of Perceptron with the given maximum number of
     * iterations.
     *
     * @param  maxIterations The maximum number of iterations.
     */
    public Perceptron(
        final int maxIterations)
    {
        this(maxIterations, DEFAULT_MARGIN_POSITIVE, DEFAULT_MARGIN_NEGATIVE);
    }

    /**
     * Creates a new instance of Perceptron with the given parameters
     *
     * @param  maxIterations The maximum number of iterations.
     * @param  marginPositive The positive margin to enforce.
     * @param  marginNegative The negative margin to enforce.
     */
    public Perceptron(
        final int maxIterations,
        final double marginPositive,
        final double marginNegative)
    {
        this(maxIterations, marginPositive, marginNegative,
            VectorFactory.getDefault());
    }

    /**
     * Creates a new instance of Perceptron with the given parameters
     *
     * @param  maxIterations The maximum number of iterations.
     * @param  marginPositive The positive margin to enforce.
     * @param  marginNegative The negative margin to enforce.
     * @param  vectorFactory The VectorFactory to use to create the weight 
     *         vector.
     */
    public Perceptron(
        final int maxIterations,
        final double marginPositive,
        final double marginNegative,
        final VectorFactory vectorFactory)
    {
        super(maxIterations);

        this.setMarginPositive(marginPositive);
        this.setMarginNegative(marginNegative);
        this.setVectorFactory(vectorFactory);
    }
    
    @Override
    public Perceptron clone()
    {
        final Perceptron clone = (Perceptron) super.clone();
        clone.result = null;
        clone.errorCount = 0;
        return clone;
    }

    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null)
        {
            // Error: No data to learn on.
            return false;
        }

        // Computer the dimensionality of the data and ensure it is correct.
        int dimensionality = -1;
        Vector first = null;

        for (InputOutputPair<? extends Vectorizable, ? extends Boolean> example : this.getData())
        {
            if (data == null)
            {
                continue;
            }

            final Vector input = example.getInput().convertToVector();

            if (first == null)
            {
                first = input;
                dimensionality = input.getDimensionality();
            }
            else if (dimensionality != input.getDimensionality())
            {
                throw new DimensionalityMismatchException(dimensionality,
                    input.getDimensionality());
            }
        }

        if (dimensionality < 0)
        {
            // There was no data.
            return false;
        }

        // Initialize the result object.
        this.setResult(new LinearBinaryCategorizer(
            this.getVectorFactory().createVector(dimensionality),
            0.0));

        return true;
    }

    protected boolean step()
    {
        // Reset the number of errors for the new iteration.
        this.setErrorCount(0);

        // Loop over all the training instances.
        for (InputOutputPair<? extends Vectorizable, ? extends Boolean> example : this.getData())
        {
            if (example == null)
            {
                continue;
            }

            // Compute the predicted classification and get the actual
            // classification.
            final Vector input = example.getInput().convertToVector();
            final boolean actual = example.getOutput();
            final double prediction = this.result.evaluateAsDouble(input);

            if ((actual && prediction <= this.marginPositive) || (!actual && prediction >= -this.marginNegative))
            {
                // The classification was incorrect so we need to update
                // the perceptron.
                this.setErrorCount(this.getErrorCount() + 1);

                final Vector weights = this.result.getWeights();
                double bias = this.result.getBias();

                if (actual)
                {
                    // Update for a positive example so add to the
                    // weights and the bias.
                    weights.plusEquals(input);
                    bias += 1.0;
                }
                else
                {
                    // Update for a negative example so subtract from
                    // the weights and the bias.
                    weights.minusEquals(input);
                    bias -= 1.0;
                }

                // The weights are updated by side-effect.
                // Update the bias directly.
                this.result.setBias(bias);
            }
        // else - The classification was correct, no need to update.
        }

        // Keep going while the error count is positive.
        return this.getErrorCount() > 0;
    }

    protected void cleanupAlgorithm()
    {
        // Nothing to clean up.
    }

    /**
     * Sets both the positive and negative margin to the same value.
     *
     * @param  margin The new value for both the positive and negative margins.
     */
    public void setMargin(
        final double margin)
    {
        this.setMarginPositive(margin);
        this.setMarginNegative(margin);
    }

    /**
     * Gets the positive margin that is enforced.
     *
     * @return The positive margin that is enforced.
     */
    public double getMarginPositive()
    {
        return this.marginPositive;
    }

    /**
     * Sets the positive margin that is enforced.
     *
     * @param  marginPositive The positive margin that is enforced.
     */
    public void setMarginPositive(
        final double marginPositive)
    {
        this.marginPositive = marginPositive;
    }

    /**
     * Gets the negative margin that is enforced.
     *
     * @return The negative margin that is enforced.
     */
    public double getMarginNegative()
    {
        return this.marginNegative;
    }

    /**
     * Sets the negative margin that is enforced.
     *
     * @param  marginNegative The negative margin that is enforced.
     */
    public void setMarginNegative(
        final double marginNegative)
    {
        this.marginNegative = marginNegative;
    }

    /**
     * Gets the VectorFactory used to create the weight vector.
     *
     * @return The VectorFactory used to create the weight vector.
     */
    public VectorFactory getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the VectorFactory used to create the weight vector.
     *
     * @param  vectorFactory The VectorFactory used to create the weight vector.
     */
    public void setVectorFactory(
        final VectorFactory vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public LinearBinaryCategorizer getResult()
    {
        return this.result;
    }

    /**
     * Sets the object currently being result.
     *
     * @param  result The object currently being result.
     */
    protected void setResult(
        final LinearBinaryCategorizer result)
    {
        this.result = result;
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
    public NamedValue<Integer> getPerformance()
    {
        return new DefaultNamedValue<Integer>("error count", this.getErrorCount());
    }
}
