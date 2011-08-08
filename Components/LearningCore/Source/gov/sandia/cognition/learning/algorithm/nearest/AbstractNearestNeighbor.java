/*
 * File:                AbstractNearestNeighbor.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.DefaultDivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;

/**
 * Partial implementation of KNearestNeighbor.
 *
 * @param <InputType> Type of data upon which the KNearestNeighbor operates,
 * something like Vector, Double, or String
 * @param <OutputType> Output of the evaluator, like Matrix, Double, String
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractNearestNeighbor<InputType,OutputType>
    extends DefaultDivergenceFunctionContainer<InputType,InputType>
    implements NearestNeighbor<InputType,OutputType>
{

    /** 
     * Creates a new instance of AbstractNearestNeighbor 
     */
    public AbstractNearestNeighbor()
    {
        this( null );
    }

    /**
     * Creates a new instance of AbstractNearestNeighbor
     * @param divergenceFunction
     * The internal divergence function for the object to use.
     */
    public AbstractNearestNeighbor(
        final DivergenceFunction<? super InputType,? super InputType> divergenceFunction )
    {
        super( divergenceFunction );
    }

    public void add(
        InputOutputPair<? extends InputType, OutputType> value)
    {
        this.getData().add( value );
    }
    
}
