/*
 * File:                ConcurrentCognitiveModule.java
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
 
package gov.sandia.cognition.framework.concurrent;

import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleState;

/**
 * The ConcurrentCognitiveModule interface extends the functionality of 
 * CognitiveModule to support concurrent module evaluation within a 
 * CognitiveModel.  
 * 
 * A ConcurrentCognitiveModule breaks the update step of a CognitiveModule into
 * three pieces: readState, evaluate, and writeState.  In so doing, multiple
 * modules can be evaluated concurrently by a model as follows: Each module
 * sequentially reads in and holds its input state using readState;  All modules
 * are concurrently evaluated using the internally held input state by calling
 * evaluate; Lastly, each module sequentially writes out its resultant output
 * state using writeState.
 * 
 * Since a CognitiveModule belongs to a specific CognitiveModel, 
 * CognitiveModules are added to a CognitiveModel by passing in a 
 * CognitiveModuleFactory from which a CognitiveModel can create a 
 * CognitiveModule.
 *
 * While a CognitiveModule can store information about the CognitiveModel to
 * which it belongs, it should not contain any state-dependent information, 
 * such as activations. That data should be stored in an object that implements
 * the CognitiveModuleState interface. These module state objects are created
 * by the initalizeState method and are passed into the update method so that
 * the module is aware of its previous state and can return its new state.
 *
 * @author Zachary Benz
 * @since 2.0
 */
public interface ConcurrentCognitiveModule extends CognitiveModule
{
    /**
     * Read in and temporarily hold input state information required for 
     * performing module evaluation.
     * 
     * NOTE: input state is held temporarily for the sole purpose of supporting
     * concurrency of module evaluation; state is NEVER retained locally across
     * module update cycles
     * 
     * @param  modelState The CognitiveModelState to evaluate with
     * @param  previousModuleState The previous CognitiveModuleState returned 
     *         by this module
     */
    public void readState(
        final CognitiveModelState modelState,
        final CognitiveModuleState previousModuleState);
    
    /**
     * Perform evaluation of sampled and held input state information that
     * was captured by a call to readState, and hold the resultant output 
     * changes to model and module state pending a call to writeState
     * 
     * NOTE: input and output state is held temporarily for the sole purpose
     * of supporting concurrency of module evaluation; state is NEVER 
     * retained locally across module update cycles
     */
    public void evaluate();
       
    /**
     * Write out the model and module state changes resulting from a call to
     * evaluate
     * 
     * NOTE: output state was held temporarily for the sole purpose
     * of supporting concurrency of module evaluation; state is NEVER 
     * retained locally across module update cycles
     * 
     * @param modelState The CognitiveModelState to update
     * @return The updated CognitiveModuleState for this module
     */
    public CognitiveModuleState writeState(
        CognitiveModelState modelState);
}
