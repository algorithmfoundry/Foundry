/*
 * File:                CognitiveModel.java
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

import java.io.Serializable;
import java.util.List;

/**
 * The CognitiveModel interface defines the basic functionality of a cognitive
 * model. In particular, a model must have an update function, a method for
 * getting the current state, a method for resetting the state, and methods for
 * attaching listeners to the model.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface CognitiveModel
    extends Serializable
{
    /**
     * Updates the model by updating all the modules using the given input.
     *
     * @param input The input to the model
     */
    public void update(
        CognitiveModelInput input);
    
    /**
     * Gets the modules that are instantiated for this model.
     *
     * @return The modules contained in this model
     */
    public List<? extends CognitiveModule> getModules();
    
    /**
     * Resets the current cognitive state.
     */
    public void resetCognitiveState();
    
    /**
     * Gets the current state of the model.
     *
     * @return The model's current state.
     */
    public CognitiveModelState getCurrentState();
    
    /**
     * Gets teh semantic identifier database used by the model.
     *
     * @return The semantic identifier database used by the model.
     */
    public SemanticIdentifierMap getSemanticIdentifierMap();
    
    /**
     * Adds a CognitiveModelListener to this model.
     *
     * @param listener The listener to add
     */
    public void addCognitiveModelListener(
        CognitiveModelListener listener);
    
    /**
     * Removes a CognitiveModelListener from this model.
     *
     * @param listener The listener to remove
     */
    public void removeCognitiveModelListener(
        CognitiveModelListener listener);
}
