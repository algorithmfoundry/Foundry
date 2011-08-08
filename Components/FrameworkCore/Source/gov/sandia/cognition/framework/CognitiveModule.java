/*
 * File:                CognitiveModule.java
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

/**
 * The CognitiveModule interface defines the functionality of a general module
 * that can be used in a CognitiveModel. Since a CognitiveModule belongs to a
 * specific CognitiveModel, CognitiveModules are added to a CognitiveModel 
 * by passing in a CognitiveModuleFactory from which a CognitiveModel can 
 * create a CognitiveModule.
 *
 * While a CognitiveModule can store information about the CognitiveModel to
 * which it belongs, it should not contain any state-dependent information, 
 * such as activations. That data should be stored in an object that implements
 * the CognitiveModuleState interface. These module state objects are created
 * by the initalizeState method and are passed into the update method so that
 * the module is aware of its previous state and can return its new state.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 * @see    CognitiveModuleFactory
 * @see    CognitiveModuleState
 */
public interface CognitiveModule
    extends Serializable
{
    /**
     * This method initializes the state object for a CognitiveModel by adding
     * any necessary information to the model state and returining the default
     * state for the module.
     *
     * @param  modelState The CognitiveModelState to initalize
     * @return The initial state of the CognitiveModule
     */
    public CognitiveModuleState initializeState(
        CognitiveModelState modelState);
    
    /**
     * This method is the main method for a CognitiveModule. It updates the
     * state of the model by modifying the given CognitiveModelState object.
     * Since a module is not to store any local state information, it is given
     * its previous CognitiveModuleState object in order to provide the 
     * information about its state. It then returns its updated 
     * CognitiveModuleState as the result of the update method.
     *
     * @param  modelState The CognitiveModelState to update
     * @param  previousModuleState The previous CognitiveModuleState returned 
     *         by this module
     * @return The updated CognitiveModuleState for this module
     */
    public CognitiveModuleState update(
        CognitiveModelState modelState,
        final CognitiveModuleState previousModuleState);
    
    /**
     * Gets the human-readable name of module.
     *
     * @return The human-readable name of the module.
     */
    public String getName();
    
    /**
     * Gets the settings for the module, which can be used to create another
     * instantation of a module.
     *
     * @return The settings for the module
     */
    public CognitiveModuleSettings getSettings();
}
