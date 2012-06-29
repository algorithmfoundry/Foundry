/*
 * File:                KolmogorovSmirnovEvaluator.java
 * Authors:             jdmorr
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Apr 30, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.util.ObjectUtil;


/**
 * You can specify a particular CDF.
 * It accepts a double sample value which is buffered inside this class in
 * a finite capacity buffer.  The evaluation uses the entire contents of that
 * buffer to compute the null hypothesis probability.
 * 
 * @author jdmorr
 */
public class KolmogorovSmirnovEvaluator
    extends AbstractStatefulEvaluator<Double, Double, FiniteCapacityBuffer<Double>>
{

    /**
     * Default capacity, {@value}.
     */
    public static final int DEFAULT_CAPACITY = 100;

    /**
     * Default CDF, a 3-DOF Chi-Square.
     */
    public static final CumulativeDistributionFunction<Double> DEFAULT_CDF =
        new ChiSquareDistribution.CDF( 3.0 );

    /**
     * The cumulative distribution function to base the evaluator on.
     */
    protected CumulativeDistributionFunction<Double> cdf;

    /**
     * The capacity of the state.
     */
    private int capacity;

    /**
     * Creates a new {@code KolmogorovSmirnovEvaluator}.
     */
    public KolmogorovSmirnovEvaluator()
    {
        this( ObjectUtil.cloneSafe(DEFAULT_CDF), DEFAULT_CAPACITY );
    }

    /**
     * Creates a new {@code KolmogorovSmirnovEvaluator}.
     *
     * @param cdf
     * The cumulative distribution function to base the evaluator on.
     * @param capacity
     * The capacity of the state.
     */
    public KolmogorovSmirnovEvaluator(
        CumulativeDistributionFunction<Double> cdf,
        int capacity)
    {
        this.setCDF(cdf);
        this.setCapacity(capacity);
    }

    @Override
    public KolmogorovSmirnovEvaluator clone()
    {
        KolmogorovSmirnovEvaluator clone =
            (KolmogorovSmirnovEvaluator) super.clone();
        clone.setCDF( ObjectUtil.cloneSafe( this.getCDF() ) );
        return clone;
    }

    /**
     * takes in the double value and adds it to finite capacity buffer
     * then computes the KS null hypothesis probability on the samples
     * in the buffer against the particular CDF specified.
     *
     * @param value a sample value
     * @return null hypothesis probability
     */
    public Double evaluate(
        Double value)
    {
       Double result=0.0;

       if( value != null )
       {
           this.getState().addLast(value);

           result = KolmogorovSmirnovConfidence.evaluateNullHypothesis(
                   this.getState(), this.cdf).getNullHypothesisProbability();
       }

       return result;
    }

    /**
     * Getter for cdf
     * @return
     * The cumulative distribution function to base the evaluator on.
     */
    public CumulativeDistributionFunction<Double> getCDF()
    {
        return this.cdf;
    }

    /**
     * Setter for cdf
     * @param cdf
     * The cumulative distribution function to base the evaluator on.
     */
    public void setCDF(
        CumulativeDistributionFunction<Double> cdf)
    {
        this.cdf = cdf;
    }

    public FiniteCapacityBuffer<Double> createDefaultState()
    {
        return new FiniteCapacityBuffer<Double>(this.getCapacity());
    }

    /**
     * Getter for capacity
     * @return
     * The capacity of the state.
     */
    public int getCapacity()
    {
        return this.capacity;
    }

    /**
     * Setter for capacity
     * @param capacity
     * The capacity of the state.
     */
    public void setCapacity(
        int capacity)
    {
        this.capacity = capacity;
    }

}
