/*
 * File:                AutoRegressiveMovingAverageFilter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.DefaultPair;

/**
 * A type of filter using a moving-average calculation.  That is, a finite
 * window of inputs are scaled by a (possibly) unique coefficient and then
 * summed together.  In other words,
 * <BR>
 * y(n) + a(0)y(n-1) + a(1)y(n-2) + ... + a(p)y(n-p+1)
 * <BR>
 * = b(0)x(n) + b(1)x(n-1) + ... + b(m)x(n-m).
 * <BR>
 * Note that this is slightly different than MATLAB/octave's implementation
 * in that the first autoregressive coefficient (a(0)) scales y(n-1) and not
 * every value.
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Infinite impulse response",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Infinite_impulse_response"
)
public class AutoRegressiveMovingAverageFilter
    extends AbstractStatefulEvaluator<Double,Double,DefaultPair<FiniteCapacityBuffer<Double>,FiniteCapacityBuffer<Double>>>
    implements DiscreteTimeFilter<DefaultPair<FiniteCapacityBuffer<Double>,FiniteCapacityBuffer<Double>>>
{
    
    /**
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    private Vector movingAverageCoefficients;

    /**
     * Coefficients of the autoregressive filter.  Element 0 is applied to the
     * most-recent output, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    private Vector autoRegressiveCoefficients;

    /** 
     * Creates a new instance of AutoRegressiveMovingAverageFilter 
     * @param numAutoregressiveCoefficients
     * Number of autoregressive coefficients.
     * @param numMovingAverageCoefficients
     * Number of moving-average coefficients.
     */
    public AutoRegressiveMovingAverageFilter(
        int numAutoregressiveCoefficients,
        int numMovingAverageCoefficients )
    {
        this( VectorFactory.getDefault().createVector(
                numAutoregressiveCoefficients, 1.0/numAutoregressiveCoefficients ),
            VectorFactory.getDefault().createVector(
                numMovingAverageCoefficients, 1.0/numMovingAverageCoefficients ) );
    }

    /**
     * Creates a new instance of AutoRegressiveMovingAverageFilter
     * @param autoRegressiveCoefficients
     * Coefficients of the autoregressive filter.  Element 0 is applied to the
     * most-recent output, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     * @param movingAverageCoefficients
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public AutoRegressiveMovingAverageFilter(
        double[] autoRegressiveCoefficients,
        double[] movingAverageCoefficients )
    {
        this( VectorFactory.getDefault().copyArray( autoRegressiveCoefficients ),
            VectorFactory.getDefault().copyArray( movingAverageCoefficients ) );
    }

    /**
     * Creates a new instance of AutoRegressiveMovingAverageFilter
     * @param autoRegressiveCoefficients
     * Coefficients of the autoregressive filter.  Element 0 is applied to the
     * most-recent output, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     * @param movingAverageCoefficients
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public AutoRegressiveMovingAverageFilter(
        Vector autoRegressiveCoefficients,
        Vector movingAverageCoefficients )
    {
        this.setAutoregressiveCoefficients( autoRegressiveCoefficients );
        this.setMovingAverageCoefficients( movingAverageCoefficients );
    }

    public DefaultPair<FiniteCapacityBuffer<Double>, FiniteCapacityBuffer<Double>> createDefaultState()
    {
        return DefaultPair.create(
            new FiniteCapacityBuffer<Double>( this.getNumMovingAverageCoefficients() ),
            new FiniteCapacityBuffer<Double>( this.getNumAutoRegressiveCoefficients() ) );
    }

    public Double evaluate(
        Double input )
    {
        double yn = 0.0;
        this.getState().getFirst().addFirst( input );
        int n = 0;
        for( Double xn : this.getState().getFirst() )
        {
            final double bn = this.getMovingAverageCoefficients().getElement( n );
            yn += bn*xn;
            n++;
        }

        n = 0;
        for( Double ynm1 : this.getState().getSecond() )
        {
            final double an = this.getAutoRegressiveCoefficients().getElement( n );
            yn -= an*ynm1;
            n++;
        }

        this.getState().getSecond().addFirst( yn );

        return yn;

    }

    @Override
    public AutoRegressiveMovingAverageFilter clone()
    {
        AutoRegressiveMovingAverageFilter clone =
            (AutoRegressiveMovingAverageFilter) super.clone();
        clone.setAutoregressiveCoefficients(
            this.getAutoRegressiveCoefficients().clone() );
        clone.setMovingAverageCoefficients(
            this.getMovingAverageCoefficients().clone() );
        return clone;
    }

    public Vector convertToVector()
    {
        return this.getAutoRegressiveCoefficients().stack(
            this.getMovingAverageCoefficients() );
    }

    public void convertFromVector(
        Vector parameters )
    {
        int M = this.getNumAutoRegressiveCoefficients();
        int N = this.getNumMovingAverageCoefficients();
        if( (M+N) != parameters.getDimensionality() )
        {
            throw new IllegalArgumentException(
                "Number of dimensions of the parameter Vector aren't equal to the number expected." );
        }

        this.setAutoregressiveCoefficients( parameters.subVector( 0, M-1 ) );
        this.setMovingAverageCoefficients( parameters.subVector( M, N+M-1 ) );
    }

    /**
     * Returns the number of coefficients in the moving-average filter.
     * @return
     * Number of coefficients in the moving-average filter.
     */
    public int getNumMovingAverageCoefficients()
    {
        return (this.getMovingAverageCoefficients() == null)
            ? 0 : this.getMovingAverageCoefficients().getDimensionality();
    }

    /**
     * Setter for movingAverageCoefficients
     * @return
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public Vector getMovingAverageCoefficients()
    {
        return this.movingAverageCoefficients;
    }

    /**
     * Setter for movingAverageCoefficients
     * @param movingAverageCoefficients
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public void setMovingAverageCoefficients(
        Vector movingAverageCoefficients )
    {
        this.movingAverageCoefficients = movingAverageCoefficients;
    }

    /**
     * Gets the number of autoregressive coefficients.
     * @return
     * Number of autoregressive coefficients.
     */
    public int getNumAutoRegressiveCoefficients()
    {
        return (this.getAutoRegressiveCoefficients() == null)
            ? 0 : this.getAutoRegressiveCoefficients().getDimensionality();
    }

    /**
     * Getter for autoregressiveCoefficients
     * @return
     * Coefficients of the autoregressive filter.  Element 0 is applied to the
     * most-recent output, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public Vector getAutoRegressiveCoefficients()
    {
        return this.autoRegressiveCoefficients;
    }

    /**
     * Setter for autoregressiveCoefficients
     * @param autoRegressiveCoefficients
     * Coefficients of the autoregressive filter.  Element 0 is applied to the
     * most-recent output, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public void setAutoregressiveCoefficients(
        Vector autoRegressiveCoefficients )
    {
        this.autoRegressiveCoefficients = autoRegressiveCoefficients;
    }

}
