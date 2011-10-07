/*
 * File:                VectorFunctionLinearDiscriminant.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This class takes a function that maps a generic InputType to a Vector. We
 * then take the dot product of that Vector with a weight Vector to yield the
 * scalar output.  The parameters of this class are in the weight Vector.
 * 
 * @param <InputType> Must map this class onto a Vector
 * @author Kevin R. Dixon
 */
public class VectorFunctionLinearDiscriminant<InputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType,Double>,
    Vectorizable
{

    /**
     * The dot product of the discriminant with the output of the
     * vectorFunction is the output (scalar) value. Must have the
     * same dimensions as the outputDimensionality of vectorFunction.
     */
    private LinearDiscriminant discriminant;
    
    /**
     * Maps the input space to a Vector
     */
    private Evaluator<? super InputType,Vector> vectorFunction;
    
    /** 
     * Creates a new instance of VectorFunctionLinearDiscriminant 
     * @param vectorFunction
     * Maps the input space to a Vector
     * @param discriminant
     * The dot product of the discriminant with the output of the
     * vectorFunction is the output (scalar) value. Must have the
     * same dimensions as the outputDimensionality of vectorFunction.
     */
    public VectorFunctionLinearDiscriminant(
        Evaluator<? super InputType,Vector> vectorFunction,
        LinearDiscriminant discriminant )
    {
        this.setVectorFunction(vectorFunction);
        this.setDiscriminant(discriminant);
    }

    @Override
    public VectorFunctionLinearDiscriminant clone()
    {
        @SuppressWarnings("unchecked")
        VectorFunctionLinearDiscriminant<InputType> clone =
            (VectorFunctionLinearDiscriminant<InputType>) super.clone();
        clone.setVectorFunction(
            ObjectUtil.cloneSmart( this.getVectorFunction() ) );
        clone.setDiscriminant( ObjectUtil.cloneSafe( this.getDiscriminant() ) );
        return clone;
    }

    /**
     * Getter for discriminant
     * @return
     * The dot product of the discriminant with the output of the
     * vectorFunction is the output (scalar) value. Must have the
     * same dimensions as the outputDimensionality of vectorFunction.
     */
    public LinearDiscriminant getDiscriminant()
    {
        return this.discriminant;
    }

    /**
     * Setter for discriminant
     * @param discriminant
     * The dot product of the discriminant with the output of the
     * vectorFunction is the output (scalar) value. Must have the
     * same dimensions as the outputDimensionality of vectorFunction.
     */
    public void setDiscriminant(
        LinearDiscriminant discriminant )
    {        
        this.discriminant = discriminant;
    }

    /**
     * Getter for vectorFunction
     * @return
     * Maps the input space to a Vector
     */
    public Evaluator<? super InputType, Vector> getVectorFunction()
    {
        return this.vectorFunction;
    }

    /**
     * Setter for vectorFunction
     * @param vectorFunction
     * Maps the input space to a Vector
     */
    public void setVectorFunction(
        Evaluator<? super InputType, Vector> vectorFunction )
    {
        this.vectorFunction = vectorFunction;
    }

    public Double evaluate(
        InputType input )
    {
        Vector vfOutput = this.getVectorFunction().evaluate(input);
        return this.getDiscriminant().evaluate(vfOutput);
    }

    public Vector convertToVector()
    {
        return this.getDiscriminant().convertToVector();
    }

    public void convertFromVector(
        Vector parameters )
    {
        this.getDiscriminant().convertFromVector(parameters);
    }

}
