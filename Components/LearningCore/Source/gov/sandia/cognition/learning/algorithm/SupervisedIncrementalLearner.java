/*
 * File:                SupervisedIncrementalLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 31, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * Interface for supervised incremental learning algorithms. It contains the
 * typical generic definition conventions for an incremental, supervised
 * learning algorithm. It also contains a convenience method for providing
 * a new input/output example without packing it into an 
 * {@code InputOutputPair}.
 *
 * @param   <InputType> The type of input data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also takes this as the input parameter.
 * @param   <OutputType> The type of output data in the input-output pair that
 *      the learner can learn from. The {@code Evaluator} learned from the
 *      algorithm also produces this as its output.
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code LinearBinaryCategorizer}.
 * @author  Justin Basilico
 * @since   3.2.0
 */
public interface SupervisedIncrementalLearner<InputType, OutputType, ResultType extends Evaluator<? super InputType, ? extends OutputType>>
    extends IncrementalLearner<InputOutputPair<? extends InputType, OutputType>, ResultType>
{

    /**
     * The {@code update} method updates an object of {@code ResultType} using
     * the given a new supervised input-output pair, using some form of
     * "learning" algorithm.
     *
     * @param   target
     *      The object to update.
     * @param   input
     *      The supervised input to learn from.
     * @param   output
     *      The supervised output to learn from.
     */
    public void update(
        final ResultType target,
        final InputType input,
        final OutputType output);
    
}
