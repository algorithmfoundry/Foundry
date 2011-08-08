/*
 * File:                CognitiveModelStateChangeEvent.java
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

/**
 * The CognitiveModelStateChangeEvent class is an EventObject that contains the
 * data pertaining to a change in the state of a CognitiveModel.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class CognitiveModelStateChangeEvent
    extends java.util.EventObject
{
    /** The model whose state has changed. */
    private CognitiveModel model = null;
    
    /** The new state of the model. */
    private CognitiveModelState state = null;
    
    /** 
     * Creates a new instance of CognitiveModelStateChangeEvent.
     *
     * @param model The model that changed.
     * @param state The new state of the model.
     */
    public CognitiveModelStateChangeEvent(
        CognitiveModel model,
        CognitiveModelState state)
    {
        super(model);
        
        this.setModel(model);
        this.setState(state);
    }
    
    /**
     * Gets the model that changed state.
     *
     * @return The CognitiveModel that changed
     */
    public CognitiveModel getModel()
    {
        return this.model;
    }

    /**
     * Gets the new state of the model.
     *
     * @return The new state of the model
     */
    public CognitiveModelState getState()
    {
        return this.state;
    }

    /**
     * Sets the model that changed.
     *
     * @param model The model the event is for
     */
    protected void setModel(
        CognitiveModel model)
    {
        this.model = model;
    }

    /**
     * Sets the new state of the model.
     *
     * @param state The new state of the model
     */
    protected void setState(
        CognitiveModelState state)
    {
        this.state = state;
    }
}
