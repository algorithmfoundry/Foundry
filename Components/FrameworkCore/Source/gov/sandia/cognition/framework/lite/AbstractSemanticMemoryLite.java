/*
 * File:                AbstractSemanticMemoryLite.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework
 *
 * Copyright 2006, Sandia Corporation. Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.SemanticMemory;
import gov.sandia.cognition.framework.SemanticNetwork;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The AbstractSemanticMemoryLite implements the common functionality among
 * SemanticMemoryLite modules.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public abstract class AbstractSemanticMemoryLite
    extends java.lang.Object
    implements SemanticMemory, Serializable
{
    /** The mapping of semantic labels to semantic identifiers. */
    private SemanticIdentifierMap semanticIdentifierMap = null;
    
    /** The PatternRecognizerLite that is used. */
    private PatternRecognizerLite recognizer = null;
    
    /** The mapping of output vector entries to identifiers. */
    private ArrayList<SemanticIdentifier> outputIdentifiers = null;

    /**
     * Creates a new instance of AbstractSemanticMemoryLite.
     * 
     * @param semanticIdentifierMap The SemanticIdentifierMap to use for mapping 
     *           SemanticLabels to SemanticIdentifiers.
     * @param recognizer The PatternRecognizerLite used by this.
     */
    public AbstractSemanticMemoryLite(
        SemanticIdentifierMap semanticIdentifierMap,
        PatternRecognizerLite recognizer)
    {
        super();

        this.setSemanticIdentifierMap(semanticIdentifierMap);
        this.setRecognizer(recognizer);
        this.buildOutputMapping();
    }
    
    /**
     * Builds the mapping of output identifiers to their labels.
     */
    protected void buildOutputMapping()
    {
        // Figure out the mapping of identifiers to entries in our vector.
        Collection<SemanticLabel> outputLabels = 
            this.getRecognizer().getOutputLabels();

        // Create the array-list of ordered identifiers.
        ArrayList<SemanticIdentifier> outputs = 
            this.getSemanticIdentifierMap().addLabels(outputLabels);

        this.setOutputIdentifiers(outputs);
    }
    
    /**
     * This method initializes the state object for the CognitiveModule by
     * calling the initialState function on the underlying pattern recognizer.
     *
     * @param  modelState The CognitiveModelState to initalize
     * @return The initial state of the CognitiveModule
     */
    public CognitiveModuleState initializeState(
        CognitiveModelState modelState)
    {
        return this.getRecognizer().initialState();
    }
    
    /**
     * Updates the state of the cognitive model by modifying the given
     * CognitiveModelState object.
     *
     * @param  modelState The CognitiveModelState to update.
     * @param  previousModuleState The previous CognitiveModuleState returned 
     *         by this module.
     * @return The updated CognitiveModuleState for this module.
     */
    public CognitiveModuleState update(
        CognitiveModelState modelState,
        CognitiveModuleState previousModuleState)
    {
        // Create the vectors to store the inputs and outputs.
        Vector inputs  = this.getRecognizer().createEmptyInputVector();
        
        // Map input cogxels into a vector.
        CogxelState cogxels = modelState.getCogxels();
        for ( Cogxel cogxel : cogxels )
        {
            // Get the index of the identifier.
            SemanticIdentifier identifier = cogxel.getSemanticIdentifier();
            int index = this.findInputIndexForIdentifier(identifier);
            
            if ( index < 0 )
            {
                // Not a part of our vector.
                continue;
            }
            
            // Get the cogxel.
            double value = cogxel.getActivation();
            inputs.setElement(index, value);
        }
        
        // Get the Recognizer's output values
        Vector outputs = 
            this.getRecognizer().recognize(previousModuleState, inputs);
        
        // Map vector output to cogxels.
        int dimensionality = outputs.getDimensionality();
        for (int i = 0; i < dimensionality; i++)
        {
            double value = outputs.getElement(i);
            
            SemanticIdentifier identifier = this.getOutputIdentifiers().get(i);
            Cogxel cogxel = 
                cogxels.getOrCreateCogxel(identifier, 
                    DefaultCogxelFactory.INSTANCE);
            cogxel.setActivation(value);
        }
        
        // I have modified my previous MODULE state by side effect,
        // so I can just return the (modified) previous state as my
        // updated state.
        return previousModuleState;
    }
    
    /**
     * Finds the input vector index for a given identifier. If the identifier is not
     * in the index, -1 is returned.
     *
     * @param semanticIdentifier The identifier to find the input index of.
     * @return The input vector index for the given identifier, if it is part
     *         of the input vector. -1 otherwise.
     */
    public abstract int findInputIndexForIdentifier(
        SemanticIdentifier semanticIdentifier);

    /**
     * Gets the semantic network underlying the semantic memory.
     *
     * @return The SemanticNetwork under the memory.
     */
    public SemanticNetwork getNetwork()
    {
        return this.getRecognizer().getNetwork();
    }

    /**
     * Gets the semantic identifier map being used.
     *
     * @return The semantic identifier map being used.
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return this.semanticIdentifierMap;
    }
    
    /**
     * Gets the pattern recognizer lite for the semantic memory lite.
     *
     * @return The pattern recognizer for the semantic memory lite.
     */
    public PatternRecognizerLite getRecognizer()
    {
        return this.recognizer;
    }

    /**
     * Gets the ordered list of output identifiers.
     *
     * @return The ordered list of output identifiers.
     */
    protected ArrayList<SemanticIdentifier> getOutputIdentifiers()
    {
        return this.outputIdentifiers;
    }

    /**
     * Sets the semantic identifier map being used.
     *
     * @param semanticIdentifierMap The semantic identifier map to use.
     */
    private void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {        
        this.semanticIdentifierMap = semanticIdentifierMap;
    }

    /**
     * Sets the pattern recognizer used by the semantic memory.
     * 
     * Note: This should be called exactly once.
     * 
     * @param recognizer The new pattern recognizer.
     */
    private void setRecognizer(
        PatternRecognizerLite recognizer)
    {
        this.recognizer = recognizer;
    }
    
    /**
     * Set the output identifiers mapping to use.
     *
     * @param outputIdentifiers The new output identifiers.
     */
    private void setOutputIdentifiers(
        ArrayList<SemanticIdentifier> outputIdentifiers)
    {
        this.outputIdentifiers = outputIdentifiers;
    }
}
