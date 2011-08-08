/*
 * File:                ThresholdFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Maps the input space onto the set {LOW_VALUE,HIGH_VALUE}.  Values below
 * the threshold are assigned to LOW_VALUE, Values above (or equal to) the
 * threshold are assigned to HIGH_VALUE
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class ThresholdFunction
    extends AbstractUnivariateScalarFunction
    implements Vectorizable
{

    /**
     * Default threshold, {@value}
     */
    public static final double DEFAULT_THRESHOLD = 0.0;

    /**
     * Default low value, {@value}
     */
    public static final double DEFAULT_LOW_VALUE = -1.0;

    /**
     * Default high value, {@value}
     */
    public static final double DEFAULT_HIGH_VALUE = 1.0;

    /**
     * Values above (or equal to) threshold are assigned this value
     */
    private double highValue;

    /**
     * Values below threshold are assigned this value
     */
    private double lowValue;

    /**
     * Current threshold, below which a value is assigned lowValue, above
     * which (or equal to) is assigned highValue
     */
    private double threshold;

    /**
     * Default constructor
     */
    public ThresholdFunction()
    {
        this( DEFAULT_THRESHOLD );
    }

    /**
     * Creates a new instance of ThresholdFunction
     * @param threshold 
     * Current threshold, below which a value is assigned lowValue, above
     * which (or equal to) is assigned highValue
     */
    public ThresholdFunction(
        double threshold)
    {
        this(threshold, DEFAULT_LOW_VALUE, DEFAULT_HIGH_VALUE );
    }

    /**
     * Creates a new instance of ThresholdFunction
     * 
     * @param threshold 
     * Current threshold, below which a value is assigned lowValue, above
     * which (or equal to) is assigned highValue
     * @param lowValue 
     * Values below threshold are assigned this value
     * @param highValue 
     * Values above (or equal to) threshold are assigned this value
     */
    public ThresholdFunction(
        double threshold,
        double lowValue,
        double highValue)
    {
        this.setThreshold(threshold);
        this.setLowValue(lowValue);
        this.setHighValue(highValue);
    }

    @Override
    public ThresholdFunction clone()
    {
        return (ThresholdFunction) super.clone();
    }

    /**
     * Maps the input onto the set {LOW_VALUE, HIGH_VALUE}
     * @param input Input to consider
     * @return (input >= threshold) ? HIGH_VALUE : LOW_VALUE
     */
    public double evaluate(
        double input)
    {
        return (input >= this.getThreshold()) ? this.getHighValue() : this.getLowValue();
    }

    /**
     * Converts this function into its parameters, which consists of the
     * threshold value
     * @return one-element Vector consisting of the threshold value
     */
    public Vector convertToVector()
    {
        Vector parameters = VectorFactory.getDefault().createVector(1);
        parameters.setElement(0, this.getThreshold());
        return parameters;
    }

    /**
     * Converts this function from its parameters, which consists of the
     * threshold value
     * @param parameters one-element Vector specifying the threshold value
     */
    public void convertFromVector(
        Vector parameters)
    {
        this.setThreshold(parameters.getElement(0));
    }

    /**
     * Getter for threshold
     * @return 
     * Current threshold, below which a value is assigned LOW_VALUE, above
     * which (or equal to) is assigned HIGH_VALUE
     */
    public double getThreshold()
    {
        return this.threshold;
    }

    /**
     * Setter for threshold
     * @param threshold 
     * Current threshold, below which a value is assigned LOW_VALUE, above
     * which (or equal to) is assigned HIGH_VALUE
     */
    public void setThreshold(
        double threshold)
    {
        this.threshold = threshold;
    }

    /**
     * Getter for highValue
     * @return 
     * Values above (or equal to) threshold are assigned this value
     */
    public double getHighValue()
    {
        return this.highValue;
    }

    /**
     * Setter for highValue
     * @param highValue 
     * Values above (or equal to) threshold are assigned this value
     */
    public void setHighValue(
        double highValue)
    {
        this.highValue = highValue;
    }

    /**
     * Getter for lowValue
     * @return 
     * Values below threshold are assigned this value
     */
    public double getLowValue()
    {
        return this.lowValue;
    }

    /**
     * Setter for lowValue
     * @param lowValue 
     * Values below threshold are assigned this value
     */
    public void setLowValue(
        double lowValue)
    {
        this.lowValue = lowValue;
    }

}
