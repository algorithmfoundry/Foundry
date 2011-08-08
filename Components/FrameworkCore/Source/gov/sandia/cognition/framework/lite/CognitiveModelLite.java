/*
 * File:                CognitiveModelLite.java
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

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides a lite implementation of the CognitiveModel interface.
 * This implementation is "lite" in the way that the modules used by the model
 * must be provided in the constructor. They cannot be dynamically added and
 * removed from the model.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class CognitiveModelLite
    extends AbstractCognitiveModelLite
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
        
    /** The CognitiveModules that are part of this model. */
    private CognitiveModule[] modules = null;
        
    /**
     * Creates a new instance of CognitiveModelLite. It instantiates new
     * CognitiveModules from all the given CognitiveModuleFactories
     *
     * @param moduleFactories The CognitiveModuleFactories used to create the
     *        CognitiveModules for this model
     */
    public CognitiveModelLite(
        CognitiveModuleFactory... moduleFactories)
    {
        this(Arrays.asList(moduleFactories));
    }
    
    /**
     * Creates a new instance of CognitiveModelLite. It instantiates new
     * CognitiveModules from all the given CognitiveModuleFactories
     *
     * @param moduleFactories The CognitiveModuleFactories used to create the
     *        CognitiveModules for this model
     */
    public CognitiveModelLite(
        Iterable<? extends CognitiveModuleFactory> moduleFactories)
    {
        super();
        
        this.setSemanticIdentifierMap(
            new DefaultSemanticIdentifierMap());
        
        // Instantiate the CognitiveModules from the given factories.
        LinkedList<CognitiveModule> moduleList = 
            new LinkedList<CognitiveModule>();
        
        for ( CognitiveModuleFactory factory :
            moduleFactories )
        {
            if( factory != null )
            {
                CognitiveModule module = factory.createModule(this);
                moduleList.add(module);
            }
        }
        
        // Set the modules.
        this.setModules(moduleList);
        this.resetCognitiveState();
    }

    /**
     * Updates the state of the model from the new input.
     *
     * @param input The input to the model.
     */
    public void update(
        CognitiveModelInput input)
    {
        // Set the input on the state.
        this.state.setInput(input);
        
        CognitiveModuleState[] moduleStates = this.state.getModuleStatesArray();
        
        for (int i = 0; i < this.numModules; i++)
        {
            CognitiveModule module = this.modules[i];
            
            CognitiveModuleState previousModuleState = moduleStates[i];
            
            CognitiveModuleState moduleState = 
                module.update(this.state, previousModuleState);
            
            // We are operating by side effect with this line of code... it's
            // changing the this.state.getModuleStatesArray return members.
            moduleStates[i] = moduleState;
        }
        
        this.fireModelStateChangedEvent();
    }    

    /**
     * Gets the modules in the model.
     *
     * @return The modulese that are part of the model.
     */
    public List<CognitiveModule> getModules()
    {
        return Arrays.asList(this.modules);
    }
    
    /**
     * Sets the modules to use in the model.
     *
     * Note: This is declared private because it cannot be changed from its
     * initial value without breaking the model.
     *
     * This function should be called exactly once.
     *
     * @param moduleCollection The modules to use in the model
     */
    private void setModules(
        Collection<CognitiveModule> moduleCollection)
    {
        this.numModules = moduleCollection.size();
        this.modules = moduleCollection.toArray(
            new CognitiveModule[this.numModules]);
    }    
}
