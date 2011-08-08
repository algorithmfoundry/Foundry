/*
 * File:                AbstractStatefulEvaluator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The {@code AbstractStatefulEvalutor} class is an abstract implementation of
 * the {@code StatefulEvalutor} interface. It provides a state object field plus
 * a getter and setter for the field plus an implementation of the method to
 * reset the state.
 *
 * @param  <InputType> Input of the Evaluator
 * @param  <OutputType> Output of the Evaluator
 * @param  <StateType> State object contain in this
 * @author Kevin R. Dixon
 * @since  2.0
 * @see    StatefulEvaluator
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Looks fine."
        ),
        @CodeReview(
            reviewer="Justin Basilico",
            date="2007-11-20",
            changesNeeded=false,
            comments="Cleaned up."
        )
    }
)
public abstract class AbstractStatefulEvaluator<InputType, OutputType, StateType extends CloneableSerializable>
    extends AbstractCloneableSerializable
    implements StatefulEvaluator<InputType, OutputType, StateType>
{

    /** Current state object. */
    private StateType state;

    /**
     * Creates a new instance of {@code AbstractStatefulEvaluator}.
     */
    public AbstractStatefulEvaluator()
    {
        this( null );
    }

    /**
     * Creates a new instance of {@code AbstractStatefulEvaluator}.
     *
     * @param initialState The initial state object.
     */
    public AbstractStatefulEvaluator(
        StateType initialState )
    {
        super();

        this.setState( initialState );
    }

    @Override
    public AbstractStatefulEvaluator<InputType, OutputType, StateType> clone()
    {
        @SuppressWarnings("unchecked")
        final AbstractStatefulEvaluator<InputType, OutputType, StateType> result =
            (AbstractStatefulEvaluator<InputType, OutputType, StateType>) super.clone();
        result.state = ObjectUtil.cloneSafe(this.state);
        return result;
    }
    
    public OutputType evaluate(
        InputType input,
        StateType state )
    {
        this.setState( state );
        return this.evaluate( input );
    }

    public StateType getState()
    {
        if (this.state == null)
        {
            this.setState( this.createDefaultState() );
        }

        return this.state;
    }

    public void setState(
        StateType state )
    {
        this.state = state;
    }

    public void resetState()
    {
        this.setState( this.createDefaultState() );
    }

}
