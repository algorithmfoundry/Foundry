/*
 * File:                VectorElementThresholdCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * The {@code VectorElementThresholdCategorizer} class implements a 
 * {@code BinaryCategorizer} that categorizes an input vector by applying a
 * threshold to an element in a the vector. The threshold is a 
 * greater-than-or-equal threshold.
 *
 *    f(x) = x[i] >= t
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorElementThresholdCategorizer
    extends AbstractThresholdBinaryCategorizer<Vectorizable>
{
    /** The default index is {@value}. */
    public static final int DEFAULT_INDEX = -1;
    
    /** The index to apply the threshold to. */
    protected int index;
    
    /**
     * Creates a new instance of {@code VectorElementThresholdCategorizer}.
     */
    public VectorElementThresholdCategorizer()
    {
        this(DEFAULT_INDEX, DEFAULT_THRESHOLD);
    }
    
    /**
     * Creates a new instance of {@code VectorElementThresholdCategorizer}.
     * 
     * @param  index The index to threshold.
     * @param  threshold The threshold to apply.
     */
    public VectorElementThresholdCategorizer(
        final int index,
        final double threshold)
    {
        super(threshold);
        
        this.setIndex(index);
    }
    
    /**
     * Creates a new instance of {@code VectorElementThresholdCategorizer}.
     * 
     * @param  other The object to copy.
     */
    public VectorElementThresholdCategorizer(
        final VectorElementThresholdCategorizer other)
    {
        this(other.getIndex(), other.getThreshold());
    }
    
    @Override
    public VectorElementThresholdCategorizer clone()
    {
        return (VectorElementThresholdCategorizer) super.clone();
    }

    @Override
    protected double evaluateAsDouble(
        Vectorizable input)
    {
        final Vector vector = input.convertToVector();
        return vector.getElement(this.index);
    }

    /**
     * Gets the vector index that the threshold is being applied to.
     * 
     * @return  The vector index to threshold.
     */
    public int getIndex()
    {
        return this.index;
    }
    
    /**
     * Sets the vector index that the threshold is being applied to.
     * 
     * @param   index The vector index to threshold.
     */
    public void setIndex(
        final int index)
    {
        this.index = index;
    }
    
}
