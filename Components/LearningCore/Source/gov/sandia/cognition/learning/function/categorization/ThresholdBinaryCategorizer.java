/*
 * File:                ThresholdBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 03, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

/**
 * Interface for a binary categorizer that uses a threshold to determine the
 * categorization.
 * 
 * @param <InputType>
 *      The type of the input to categorize.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface ThresholdBinaryCategorizer<InputType>
    extends DiscriminantBinaryCategorizer<InputType>
{
    
    /**
     * Gets the threshold between the two categories used in binary
     * categorization.
     *
     * @return
     *      The threshold.
     */
    public double getThreshold();

    /**
     * Sets the threshold between the two categories used in binary
     * categorization.
     *
     * @param   threshold
     *      The threshold.
     */
    public void setThreshold(
        final double threshold);
    
}
