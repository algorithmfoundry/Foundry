/*
 * File:                PolynomialFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 4, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.gradient.ParameterGradientEvaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A single polynomial term specified by a real-valued exponent.  Evaluates for
 * "y" such that y=x^a, where "x" is the input and "a" is the real-valued
 * exponent.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class PolynomialFunction
    extends AbstractDifferentiableUnivariateScalarFunction
    implements ParameterGradientEvaluator<Double, Vector>
{

    /**
     * Real-valued exponent of this polynomial
     */
    private double exponent;

    /**
     * Creates a new instance of PolynomialFunction
     * @param exponent
     * Real-valued exponent of this polynomial
     */
    public PolynomialFunction(
        double exponent )
    {
        this.setExponent( exponent );
    }

    /**
     * Copy Constructor
     * @param other
     * PolynomialFunction to copy
     */
    public PolynomialFunction(
        PolynomialFunction other )
    {
        this( other.getExponent() );
    }

    @Override
    public PolynomialFunction clone()
    {
        return (PolynomialFunction) super.clone();
    }

    /**
     * Returns the value of the exponent
     * @return
     * Exponent of this polynomial
     */
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getExponent() );
    }

    /**
     * Sets the value of the exponent
     * @param parameters
     * Exponent of this polynomial
     */
    public void convertFromVector(
        Vector parameters )
    {
        this.setExponent( parameters.getElement( 0 ) );
    }

    public double differentiate(
        double input )
    {

        // d[x^a]/dx = a*x^{a-1}
        double a = this.getExponent();
        double dydx = a * Math.pow( input, a - 1.0 );
        return dydx;
    }

    /**
     * Evaluates the polynomial for the given input, returning
     * Math.pow( input, exponent )
     * @param input
     * Input about which to compute the polynomial
     * @return
     * Math.pow(input,exponent)
     */
    public double evaluate(
        double input )
    {
        // Note that this will fail if "input" is less than zero AND
        // "exponent" is negative AND "exponent" isn't an integer:
        // for example, Math.pow(-2, -1.1) -> Exception!
        return Math.pow( input, this.exponent );
    }

    public Vector computeParameterGradient(
        Double input )
    {

        // We're computing the derivative of:
        // d/da[x^a] = ln(x)*x^a
        // This is degenerate when x==0.0, and we assume that
        // 0.0^a == 0.0 for any a, so that the derivative would be zero
        // as well
        double x = input;
        double a = this.exponent;
        double dyda;
        if (x == 0.0)
        {
            dyda = 0.0;
        }
        else
        {
            dyda = Math.log( x ) * Math.pow( x, a );
        }
        Vector gradient = VectorFactory.getDefault().copyValues( dyda );
        return gradient;

    }

    @Override
    public String toString()
    {
        return "x^" + this.getExponent();
    }

    /**
     * Getter for exponent
     * @return
     * Real-valued exponent of this polynomial
     */
    public double getExponent()
    {
        return this.exponent;
    }

    /**
     * Setter for exponent
     * @param exponent
     * Real-valued exponent of this polynomial
     */
    public void setExponent(
        double exponent )
    {
        this.exponent = exponent;
    }

    /**
     * Creates an array of PolynomialFunctions from the array of their exponents
     * @param polynomialExponents
     * Exponents for the various polynomials
     * @return
     * Array of PolynomialFunctions from the given exponents
     */
    public static ArrayList<PolynomialFunction> createPolynomials(
        double... polynomialExponents )
    {
        int num = polynomialExponents.length;
        ArrayList<PolynomialFunction> functions =
            new ArrayList<PolynomialFunction>( num );
        for (int i = 0; i < num; i++)
        {
            functions.add( new PolynomialFunction( polynomialExponents[i] ) );
        }

        return functions;
    }
    

    /**
     * Describes functionality of a closed-form algebraic polynomial function
     */
    public interface ClosedForm
        extends DifferentiableUnivariateScalarFunction
    {
        
        /**
         * Finds the real-valued roots (zero crossings) of the polynomial
         * @return
         * Array of roots, will never be null
         */
        public Double[] roots();
        
        /**
         * Finds the real-valued stationary points (zero slope) maxima or minima
         * of the polynomial
         * @return
         * Array of stationary points, will never be null
         */
        public Double[] stationaryPoints();
        
    }
    
    
    /**
     * Utilities for algebraic treatment of a linear polynomial of the form
     * y(x) = q0 + q1*x
     */
    public static class Linear
        extends AbstractDifferentiableUnivariateScalarFunction
        implements PolynomialFunction.ClosedForm
    {
        
        /**
         * Constant (zeroth-order) coefficient
         */
        private double q0;
        
        /**
         * Linear (first-order) coefficient
         */
        private double q1;
        
        /**
         * Tolerance below which to consider something zero, {@value}
         */
        public final static double COLLINEAR_TOLERANCE = 0.0;        

        /**
         * Creates a new instance of Linear
         * @param q0
         * Constant (zeroth-order) coefficient
         * @param q1
         * Linear (first-order) coefficient
         */
        public Linear(
            double q0,
            double q1 )
        {
            this.setQ0( q0 );
            this.setQ1( q1 );
        }
        
        @Override
        public Linear clone()
        {
            return (Linear) super.clone();
        }

        public double evaluate(
            double input )
        {
            return this.getQ0() + this.getQ1()*input;
        }

        public double differentiate(
            double input )
        {
            return this.getQ1();
        }

        public Double[] roots()
        {            
            if( Math.abs(this.getQ1()) <= COLLINEAR_TOLERANCE )
            {
                return new Double[0];
            }
            else
            {
                return new Double[] { -this.getQ0() / this.getQ1() };
            }
        }

        public Double[] stationaryPoints()
        {
            // No stationary points
            // (unless q1==0.0, but then every point is stationary)
            return new Double[0];
        }
        
        /**
         * Fits a linear (straight-line) curve to the given data points
         * @param p0
         * First point
         * @param p1
         * Second point
         * @return
         * closed-form Linear function representing the data points
         */
        public static PolynomialFunction.Linear fit(
            InputOutputPair<Double,Double> p0,
            InputOutputPair<Double,Double> p1 )
        {
            
            double x0 = p0.getInput();
            double x1 = p1.getInput();
            
            double y0 = p0.getOutput();
            double y1 = p1.getOutput();


            double denom = x1-x0;

            if( Double.isInfinite(y0) )
            {
                y0 = Math.signum(y0) * Double.MAX_VALUE/10.0;
            }
            if( Double.isInfinite(y1) )
            {
                y1 = Math.signum(y1) * Double.MAX_VALUE/10.0;
            }
            
            if( Math.abs(denom) <= COLLINEAR_TOLERANCE )
            {
                throw new IllegalArgumentException(
                    "Linear interpolation points are effectively collinear: " + denom );
            }
            
            // y = mx+b
            // Solving for m = (y1-y0) / (x1-x0) ("rise over run")
            // Solving for b = y-mx ({x0,y0} or {x1,y1} will yield same result)
            double q1 = (y1-y0) / denom;
            double q0 = y0 - q1*x0;
            return new PolynomialFunction.Linear( q0, q1 );
            
        }
        
        /**
         * Fits a linear (stright-line) curve to the given data point
         * @param p0
         * First point
         * @return
         * closed-form Linear function representing the data points
         */
        public static PolynomialFunction.Linear fit(
            InputOutputSlopeTriplet p0 )
        {
            double x0 = p0.getInput();
            double y0 = p0.getOutput();
            
            double q1 = p0.getSlope();
            double q0 = y0 - q1*x0;
            
            return new PolynomialFunction.Linear( q0, q1 );
        }
        
        
        /**
         * Getter for q0
         * @return
         * Zeroth order coefficient
         */
        public double getQ0()
        {
            return this.q0;
        }

        /**
         * Setter for q0
         * @param q0
         * Zeroth order coefficient
         */
        public void setQ0(
            double q0 )
        {
            this.q0 = q0;
        }

        /**
         * Getter for q1
         * @return
         * First-order coefficient
         */
        public double getQ1()
        {
            return this.q1;
        }

        /**
         * Setter for q1
         * @param q1
         * First-order coefficient
         */
        public void setQ1(
            double q1 )
        {
            this.q1 = q1;
        }

        @Override
        public String toString()
        {
            return "f(x) = " + this.getQ0() + " + " + this.getQ1() + "x";
        }
        
    }
    
    /**
     * Utilities for algebraic treatment of a quadratic polynomial of the form
     * y(x) = q0 + q1*x + q2*x^2.
     */
    public static class Quadratic
        extends Linear
    {
        
        /**
         * Quadratic (second-order) coefficient
         */
        private double q2;
        
        /**
         * Creates a new instance of Quadratic
         * @param q0
         * Constant (zeroth-order) coefficient
         * @param q1
         * Linear (first-order) coefficient
         * @param q2
         * Quadratic (second-order) coefficient
         */
        public Quadratic(
            double q0,
            double q1,
            double q2 )
        {
            super( q0, q1 );
            this.setQ2( q2 );
        }
        
        /**
         * Copy constructor
         * @param other
         * Quadratic to copy
         */
        public Quadratic(
            Quadratic other )
        {
            this( other.getQ0(), other.getQ1(), other.getQ2() );
        }
        
        @Override
        public Quadratic clone()
        {
            return (Quadratic) super.clone();
        }

        @Override
        public String toString()
        {
            return super.toString() + " + " + this.getQ2() + "x^2";
        }
        
        @Override
        public double evaluate(
            double input )
        {
            return Quadratic.evaluate( input, this.getQ0(), this.getQ1(), this.getQ2() );
        }

        @Override
        public double differentiate(
            double input )
        {
            // dy/dx = q1 + 2*q2*x
            return this.getQ1() + 2.0*this.getQ2()*input;
        }        
        
        /**
         * Finds the roots (zero-crossings) of the quadratic, which has at most
         * two, but possibly one or zero
         * @return
         * Array of roots
         */
        @Override
        public Double[] roots()
        {
            return Quadratic.roots( this.getQ0(), this.getQ1(), this.getQ2() );
        }
        
        /**
         * Finds the real-valued stationary points (zero-derivatives) of the
         * quadratic.  A quadratic has at most one stationary point, it may be
         * a minimum or maximum.
         * @return
         * Zero- or One-length array of stationary points
         */
        @Override
        public Double[] stationaryPoints()
        {
            return Quadratic.stationaryPoints( this.getQ0(), this.getQ1(), this.getQ2() );
        }
        
        /**
         * Evaluates a quadratic polynomial of the form
         * y(x) = q0 + q1*x + q2*x^2 for a given value of "x"
         * @param x
         * Value at which to evaluate the polynomial
         * @param q0
         * Constant-term coefficient
         * @param q1
         * Linear-term coefficient
         * @param q2
         * Quadratic-term coefficient
         * @return
         * Value of the polynomial at "x"
         */
        public static double evaluate(
            double x,
            double q0,
            double q1,
            double q2 )
        {
            return q0 + x*(q1 + x*q2);
        }
        
        /**
         * Fits a quadratic to three points
         * @param p0
         * First point
         * @param p1
         * Second point
         * @param p2
         * Third point
         * @return
         * Quadratic fitting the three points
         */
        public static Quadratic fit(
            InputOutputPair<Double,Double> p0,
            InputOutputPair<Double,Double> p1,        
            InputOutputPair<Double,Double> p2 )
        {
            
            double x0 = p0.getInput();
            double x1 = p1.getInput();
            double x2 = p2.getInput();

            double x02 = x0*x0;
            double x12 = x1*x1;
            double x22 = x2*x2;

            double y0 = p0.getOutput();
            double y1 = p1.getOutput();
            double y2 = p2.getOutput();

            // This was computed with MATLAB's symbolic toolbox:
            // >> syms x0 x1 x2 y0 y1 y2
            // >> A = [ x0^2 x0 1; x1^2 x1 1; x2^2 x2 1 ];
            // >> y = [ y0; y1; y2 ];
            // >> factor(A\y)
            // ans=
            // -(y0*x1-y0*x2-y2*x1+x0*y2-x0*y1+x2*y1)/(x0-x2)/(x1-x2)/(-x0+x1)
            // (-x1^2*y2+x1^2*y0-x2^2*y0-y1*x0^2+y1*x2^2+y2*x0^2)/(x0-x2)/(x1-x2)/(-x0+x1)
            // -(-x2^2*y0*x1+x2^2*x0*y1+x1^2*y0*x2-y1*x0^2*x2+y2*x0^2*x1-x1^2*x0*y2)/(x0-x2)/(x1-x2)/(-x0+x1)


            double denom = (x0-x2) * (x1-x2) * (x1-x0);

            if( Math.abs( denom ) <= COLLINEAR_TOLERANCE )
            {
                throw new IllegalArgumentException(
                    "Parabolic interpolation points are effectively collinear: " + denom );
            }


            double v2 = x0*(y1-y2) + x1*(y2-y0) + x2*(y0-y1);
            double v1 = x02*(y2-y1) + x12*(y0-y2) + x22*(y1-y0);
            double v0 = x02*(x2*y1-x1*y2) + x12*(x0*y2-x2*y0) + x22*(x1*y0-x0*y1);

            double q2 = v2 / denom;
            double q1 = v1 / denom;
            double q0 = v0 / denom;
            
            return new Quadratic( q0, q1, q2 );
            
        }
        
        /**
         * Fits a quadratic to two points, one of which has slope information.
         * @param p0
         * @param p1
         * @return The quadratic fit.
         */
        public static Quadratic fit(
            InputOutputSlopeTriplet p0,
            InputOutputPair<Double, Double> p1 )
        {
            
            // These are from the MATLAB command:
            // >> syms x0 x1 x2 y0 y1 m0
            // >> A = [ x0^2 x0 1; 2*x0 1 0; x1^2 x1 1 ]
            // >> y = [ y0; m0; y1 ]
            // >> factor(A\y)
            // ans =
            // -(-x0*m0+y0+x1*m0-y1)/(-x0+x1)^2
            // (-x0^2*m0+2*y0*x0-2*x0*y1+m0*x1^2)/(-x0+x1)^2
            // (x0^2*x1*m0+y1*x0^2-x0*m0*x1^2+x1^2*y0-2*y0*x0*x1)/(-x0+x1)^2

            double x0 = p0.getInput();
            double x1 = p1.getInput();

            double x02 = x0*x0;
            double x12 = x1*x1;

            double y0 = p0.getOutput();
            double m0 = p0.getSlope();
            double y1 = p1.getOutput();
            
            double dy = y0-y1;
            double dx = x0-x1;
            double denom = dx*dx;
            if( Math.abs( denom ) <= COLLINEAR_TOLERANCE )
            {
                throw new IllegalArgumentException(
                    "Parabolic interpolation points are effectively collinear: " + denom );
            }            
            
            double v2 = m0*dx-dy;
            double v1 = m0*(x12-x02) + 2.0*x0*dy;
            double v0 = x02*(y1+x1*m0) + x12*(y0-x0*m0) - 2.0*y0*x0*x1;
            
            double q2 = v2 / denom;
            double q1 = v1 / denom;
            double q0 = v0 / denom;
            
            return new Quadratic( q0, q1, q2 );
        }
        
        /**
         * Finds the roots of the quadratic equation using the quadratic 
         * formula. That is, finding the values of "x" such that 
         * y(x) = q0 + q1*x + q2*x^2 = 0.0.
         * There will be at most two roots, but there can also be a single root,
         * or no roots.  In the case of two roots, the return Pair will have
         * the "x" value for value.  In the case of a single root, the Pair
         * will have an "x" value for the First, but null for the second.  In
         * the case when there are no REAL roots, the return value will be null.
         * @param q0
         * Constant-term coefficient
         * @param q1
         * Linear-term coefficient
         * @param q2
         * Quadratic-term coefficient
         * @return
         * In the case of two roots, the return Pair will have
         * the "x" value for value.  In the case of a single root, the Pair
         * will have an "x" value for the First, but null for the second.  In
         * the case when there are no REAL roots, the return value will be a
         * zero-length array.
         * 
         */
        @PublicationReference(
            author="Wikipedia",
            title="Quadratic formula",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Quadratic_formula#Quadratic_formula"
        )
        public static Double[] roots(
            double q0,
            double q1,
            double q2 )
        {
            
            // If there's no quadratic term, then just solve the linear quation
            // such that x = -q0/q1
            if( Math.abs(q2) <= COLLINEAR_TOLERANCE )
            {
                // There are no roots because this equation is a constant q0
                if( Math.abs(q1) <= COLLINEAR_TOLERANCE )
                {
                    return new Double[0];
                }
                else
                {
                    double xstar = -q0/q1;
                    return new Double[]{ xstar };
                }
            }
            
            double discriminant = q1*q1 - 4.0*q2*q0;
            
            // Here there are only complex roots, so just return nothing
            if( discriminant < 0.0 )
            {
                return new Double[0];
            }
            
            // One repeated root, so just return that one
            else if( Math.abs(discriminant) <= COLLINEAR_TOLERANCE )
            {
                // There's no way that q2 can be effectively zero, as that
                // was caught in the first if-statement
                double xstar = -0.5 * q1 / q2;
                return new Double[]{ xstar };
            }
            
            // Here we've got two real roots!
            else
            {
                // There's no way that q2 can be effectively zero, as that
                // was caught in the first if-statement
                double denom = -2.0*q2;
                double sqrtdisc = Math.sqrt( discriminant );
                double xpos = (q1+sqrtdisc) / denom;
                double xneg = (q1-sqrtdisc) / denom;
                return new Double[]{ xpos, xneg };
            }
            
        }
        
        /**
         * Finds the stationary point of the quadratic equation.  That is,
         * the point when the derivative f'(x)=0.0
         * @param q0
         * Constant-term coefficient
         * @param q1
         * Linear-term coefficient
         * @param q2
         * Quadratic-term coefficient
         * @return
         * Value of "x" when the derivative is zero, null when none is found
         */
        public static Double[] stationaryPoints(
            double q0,
            double q1,
            double q2 )
        {
            
            // The derivative of the quadratic is
            // f'(x) = q1 + 2.0*q2*x
            // The stationary point is when f'(x)=0.0
            
            // No quadratic term means there is no stationary point
            if( Math.abs(q2) <= COLLINEAR_TOLERANCE )
            {
                return new Double[0];
            }
            
            return new Double[]{ q1 / (-2.0*q2) };
            
        }

        /**
         * Getter for q2
         * @return
         * Second-order coefficient
         */
        public double getQ2()
        {
            return this.q2;
        }

        /**
         * Setter for q2
         * @param q2
         * Second-order coefficient
         */
        public void setQ2(
            double q2 )
        {
            this.q2 = q2;
        }
        
    }
    
    /**
     * Algebraic treatment for a polynomial of the form
     * y(x) = q0 + q1*x + q2*x^2 + q3*x^3
     */
    public static class Cubic
        extends Quadratic
        implements PolynomialFunction.ClosedForm
    {

        /**
         * Cubic (third-order) coefficient
         */
        private double q3;
        
        /**
         * Creates a new instance of Quadratic
         * @param q0
         * Constant (zeroth-order) coefficient
         * @param q1
         * Linear (first-order) coefficient
         * @param q2
         * Quadratic (second-order) coefficient
         * @param q3
         * Cubic (third-order) coefficient
         */
        public Cubic(
            double q0,
            double q1,
            double q2,
            double q3 )
        {
            super( q0, q1, q2 );
            this.setQ3( q3 );
        }

        /**
         * Copy constructor
         * @param other
         * Cubic to copy
         */
        public Cubic(
            Cubic other )
        {
            super( other );
            this.setQ3( other.getQ3() );
        }
        
        @Override
        public Cubic clone()
        {
            return (Cubic) super.clone();
        }

        @Override
        public String toString()
        {
            return super.toString() + " + " + this.getQ3() + "x^3";
        }
        
        @Override
        public double evaluate(
            double input )
        {
            return Cubic.evaluate( input, this.getQ0(), this.getQ1(), this.getQ2(), this.getQ3() );
        }

        @Override
        public double differentiate(
            double input )
        {
            return Quadratic.evaluate( input, this.getQ1(), 2.0*this.getQ2(), 3.0*this.getQ3());
        }

        @Override
        public Double[] roots()
        {
            throw new IllegalArgumentException(
                "Not yet implemented" );
        }

        @Override
        public Double[] stationaryPoints()
        {
            return Cubic.stationaryPoints( this.getQ0(), this.getQ1(), this.getQ2(), this.getQ3() );
        }
        
        /**
         * Evaluates a quadratic polynomial of the form
         * y(x) = q0 + q1*x + q2*x^2 + q3*x^3 for a given value of "x"
         * @param x
         * Value at which to evaluate the polynomial
         * @param q0
         * Constant-term coefficient
         * @param q1
         * Linear-term coefficient
         * @param q2
         * Quadratic-term coefficient
         * @param q3 
         * Cubic-term coefficient
         * @return
         * Value of the polynomial at "x"
         */
        public static double evaluate(
            double x,
            double q0,
            double q1,
            double q2,
            double q3 )
        {
            return q0 + x*(q1 + x*(q2 + x*q3));
        }
        
        /**
         * 
         * Finds the stationary point of the quadratic equation.  That is,
         * the point when the derivative f'(x)=0.0
         * @param q0
         * Constant-term coefficient
         * @param q1
         * Linear-term coefficient
         * @param q2
         * Quadratic-term coefficient
         * @param q3 
         * Cubic-term coefficient
         * @return
         * Value of "x" when the derivative is zero, null when none is found
         */
        public static Double[] stationaryPoints(
            double q0,
            double q1,
            double q2,
            double q3 )
        {
            // The derivative is given as:
            // f'(x) = q1 + 2.0*q2*x + 3.0*q3*x^2
            // Then we can just use the quadratic root finder
            double p0 = q1;
            double p1 = 2.0 * q2;
            double p2 = 3.0 * q3;
            return Quadratic.roots( p0, p1, p2);
        }

        /**
         * Getter for q3
         * @return
         * Cubic (third-order) coefficient
         */
        public double getQ3()
        {
            return this.q3;
        }

        /**
         * Setter for q3
         * @param q3
         * Cubic (third-order) coefficient
         */
        public void setQ3(
            double q3 )
        {
            this.q3 = q3;
        }
        
        /**
         * Fits a cubic to two InputOutputSlopeTriplets using a closed-form
         * solution
         * @param p0
         * First point
         * @param p1
         * Second point
         * @return
         * Cubic fitting the points
         */
        public static Cubic fit(
            InputOutputSlopeTriplet p0,
            InputOutputSlopeTriplet p1 )
        {
            
            // From the MATLAB symbolic toolbox command sequence:
            // >> syms x0 x1 y0 y1 m0 m1
            // >> A = [ x0^3 x0^2 x0 1; 3*x0^2 2*x0 1 0; x1^3 x1^2 x1 1; 3*x1^2 2*x1 1 0 ]
            // >> y = [ y0; m0; y1; m1 ]
            // >> factor(A\y)
            // ans = 
            // (x1*m1-2*y1-x0*m1-x0*m0+x1*m0+2*y0)/(-x0+x1)^3
            // -(2*m0*x1^2+x1^2*m1+3*y0*x1-x0*x1*m0-3*x1*y1+x1*x0*m1-2*x0^2*m1+3*y0*x0-x0^2*m0-3*x0*y1)/(-x0+x1)^3
            // (2*x1^2*x0*m1+x0*m0*x1^2+x1^3*m0-x0^2*x1*m1+6*y0*x0*x1-2*x0^2*x1*m0-6*x1*x0*y1-x0^3*m1)/(-x0+x1)^3
            // (-x1^3*x0*m0+x1^3*y0-x1^2*x0^2*m1-3*y0*x0*x1^2+x1^2*x0^2*m0+x1*m1*x0^3-x0^3*y1+3*y1*x0^2*x1)/(-x0+x1)^3
            double x0 = p0.getInput();
            double x1 = p1.getInput();

            double x02 = x0*x0;
            double x12 = x1*x1;

            double y0 = p0.getOutput();
            double m0 = p0.getSlope();
            double y1 = p1.getOutput();
            double m1 = p1.getSlope();

            double dy = y1-y0;
            double dx = x1-x0;
            double m1pm0 = m1+m0;
            
            double denom = dx*dx*dx;
            if( Math.abs( denom ) <= COLLINEAR_TOLERANCE )
            {
                throw new IllegalArgumentException(
                    "Cubic interpolation points are effectively collinear: " + denom );
            }
            
            double v3 = dx*m1pm0 - 2.0*dy;
            double v2 = x02*(m1+m1pm0) - x12*(m0+m1pm0) + 3.0*(x1+x0)*dy - x0*x1*(m1-m0);
            double v1 = x12*(x0*(m1+m1pm0)+x1*m0) - x02*(x1*(m0+m1pm0)+x0*m1) - 6.0*x0*x1*dy;
            double v0 = x12*(x1*(y0-x0*m0)+x0*(x0*(m0-m1)-3.0*y0)) + x02*(x0*(x1*m1-y1)+3.0*y1*x1);

            double q3 = v3 / denom;
            double q2 = v2 / denom;
            double q1 = v1 / denom;
            double q0 = v0 / denom;
            
            return new Cubic( q0, q1, q2, q3 );
            
        }
        
    }

    /**
     * Performs Linear Regression using an arbitrary set of
     * PolynomialFunction basis functions
     */
    public static class Regression
        extends LinearRegression<Double>
    {

        /**
         * Creates a new instance of Regression
         * @param polynomialExponents
         * Set of polynomial exponents to use during the regression
         */
        public Regression(
            double... polynomialExponents )
        {
            super( PolynomialFunction.createPolynomials( polynomialExponents ) );
        }

        /**
         * Performs LinearRegression using all integer-exponent polynomials
         * less than or equal to the maxOrder
         * @param maxOrder
         * Uses all polynomials below the maxOrder: a0*x^0 + a1*x^1 + ... am*a^m
         * @param data
         * Data set to use for the LinearRegression
         * @return
         * LinearCombinationFunction that combines the desired
         * PolynomialFunctions with weighting coefficients determined by
         * the LinearRegression algorithm
         */
        public static VectorFunctionLinearDiscriminant<Double> learn(
            int maxOrder,
            Collection<? extends InputOutputPair<Double, Double>> data )
        {
            double[] polynomialExponents = new double[maxOrder + 1];
            for (int i = 0; i < polynomialExponents.length; i++)
            {
                polynomialExponents[i] = i;
            }

            PolynomialFunction.Regression r =
                new PolynomialFunction.Regression( polynomialExponents );
            return r.learn( data );
        }

    }

}
