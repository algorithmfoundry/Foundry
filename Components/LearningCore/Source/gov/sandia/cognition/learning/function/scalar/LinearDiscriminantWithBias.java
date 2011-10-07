/*
 * File:                LinearDiscriminantWithBias.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 30, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * A LinearDiscriminant with an additional bias term that gets added to the
 * output of the dot product.
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
public class LinearDiscriminantWithBias 
    extends LinearDiscriminant
{

    /**
     * Bias term that gets added to the output of the dot product.
     */
    protected double bias;

    /** 
     * Creates a new instance of LinearDiscriminantWithBias 
     */
    public LinearDiscriminantWithBias()
    {
        this( (Vector) null );
    }

    /**
     * Creates a new instance of LinearClassifier
     * @param weightVector
     * Weight Vector to dot-product with the input
     */
    public LinearDiscriminantWithBias(
        final Vector weightVector )
    {
        this( weightVector, 0.0 );
    }

    /**
     * Creates a new instance of LinearClassifier
     * @param weightVector
     * Weight Vector to dot-product with the input
     * @param bias
     * Bias term that gets added to the output of the dot product.
     */
    public LinearDiscriminantWithBias(
        final Vector weightVector,
        final double bias )
    {
        super( weightVector );
        this.setBias(bias);
    }

    @Override
    public LinearDiscriminantWithBias clone()
    {
        return (LinearDiscriminantWithBias) super.clone();
    }

    /**
     * Getter for bias.
     * @return 
     * Bias term that gets added to the output of the dot product.
     */
    public double getBias()
    {
        return this.bias;
    }

    /**
     * Setter for bias.
     * @param bias
     * Bias term that gets added to the output of the dot product.
     */
    public void setBias(
        double bias)
    {
        this.bias = bias;
    }

    @Override
    public Double evaluate(
        Vectorizable input)
    {
        final double dot = super.evaluate( input );
        return dot + this.bias;
    }

    @Override
    public Vector convertToVector()
    {
        final int dim = this.getInputDimensionality() + 1;
        Vector p = VectorFactory.getDefault().createVector(dim);
        for( int i = 0; i < dim-1; i++ )
        {
            p.setElement(i, this.weightVector.getElement(i) );
        }
        p.setElement(dim-1, this.bias);
        return p;
    }

    @Override
    public void convertFromVector(
        Vector parameters)
    {
        final int dim = this.getInputDimensionality() + 1;
        parameters.assertDimensionalityEquals( dim );
        this.setWeightVector( parameters.subVector(0, dim-2) );
        this.setBias( parameters.getElement(dim-1) );
    }

}
