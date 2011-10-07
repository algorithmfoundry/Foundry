/*
 * File:                GeneralizedLinearModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A VectorizableVectorFunction that is a matrix multiply followed by a
 * VectorFunction... a no-hidden-layer neural network
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Generalized linear model",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/Generalized_linear_model"
)
public class GeneralizedLinearModel
    extends AbstractCloneableSerializable
    implements VectorizableVectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>
{

    /**
     * GradientDescendable that multiplies an input by the internal matrix
     */
    private MultivariateDiscriminant discriminant;

    /**
     * VectorFunction that is applied to the output of the matrix multiply
     */
    private VectorFunction squashingFunction;

    /**
     * Default constructor.
     */
    public GeneralizedLinearModel()
    {
        this( 1, 1, new AtanFunction() );
    }

    /**
     * Creates a new instance of GeneralizedLinearModel
     * 
     * @param numInputs
     * Number of inputs of the function (number of matrix columns)
     * @param numOutputs
     * Number of outputs of the function (number of matrix rows)
     * @param scalarFunction 
     * Function to apply to each output
     */
    public GeneralizedLinearModel(
        int numInputs,
        int numOutputs,
        UnivariateScalarFunction scalarFunction )
    {
        this( new MultivariateDiscriminant( numInputs, numOutputs ),
            new ElementWiseVectorFunction( scalarFunction ) );
    }
    
    /**
     * Creates a new instance of GeneralizedLinearModel
     * @param discriminant
     * GradientDescendable that multiplies an input by the internal matrix
     * @param squashingFunction 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public GeneralizedLinearModel(
        MultivariateDiscriminant discriminant,
        VectorFunction squashingFunction )
    {
        this.setDiscriminant( discriminant );
        this.setSquashingFunction( squashingFunction );
    }

    /**
     * Creates a new instance of GeneralizedLinearModel
     * @param other GeneralizedLinearModel to copy
     */
    public GeneralizedLinearModel(
        GeneralizedLinearModel other )
    {
        this( other.getDiscriminant().clone(), other.getSquashingFunction() );
    }

    /**
     * Getter for discriminant
     * @return 
     * GradientDescendable that multiplies an input by the internal matrix
     */
    public MultivariateDiscriminant getDiscriminant()
    {
        return this.discriminant;
    }

    /**
     * Setter for discriminant
     * @param discriminant
     * GradientDescendable that multiplies an input by the internal matrix
     */
    public void setDiscriminant(
        MultivariateDiscriminant discriminant )
    {
        this.discriminant = discriminant;
    }

    /**
     * Getter for squashingFunction
     * @return 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public VectorFunction getSquashingFunction()
    {
        return this.squashingFunction;
    }

    /**
     * Setter for squashingFunction
     * @param squashingFunction 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public void setSquashingFunction(
        VectorFunction squashingFunction )
    {
        this.squashingFunction = squashingFunction;
    }

    public Vector convertToVector()
    {
        return this.getDiscriminant().convertToVector();
    }

    public void convertFromVector(
        Vector parameters )
    {
        this.getDiscriminant().convertFromVector( parameters );
    }

    public Vector evaluate(
        Vector input )
    {
        return this.squashingFunction.evaluate(
            this.discriminant.evaluate( input ) );
    }

    @Override
    public GeneralizedLinearModel clone()
    {
        GeneralizedLinearModel clone =
            (GeneralizedLinearModel) super.clone();
        clone.setDiscriminant(
            ObjectUtil.cloneSafe(this.getDiscriminant()) );
        return clone;
    }

    @Override
    public String toString()
    {
        String retval = "Squashing: " + this.getSquashingFunction().getClass()
            + "Weights:\n" + this.getDiscriminant().getDiscriminant();
        return retval;
    }

    public int getInputDimensionality()
    {
        return this.getDiscriminant().getInputDimensionality();
    }

    public int getOutputDimensionality()
    {
        return this.getDiscriminant().getOutputDimensionality();
    }

}
