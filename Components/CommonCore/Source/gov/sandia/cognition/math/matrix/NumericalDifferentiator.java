/*
 * File:                NumericalDifferentiator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 30, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Automatically differentiates a function by the method of forward differences.
 * 
 * @param <InputType> Class of the input to the Evaluator
 * @param <OutputType> Class of the output from the Evaluator
 * @param <DerivativeType> Class of the derivative of the Evaluator
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReference(
    author="Wikipedia",
    title="Numerical differentiation",
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Numerical_differentiation",
    year=2008
)
public abstract class NumericalDifferentiator<InputType,OutputType,DerivativeType>
    extends AbstractCloneableSerializable
    implements DifferentiableEvaluator<InputType,OutputType,DerivativeType>
{

    /**
     * Default value for x-value differencing, {@value}
     */
    static final double DEFAULT_DELTA = 1e-10;
    
    /**
     * Value for x-value differencing, must be greater than 0.0
     */
    private double delta;
    
    /**
     * Internal function to numerically differencing.
     */
    private Evaluator<? super InputType,OutputType> internalFunction;
    
    /**
     * Creates a new instance of NumericalDifferentiator
     * @param internalFunction
     * Internal function to numerically differencing.
     * @param delta
     * Value for x-value differencing, must be greater than 0.0
     */
    public NumericalDifferentiator(
        Evaluator<? super InputType,OutputType> internalFunction,
        double delta )
    {
        this.setInternalFunction( internalFunction );
        this.setDelta( delta );
    }

    @Override
    public NumericalDifferentiator<InputType,OutputType,DerivativeType> clone()
    {
        @SuppressWarnings("unchecked")
        NumericalDifferentiator<InputType,OutputType,DerivativeType> clone =
            (NumericalDifferentiator<InputType,OutputType,DerivativeType>) super.clone();
        clone.setInternalFunction( ObjectUtil.cloneSmart( this.getInternalFunction() ) );
        return clone;
    }
    
    /**
     * Getter for internalFunction
     * @return
     * Internal function to numerically differencing.
     */
    public Evaluator<? super InputType,OutputType> getInternalFunction()
    {
        return this.internalFunction;
    }

    /**
     * Setter for internalFunction
     * @param internalFunction
     * Internal function to numerically differencing.
     */
    public void setInternalFunction(
        Evaluator<? super InputType,OutputType> internalFunction )
    {
        this.internalFunction = internalFunction;
    }

    /**
     * Getter for delta
     * @return
     * Value for x-value differencing, must be greater than 0.0
     */
    public double getDelta()
    {
        return this.delta;
    }

    /**
     * Setter for delta
     * @param delta
     * Value for x-value differencing, must be greater than 0.0
     */
    public void setDelta(
        double delta )
    {
        if( delta <= 0.0 )
        {
            throw new IllegalArgumentException(
                "delta must be > 0.0" );
        }
        this.delta = delta;
    }

    public OutputType evaluate(
        InputType input )
    {
        return this.getInternalFunction().evaluate( input );
    }
    
    /**
     * Numerical differentiator based on a Vector Jacobian.
     */
    public static class DoubleJacobian
        extends NumericalDifferentiator<Double,Double,Double> 
    {
        
        /**
         * Default constructor
         */
        public DoubleJacobian()
        {
            this( null );
        }
        
        /**
         * Creates a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         */
        public DoubleJacobian(
            Evaluator<? super Double,Double> internalFunction )
        {
            this( internalFunction, DEFAULT_DELTA );
        }
        
        /**
         * Create a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         * @param delta
         * Value for x-value differencing
         */
        public DoubleJacobian(
            Evaluator<? super Double,Double> internalFunction,
            double delta )
        {
            super( internalFunction, delta );
        }

        /**
         * Static access to the numerical differentiation procedure.
         * @param input
         * Input about which to approximate the derivative.
         * @param f
         * Function of which to approximate the derivative.
         * @return
         * Approximated Jacobian, of the same dimension as input
         */
        public static Double differentiate(
            double input,
            Evaluator<? super Double,Double> f )
        {
            return DoubleJacobian.differentiate( input, f, DEFAULT_DELTA );
        }
        
        /**
         * Static access to the numerical differentiation procedure.
         * @param input
         * Input about which to approximate the derivative.
         * @param f
         * Function of which to approximate the derivative.
         * @param h
         * Value for x-value differencing
         * @return
         * Approximated Jacobian, of the same dimension as input
         */
        public static Double differentiate(
            double input,
            Evaluator<? super Double,Double> f,
            double h )
        {
            double forig = f.evaluate( input );
            double fdelta =  f.evaluate( input + h );
            return (fdelta-forig) / h;
        }
        
        public Double differentiate(
            Double input )
        {
            return DoubleJacobian.differentiate(
                input, this.getInternalFunction(), this.getDelta() );
        }
        
    }

    /**
     * Numerical differentiator based on a Vector Jacobian.
     */
    public static class VectorJacobian
        extends NumericalDifferentiator<Vector,Double,Vector>
    {
        
        /**
         * Default constructor
         */
        public VectorJacobian()
        {
            this( null );
        }
        
        /**
         * Creates a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         */
        public VectorJacobian(
            Evaluator<? super Vector,Double> internalFunction )
        {
            this( internalFunction, DEFAULT_DELTA );
        }
        
        /**
         * Create a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         * @param delta
         * Value for x-value differencing
         */
        public VectorJacobian(
            Evaluator<? super Vector,Double> internalFunction,
            double delta )
        {
            super( internalFunction, delta );
        }
        
        /**
         * Static access to the numerical differentiation procedure.
         * @param input
         * Input about which to approximate the derivative.
         * @param f
         * Function of which to approximate the derivative.
         * @return
         * Approximated Jacobian, of the same dimension as input
         */
        public static Vector differentiate(
            Vectorizable input,
            Evaluator<? super Vector,Double> f )
        {
            return VectorJacobian.differentiate( input, f, DEFAULT_DELTA );
        }
        
        /**
         * Static access to the numerical differentiation procedure.
         * @param input
         * Input about which to approximate the derivative.
         * @param f
         * Function of which to approximate the derivative.
         * @param h
         * Value for x-value differencing
         * @return
         * Approximated Jacobian, of the same dimension as input
         */
        public static Vector differentiate(
            final Vectorizable input,
            final Evaluator<? super Vector,Double> f,
            final double h )
        {
            final Vector x = input.convertToVector();
            final double forig = f.evaluate( x );
            final Vector inputPlusDeltai = x.clone();
            
            int M = x.getDimensionality();
            Vector J = VectorFactory.getDefault().createVector( M );
            for( int i = 0; i < M; i++ )
            {
                final double xi = x.getElement( i );
                inputPlusDeltai.setElement( i, xi+h );
                final double fi = f.evaluate( inputPlusDeltai );
                final double di = (fi - forig) / h;
                inputPlusDeltai.setElement( i, xi );
                
                J.setElement( i, di );
            }
            
            return J;
            
        }
        
        public Vector differentiate(
            Vector input )
        {
            return VectorJacobian.differentiate(
                input, this.getInternalFunction(), this.getDelta() );
        }
        
    }
    
    /**
     * Numerical differentiator based on a Matrix Jacobian.
     */
    public static class MatrixJacobian
        extends NumericalDifferentiator<Vector,Vector,Matrix>
    {
        
        /**
         * Default constructor
         */
        public MatrixJacobian()
        {
            this( (Evaluator<? super Vector,Vector>) null );
        }
        
        /**
         * Creates a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         */
        public MatrixJacobian(
            Evaluator<? super Vector,Vector> internalFunction )
        {
            this( internalFunction, DEFAULT_DELTA );
        }
        
        /**
         * Create a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         * @param delta
         * Value for x-value differencing
         */
        public MatrixJacobian(
            Evaluator<? super Vector,Vector> internalFunction,
            double delta )
        {
            super( internalFunction, delta );
        }
       
        /**
         * Static access to the numerical differentiation procedure.
         * @param input
         * Input about which to approximate the derivative.
         * @param f
         * Function of which to approximate the derivative.
         * @return
         * Approximated Jacobian, of the same dimension as input
         */
        public static Matrix differentiate(
            Vector input,
            Evaluator<? super Vector,Vector> f )
        {
            return MatrixJacobian.differentiate( input, f, DEFAULT_DELTA );
        }
        
        /**
         * Static access to the numerical differentiation procedure.
         * @param input
         * Input about which to approximate the derivative.
         * @param f
         * Function of which to approximate the derivative.
         * @param h
         * Value for x-value differencing
         * @return
         * Approximated Jacobian, of the same dimension as input
         */
        public static Matrix differentiate(
            final Vectorizable input,
            final Evaluator<? super Vector,Vector> f,
            final double h )
        {
            final Vector x = input.convertToVector();
            final Vector forig = f.evaluate( x );
            final Vector inputPlusDeltaj = x.clone();
            
            final int M = forig.getDimensionality();
            final int N = x.getDimensionality();
            final Matrix J = MatrixFactory.getDefault().createMatrix( M, N );
            double Jij;
            double xj;
            for( int j = 0; j < N; j++ )
            {
                xj = x.getElement(j);
                inputPlusDeltaj.setElement( j, xj+h );
                Vector fj = f.evaluate( inputPlusDeltaj );
                for( int i = 0; i < M; i++ )
                {
                    Jij = (fj.getElement(i) - forig.getElement(i)) / h;
                    J.setElement( i, j, Jij);
                }
                inputPlusDeltaj.setElement( j, xj );
            }
            
            return J;
            
        }
        
        public Matrix differentiate(
            Vector input )
        {
            return MatrixJacobian.differentiate(
                input, this.getInternalFunction(), this.getDelta() );
        }
        
    }    
    
}
