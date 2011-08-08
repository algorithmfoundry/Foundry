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
    private Evaluator<InputType,OutputType> internalFunction;
    
    /**
     * Creates a new instance of NumericalDifferentiator
     * @param internalFunction
     * Internal function to numerically differencing.
     * @param delta
     * Value for x-value differencing, must be greater than 0.0
     */
    public NumericalDifferentiator(
        Evaluator<InputType,OutputType> internalFunction,
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
    public Evaluator<InputType,OutputType> getInternalFunction()
    {
        return this.internalFunction;
    }

    /**
     * Setter for internalFunction
     * @param internalFunction
     * Internal function to numerically differencing.
     */
    public void setInternalFunction(
        Evaluator<InputType,OutputType> internalFunction )
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
            Evaluator<Double,Double> internalFunction )
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
            Evaluator<Double,Double> internalFunction,
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
            Evaluator<Double,Double> f )
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
            Evaluator<Double,Double> f,
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
            Evaluator<Vector,Double> internalFunction )
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
            Evaluator<Vector,Double> internalFunction,
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
            Vector input,
            Evaluator<Vector,Double> f )
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
            Vector input,
            Evaluator<Vector,Double> f,
            double h )
        {
            double forig = f.evaluate( input );
            Vector inputPlusDeltai = input.clone();
            
            int M = input.getDimensionality();
            Vector J = VectorFactory.getDefault().createVector( M );
            for( int i = 0; i < M; i++ )
            {
                double inputi = input.getElement( i );
                inputPlusDeltai.setElement( i, inputi+h );
                double fi = f.evaluate( inputPlusDeltai );
                double di = (fi - forig) / h;
                inputPlusDeltai.setElement( i, inputi );
                
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
            this( (Evaluator<Vector,Vector>) null );
        }
        
        /**
         * Creates a new instance of VectorJacobian
         * @param internalFunction
         * Internal function to numerically differencing.
         */
        public MatrixJacobian(
            Evaluator<Vector,Vector> internalFunction )
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
            Evaluator<Vector,Vector> internalFunction,
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
            Evaluator<Vector,Vector> f )
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
            Vector input,
            Evaluator<Vector,Vector> f,
            double h )
        {
            Vector forig = f.evaluate( input );
            Vector inputPlusDeltaj = input.clone();
            
            int M = forig.getDimensionality();
            int N = input.getDimensionality();
            Matrix J = MatrixFactory.getDefault().createMatrix( M, N );
            double Jij;
            double inputj;
            for( int j = 0; j < N; j++ )
            {
                inputj = input.getElement(j);
                inputPlusDeltaj.setElement( j, inputj+h );
                Vector fj = f.evaluate( inputPlusDeltaj );
                for( int i = 0; i < M; i++ )
                {
                    Jij = (fj.getElement(i) - forig.getElement(i)) / h;
                    J.setElement( i, j, Jij);
                }
                inputPlusDeltaj.setElement( j, inputj );
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
