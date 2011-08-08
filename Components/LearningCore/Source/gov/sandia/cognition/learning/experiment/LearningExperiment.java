/*
 * File:                LearningExperiment.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright August 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.experiment;

/**
 * The {@code LearningExperiment} interface defines the general functionality
 * of an object that implements an experiment regarding machine learning 
 * algorithms. It defines listeners plus a method for getting the number of
 * trials in an experiment.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public interface LearningExperiment
{
    /**
     * Adds the given listener to this object.
     *
     * @param  listener The listener to add.
     */
    public void addListener(
        LearningExperimentListener listener);
    
    /**
     * Removes the given listener from this object.
     *
     * @param  listener The listener to remove.
     */
    public void removeListener(
        LearningExperimentListener listener);
    
    /**
     * Gets the number of trials in the experiment.
     *
     * @return The number of trials in the experiment.
     */
    public int getNumTrials();
}
