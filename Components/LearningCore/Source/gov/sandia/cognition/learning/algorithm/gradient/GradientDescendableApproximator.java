/*
 * File:                GradientDescendableApproximator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.gradient;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Creates a {@code radientDescendable} from a 
 * {@code VectorizableVectorFunction} by estimating the parameter gradient
 * using a forward-difference approximation of the parameter Jacobian.
 * Requires N+1 VectorFunction evaluations to approximate the Jacobian, where N
 * is the number of parameters in the {@code VectorizableVectorFunction}.
 * To compute the approximated derivative of the output with respect to the
 * input, use NumericalDifferentiator.
 * 
 * @see gov.sandia.cognition.math.matrix.NumericalDifferentiator
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Minor change to class javadoc.",
        "Looks fine."
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Numerical differentiation",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Numerical_differentiation"
)
public class GradientDescendableApproximator
    extends AbstractCloneableSerializable
    implements GradientDescendable
{

    /**
     * Default deltaSize, {@value}
     */
    public final static double DEFAULT_DELTA_SIZE = 1e-5;

    /**
     * Size of the finite-difference unit vectors, typically ~1e-5
     */
    private double deltaSize;

    /**
     * Internal VectorizableVectorFunction to consider
     */
    private VectorizableVectorFunction function;

    /**
     * Default constructor.
     */
    public GradientDescendableApproximator()
    {
        this( null );
    }

    /**
     * Creates a new instance of GradientDescendableApproximator
     * @param function 
     * Internal VectorizableVectorFunction to consider
     */
    public GradientDescendableApproximator(
        VectorizableVectorFunction function )
    {
        this( function, DEFAULT_DELTA_SIZE );
    }

    /**
     * Creates a new instance of GradientDescendableApproximator
     * @param function 
     * Internal VectorizableVectorFunction to consider
     * @param deltaSize 
     * Size of the finite-difference unit vectors, typically ~1e-5
     */
    public GradientDescendableApproximator(
        VectorizableVectorFunction function,
        double deltaSize )
    {
        this.setFunction( function );
        this.setDeltaSize( deltaSize );
    }

    @Override
    public GradientDescendableApproximator clone()
    {
        GradientDescendableApproximator clone =
            (GradientDescendableApproximator) super.clone();
        clone.setFunction( ObjectUtil.cloneSafe( this.getFunction() ) );
        return clone;
    }

    /**
     * Getter for function
     * @return 
     * Internal VectorizableVectorFunction to consider
     */
    public VectorizableVectorFunction getFunction()
    {
        return this.function;
    }

    /**
     * Setter for function
     * @param function 
     * Internal VectorizableVectorFunction to consider
     */
    public void setFunction(
        VectorizableVectorFunction function )
    {
        this.function = function;
    }

    public Vector convertToVector()
    {
        return this.getFunction().convertToVector();
    }

    public void convertFromVector(
        Vector parameters )
    {
        this.getFunction().convertFromVector( parameters );
    }

    public Vector evaluate(
        Vector input )
    {
        return this.getFunction().evaluate( input );
    }

    /**
     * Computes a forward-differences approximation to the parameter Jacobian
     * @param function
     * Internal VectorizableVectorFunction to consider
     * @param input
     * Input about which to estimate the Jacobian
     * @param deltaSize
     * Size of the finite-difference unit vectors, typically ~1e-5
     * @return Forward-difference approximated Jacobian about the input
     */
    public static Matrix computeParameterGradient(
        VectorizableVectorFunction function,
        Vector input,
        double deltaSize )
    {
        // Compute the Jacobian approximation as a forward difference
        Vector fx = function.evaluate( input );
        int M = fx.getDimensionality();
        Vector p = function.convertToVector();
        int N = p.getDimensionality();

        Matrix J = MatrixFactory.getDefault().createMatrix( M, N );

        for (int j = 0; j < N; j++)
        {
            // Add a unit vector in the jth direction
            double v = p.getElement( j );
            p.setElement( j, v + deltaSize );
            function.convertFromVector( p );
            Vector fjx = function.evaluate( input );
            fjx.minusEquals( fx );
            fjx.scaleEquals( 1.0 / deltaSize );
            J.setColumn( j, fjx );
            p.setElement( j, v );
        }

        return J;

    }

    /**
     * Computes a forward-differences approximation to the parameter Jacobian
     * @param input 
     * Input about which to estimate the Jacobian
     * @return Forward-difference approximated Jacobian about the input
     */
    public Matrix computeParameterGradient(
        Vector input )
    {
        return computeParameterGradient(
            this.getFunction(), input, this.getDeltaSize() );
    }

    /**
     * Getter for deltaSize
     * @return 
     * Size of the finite-difference unit vectors, typically ~1e-5
     */
    public double getDeltaSize()
    {
        return this.deltaSize;
    }

    /**
     * Setter for deltaSize
     * @param deltaSize 
     * Size of the finite-difference unit vectors, typically ~1e-5
     */
    public void setDeltaSize(
        double deltaSize )
    {
        this.deltaSize = deltaSize;
    }

}
