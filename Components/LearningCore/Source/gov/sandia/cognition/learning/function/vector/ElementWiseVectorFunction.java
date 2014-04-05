/*
 * File:                ElementWiseVectorFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A VectorFunction that operates on each element of the Vector indepenently
 * of all others.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class ElementWiseVectorFunction
    extends AbstractCloneableSerializable
    implements VectorFunction
{

    /**
     * Underlying scalar function to apply to each Vector element
     */
    private UnivariateScalarFunction scalarFunction;

    /**
     * Creates a new {@code ElementWiseVectorFunction} with a default linear
     * scalar function.
     */
    public ElementWiseVectorFunction()
    {
        this(new LinearFunction());
    }

    /**
     * Creates a new instance of ElementWiseVectorFunction
     * @param scalarFunction 
     * Underlying scalar function to apply to each Vector element
     */
    public ElementWiseVectorFunction(
        UnivariateScalarFunction scalarFunction )
    {
        super();

        this.setScalarFunction( scalarFunction );
    }

    /**
     * Copy Constructor
     * @param other 
     * ElementWiseVectorFunction to copy
     */
    public ElementWiseVectorFunction(
        ElementWiseVectorFunction other )
    {
        this(ObjectUtil.cloneSmart(other.getScalarFunction()));
    }

    @Override
    public ElementWiseVectorFunction clone()
    {
        ElementWiseVectorFunction clone = (ElementWiseVectorFunction) super.clone();
        clone.setScalarFunction(ObjectUtil.cloneSmart(this.getScalarFunction()));
        return clone;
    }

    /**
     * Getter for scalarFunction
     * @return 
     * Underlying scalar function to apply to each Vector element
     */
    public UnivariateScalarFunction getScalarFunction()
    {
        return this.scalarFunction;
    }

    /**
     * Setter for scalarFunction
     * @param scalarFunction 
     * Underlying scalar function to apply to each Vector element
     */
    public void setScalarFunction(
        UnivariateScalarFunction scalarFunction )
    {
        this.scalarFunction = scalarFunction;
    }

    /**
     * Applies the scalar function to each element of the input Vector
     * independently of all others, returning a Vector of equal dimension as
     * the input
     * @param input Input Vector to consider
     * @return 
     * Vector of identical dimension as input, having scalarFunction applied
     * to each element of the input
     */
    public Vector evaluate(
        Vector input )
    {
        return evaluate( input, this.scalarFunction );
    }

    /**
     * Applies the scalar function to each element of the input Vector
     * independently of all others, returning a Vector of equal dimension as
     * the input
     * @param input Input Vector to consider
     * @param function
     * Scalar function to apply to each element
     * @return
     * Vector of identical dimension as input, having scalarFunction applied
     * to each element of the input
     */
    public static Vector evaluate(
        Vector input,
        UnivariateScalarFunction function )
    {
        int M = input.getDimensionality();
        double[] outputs = new double[M];
        for (int i = 0; i < M; i++)
        {
            outputs[i] = function.evaluate( input.getElement( i ) );
        }

        return VectorFactory.getDefault().copyArray(outputs );

    }

}
