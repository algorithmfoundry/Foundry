/*
 * File:                StatefulEvaluatorBasedCognitiveModule.java
 * Authors:             Justin Basilico, Kevin R. Dixon, and Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.lite.CognitiveModuleStateWrapper;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.evaluator.StatefulEvaluator;
import gov.sandia.cognition.framework.CogxelState;

/**
 * The StatefulEvaluatorBasedCognitiveModule implements a CognitiveModule that 
 * wraps a StatefulEvaluator object.
 *
 * @param <InputType> Input type of the embedded Evaluator
 * @param <OutputType> Output type of the embedded Evaluator
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @author Zachary Benz
 * @since  2.0
 */
public class StatefulEvaluatorBasedCognitiveModule<InputType, OutputType>
    extends EvaluatorBasedCognitiveModule<InputType, OutputType>
{
    /** A place to store the wrapper for the CognitiveModuleState
     *  that is initialized by readState and later used by writeState
     */
    private CognitiveModuleStateWrapper stateWrapper;    
    
    /**
     * Creates a new instance of StatefulEvaluatorBasedCognitiveModule.
     *
     * @param  model The model to create the module for.
     * @param  settings The settings of the module.
     * @param name High-level descriptive name of the module
     */
    public StatefulEvaluatorBasedCognitiveModule(
        CognitiveModel model,
        EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> settings,
        String name )
    {
        super(model, settings, name );
        
        if ( !(settings.getEvaluator() instanceof StatefulEvaluator) )
        {
            throw new IllegalArgumentException(
                "The given Evaluator is not a StatefulEvaluator.");
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  modelState {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CognitiveModuleState initializeState(
        CognitiveModelState modelState)
    {
        // This module has no state.
        return new CognitiveModuleStateWrapper(
            this.getStatefulEvaluator().createDefaultState());
    }   
    
    /**
     * {@inheritDoc}
     * 
     * @param modelState {@inheritDoc}
     * @param previousModuleState {@inheritDoc}
     * @since 2.0
     */
    @Override
    public void readState(CognitiveModelState modelState,
        CognitiveModuleState previousModuleState)
    {
        // Make sure the previous module state is valid.
        if ( previousModuleState == null )
        {
            throw new IllegalArgumentException(
                "previousModuleState cannot be null");
        }
        else if (!(previousModuleState instanceof CognitiveModuleStateWrapper) )
        {
            throw new IllegalArgumentException("previousModuleState is not of "
                + "type CognitiveModuleStateWrapper");
        }
        
        // Get the module state wrapper along with the stateful evaluator.
        this.stateWrapper = 
            (CognitiveModuleStateWrapper) previousModuleState;
        StatefulEvaluator<InputType, OutputType, CloneableSerializable> 
            statefulEvaluator = this.getStatefulEvaluator();
        
        // Get the module state from the wrapper for the stateful evaluator.
        statefulEvaluator.setState(this.stateWrapper.getInternalState());
        
        // Get the cogxels from the model state.
        CogxelState cogxels = modelState.getCogxels();
        
        // Convert the Cogxels to the input type.
        this.input = this.getInputConverter().fromCogxels(cogxels);
    }

    /**
     * {@inheritDoc}
     * 
     * @param modelState {@inheritDoc}
     * @return {@inheritDoc}
     * @since 2.0
     */
    @Override
    public CognitiveModuleState writeState(CognitiveModelState modelState)
    {
        // Get the cogxels from the model state.
        CogxelState cogxels = modelState.getCogxels();
        
        // Convert the output type back to Cogxels.
        this.getOutputConverter().toCogxels(this.output, cogxels);
        
        // Done with temporarily held output, so blow it away (we NEVER retain 
        // state locally across module update cycles)
        this.output = null;
        
        // Set the module state back in the wrapper.
        StatefulEvaluator<InputType, OutputType, CloneableSerializable> 
            statefulEvaluator = this.getStatefulEvaluator();
        this.stateWrapper.setInternalState(statefulEvaluator.getState());                
        
        // Return updated module state
        return this.stateWrapper;       
    } 
    
    
    /**
     * Gets the StatefulEvaluator used by the module.
     *
     * @return The StatefulEvaluator used by the module.
     */
    @SuppressWarnings("unchecked")
    public StatefulEvaluator<InputType, OutputType, CloneableSerializable>
        getStatefulEvaluator()
    {
        return (StatefulEvaluator<InputType, OutputType, CloneableSerializable>)
            this.getEvaluator();
    }
}
