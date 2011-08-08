/*
 * File:                CosineFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 15, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * A closed-form cosine function.  The output "y" is given by the equation
 * y=A*cos(2*pi*f*x + phase).
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class CosineFunction 
    extends AbstractDifferentiableUnivariateScalarFunction
    implements Vectorizable
{

    /**
     * Value of 2.0*pi = {@value}.
     */
    public static final double TWO_PI = Math.PI * 2.0;

    /**
     * Maximum value of the cosine function. The value of "A" in
     * y=A*cos(2*pi*f*x + phase).
     */
    private double amplitude;

    /**
     * Frequency of the cosine function.  The value of "f" in
     * y=A*cos(2*pi*f*x + phase).
     */
    private double frequency;

    /**
     * Phase of the cosine function.  The value of "phase" in
     * y=A*cos(2*pi*f*x + phase).
     */
    private double phase;

    /** 
     * Creates a new instance of CosineFunction 
     */
    public CosineFunction()
    {
        this( 1.0, 1.0 );
    }

    /**
     * Creates a new instance of CosineFunction
     * @param amplitude
     * Maximum value of the cosine function. The value of "A" in
     * y=A*cos(2*pi*f*x + phase).
     * @param frequency
     * Frequency of the cosine function.  The value of "f" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public CosineFunction(
        double amplitude,
        double frequency )
    {
        this( amplitude, frequency, 0.0 );
    }

    /**
     * Creates a new instance of CosineFunction
     * @param amplitude
     * Maximum value of the cosine function. The value of "A" in
     * y=A*cos(2*pi*f*x + phase).
     * @param frequency
     * Frequency of the cosine function.  The value of "f" in
     * y=A*cos(2*pi*f*x + phase).
     * @param phase
     * Phase of the cosine function.  The value of "phase" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public CosineFunction(
        double amplitude,
        double frequency,
        double phase )
    {
        this.setAmplitude(amplitude);
        this.setFrequency(frequency);
        this.setPhase(phase);
    }

    @Override
    public CosineFunction clone()
    {
        return (CosineFunction) super.clone();
    }

    public double evaluate(
        double input)
    {
        return this.getAmplitude() * Math.cos(
            TWO_PI * this.getFrequency() * input + this.getPhase() );
    }

    public double differentiate(
        double input)
    {
        double twopif = TWO_PI * this.getFrequency();
        return -this.getAmplitude() * twopif * Math.sin(
            twopif * input + this.getPhase() );
    }

    /**
     * Getter for amplitude
     * @return
     * Maximum value of the cosine function. The value of "A" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public double getAmplitude()
    {
        return this.amplitude;
    }

    /**
     * Setter for amplitude
     * @param amplitude
     * Maximum value of the cosine function. The value of "A" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public void setAmplitude(
        double amplitude)
    {
        this.amplitude = amplitude;
    }

    /**
     * Getter for frequency
     * @return
     * Frequency of the cosine function.  The value of "f" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public double getFrequency()
    {
        return this.frequency;
    }

    /**
     * Setter for frequency
     * @param frequency
     * Frequency of the cosine function.  The value of "f" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public void setFrequency(
        double frequency)
    {
        this.frequency = frequency;
    }

    /**
     * Getter for phase
     * @return
     * Phase of the cosine function.  The value of "phase" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public double getPhase()
    {
        return this.phase;
    }

    /**
     * Setter for phase
     * @param phase
     * Phase of the cosine function.  The value of "phase" in
     * y=A*cos(2*pi*f*x + phase).
     */
    public void setPhase(
        double phase)
    {
        this.phase = phase;
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.amplitude, this.frequency, this.phase );
    }

    public void convertFromVector(
        Vector parameters)
    {
        if( parameters.getDimensionality() != 3 )
        {
            throw new IllegalArgumentException(
                "Expected three parameters: amplitude, frequency, phase" );
        }

        this.amplitude = parameters.getElement(0);
        this.frequency = parameters.getElement(1);
        this.phase = parameters.getElement(2);
    }

    @Override
    public String toString()
    {
        return this.getAmplitude() + "*cos( 2pi*" + this.getFrequency() + "*x + " + this.getPhase() + " )";
    }
    
}
