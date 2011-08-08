/*
 * File:                ArrayBasedPerceptionModule.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 29, 2006, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.framework.CognitiveModuleSettings;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.concurrent.AbstractConcurrentCognitiveModule;
import java.io.Serializable;

/**
 * The ArrayBasedPerceptionModule class implements a simple CognitiveModule that
 * sets the state of Cogxels based on a given input of SemanticIdentifiers and
 * their associated activation values. It makes use of a given CogxelFactory
 * to create the initial Cogxels from the SemanticIdentifiers.
 *
 * @author Justin Basilico
 * @author Zachary Benz
 * @since 1.0
 */
public class ArrayBasedPerceptionModule
    extends AbstractConcurrentCognitiveModule
    implements Serializable
{
    /** The name of the module. */
    public static final String MODULE_NAME = "Array-based Perception Module";
    
    /**
     * The semantic identifier map. Currently unused, but provided because 
     * there may be a need to support SemanticLabels instead of identifiers
     * later on.
     */
    private SemanticIdentifierMap semanticIdentifierMap = null;
    
    /** The factory for creating Cogxels for perception. */
    private CogxelFactory cogxelFactory = null;
    
    /** 
     * A place to temporarily store the input read in by a call to readState;
     * this temporary store is blown away as soon as it used by writeState,
     * because we NEVER retain state interally across module update cycles
     */
    private ArrayBasedCognitiveModelInput input;

    /**
     * Creates a new instance of ArrayBasedPerceptionModule.
     *
     * @param semanticIdentifierMap The semantic identifier map to use.
     * @param cogxelFactory The CogxelFactory for creating Cogxels.
     */
    public ArrayBasedPerceptionModule(
        SemanticIdentifierMap semanticIdentifierMap,
        CogxelFactory cogxelFactory)
    {
        super();
        
        this.setSemanticIdentifierMap(semanticIdentifierMap);
        this.setCogxelFactory(cogxelFactory);
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
     * @param modelState {@inheritDoc}
     * @param previousModuleState {@inheritDoc}
     * @since 2.0
     */
    public void readState(CognitiveModelState modelState,
        CognitiveModuleState previousModuleState)
    {
        // Get the input from the state object.
        CognitiveModelInput inputObject = modelState.getInput();
        
        if ( inputObject == null )
        {
            // Error: Bad input object.
            throw new IllegalArgumentException( "Expecting " +
                "ArrayBasedCognitiveModelInput but received null input");
        }
        else if ( !(inputObject instanceof ArrayBasedCognitiveModelInput) )
        {
            // Not the type of input that this perception module is expecting
            // to receive.
            throw new IllegalArgumentException( "Expecting " +
                "ArrayBasedCognitiveModelInput but received: " + inputObject.getClass() );
        }
        
        // Get our input object.
        this.input = (ArrayBasedCognitiveModelInput) inputObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 2.0
     */
    public void evaluate()
    {
        // Nothing to do; everything happens during writeState, since we are
        // simply copying the input to output cogxels in the model state
    }

    /**
     * {@inheritDoc}
     * 
     * @param modelState {@inheritDoc}
     * @return {@inheritDoc}
     * @since 2.0
     */
    public CognitiveModuleState writeState(CognitiveModelState modelState)
    {
        // Get the CogxelState because we are going to be adding Cogxels to it.
        CogxelState cogxels = modelState.getCogxels();
                
        // Loop over the input and add all the Cogxels for the identifiers.
        int numInputs = this.input.getNumInputs();
        for (int i = 0; i < numInputs; i++)
        {
            // Get the identifier and its value.
            SemanticIdentifier identifier = this.input.getIdentifier(i);
            double value = this.input.getValue(i);
            
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
        
        // Done with temporarily held input, so blow it away (we NEVER retain 
        // state locally across module update cycles)
        this.input = null;
        
        // We're done, but we don't have any module state so just return null.
        return null;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getName()
    {
        return ArrayBasedPerceptionModule.MODULE_NAME;
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
     * Sets the semantic identifier map.
     *
     * @param semanticIdentifierMap The new semantic identifier map.
     */
    protected void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        if ( semanticIdentifierMap == null )
        {
            // Error: Bad semanticIdentifierMap.
            throw new NullPointerException(
                "The semanticIdentifierMap cannot be null.");
        }
        
        this.semanticIdentifierMap = semanticIdentifierMap;
    }
    
    /**
     * Sets the cogxel factory to use.
     *
     * @param cogxelFactory The new cogxel factory to use.
     */
    protected void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        if ( cogxelFactory == null )
        {
            // Error: Bad factory.
            throw new NullPointerException("The cogxelFactory cannot be null.");
        }
        
        this.cogxelFactory = cogxelFactory;
    }

    /**
     * Getter for semanticIdentifierMap
     *
     * @return The semantic identifier map. Currently unused, but provided
     * because there may be a need to support SemanticLabels instead of
     * identifiers later on.
     */
    protected SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return semanticIdentifierMap;
    }

    /**
     * Getter for cogxelFactory 
     *
     * @return The factory for creating Cogxels for perception.
     */
    protected CogxelFactory getCogxelFactory()
    {
        return cogxelFactory;
    }
}

