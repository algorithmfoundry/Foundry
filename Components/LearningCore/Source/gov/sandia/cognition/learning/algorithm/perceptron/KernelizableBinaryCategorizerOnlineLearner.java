/*
 * File:                KernelizableBinaryCategorizerOnlineLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.SupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Interface for an online learner of a linear binary categorizer that can also
 * be used with a kernel function. Thus, there are a second set of methods
 * that are similar to the ones for the normal learner that are specifically
 * for kernel learning. The companion to this class is
 * {@code LinearizableBinaryCategorizerOnlineLearner}.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 * @see     LinearizableBinaryCategorizerOnlineLearner
 */
public interface KernelizableBinaryCategorizerOnlineLearner
    extends SupervisedBatchAndIncrementalLearner<Vectorizable, Boolean, LinearBinaryCategorizer>
{

    /**
     * Creates the initial learned object with a given kernel.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   kernel
     *      The kernel function to use.
     * @return
     *      A new, empty learned object.
     */
    public <InputType> DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject(
        final Kernel<? super InputType> kernel);

    /**
     * Performs a kernel-based incremental update step on the given object
     * using the given supervised data.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   target
     *      The target object to update.
     * @param   data
     *      The supervised training data.
     */
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final Iterable<? extends InputOutputPair<? extends InputType, Boolean>> data);
    
    /**
     * Performs a kernel-based incremental update step on the given object
     * using the given supervised data.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   target
     *      The target object to update.
     * @param   data
     *      The supervised training data.
     */
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputOutputPair<? extends InputType, Boolean> data);
    
    /**
     * Performs a kernel-based incremental update step on the given object
     * using the given supervised data.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   target
     *      The target object to update.
     * @param   input
     *      The supervised input value.
     * @param   output
     *      The supervised output value (label).
     */
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final Boolean output);

    /**
     * Performs a kernel-based incremental update step on the given object
     * using the given supervised data.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   target
     *      The target object to update.
     * @param   input
     *      The supervised input value.
     * @param   output
     *      The supervised output value (label).
     */
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean output);

    /**
     * Run this algorithm on a batch of data using the given kernel function.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   kernel
     *      The kernel function to use.
     * @param   data
     *      The supervised training data.
     * @return
     *      A new object trained on the given data.
     */
    public <InputType> DefaultKernelBinaryCategorizer<InputType> learn(
        final Kernel<? super InputType> kernel,
        final Iterable<? extends InputOutputPair<? extends InputType, Boolean>> data);

    /**
     * Creates a new kernel-based learner using the standard learning interfaces
     * based on this learner and its parameters.
     *
     * @param   <InputType>
     *      The input type for supervised learning. Will be passed to the
     *      kernel function.
     * @param   kernel
     *      The kernel function to use.
     * @return
     *      A kernel-based version of this learning algorithm.
     */
    public <InputType> SupervisedBatchAndIncrementalLearner<InputType, Boolean, DefaultKernelBinaryCategorizer<InputType>> createKernelLearner(
        final Kernel<? super InputType> kernel);
    
}
