/*
 * File:                DelayFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.collection.FiniteCapacityBuffer;

/**
 * Delays the input and returns the input from the parameterized number of
 * samples previous.
 *
 * @param  <DataType> The type of data to delay.
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class DelayFunction<DataType>
    extends AbstractStatefulEvaluator<DataType,DataType,FiniteCapacityBuffer<DataType>>
{
    
    /**
     * Number of samples to delay the value
     */
    private int delaySamples;

    /**
     * Default constructor.
     */
    public DelayFunction()
    {
        this( 0 );
    }

    /**
     * Creates a new instance of DelayFunction
     * @param delaySamples 
     * Number of samples to delay the value
     */
    public DelayFunction( 
        int delaySamples )
    {
        super();
        this.setDelaySamples( delaySamples );
    }
    
    /**
     * Copy Constructor
     * @param other DelayFunction to copy
     */
    public DelayFunction(
        DelayFunction<DataType> other )
    {
        this( other.getDelaySamples() );
        this.setState( other.getState().clone() );
    }
    
    @Override
    public DelayFunction<DataType> clone()
    {
        return (DelayFunction<DataType>) super.clone();
    }    
    
    /**
     * Getter for delaySamples
     * @return 
     * Number of samples to delay the value
     */
    public int getDelaySamples()
    {
        return this.delaySamples;
    }

    /**
     * Setter for delaySamples
     * @param delaySamples 
     * Number of samples to delay the value
     */
    public void setDelaySamples(
        int delaySamples)
    {
        if( delaySamples < 0 )
        {
            throw new IllegalArgumentException( "Delay must be >= 0" );
        }
        this.delaySamples = delaySamples;
        this.setState(null);
    }

    public FiniteCapacityBuffer<DataType> createDefaultState()
    {
        return new FiniteCapacityBuffer<DataType>( this.getDelaySamples() + 1 );
    }

    /**
     * Returns the input from delaySamples previous evalutes
     * @param input Input to store into the buffer
     * @return input from delaySamples ago, will return oldest sample if
     * there are fewer than "delaySamples" in the buffer
     */
    public DataType evaluate(
        DataType input)
    {
        this.getState().addLast( input );
        return this.getState().iterator().next();
    }
    
}
