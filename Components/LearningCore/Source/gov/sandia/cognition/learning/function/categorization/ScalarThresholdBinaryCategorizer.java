/*
 * File:                BinaryClassificationFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * The {@code ScalarThresholdBinaryCategorizer} class implements a binary 
 * categorizer that uses a threshold to categorize a given double.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class ScalarThresholdBinaryCategorizer
    extends AbstractThresholdBinaryCategorizer<Double>
    implements Vectorizable
{
    
    /**
     * Creates a new instance of ScalarThresholdBinaryCategorizer.
     */
    public ScalarThresholdBinaryCategorizer()
    {
        this(DEFAULT_THRESHOLD);
    }
    
    /**
     * Creates a new instance of ScalarThresholdBinaryCategorizer.
     *
     * @param threshold 
     * Threshold below which to consider "false" and greater than or equal to
     * consider "true".
     */
    public ScalarThresholdBinaryCategorizer(
        final double threshold)
    {
        super(threshold);
    }

    /**
     * Copy constructor.
     *
     * @param other BinaryClassicationFunction to clone
     */
    public ScalarThresholdBinaryCategorizer(
        final ScalarThresholdBinaryCategorizer other)
    {
        this(other.getThreshold());
    }
    
    @Override
    public ScalarThresholdBinaryCategorizer clone()
    {
        return (ScalarThresholdBinaryCategorizer) super.clone();
    }
    
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(this.getThreshold());
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        if( parameters.getDimensionality() != 1 )
        {
            throw new IllegalArgumentException(
                "Parameter size must be 1." );
        }
        this.setThreshold(parameters.getElement(0));
    }

    @Override
    protected double evaluateWithoutThreshold(
        final Double input)
    {
        return input;
    }

}
