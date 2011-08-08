/*
 * File:                SharedSemanticMemoryLite.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * The SharedSemanticMemoryLite class implements a semantic memory that 
 * uses a shared piece of memory to store the settings.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class SharedSemanticMemoryLite
    extends AbstractSemanticMemoryLite
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
    
    /** The shared settings for this semantic memory. */
    private SharedSemanticMemoryLiteSettings sharedSettings = null;
    
    /** The minimum identifier number. */
    private int minIdentifier = 0;
    
    /** The maximum identifier number. */
    private int maxIdentifier = 0;
    
    /** The mapping of input indentifier to input index. */
    private int[] identifierToInputIndexMap = null;
    
    /**
     * Creates a new instance of SharedSemanticMemoryLite.
     * 
     * @param semanticIdentifierMap The SemanticIdentifierMap to use.
     * @param sharedSettings The shared settings to use.
     */
    public SharedSemanticMemoryLite(
        SemanticIdentifierMap semanticIdentifierMap,
        SharedSemanticMemoryLiteSettings sharedSettings)
    {
        super(semanticIdentifierMap, sharedSettings.getRecognizer());

        this.setSharedSettings(sharedSettings);
        
        Collection<SemanticLabel> inputLabels = 
            this.getRecognizer().getInputLabels();
        
        TreeMap<Integer, Integer> identifierMap = 
            new TreeMap<Integer, Integer>();
        
        int inputIndex = 0;
        for ( SemanticLabel label : inputLabels )
        {
            // Get the semantic identifier for the label.
            SemanticIdentifier semanticIdentifier 
                = semanticIdentifierMap.addLabel(label);
            
            int identifier = semanticIdentifier.getIdentifier();
            identifierMap.put(identifier, inputIndex);
            inputIndex++;
        }
        
        this.setIdentifierToInputIndexMap(identifierMap);
    }

    /**
     * {@inheritDoc}
     *
     * @param semanticIdentifier {@inheritDoc}
     * @return {@inheritDoc}
     */
    public @Override int findInputIndexForIdentifier(
        SemanticIdentifier semanticIdentifier)
    {
        if ( semanticIdentifier == null )
        {
            // Error: Bad identifier.
            return -1;
        }
        
        int identifier = semanticIdentifier.getIdentifier();
        
        if (    identifier < this.minIdentifier 
             || identifier > this.maxIdentifier )
        {
            // The identifier is not in the vector.
            return -1;
        }
        else
        {
            // Look up the identifier in our map.
            int index = identifier - this.minIdentifier;
            return this.identifierToInputIndexMap[index];
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SharedSemanticMemoryLiteSettings getSettings()
    {
        return this.sharedSettings;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getName()
    {
        return "Shared Semantic Memory Lite";
    }

    /**
     * Sets the shared settings used by the memory.
     *
     * @param sharedSettings The new shared settings.
     */
    // Note: This setter method is private because the shared settings can
    // only be set once and cannot change after that.
    private void setSharedSettings(
        SharedSemanticMemoryLiteSettings sharedSettings)
    {
        if ( sharedSettings == null )
        {
            // Error: Illegal settings.
            throw new NullPointerException(
                "The sharedSettings cannot be null.");
        }
        
        this.sharedSettings = sharedSettings;
    }
    
    /**
     * Sets the mapping of identifiers to input indices to use.
     *
     * @param identifierToInputIndexTree The new mapping.
     */
    // Note: This setter method is private because the mapping can
    // only be set once and cannot change after that.
    private void setIdentifierToInputIndexMap(
        TreeMap<Integer, Integer> identifierToInputIndexTree)
    {
        if ( identifierToInputIndexTree.size() <= 0 )
        {
            // This is an empty mapping.
            this.minIdentifier = 0;
            this.maxIdentifier = 0;
            this.identifierToInputIndexMap = new int[0];
            return;
        }
        
        // Create an array from minIdentifier to maxIdentifier.
        this.minIdentifier = identifierToInputIndexTree.firstKey();
        this.maxIdentifier = identifierToInputIndexTree.lastKey();
        
        int arraySize = this.maxIdentifier - this.minIdentifier + 1;
        this.identifierToInputIndexMap = new int[arraySize];
        
        for ( Entry<Integer, Integer> entry 
                : identifierToInputIndexTree.entrySet() )
        {
            int identifier = entry.getKey();
            int labelNum   = entry.getValue();
            
            this.identifierToInputIndexMap[identifier - minIdentifier] = 
                labelNum;
        }
    }
}
