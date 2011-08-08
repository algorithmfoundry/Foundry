/*
 * File:                AbstractThresholdBinaryCategorizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 1, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

/**
 * Categorizer that first maps the input space onto a real value, then
 * uses a threshold to map the result onto lowValue (for strictly less than the
 * threshold) or highValue (for greater than or equal to the threshold).
 * @param   <InputType> The type of the input the categorizer can use.
 * @author  Kevin R. Dixon
 * @since   3.0
 */
public abstract class AbstractThresholdBinaryCategorizer<InputType>
    extends AbstractDiscriminantBinaryCategorizer<InputType>
    implements ThresholdBinaryCategorizer<InputType>
{

    /**
     * Default threshold, {@value}.
     */
    public static final double DEFAULT_THRESHOLD = 0.0;

    /**
     * Threshold, below which I will return lowValue, above or equal to I will
     * return highValue.
     */
    private double threshold;

    /**
     * Creates a new AbstractThresholdBinaryCategorizer
     * @param threshold
     * Threshold, below which I will return lowValue, above or equal to I will
     * return highValue.
     */
    public AbstractThresholdBinaryCategorizer(
        double threshold )
    {
        this.setThreshold(threshold);
    }

    /**
     * Computes the discriminant.  This maps the input space onto the real
     * line, which will then be passed to the threshold.
     * @param   input
     *      Input to map onto the real number line.
     * @return
     *      Real-value equivalent of the input.
     */
    protected abstract double evaluateWithoutThreshold(
        final InputType input);

    @Override
    public double evaluateAsDouble(
        final InputType input)
    {
        return this.evaluateWithoutThreshold(input) - this.threshold;
    }
    
    /**
     * Getter for threshold
     * @return
     * Threshold, below which I will return lowValue, above or equal to I will
     * return highValue.
     */
    public double getThreshold()
    {
        return this.threshold;
    }

    /**
     * Setter for threshold
     * @param threshold
     * Threshold, below which I will return lowValue, above or equal to I will
     * return highValue.
     */
    public void setThreshold(
        double threshold)
    {
        this.threshold = threshold;
    }
    
}
