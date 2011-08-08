/*
 * File:                LinearCombinationFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;

/**
 * A function whose output is a weighted linear combination of (potentially)
 * nonlinear basis function.
 * The output is produced as: y = a0*f0(x) + a1*f1(x) + ... + an*fn(x),
 * where "y" is the output, "x" is the given input,
 * "ai" is the ith coefficient and "fi" is the ith basis function
 *
 *
 * @param <InputType> Input class of the Evaluator basis functions
 * @param <OutputType> Output class of the Evaluator basis functions
 * @author Kevin R. Dixon
 * @since  2.0
 * @see    gov.sandia.cognition.learning.algorithm.ensemble.WeightedBinaryEnsemble
 */
public abstract class LinearCombinationFunction<InputType, OutputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType, OutputType>,
    Vectorizable
{

    /**
     * Collection of basis functions to combine to produce the output
     */
    private ArrayList<? extends Evaluator<? super InputType, ? extends OutputType>> basisFunctions;

    /**
     * Coefficients for the basisFunctions
     */
    private Vector coefficients;

    /**
     * Creates a new instance of LinearCombinationFunction
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     * @param coefficients
     * Coefficients for the basisFunctions
     */
    public LinearCombinationFunction(
        ArrayList<? extends Evaluator<? super InputType, ? extends OutputType>> basisFunctions,
        Vector coefficients )
    {
        this.setBasisFunctions( basisFunctions );
        this.setCoefficients( coefficients );
    }

    @Override
    public LinearCombinationFunction<InputType, OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        LinearCombinationFunction<InputType, OutputType> clone =
            (LinearCombinationFunction<InputType, OutputType>) super.clone();
        clone.setBasisFunctions( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getBasisFunctions() ) );
        clone.setCoefficients( ObjectUtil.cloneSafe(this.getCoefficients()) );
        return clone;
    }

    /**
     * Getter for coefficients
     * @return
     * Coefficients for the basisFunctions
     */
    public Vector getCoefficients()
    {
        return this.coefficients;
    }

    /**
     * Setter for coefficients
     * @param coefficients
     * Coefficients for the basisFunctions
     */
    public void setCoefficients(
        Vector coefficients )
    {
        if (coefficients.getDimensionality() != this.getBasisFunctions().size())
        {
            throw new IllegalArgumentException(
                "Must have as many coefficients as basis functions!" );
        }
        this.coefficients = coefficients;
    }

    /**
     * Getter for basisFunctions
     * @return
     * Collection of basis functions to combine to produce the output
     */
    public ArrayList<? extends Evaluator<? super InputType, ? extends OutputType>> getBasisFunctions()
    {
        return this.basisFunctions;
    }

    /**
     * Setter for basisFunctions
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     */
    public void setBasisFunctions(
        ArrayList<? extends Evaluator<? super InputType, ? extends OutputType>> basisFunctions )
    {
        this.basisFunctions = basisFunctions;
    }

    public Vector convertToVector()
    {
        return this.getCoefficients();
    }

    public void convertFromVector(
        Vector parameters )
    {
        this.setCoefficients( parameters );
    }

    @Override
    public String toString()
    {
        int num = this.getBasisFunctions().size();
        StringBuilder builder = new StringBuilder( 10 * num );
        for (int i = 0; i < num; i++)
        {
            builder.append( this.getCoefficients().getElement( i ) + "*" + this.getBasisFunctions().get( i ) + " + " );
        }
        return builder.toString();
    }

    /**
     * Evaluates the LinearCombinationFunction about the given input.
     * The output is produced as: y = a0*f0(x) + a1*f1(x) + ... + an*fn(x),
     * where "y" is the output, "x" is the given input,
     * "ai" is the ith coefficient and "fi" is the ith basis function
     * @param input
     * The input about which to compute the output
     * @return
     * y = a0*f0(x) + a1*f1(x) + ... + an*fn(x)
     */
    abstract public OutputType evaluate(
        InputType input );

}
