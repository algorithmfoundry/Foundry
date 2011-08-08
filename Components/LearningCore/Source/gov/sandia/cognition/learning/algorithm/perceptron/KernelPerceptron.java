/*
 * File:                KernelPerceptron.java
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
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * The <code>KernelPerceptron</code> class implements the kernel version of 
 * the Perceptron algorithm. That is, it replaces the inner-product used in the 
 * standard Perceptron algorithm with a kernel method. This allows the 
 * algorithm to be used with data and a kernel that would map it into a 
 * high-dimensional space but does not need to since the kernel can compute the 
 * inner-product in the high-dimensional space without actually creating the 
 * vectors for it.
 *
 * @param   <InputType> Input class of the {@code InputOutputPairs}
 * @author  Justin Basilico
 * @since   2.0
 * @see     Perceptron
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Added PublicationReference to the original article.",
        "Minor changes to javadoc.",
        "Looks fine."
    }
)
@PublicationReference(
    author={
        "Yoav Freund",
        "Robert E. Schapire"
    },
    title="Large margin classification using the perceptron algorithm",
    publication="Machine Learning",
    type=PublicationType.Journal,
    year=1999,
    notes="Volume 37, Number 3",
    pages={277,296},
    url="http://www.cs.ucsd.edu/~yfreund/papers/LargeMarginsUsingPerceptron.pdf"
)
public class KernelPerceptron<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Boolean, KernelBinaryCategorizer<InputType>>
    implements MeasurablePerformanceAlgorithm
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS =
        Perceptron.DEFAULT_MAX_ITERATIONS;

    /** The default positive margin, {@value}. */
    public static final double DEFAULT_MARGIN_POSITIVE =
        Perceptron.DEFAULT_MARGIN_POSITIVE;

    /** The default negative margin, {@value}. */
    public static final double DEFAULT_MARGIN_NEGATIVE =
        Perceptron.DEFAULT_MARGIN_NEGATIVE;

    /** The kernel to use. */
    private Kernel<? super InputType> kernel;

    /** The positive margin to enforce. */
    private double marginPositive;

    /** The negative margin to enforce. */
    private double marginNegative;

    /** The result categorizer. */
    private KernelBinaryCategorizer<InputType> result;

    /** The number of errors on the most recent iteration. */
    private int errorCount;

    /** The mapping of weight objects to non-zero weighted examples 
     *  (support vectors). */
    private LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, DefaultWeightedValue<InputType>> supportsMap;

    /**
     * Creates a new instance of KernelPerceptron.
     */
    public KernelPerceptron()
    {
        this(null);
    }

    /**
     * Creates a new KernelPerceptron with the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public KernelPerceptron(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new KernelPerceptron with the given kernel and maximum number
     * of iterations.
     *
     * @param  kernel The kernel to use.
     * @param  maxIterations The maximum number of iterations.
     */
    public KernelPerceptron(
        final Kernel<? super InputType> kernel,
        final int maxIterations)
    {
        this(kernel, maxIterations,
            DEFAULT_MARGIN_POSITIVE, DEFAULT_MARGIN_NEGATIVE);
    }

    /**
     * Creates a new KernelPerceptron with the given parameters.
     *
     * @param  kernel The kernel to use.
     * @param  maxIterations The maximum number of iterations.
     * @param  marginPositive The positive margin to enforce.
     * @param  marginNegative The negative margin to enforce.
     */
    public KernelPerceptron(
        final Kernel<? super InputType> kernel,
        final int maxIterations,
        final double marginPositive,
        final double marginNegative)
    {
        super(maxIterations);

        this.setKernel(kernel);
        this.setMarginPositive(marginPositive);
        this.setMarginNegative(marginNegative);

        this.setResult(null);
        this.setErrorCount(0);
        this.setSupportsMap(null);
    }

    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null)
        {
            // Error: No data to learn on.
            return false;
        }

        // Count the number of valid examples.
        int validCount = 0;
        for (InputOutputPair<? extends InputType, ? extends Boolean> example : this.getData())
        {
            if (example != null)
            {
                validCount++;
            }
        }

        if (validCount <= 0)
        {
            // Nothing to perform learning on.
            return false;
        }

        // Set up the learning variables.
        this.setErrorCount(validCount);
        this.setSupportsMap(new LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, DefaultWeightedValue<InputType>>());
        this.setResult(new KernelBinaryCategorizer<InputType>(
            this.getKernel(), this.getSupportsMap().values(), 0.0));

        return true;
    }

    protected boolean step()
    {
        // Reset the number of errors for the new iteration.
        this.setErrorCount(0);

        // Loop over all the training instances.
        for (InputOutputPair<? extends InputType, ? extends Boolean> example : this.getData())
        {
            if (example == null)
            {
                continue;
            }

            // Compute the predicted classification and get the actual
            // classification.
            final InputType input = example.getInput();
            final boolean actual = example.getOutput();
            final double prediction = this.result.evaluateAsDouble(input);

            if ((actual && prediction <= +this.marginPositive) || (!actual && prediction >= -this.marginNegative))
            {
                // The classification was incorrect so we need to update
                // the perceptron.
                this.setErrorCount(this.getErrorCount() + 1);

                // We are going to update the weight for this example and the
                // global bias.
                double weight = 0.0;
                double bias = this.result.getBias();

                // If the weight exists get it from the support for the 
                // example.
                DefaultWeightedValue<InputType> support =
                    this.supportsMap.get(example);
                if (support != null)
                {
                    weight = support.getWeight();
                }

                if (actual)
                {
                    // Update for a positive example so add to the
                    // weights and the bias.
                    weight += 1.0;
                    bias += 1.0;
                }
                else
                {
                    // Update for a negative example so subtract from
                    // the weights and the bias.
                    weight -= 1.0;
                    bias -= 1.0;
                }

                if (support == null)
                {
                    // Add a support for this example.
                    support = new DefaultWeightedValue<InputType>(input, weight);
                    this.supportsMap.put(example, support);
                }
                else if (weight == 0.0)
                {
                    // This example is no longer a support.
                    this.supportsMap.remove(example);
                }
                else
                {
                    // Update the weight for the support.
                    support.setWeight(weight);
                }

                // Update the bias.
                this.result.setBias(bias);
            }
        // else - The classification was correct, no need to update.
        }

        // Keep going while the error count is positive.
        return this.getErrorCount() > 0;
    }

    protected void cleanupAlgorithm()
    {
        if (this.getSupportsMap() != null)
        {
            // Make the result object have a more efficient backing collection
            // at the end.
            this.getResult().setExamples(
                new ArrayList<WeightedValue<InputType>>(
                this.getSupportsMap().values()));

            this.setSupportsMap(null);
        }
    }

    /**
     * Gets the kernel to use.
     *
     * @return The kernel to use.
     */
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the kernel to use.
     *
     * @param  kernel The kernel to use.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
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

    public KernelBinaryCategorizer<InputType> getResult()
    {
        return this.result;
    }

    /**
     * Sets the object currently being result.
     *
     * @param  result The object currently being result.
     */
    protected void setResult(
        final KernelBinaryCategorizer<InputType> result)
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
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @return The mapping of examples to weight objects.
     */
    protected LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, DefaultWeightedValue<InputType>> getSupportsMap()
    {
        return supportsMap;
    }

    /**
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @param  supportsMap The mapping of examples to weight objects.
     */
    protected void setSupportsMap(
        final LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, DefaultWeightedValue<InputType>> supportsMap)
    {
        this.supportsMap = supportsMap;
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
