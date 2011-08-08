/*
 * File:                LinearFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 15, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.DifferentiableVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>LinearFunction</code> class is a simple 
 * <code>VectorFunction</code> that just scales the given input vector by a
 * scalar value.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-05",
    changesNeeded=false,
    comments="Class looks fine."
)
public class LinearVectorFunction
    extends AbstractCloneableSerializable
    implements DifferentiableVectorFunction
{
    
    /** Scale factor for default constructor, {@value}. */
    public static final double DEFAULT_SCALE_FACTOR = 1.0;
    
    /** Scales the input by this amount. */
    private double scaleFactor;
    
    /**
     * Creates a new instance of LinearVectorFunction 
     * with the default scale factor.
     */
    public LinearVectorFunction()
    {
        this( DEFAULT_SCALE_FACTOR );
    }
    
    /**
     * Creates a new instance of LinearFunction.
     *
     * @param  scaleFactor The amount to scale.
     */
    public LinearVectorFunction(
        double scaleFactor)
    {
        super();
        
        this.setScaleFactor(scaleFactor);
    }

    /**
     * Gets the linear scale factor.
     *
     * @return The linear scale factor.
     */
    public double getScaleFactor()
    {
        return this.scaleFactor;
    }

    /**
     * Sets the linear scale factor.
     *
     * @param  scaleFactor The scale factor.
     */
    public void setScaleFactor(
        double scaleFactor)
    {
        this.scaleFactor = scaleFactor;
    }

    public Vector evaluate(
        Vector input)
    {
        return input.scale( this.getScaleFactor() );
    }

    public Matrix differentiate(
        Vector input)
    {
        int M = input.getDimensionality();
        Matrix dydx = MatrixFactory.getDefault().createIdentity(M,M).scale( 
            this.getScaleFactor() );
        return dydx;
    }
    
}
