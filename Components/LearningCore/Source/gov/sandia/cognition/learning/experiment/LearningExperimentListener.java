/*
 * File:                LearningExperimentListener.java
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
 * The {@code LearningExperimentListener} interface defines the functionality
 * of an object that listens to events from a {@code LearningExperiment}.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public interface LearningExperimentListener
{
    /**
     * Fired when the experiment has started.
     *
     * @param  experiment The experiment that started.
     */
    public void experimentStarted(
        LearningExperiment experiment);
    
    /**
     * Fired when the experiment has ended.
     *
     * @param  experiment The experiment that ended.
     */
    public void experimentEnded(
        LearningExperiment experiment);
    
    
    /**
     * Fired when a new trial in the experiment has started.
     *
     * @param  experiment The experiment that has started a new trial.
     */
    public void trialStarted(
        LearningExperiment experiment);
    
    
    /**
     * Fired when a trial in the experiment has ended.
     *
     * @param  experiment The experiment that has ended a trial.
     */
    public void trialEnded(
        LearningExperiment experiment);
}
