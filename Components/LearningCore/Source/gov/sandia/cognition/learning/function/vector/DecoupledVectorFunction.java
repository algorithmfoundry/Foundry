/*
 * File:                DecoupledVectorFunction.java
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

import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Assigns one scalar function to each element in a vector, which is identical
 * to treating each element in the vector as a decoupled set of equations.
 * Produces a Vector output where each element of the output is the evaluation 
 * of the ith element in the input vector with the ith scalar function.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class DecoupledVectorFunction
    extends AbstractCloneableSerializable
    implements VectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>
{

    /**
     * Set of scalar functions to apply to the corresponding elements of
     * input Vectors
     */
    private Collection<? extends Evaluator<? super Double, Double>> scalarFunctions;

    /**
     * Creates a new instance of DecoupledVectorFunction
     * @param scalarFunctions 
     * Set of scalar functions to apply to the corresponding elements of
     * input Vectors
     */
    public DecoupledVectorFunction(
        UnivariateScalarFunction... scalarFunctions)
    {
        this(Arrays.asList(scalarFunctions));
    }

    /**
     * Creates a new instance of DecoupledVectorFunction
     * @param scalarFunctions 
     * Set of scalar functions to apply to the corresponding elements of
     * input Vectors
     */
    public DecoupledVectorFunction(
        Collection<? extends Evaluator<? super Double, Double>> scalarFunctions)
    {
        this.setScalarFunctions(scalarFunctions);
    }

    /**
     * Copy Constructor
     * @param other DecoupledVectorFunction to copy
     */
    public DecoupledVectorFunction(
        DecoupledVectorFunction other)
    {
        this(new ArrayList<Evaluator<? super Double, Double>>(
            other.getScalarFunctions()));
    }

    /**
     * Getter for scalarFunctions
     * @return 
     * Set of scalar functions to apply to the corresponding elements of
     * input Vectors
     */
    public Collection<? extends Evaluator<? super Double, Double>> getScalarFunctions()
    {
        return this.scalarFunctions;
    }

    /**
     * Setter for scalarFunctions
     * @param scalarFunctions 
     * Set of scalar functions to apply to the corresponding elements of
     * input Vectors
     */
    public void setScalarFunctions(
        Collection<? extends Evaluator<? super Double, Double>> scalarFunctions)
    {
        if (scalarFunctions.size() < 1)
        {
            throw new IllegalArgumentException(
                "Must have at least one scalar function!");
        }
        this.scalarFunctions = scalarFunctions;
    }

    /**
     * Gets the input-output Vector dimensionality
     * @return 
     * Dimensionality expected from the inputs and outputs
     */
    public int getDimensionality()
    {
        return this.getScalarFunctions().size();
    }

    /**
     * Evaluates the elements in the input Vector as though there were separate
     * scalar equations, evaluating the ith element in the input with the
     * ith scalar function
     * @param input 
     * Vector input to consider
     * @return 
     * Vector output where each element of the output is the evaluation of the
     * ith element in the input vector with the ith scalar function
     */
    public Vector evaluate(
        Vector input)
    {

        int M = input.getDimensionality();
        if (M != this.getDimensionality())
        {
            throw new IllegalArgumentException(
                "Input dimensionality doesn't equal number of scalar functions!");
        }

        Vector output = VectorFactory.getDefault().createVector(M);
        int i = 0;
        for (Evaluator<? super Double, Double> fi : this.getScalarFunctions())
        {
            double xi = input.getElement(i);
            double yi = fi.evaluate(xi);
            output.setElement(i, yi);
            i++;
        }

        return output;

    }

    public int getInputDimensionality()
    {
        return this.getDimensionality();
    }

    public int getOutputDimensionality()
    {
        return this.getDimensionality();
    }

}
