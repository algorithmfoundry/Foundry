/*
 * File:                AbstractLearningExperiment.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.LinkedList;

/**
 * The {@code AbstractLearningExperiment} class implements the general 
 * functionality of the {@code LearningExperiment} interface, which is mainly
 * the handling of listeners and firing of events.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public abstract class AbstractLearningExperiment
    extends AbstractCloneableSerializable
    implements LearningExperiment
{
    /** The listeners for the experiment. */
    protected transient LinkedList<LearningExperimentListener> listeners;
    
    /**
     * Creates a new instance of AbstractLearningExperiment.
     */
    public AbstractLearningExperiment()
    {
        super();
        
        this.setListeners(null);
    }
    
    public void addListener(
        final LearningExperimentListener listener)
    {
        if ( this.getListeners() == null )
        {
            this.setListeners(new LinkedList<LearningExperimentListener>());
        }
        
        this.getListeners().add(listener);
    }
    
    public void removeListener(
        final LearningExperimentListener listener)
    {
        if ( this.getListeners() != null )
        {
            this.getListeners().remove(listener);
            
            if ( this.getListeners().isEmpty() )
            {
                this.setListeners(null);
            }
        }
    }
    
    /**
     * Fires the experimentStarted event for all listeners.
     */
    protected void fireExperimentStarted()
    {
        if ( this.listeners != null )
        {
            for ( LearningExperimentListener listener : this.getListeners() )
            {
                listener.experimentStarted(this);
            }
        }
    }
    
    /**
     * Fires the experimentEnded event for all listeners.
     */
    protected void fireExperimentEnded()
    {
        if ( this.listeners != null )
        {
            for ( LearningExperimentListener listener : this.getListeners() )
            {
                listener.experimentEnded(this);
            }
        }
    }
    
    /**
     * Fires the trialStarted event for all listeners.
     */
    protected void fireTrialStarted()
    {
        if ( this.listeners != null )
        {
            for ( LearningExperimentListener listener : this.getListeners() )
            {
                listener.trialStarted(this);
            }
        }
    }
    
    /**
     * Fires the trialEnded event for all listeners.
     */
    protected void fireTrialEnded()
    {
        if ( this.listeners != null )
        {
            for ( LearningExperimentListener listener : this.getListeners() )
            {
                listener.trialEnded(this);
            }
        }
    }

    /**
     * Gets the listeners for this experiment.
     *
     * @return The list of listeners for this experiment.
     */
    public LinkedList<LearningExperimentListener> getListeners()
    {
        return this.listeners;
    }

    /**
     * Sets the listeners for this experiment.
     *
     * @param  listeners The listeners for this experiment.
     */
    protected void setListeners(
        final LinkedList<LearningExperimentListener> listeners)
    {
        this.listeners = listeners;
    }
    
}
