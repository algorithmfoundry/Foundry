/*
 * File:                LinearizableBinaryCategorizerOnlineLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.SupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedIncrementalLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Interface for an online learner of a kernel binary categorizer that can also
 * be used for learning a linear categorizer. Thus, there are a second set of
 * methods that are similar to the one for the normal learner that are
 * specifically for linear learning. The companion to this class is
 * {@code KernelizableBinaryCategorizerOnlineLearner}.
 * 
 * @param   <InputType>
 *      The input type that kernel learning happens on.
 * @author  Justin Basilico
 * @since   3.3.0
 * @see     KernelizableBinaryCategorizerOnlineLearner
 */
public interface LinearizableBinaryCategorizerOnlineLearner<InputType>
    extends SupervisedBatchAndIncrementalLearner<InputType, Boolean, DefaultKernelBinaryCategorizer<InputType>>
{

    /**
     * Creates the initial learned object.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     * @return
     *      A new linear binary categorizer.
     */
    public LinearBinaryCategorizer createInitialLinearLearnedObject(
        final VectorFactory<?> vectorFactory);

    /**
     * Performs a linear incremental update step on the given object using the
     * given supervised data.
     *
     * @param   target
     *      The target object to update.
     * @param   data
     *      The supervised training data
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public void update(
        final LinearBinaryCategorizer target,
        final Iterable<? extends InputOutputPair<? extends Vectorizable, Boolean>> data,
        final VectorFactory<?> vectorFactory);

    /**
     * Performs a linear incremental update step on the given object using the
     * given supervised data.
     *
     * @param   target
     *      The target object to update.
     * @param   data
     *      The supervised training data
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public void update(
        final LinearBinaryCategorizer target,
        final InputOutputPair<? extends Vectorizable, Boolean> data,
        final VectorFactory<?> vectorFactory);

    /**
     * Performs a linear incremental update step on the given object using the
     * given supervised data.
     *
     * @param   target
     *      The target object to update.
     * @param   input
     *      The supervised input value.
     * @param   output
     *      The supervised output value (label).
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public void update(
        final LinearBinaryCategorizer target,
        final Vectorizable input,
        final Boolean output,
        final VectorFactory<?> vectorFactory);
    
    /**
     * Performs a linear incremental update step on the given object using the
     * given supervised data.
     *
     * @param   target
     *      The target object to update.
     * @param   input
     *      The supervised input value.
     * @param   output
     *      The supervised output value (label).
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public void update(
        final LinearBinaryCategorizer target,
        final Vectorizable input,
        final boolean output,
        final VectorFactory<?> vectorFactory);

    /**
     * Creates a new linear learner using the standard learning interfaces
     * based on this learner and its parameters.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     * @return
     *      A linear version of this learning algorithm.
     */
    public SupervisedIncrementalLearner<Vectorizable, Boolean, LinearBinaryCategorizer> createLinearLearner(
        final VectorFactory<?> vectorFactory);

}
