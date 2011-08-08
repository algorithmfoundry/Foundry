/*
 * File:                MovingAverageFilter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 23, 2009, Sandia Corporation.
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

/**
 * A type of filter using a moving-average calculation.  That is, a finite
 * window of inputs are scaled by a (possibly) unique coefficient and then
 * summed together.  In other words,
 * y(n) = b(0)x(n) + b(1)x(n-1) + ... + b(m)x(n-m).
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Finite impulse response",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Finite_impulse_response"
)
public class MovingAverageFilter
    extends AbstractStatefulEvaluator<Double,Double,FiniteCapacityBuffer<Double>>
    implements DiscreteTimeFilter<FiniteCapacityBuffer<Double>>
{

    /**
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    private Vector movingAverageCoefficients;

    /** 
     * Creates a new instance of MovingAverageFilter 
     * @param numCoefficients
     * Number of coefficients in the filter, with each coefficient having a
     * value of 1.0/numCoefficients.
     */
    public MovingAverageFilter(
        int numCoefficients )
    {
        this( VectorFactory.getDefault().createVector(
            numCoefficients, 1.0 / numCoefficients ) );
    }

    /**
     * Creates a new instance of MovingAverageFilter
     * @param coefficients
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.
     */
    public MovingAverageFilter(
        double ... coefficients )
    {
        this( VectorFactory.getDefault().copyArray( coefficients ) );
    }

    /**
     * Creates a new instance of MovingAverageFilter
     * @param movingAverageCoefficients
     * Coefficients of the moving-average filter.  Element 0 is applied to the
     * most-recent input, Element 1 is applied to the second-most-recent,
     * and so forth.  The dimensionality of the Vector is the order of the
     * filter.
     */
    public MovingAverageFilter(
        Vector movingAverageCoefficients )
    {
        super();
        this.setMovingAverageCoefficients( movingAverageCoefficients );
    }

    public FiniteCapacityBuffer<Double> createDefaultState()
    {
        return new FiniteCapacityBuffer<Double>(
            this.getNumMovingAverageCoefficients() );
    }

    public Double evaluate(
        Double input )
    {
        double sum = 0.0;
        this.getState().addFirst( input );
        int n = 0;
        for( Double xn : this.getState() )
        {
            final double an = this.getMovingAverageCoefficients().getElement( n );
            sum += an*xn;
            n++;
        }

        return sum;
    }

    @Override
    public MovingAverageFilter clone()
    {
        MovingAverageFilter clone = (MovingAverageFilter) super.clone();
        clone.setMovingAverageCoefficients(
            this.getMovingAverageCoefficients().clone() );
        return clone;
    }

    public Vector convertToVector()
    {
        return this.getMovingAverageCoefficients();
    }

    public void convertFromVector(
        Vector parameters )
    {
        if( this.getNumMovingAverageCoefficients() != parameters.getDimensionality() )
        {
            throw new IllegalArgumentException( "Wrong number of parameters!" );
        }
        this.setMovingAverageCoefficients( parameters );
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
    
}
