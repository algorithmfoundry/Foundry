/*
 * File:                InputOutputSlopeTriplet.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 6, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Stores an InputOutputPair with corresponding slope (gradient) information
 * @author Kevin R. Dixon
 * @since 2.2
 */
public class InputOutputSlopeTriplet 
    extends DefaultInputOutputPair<Double,Double>
{

    /**
     * Slope (first derivative) at the given point
     */
    private Double slope;
    
    /** 
     * Creates a new instance of InputOutputSlopeTriplet 
     */
    public InputOutputSlopeTriplet()
    {
        this( null, null, null );
    }

    /**
     * Creates a new instance of InputOutputSlopeTriplet
     * @param input
     * Input to consider
     * @param output
     * Output to consider
     * @param slope
     * Slope (first derivative) at the given point
     */
    public InputOutputSlopeTriplet(
        Double input,
        Double output,
        Double slope )
    {
        super( input, output );
        this.setSlope( slope );
    }
    
    /**
     * Copy constructor
     * @param other
     * InputOutputSlopeTriplet to copy
     */
    public InputOutputSlopeTriplet(
        InputOutputSlopeTriplet other )
    {
        this( other.getInput(), other.getOutput(), other.getSlope() );
    }

    @Override
    public InputOutputSlopeTriplet clone()
    {
        InputOutputSlopeTriplet clone = (InputOutputSlopeTriplet) super.clone();
        clone.setSlope(this.getSlope());
        return clone;
    }

    /**
     * Getter for slope
     * @return
     * Slope (first derivative) at the given point
     */
    public Double getSlope()
    {
        return this.slope;
    }

    /**
     * Setter for slope
     * @param slope
     * Slope (first derivative) at the given point
     */
    public void setSlope(
        Double slope )
    {
        this.slope = slope;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", Slope: " + this.getSlope();
    }

}
