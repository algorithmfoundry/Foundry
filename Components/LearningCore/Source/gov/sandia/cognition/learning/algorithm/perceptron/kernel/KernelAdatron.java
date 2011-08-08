/*
 * File:                KernelAdatron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * The {@code KernelAdatron} class implements an online version of the Support
 * Vector Machine learning algorithm. It is based on an extension of the 
 * Perceptron algorithm.
 *
 * @param   <InputType> Input type of the {@code InputOutputPairs}
 * @author  Justin Basilico
 * @since   2.0
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
        "Thilo-Thomas Friess",
        "Nello Cristianini",
        "Colin Campbell"
    },
    title="The Kernel-Adatron Algorithm: A Fast and Simple Learning Procedure for Support Vector Machines",
    type=PublicationType.Conference,
    publication="Proceedings of the Fifteenth International Conference on Machine Learning",
    year=1998,
    pages={188,196}
)
public class KernelAdatron<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Boolean, KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>>>
    implements MeasurablePerformanceAlgorithm
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The kernel to use. */
    private Kernel<? super InputType> kernel;

    /** The result categorizer. */
    private KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> result;

    /** The number of errors on the most recent iteration. */
    private int errorCount;

    /** The mapping of weight objects to non-zero weighted examples 
     *  (support vectors). */
    private LinkedHashMap<InputOutputPair<? extends InputType, Boolean>, DefaultWeightedValue<InputType>> supportsMap;

    /**
     * Creates a new instance of KernelAdatron.
     */
    public KernelAdatron()
    {
        this(null);
    }

    /**
     * Creates a new KernelAdatron with the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public KernelAdatron(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new KernelAdatron with the given kernel and maximum number
     * of iterations.
     *
     * @param  kernel The kernel to use.
     * @param  maxIterations The maximum number of iterations.
     */
    public KernelAdatron(
        final Kernel<? super InputType> kernel,
        final int maxIterations)
    {
        super(maxIterations);

        this.setKernel(kernel);

        this.setLearned(null);
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
        for (InputOutputPair<? extends InputType, Boolean> example : this.getData())
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
        this.setSupportsMap(new LinkedHashMap<InputOutputPair<? extends InputType, Boolean>, DefaultWeightedValue<InputType>>());
        this.setLearned(new DefaultKernelBinaryCategorizer<InputType>(
            this.getKernel(), this.getSupportsMap().values(), 0.0));

        return true;
    }

    protected boolean step()
    {
// TODO: The current stopping criteria may have problems with numerical
// instability. An additional stopping criteria should be used instead. One
// possibility would be some epsilon value applied either to a single change
// or to the total change. - Justin
        // Reset the number of errors for the new iteration.
        this.setErrorCount(0);

        // Loop over all the training instances.
        for (InputOutputPair<? extends InputType, Boolean> example : this.getData())
        {
            if (example == null)
            {
                continue;
            }

            // Compute the predicted classification and get the actual
            // classification.
            final InputType input = example.getInput();
            final boolean actual = example.getOutput();
            final double actualDouble = actual ? +1.0 : -1.0;
            final double prediction = this.result.evaluateAsDouble(input);

            // alpha_i = alpha_i + (1 - y_i sum alpha_j y_j k(x_j, x_i)) / k(x_i, x_i)
            // if alpha_i < 0 then alpha_i = 0;
            DefaultWeightedValue<InputType> support = this.supportsMap.get(example);
            final double oldWeight = support == null ? 0.0 : support.getWeight();
            final double oldAlpha = actualDouble * oldWeight;
            double alpha = oldAlpha + (1.0 - actualDouble * prediction) / this.kernel.evaluate(input, input);
            if (alpha < 0.0)
            {
                alpha = 0.0;
            }

            final double newWeight = actualDouble * alpha;
            final double difference = newWeight - oldWeight;
            if (difference != 0.0)
            {
                // We need to change the kernel classifier.
                this.setErrorCount(this.getErrorCount() + 1);

                // We are going to update the weight for this example and the
                // global bias.
                final double oldBias = this.result.getBias();
                final double newBias = oldBias + difference;

                if (support == null)
                {
                    // Add a support for this example.
                    support = new DefaultWeightedValue<InputType>(input, newWeight);
                    this.supportsMap.put(example, support);
                }
                else if (newWeight == 0.0)
                {
                    // This example is no longer a support.
                    this.supportsMap.remove(example);
                }
                else
                {
                    // Update the weight for the support.
                    support.setWeight(newWeight);
                }

                // Update the bias.
                this.result.setBias(newBias);
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
                new ArrayList<DefaultWeightedValue<InputType>>(
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

    public KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> getResult()
    {
        return this.result;
    }

    /**
     * Sets the object currently being result.
     *
     * @param  result The object currently being result.
     */
    protected void setLearned(
        final KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> result)
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
    protected LinkedHashMap<InputOutputPair<? extends InputType, Boolean>, DefaultWeightedValue<InputType>> getSupportsMap()
    {
        return this.supportsMap;
    }

    /**
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @param  supportsMap The mapping of examples to weight objects.
     */
    protected void setSupportsMap(
        final LinkedHashMap<InputOutputPair<? extends InputType, Boolean>, DefaultWeightedValue<InputType>> supportsMap)
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
