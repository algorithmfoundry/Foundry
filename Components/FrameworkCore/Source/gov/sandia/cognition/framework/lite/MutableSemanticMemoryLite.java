/*
 * File:                MutableSemanticMemoryLite.java
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
import java.util.HashMap;
import java.util.HashSet;

/**
 * The MutableSemanticMemoryLite implements a SemanticMemory that can be 
 * dynamically changed. Because it can be dynamically changed,
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class MutableSemanticMemoryLite
    extends AbstractSemanticMemoryLite
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
    
    /** The mutable pattern recognizer to modify. */
    private MutablePatternRecognizerLite mutableRecognizer = null;
    
    /** The mapping of identifier number to input vector index number. */
    private HashMap<Integer, Integer> identifierToInputIndexMap = null;
    
    /** The set of nodes used by the memory. */
    private HashSet<SemanticLabel> nodeSet = null;
        
    /**
     * Creates a new instance of MutableSemanticMemoryLite. It creates its own
     * copy of the given mutable recognizer.
     * 
     * @param semanticIdentifierMap The SemanticIdentifierMap to use.
     * @param recognizer The pattern recognizer to use, which is copied and
     *        stored internally.
     */
    public MutableSemanticMemoryLite(
        SemanticIdentifierMap semanticIdentifierMap,
        MutablePatternRecognizerLite recognizer)
    {
        super(semanticIdentifierMap, recognizer.clone());
        
        this.setMutableRecognizer(
            (MutablePatternRecognizerLite) this.getRecognizer());
        this.buildInputMapping();
        
        
        HashSet<SemanticLabel> nodes = new HashSet<SemanticLabel>();
        for ( SemanticLabel label : recognizer.getAllLabels() )
        {
            // Add this label to the label set.
            nodes.add(label);
        }
        
        this.setNodeSet(nodes);
    }
    
    /**
     * Builds the mapping of identifiers to input indices.
     */
    protected void buildInputMapping()
    {
        // Create the mapping of input identifiers to indices along with the 
        // set of labels that are in use by the memory.
        Collection<SemanticLabel> inputLabels = 
            this.getRecognizer().getInputLabels();
        HashMap<Integer, Integer> identifierToIndexMap = 
            new HashMap<Integer, Integer>(inputLabels.size());
        SemanticIdentifierMap semanticIdentifierMap = 
            this.getSemanticIdentifierMap();
        
        // Use a counter for the vector index.
        int vectorIndex = 0;
        for ( SemanticLabel label : inputLabels )
        {
            // Get the semantic identifier for the label.
            SemanticIdentifier semanticIdentifier =
                semanticIdentifierMap.addLabel(label);
            int identifier = semanticIdentifier.getIdentifier();
         
            // Add this entry to the identifier to index mapping.
            identifierToIndexMap.put(identifier, vectorIndex);
            
            // Update the vector index counter.
            vectorIndex++;
        }
        
        this.setIdentifierToInputIndexMap(identifierToIndexMap);
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
        Integer index = this.identifierToInputIndexMap.get(identifier);
        
        if ( index == null )
        {
            // Identifier is not in the map.
            return -1;
        }
        else
        {
            // Return the index.
            return index;
        }
    }
    
    /**
     * Returns true if the given semantic label is a node in the network.
     *
     * @param  label The label to look at.
     * @return True if the given label is a node in the semantic network and 
     *         false otherwise.
     */
    public boolean isNode(
        SemanticLabel label)
    {
        return this.nodeSet.contains(label);
    }
    
    /**
     * Adds a node to the semantic memory.
     *
     * @param label The label for the node to add.
     */
    public void addNode(
        SemanticLabel label)
    {
        if ( this.isNode(label) )
        {
            // No need to add the node, it is already in the network.
            return;
        }
        
        // Add the node to the recognizer.
        this.mutableRecognizer.addNode(label);
        this.nodeSet.add(label);
        
        // Add an identifier for this label.
        // SemanticIdentifier identifier = 
        this.getSemanticIdentifierMap().addLabel(label);
        
        if ( mutableRecognizer.isInputLabel(label) )
        {
            this.buildInputMapping();
        }
        
        if ( mutableRecognizer.isOutputLabel(label) )
        {
            this.buildOutputMapping();
        }
    }
    
    /**
     * Removes a node and all links associated with that node from the semantic
     * memory.
     *
     * @param label The label of the node to remove.
     */
    public void removeNode(
        SemanticLabel label)
    {
        if ( !this.isNode(label) )
        {
            // This isn't a node so there is nothing to remove.
            return;
        }
        
        // See if this was an input or output label.
        boolean wasInputLabel  = this.mutableRecognizer.isInputLabel(label);
        boolean wasOutputLabel = this.mutableRecognizer.isOutputLabel(label);
        
        // Remove the from the nodeset.
        this.nodeSet.remove(label);
        
        // Have the recognizer remove the node.
        this.mutableRecognizer.removeNode(label);
        
        // Get the semantic identifier.
        SemanticIdentifier identifier = 
            this.getSemanticIdentifierMap().findIdentifier(label);
        
        if ( wasInputLabel )
        {
            // Update the input mapping.
            this.buildInputMapping();
        }
        
        if ( wasOutputLabel )
        {
            // Update the output mapping.
            this.getOutputIdentifiers().remove(identifier);
        }
    }
    
    /**
     * Attempts to set whether or not the given label is an input label. It 
     * returns true if the set operation was successful and false otherwise.
     *
     * @param  label The label to set whether or not it is input label.
     * @param  inputLabel Whether or not the label is an input label.
     * @return True if the operation was successful and false if it was not.
     */
    public boolean trySetInputLabel(
        SemanticLabel label,
        boolean inputLabel)
    {
        if ( this.mutableRecognizer.isInputLabel(label) == inputLabel )
        {
            // No change is required.
            return true;
        }
        
        // Call the change on the recognizer.
        boolean success = 
            this.mutableRecognizer.trySetInputLabel(label, inputLabel);
        
        if ( success )
        {
            // We changed the input mapping so rebuild it.
            this.buildInputMapping();
        }
        
        return success;
    }
    
    /**
     * Attempts to set whether or not the given label is an output label. It 
     * returns true if the set operation was successful and false otherwise.
     *
     * @param  label The label to set whether or not it is output label.
     * @param  outputLabel Whether or not the label is an output label.
     * @return True if the operation was successful and false if it was not.
     */
    public boolean trySetOutputLabel(
        SemanticLabel label,
        boolean outputLabel)
    {
        if ( this.mutableRecognizer.isOutputLabel(label) == outputLabel )
        {
            // No change is required.
            return true;
        }
        
        // Call the change on the recognizer.
        boolean success = 
            this.mutableRecognizer.trySetOutputLabel(label, outputLabel);
        
        if ( success )
        {
            // We changed the output mapping so rebuild it.
            this.buildOutputMapping();
        }
        
        return success;
    }
    
    /**
     * Sets the association between nodes in the recognizer.
     *
     * @param from The label of the node the assocation is from.
     * @param to The label of the node the assocation is to.
     * @param weight The weight of the association.
     */
    public void setAssociation(
        SemanticLabel from,
        SemanticLabel to,
        double weight)
    {
        this.assertArgumentIsNode(from);
        this.assertArgumentIsNode(to);
        
        // Modify the association.
        this.mutableRecognizer.setAssociation(from, to, weight);
    }
    
    /**
     * Asserts that the given argument is a node, throwing an
     * IllegalArgumentException if it is not a node.
     *
     * @param  label The label to assert that it is a node in the recognizer.
     * @throws IllegalArgumentException If the given label is not a node.
     */
    protected void assertArgumentIsNode(
        SemanticLabel label)
    {
        if ( !this.isNode(label) )
        {
            // Error: No such node.
            throw new IllegalArgumentException(
                "The given SemanticLabel " + label.getName() + " is not a node "
                + "in the semantic memory.");
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getName()
    {
        return "Mutable Semantic Memory Lite";
    }    
    
    /**
     * Gets the pattern recognizer.
     *
     * @return The pattern recognizer.
     */
    public MutablePatternRecognizerLite getSettings()
    {
        return this.mutableRecognizer;
    }
    
    /**
     * Gets the mutable recognizer.
     *
     * @return The mutable recognizer.
     */
    private MutablePatternRecognizerLite getMutableRecognizer()
    {
        return this.mutableRecognizer;
    }
    
    /**
     * Sets the mutable recognizer.
     *
     * @param mutableRecognizer The new mutable recognizer.
     */
    private void setMutableRecognizer(
        MutablePatternRecognizerLite mutableRecognizer)
    {
        if ( mutableRecognizer == null )
        {
            // Error: Bad mutable recognizer.
            throw new NullPointerException(
                "The mutableRecognizer cannot be null.");
        }
        
        this.mutableRecognizer = mutableRecognizer;
    }

    /**
     * Sets the identifier to input index map to use.
     *
     * @param identifierToInputIndexMap The mapping to use.
     */
    private void setIdentifierToInputIndexMap(
        HashMap<Integer, Integer> identifierToInputIndexMap)
    {
        if ( identifierToInputIndexMap == null )
        {
            // Error: Bad mapping.
            throw new NullPointerException(
                "The identifierToInputIndexMap cannot be null.");
        }
        
        this.identifierToInputIndexMap = identifierToInputIndexMap;
    }

    /**
     * Sets the set of labels that are in the memory.
     *
     * @param nodeSet The set of labels.
     */
    private void setNodeSet(
        HashSet<SemanticLabel> nodeSet)
    {
        if ( nodeSet == null )
        {
            // Error: Bad label set.
            throw new NullPointerException("The nodeSet cannot be null.");
        }
        
        this.nodeSet = nodeSet;
    }
}
