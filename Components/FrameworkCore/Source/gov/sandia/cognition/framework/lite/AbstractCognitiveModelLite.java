/*
 * File:                AbstractCognitiveModelLite.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 * 
 * Copyright Jan 9, 2008, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 * 
 */
 
package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.AbstractCognitiveModel;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticIdentifierMap;

/**
 * The AbstractCognitiveModelLite class is an abstract class that implements
 * common functionality of classes that share general functionality
 * with the CognitiveModelLite - i.e. they use a DefaultSemanticIdentifierMap
 * and store their state in a CognitiveModelLiteState 
 *
 * @author Zachary Benz
 * @since  2.0
 */
public abstract class AbstractCognitiveModelLite
    extends AbstractCognitiveModel
{
    /** The number of modules. */
    protected int numModules = 0;
    
    /** The semantic identifier database. */
    private DefaultSemanticIdentifierMap semanticIdentifierMap = null;
    
    /** The current state of the model. */
    protected CognitiveModelLiteState state = null;
    
    /** 
     * Creates a new instance of AbstractCognitiveModelLite.
     */
    public AbstractCognitiveModelLite()
    {
        super();        
    }
    
    /**
     * Resets the current state of the model.
     */
    public void resetCognitiveState()
    {
        int numIdentifiers = this.semanticIdentifierMap.getIdentifiers().size();
        int initialCapacity = (numIdentifiers > 0) ? numIdentifiers : 1;
        this.setCognitiveState(new CognitiveModelLiteState(
            this.numModules, initialCapacity ) );
    }
    
    /**
     * Sets the cognitive state to the given state. If the state has not been
     * initialized it is now initialized.
     * @param state The state to initialize.
     * @throws IllegalArgumentException If the given state does not have the
     *         proper module states.
     */ 
    public void setCognitiveState(
        CognitiveModelLiteState state)
    {
        if ( this.numModules != state.getNumModuleStates() )
        {
            // Error: Wrong number of modules.
            throw new IllegalArgumentException(
                "State does not have the proper number of module states.");
        }
        
        this.state = state;
        
        if ( !this.state.isInitialized() )
        {
            // The state is not initialized so initialize it.
            this.initializeCognitiveState(this.state);
        }
        
        this.fireModelStateChangedEvent();
    }
    
    /**
     * This method takes a cognitive state and initializes it by initializing
     * all the module states on it. If the state is already initialized it will
     * clear out the existing module states.
     *
     * @param  state The CognitiveModelLiteState to initialize.
     * @throws IllegalArgumentException If the given state does not have the
     *         proper number of module states.
     */
    public void initializeCognitiveState(
        CognitiveModelLiteState state)
    {
        if ( this.numModules != state.getNumModuleStates() )
        {
            // Error: Wrong number of modules.
            throw new IllegalArgumentException(
                "State does not have the proper number of module states.");
        }
        
        // Clear the state.
        state.clear();
        
        // Get the module states array.
        CognitiveModuleState[] moduleStates = state.getModuleStatesArray();
        
        for (int i = 0; i < this.numModules; i++)
        {
            CognitiveModule module = this.getModules().get(i);
            
            // Initialize the state of the model and get the initial module
            // state.
            CognitiveModuleState moduleState = module.initializeState(state);
            
            // Add the module states to the list. This is operating by side
            // effect on the states.getModuleStatesArray() values.
            moduleStates[i] = moduleState;
        }
        
        // The state has now been initialized.
        state.setInitialized(true);
    }
    
    /**
     * Gets the current state of the model.
     *
     * @return The current state of the model.
     */
    public CognitiveModelLiteState getCurrentState()
    {
        return this.state;
    }   
    
    /**
     * Gets teh semantic identifier database used by the model.
     *
     * @return The semantic identifier database used by the model.
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return this.semanticIdentifierMap;
    }    
    
    /**
     * Sets the semantic identifier map.
     *
     * Note: This is declared private because it cannot be changed from its
     * initial value without breaking the model.
     * 
     * @param semanticIdentifierMap The new map.
     */
    protected void setSemanticIdentifierMap(
        DefaultSemanticIdentifierMap semanticIdentifierMap)
    {
        if ( semanticIdentifierMap == null )
        {
            // Error: Bad semantic identifier map.
            throw new NullPointerException(
                "The semanticIdentifierMap cannot be null.");
        }
        
        this.semanticIdentifierMap = semanticIdentifierMap;
    }
}