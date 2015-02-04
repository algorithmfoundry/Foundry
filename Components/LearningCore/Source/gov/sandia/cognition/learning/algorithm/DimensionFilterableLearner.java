/*
 * File:            DimensionFilterableLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm;

/**
 * Interface for a learner that can be filtered by which dimensions it 
 * includes in learning.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public interface DimensionFilterableLearner
{
    /**
     * Gets the dimensions that the learner is to consider. Null means that all
     * of them are included.
     *
     * @return
     *      The array of vector dimensions to consider. Null means all of them
     *      are considered.
     */
    public int[] getDimensionsToConsider();

    /**
     *
     * Gets the dimensions that the learner is to consider. Null means that all
     * of them are included.
     *
     * @param   dimensionsToConsider
     *      The array of vector dimensions to consider. Null means all of them
     *      are considered.
     */
    public void setDimensionsToConsider(
        final int... dimensionsToConsider);
}
