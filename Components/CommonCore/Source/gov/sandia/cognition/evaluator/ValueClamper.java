/*
 * File:                ValueClamper.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 03, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.io.Serializable;

/**
 * An evaluator that clamps a number between minimum and maximum values. It
 * can be used as a one-sided clamper by having the other value set to null or
 * as a two-sided clamper by setting both the minimum and maximum values.
 * 
 * @param   <DataType> The type to clamp.
 * @author  Justin Basilico
 * @since   3.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments="Looks good... very clever and clear implementation."
)
public class ValueClamper<DataType extends Comparable<DataType>>
    extends AbstractCloneableSerializable
    implements Evaluator<DataType, DataType>, Serializable
{

    /** The minimum possible value. May be null to not clamp to the minimum. */
    private DataType minimum;

    /** The maximum possible value. May be null to not clamp to the maximum. */
    private DataType maximum;

    /**
     * Creates a new {@code ValueClamper}. The minimum and maximum are 
     * initialized to null.
     */
    public ValueClamper()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code ValueClamper} with the given minimum and maximum
     * values.
     * 
     * @param   minimum
     *      The minimum value.
     * @param   maximum
     *      The maximum value.
     */
    public ValueClamper(
        final DataType minimum,
        final DataType maximum)
    {
        super();

        this.setMinimum(minimum);
        this.setMaximum(maximum);
    }

    @Override
    public ValueClamper<DataType> clone()
    {
        @SuppressWarnings("unchecked")
        final ValueClamper<DataType> result = (ValueClamper<DataType>)
            super.clone();
        result.minimum = ObjectUtil.cloneSmart(this.minimum);
        result.maximum = ObjectUtil.cloneSmart(this.maximum);
        return result;
    }
    
    

    public DataType evaluate(
        final DataType input)
    {
        if (input == null)
        {
            // Bad input.
            return null;
        }

        // We use compareTo for the comparisons.
        if (this.minimum != null && input.compareTo(this.minimum) < 0)
        {
            // It is less than the minimum.
            return this.minimum;
        }
        else if (this.maximum != null && input.compareTo(this.maximum) > 0)
        {
            // It is greater than the maximum.
            return this.maximum;
        }
        else
        {
            // It is between the minimum and maximum, so just return the
            // input.
            return input;
        }
    }

    /**
     * Gets the minimum value to allow. May be null to not enforce a minimum.
     * 
     * @return  
     *      The minimum value to allow.
     */
    public DataType getMinimum()
    {
        return this.minimum;
    }

    /**
     * Gets the minimum value to allow. May be null to not enforce a minimum.
     * 
     * @param   minimum
     *      The minimum value to allow.
     */
    public void setMinimum(
        final DataType minimum)
    {
        this.minimum = minimum;
    }

    /**
     * Gets the maximum value to allow. May be null to not enforce a maximum.
     * 
     * @return  
     *      The maximum value to allow.
     */
    public DataType getMaximum()
    {
        return this.maximum;
    }

    /**
     * Sets the maximum value to allow. May be null to not enforce a maximum.
     * 
     * @param   maximum
     *      The maximum value to allow.
     */
    public void setMaximum(
        final DataType maximum)
    {
        this.maximum = maximum;
    }

}
