/*
 * File:                VectorBasedPerceptionModule.java
 * Authors:             Kevin R. Dixon
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

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleSettings;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifier;
import java.io.Serializable;

/**
 * Module that takes CognitiveModelInputs and turns them into a Vector
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class VectorBasedPerceptionModule
    implements CognitiveModule, Serializable
{
    
    /** The name of the module. */
    public static final String MODULE_NAME = "Vector-based Perception Module";
    
    
    /**
     * Factory for creating Cogxels for perception
     */
    private CogxelFactory cogxelFactory;    
    
    
    /**
     * Creates a new instance of VectorBasedPerceptionModule
     * @param cogxelFactory 
     * Factory for creating Cogxels for perception
     */
    public VectorBasedPerceptionModule(
        CogxelFactory cogxelFactory )
    {
        this.setCogxelFactory( cogxelFactory );
    }

    /**
     * {@inheritDoc}
     *
     * @param  modelState {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModuleState initializeState(
        CognitiveModelState modelState)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getName()
    {
        return VectorBasedPerceptionModule.MODULE_NAME;
    }
    
    /**
     * Returns null because this module has no settings.
     *
     * @return Null because this module has no settings.
     */
    public CognitiveModuleSettings getSettings()
    {
        return null;
    }
        

    /**
     * {@inheritDoc}
     *
     * @param  modelState {@inheritDoc}
     * @param  previousModuleState {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModuleState update(
        CognitiveModelState modelState, 
        final CognitiveModuleState previousModuleState)
    {
        // Get the input from the state object.
        CognitiveModelInput inputObject = modelState.getInput();
        
        if ( inputObject == null )
        {
            // Error: Bad input object.
            return null;
        }
        else if ( !(inputObject instanceof VectorBasedCognitiveModelInput) )
        {
            // Not the type of input that this perception module is expecting
            // to receive.
            throw new IllegalArgumentException( "Expecting VectorBasedCognitiveModelInput but received: " + inputObject.getClass() );
        }
        
        // Get our input object.
        VectorBasedCognitiveModelInput input = 
            (VectorBasedCognitiveModelInput) inputObject;
        
        // Get the CogxelState because we are going to be adding Cogxels to it.
        CogxelState cogxels = modelState.getCogxels();
        
        // Loop over the input and add all the Cogxels for the identifiers.
        int numInputs = input.getNumInputs();
        for (int i = 0; i < numInputs; i++)
        {
            // Get the identifier and its value.
            SemanticIdentifier identifier = input.getIdentifier(i);
            double value = input.getValues().getElement(i);
            
            // Create a cogxel for it.
            Cogxel cogxel = 
                cogxels.getOrCreateCogxel(identifier, this.getCogxelFactory());
            
            if ( cogxel != null )
            {
                // Set the activation of the cogxel to the given value.
                cogxel.setActivation(value);
            }
            // else - It should never be null, but if it is we'll just ignore 
            //        it.
        }
        
        // We're done, but we don't have any state so just return null.
        return null;
    }
    

    /**
     * Getter for cogxelFactory
     * @return 
     * Factory for creating Cogxels for perception
     */
    public CogxelFactory getCogxelFactory()
    {
        return this.cogxelFactory;
    }

    /**
     * Setter for cogxelFactory
     * @param cogxelFactory 
     * Factory for creating Cogxels for perception
     */
    public void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        this.cogxelFactory = cogxelFactory;
    }
    
}
