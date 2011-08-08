/*
 * File:                AbstractCognitiveModel.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import java.util.LinkedList;

/**
 * The AbstractCognitiveModel class is an abstract class that implements
 * common functionality of classes that implement the CognitiveModel 
 * interface may wish to have.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public abstract class AbstractCognitiveModel
    extends java.lang.Object
    implements CognitiveModel
{
    /** The listeners for the model. */
    private transient LinkedList<CognitiveModelListener> modelListeners = null;
    
    /** 
     * Creates a new instance of AbstractCognitiveModel.
     */
    public AbstractCognitiveModel()
    {
        super();
        
        this.setModelListeners(null);
    }
    
    /**
     * Adds a CognitiveModelListener to this model.
     *
     * @param listener The listener to add
     */
    public void addCognitiveModelListener(
        CognitiveModelListener listener)
    {
        if ( listener == null )
        {
            // Error: Bad listener.
            return;
        }
        
        if ( this.getModelListeners() == null )
        {
            // Make sure we have a valid list of listeners.
            this.setModelListeners(new LinkedList<CognitiveModelListener>());
        }
        
        this.getModelListeners().add(listener);
    }
    
    /**
     * Removes a CognitiveModelListener from this model.
     *
     * @param listener The listener to remove
     */
    public void removeCognitiveModelListener(
        CognitiveModelListener listener)
    {
        if ( listener == null )
        {
            // Error: Bad listener.
            return;
        }
        
        if ( this.getModelListeners() == null )
        {
            return;
        }
        
        this.getModelListeners().remove(listener);
    }
    
    /**
     * Triggers the CognitiveModelStateChangeEvent on all the registered
     * CognitiveModelListners.
     */
    protected void fireModelStateChangedEvent()
    {
        this.fireModelStateChangedEvent(
            new CognitiveModelStateChangeEvent(this, this.getCurrentState()));
    }
    
    /**
     * Triggers the CognitiveModelStateChangeEvent on all the registered
     * CognitiveModelListners.
     *
     * @param evt The event to pass to the listners
     */
    protected void fireModelStateChangedEvent(
        CognitiveModelStateChangeEvent evt)
    {
        if ( evt == null )
        {
            // Error: Bad event.
            return;
        }
        else if ( this.getModelListeners() == null )
        {
            // No listeners.
            return;
        }
        
        // Loop over the listeners and fire off the event.
        for ( CognitiveModelListener listener : modelListeners )
        {
            listener.modelStateChanged(evt);
        }
    }

    /**
     * Sets the list of model listeners.
     *
     * @param modelListeners The list of model listeners.
     */
    // Note: This setter is private because this class is in charge of managing
    // the list of listeners. Others can call the proper add/remove methods.
    private void setModelListeners(
        LinkedList<CognitiveModelListener> modelListeners)
    {
        this.modelListeners = modelListeners;
    }

    /**
     * Getter for the model listeners.
     *
     * @return The listeners for the model.
     */
    public LinkedList<CognitiveModelListener> getModelListeners()
    {
        return this.modelListeners;
    }
}
