/*
 * File:                StatefulEvaluator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The {@code StatefulEvaluator} interface defines the functionality of an
 * {@code Evaluator} that maintains an internal state. It defines the methods
 * for manipulating the state object.
 *
 * @param  <InputType> The type of the input the evaluator can use.
 * @param  <OutputType> The type of the output the evaluator will produce.
 * @param  <StateType> State object contained in this object.
 * @author Kevin R. Dixon
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Fixed a couple of comment typos, otherwise looks fine."
)
public interface StatefulEvaluator<InputType, OutputType, StateType extends CloneableSerializable>
    extends Evaluator<InputType, OutputType>
{

    /**
     * Creates a new default state object.
     * 
     * @return A new default state object.
     */
    StateType createDefaultState();

    /**
     * Evaluates the object using the given input and current state objects,
     * returning the output. The current state may be modified by side effect.
     * 
     * @param input The input to evaluate.
     * @return output that results from the evaluation, 
     * state is modified by side effect
     */
    OutputType evaluate(
        InputType input);

    /**
     * Evaluates the object using the given input and the given state object,
     * returning the output. The given state object may be modified by side 
     * effect. The current state of the Evaluator is also set to be the given
     * state.
     * 
     * Typically, the evaluator will set the current state to the given state
     * and then call the single-parameter version of evaluate on the input.
     * 
     * @param   input The input to evaluate.
     * @param   state The state to use, which can be modified by side effect.
     *          The current state of the Evaluator will be based on this given
     *          state.
     * @return  The output of the evaluation. The state is also modified by side
     *          effect.
     */
    OutputType evaluate(
        InputType input,
        StateType state);

    /**
     * Gets the current state of the evaluator. This is a reference to the 
     * current state, which may later be modified by side-effect. If a true
     * copy of the state is needed, this returned value should be copied.
     * 
     * @return The current state.
     */
    StateType getState();

    /**
     * Sets the current state of the evaluator. Only states that are created by
     * {@code createDefaultState()} or {@code getState()} should be used for
     * proper behavior.
     * 
     * @param state The new state for the evaluator.
     */
    void setState(
        StateType state);

    /**
     * Resets the state of the evaluator to a default state.
     */
    void resetState();

}
