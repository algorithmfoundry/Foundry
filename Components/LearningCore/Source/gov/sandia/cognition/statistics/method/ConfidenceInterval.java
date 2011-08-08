/*
 * File:                ConfidenceInterval.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Contains a specification for a confidence interval, that is, the solution of
 * Pr{ lowerBound <= x(centralValue) <= upperBound } >= confidence
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class ConfidenceInterval
    extends AbstractCloneableSerializable
{
    /**
     * Lower bound for the statistic
     */
    private double lowerBound;
    
    /**
     * Upper bound for the statistic
     */
    private double upperBound;
    
    /**
     * Central value of the statistic (expectation, maximum likelihood, etc.)
     */
    private double centralValue;
    
    /**
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definite within the bound.
     */
    private double confidence;
    
    
    /**
     * Number of sample values used to create the interval
     */
    private int numSamples;
    
    
    /**
     * Creates a new instance of ConfidenceInterval
     * @param centralValue 
     * Central value of the statistic (expectation, maximum likelihood, etc.)
     * @param lowerBound 
     * Lower bound for the statistic
     * @param upperBound 
     * Upper bound for the statistic
     * @param confidence 
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definite within the bound.
     * @param   numSamples The number of samples to take.
     */
    public ConfidenceInterval(
        double centralValue,
        double lowerBound,
        double upperBound,
        double confidence,
        int numSamples )
    {
        
        if( (upperBound < lowerBound) ||
            (centralValue < lowerBound) ||
            (centralValue > upperBound) )
        {
            throw new IllegalArgumentException( "Expected: lowerBound <= centralValue <= upperBound" );
        }
        
        this.setCentralValue( centralValue );
        this.setLowerBound( lowerBound );
        this.setUpperBound( upperBound );
        this.setConfidence( confidence );
        this.setNumSamples( numSamples );
    }

    /**
     * Copy constructor
     * @param other ConfidenceInterval to clone
     */
    public ConfidenceInterval(
        ConfidenceInterval other )
    {
        this( other.getCentralValue(), other.getLowerBound(), other.getUpperBound(), other.getConfidence(), other.getNumSamples() );
    }
    
    /**
     * Getter for lowerBound
     * @return 
     * Lower bound for the statistic
     */
    public double getLowerBound()
    {
        return this.lowerBound;
    }

    /**
     * Setter for lowerBound
     * @param lowerBound 
     * Lower bound for the statistic
     */
    protected void setLowerBound(
        double lowerBound)
    {
        this.lowerBound = lowerBound;
    }

    /**
     * Getter for upperBound
     * @return 
     * Upper bound for the statistic
     */
    public double getUpperBound()
    {
        return this.upperBound;
    }

    /**
     * Setter for upperBound
     * @param upperBound 
     * Upper bound for the statistic
     */
    protected void setUpperBound(
        double upperBound)
    {
        this.upperBound = upperBound;
    }

    /**
     * Getter for centralValue
     * @return 
     * Central value of the statistic (expectation, maximum likelihood, etc.)
     */
    public double getCentralValue()
    {
        return this.centralValue;
    }

    /**
     * Setter for centralValue
     * @param centralValue 
     * Central value of the statistic (expectation, maximum likelihood, etc.)
     */
    protected void setCentralValue(
        double centralValue)
    {
        this.centralValue = centralValue;
    }

    /**
     * Getter for confidence
     * @return 
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definite within the bound.
     */
    public double getConfidence()
    {
        return this.confidence;
    }

    /**
     * Setter for confidence
     * @param confidence 
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definite within the bound.
     */
    protected void setConfidence(
        double confidence)
    {
        if( (confidence < 0.0) ||
            (confidence > 1.0) )
        {
            throw new IllegalArgumentException( "0.0 <= confidence <= 1.0" );
        }
        this.confidence = confidence;
    }

    @Override
    public String toString()
    {
//        return "Pr{" + this.getLowerBound() + "<=x("+this.getCentralValue()+")<=" + this.getUpperBound() + "} >= " + this.getConfidence() + ", Based on " + this.getNumSamples() + " samples" ;
        return this.getLowerBound() + " " + this.getCentralValue() + " " + this.getUpperBound() + " " + this.getConfidence() + " " + this.getNumSamples();
    }
     
    /**
     * Getter for numSamples
     * @return 
     * Number of sample values used to create the interval
     */
    public int getNumSamples()
    {
        return this.numSamples;
    }

    /**
     * Setter for numSamples
     * @param numSamples 
     * Number of sample values used to create the interval
     */
    public void setNumSamples(
        int numSamples)
    {
        if( numSamples <= 0 )
        {
            throw new IllegalArgumentException( "numSamples > 0" );
        }
        this.numSamples = numSamples;
    }
    
    
    /**
     * Returns whether or not the value is within the specified interval
     * @param value 
     * Value to determine if its within the interval
     * @return 
     * True if within the interval, false otherwise
     */
    public boolean withinInterval(
        double value )
    {
        return ((this.getLowerBound() <= value) && 
            (value <= this.getUpperBound()));
    }

 
}
