/*
 * File:                AbstractKernelizableBinaryCategorizerOnlineLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.perceptron.kernel.KernelBinaryCategorizerOnlineLearnerAdapter;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * An abstract implementation of the {@code KernelizableBinaryCategorizerOnlineLearner}
 * interface. It handles a lot of the convenience methods to string them
 * together, making it necessary for sub-classes to only implement one
 * update method.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public abstract class AbstractKernelizableBinaryCategorizerOnlineLearner
    extends AbstractOnlineLinearBinaryCategorizerLearner
    implements KernelizableBinaryCategorizerOnlineLearner
{

    /**
     * Creates a new {@code AbstractKernelizableBinaryCategorizerOnlineLearner}.
     */
    public AbstractKernelizableBinaryCategorizerOnlineLearner()
    {
        super();
    }

    /**
     * Creates a new {@code AbstractKernelizableBinaryCategorizerOnlineLearner}
     * with the given vector factory.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractKernelizableBinaryCategorizerOnlineLearner(
        final VectorFactory<?> vectorFactory)
    {
        super(vectorFactory);
    }

    @Override
    public <InputType> DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject(
        final Kernel<? super InputType> kernel)
    {
        return new DefaultKernelBinaryCategorizer<InputType>(
            kernel);
    }

    @Override
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final Iterable<? extends InputOutputPair<? extends InputType, Boolean>> data)
    {
        for (InputOutputPair<? extends InputType, Boolean> example : data)
        {
            this.update(target, example);
        }
    }

    @Override
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputOutputPair<? extends InputType, Boolean> data)
    {
        this.update(target, data.getInput(), data.getOutput());
    }

    @Override
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final Boolean output)
    {
        this.update(target, input, (boolean) output);
    }

    @Override
    public <InputType> DefaultKernelBinaryCategorizer<InputType> learn(
        final Kernel<? super InputType> kernel,
        final Iterable<? extends InputOutputPair<? extends InputType, Boolean>> data)
    {
        // Create the object.
        final DefaultKernelBinaryCategorizer<InputType>
            result = this.createInitialLearnedObject(kernel);

        // Update it.
        this.update(result, data);

        // Return the result.
        return result;
    }

    @Override
    public <InputType> SupervisedBatchAndIncrementalLearner<InputType, Boolean, DefaultKernelBinaryCategorizer<InputType>> createKernelLearner(
        final Kernel<? super InputType> kernel)
    {
        // Create the kernel wrapper for the learner.
        return new KernelBinaryCategorizerOnlineLearnerAdapter<InputType>(
            kernel, this);
    }

}